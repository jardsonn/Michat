package com.jalloft.michat.ui.screens.chat

import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.speech.RecognizerIntent
import android.view.WindowManager
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.jalloft.michat.R
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.systemMessage
import com.jalloft.michat.ui.components.ChatTopBar
import com.jalloft.michat.ui.components.LoadingAnimation
import com.jalloft.michat.ui.theme.White
import com.jalloft.michat.utils.ConnectionState
import com.jalloft.michat.utils.connectivityState
import com.jalloft.michat.utils.keyboardAsState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.*
import kotlin.math.max
import kotlin.math.min

@OptIn(BetaOpenAI::class, ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
//    viewModel: ChatViewModel = hiltViewModel(),
    viewModel: NewChatViewModel = hiltViewModel(),
    assistant: AssistantIdentifier,
    onNotifyNetworkWrning: () -> Unit,
    onBackClicked: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val connectionState by connectivityState()

    val isNetworkConnected = connectionState == ConnectionState.Available

    val currentMessages by viewModel.currentMessages.observeAsState()

    val chatStarted = currentMessages?.isNotEmpty() ?: false

    val isProcessing by viewModel.isProcessing.observeAsState()

    i("Esta processando 1: $isProcessing")

    if (!isNetworkConnected && isProcessing == true) {
        viewModel.cancelSendingMessage()
    }

//    if (isNetworkConnected) {
//        viewModel.answerLastMessage()
//    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                assistant,
                isProcessing = isProcessing ?: false,
                isNetworkConnected = isNetworkConnected,
                onBackClicked = onBackClicked
            )
        }
    ) { values ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(values)
        ) {
            ChatBody(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(if (chatStarted) 0.dp else 8.dp),
                scrollState,
                chatStarted,
                currentMessages,
                isNetworkConnected = isNetworkConnected,
                isProcessing ?: false,
                onSendMessage = {
                    viewModel.sendMessage(it, ChatRole.User, isNetworkConnected)
                    if (!isNetworkConnected) {
                        viewModel.cancelSendingMessage()
                        onNotifyNetworkWrning()
                    }
                }
            )

            if (!chatStarted) {
                val systemMessage = assistant.systemMessage()
                StartConversationNotice(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.Center),
                    onStartChat = {
                        if (isNetworkConnected) {
                            viewModel.sendMessage(systemMessage, ChatRole.System)
                        } else {
                            onNotifyNetworkWrning()
                        }
                    }
                )
            }
        }

    }

    LaunchedEffect(connectionState, /*currentMessages*/) {
        delay(1000)
        if (isNetworkConnected && isProcessing == false){
            viewModel.answerLastMessage()
        }
    }

}

@OptIn(BetaOpenAI::class)
@Composable
fun ChatBody(
    modifier: Modifier,
    scrollState: LazyListState,
    chatStarted: Boolean,
    currentMessages: List<ChatMessage>?,
    isNetworkConnected: Boolean,
    isProcessing: Boolean,
    onSendMessage: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val isKeyboardOpen by keyboardAsState()

    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.chat_bot_toogle) }
    var waintingAnswer by remember { mutableStateOf(false) }

    if (!isProcessing && waintingAnswer && isNetworkConnected) {
        mediaPlayer.start()
        waintingAnswer = false
    }

    val (text, setText) = rememberSaveable {
        mutableStateOf("")
    }

    ConstraintLayout(modifier = modifier) {
        val (chatTextinput, messagesList, circleTyping) = createRefs()
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(messagesList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(chatTextinput.top)
                    height = Dimension.fillToConstraints
                },
            reverseLayout = true,
            state = scrollState,
            verticalArrangement = Arrangement.Top,
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                i("ESTÁ Processando: $isProcessing")
                if (isProcessing) {
                    LoadingAnimation(
                        shape = RoundedCornerShape(100),
                        modifier = Modifier
                            .constrainAs(circleTyping) { top.linkTo(messagesList.bottom) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

            currentMessages?.let { chatMessage ->
                items(chatMessage.dropLastWhile { it.role == ChatRole.System }) {
                    MessageBody(it)
                }
            }
        }

        ChatTextInput(
            modifier = Modifier
                .constrainAs(chatTextinput) {
                    bottom.linkTo(parent.bottom)
                },
            value = text,
            isNetworkConnected = isNetworkConnected,
            onValueChange = { setText(it) },
            onVoiceClicked = {
                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )

                intent.putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE,
                    Locale.getDefault()
                )
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to text")

            },
            chatStarted = chatStarted,
            isProcessing = isProcessing,
            onSendMessage = {
                onSendMessage(text)
                waintingAnswer = true
            },
        )
    }

    LaunchedEffect(currentMessages?.size, isKeyboardOpen) {
        coroutineScope.launch {
            scrollState.animateScrollToItem(0)
        }
    }

    DisposableEffect(mediaPlayer) {
        onDispose {
            mediaPlayer.release()
        }
    }
}

@Composable
fun StartConversationNotice(modifier: Modifier, onStartChat: () -> Unit) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.onSurface
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Seja bem-vindo, você pode mandar 10 mensagens grátis.",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = onStartChat,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Começar",
                )
            }
        }

    }
}


@OptIn(BetaOpenAI::class)
@Composable
fun MessageBody(chatMessage: ChatMessage) {
    val cardRoundness = calculateBorderRadius(chatMessage.content)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = getHorizontalAlignment(chatMessage.role),
    ) {
        Card(
            modifier = Modifier.widthIn(max = 340.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (chatMessage.role == ChatRole.User) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurface
            ),
            shape = messageBodyShape(chatMessage.role, cardRoundness)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = chatMessage.content,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (chatMessage.role == ChatRole.User) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                )
            }
        }
    }
}

@Composable
fun calculateBorderRadius(text: String): Dp {
    val maxLength = 100 // Define o tamanho máximo do texto para o cálculo
    val defaultRadius = 16.dp // Raio padrão das bordas arredondadas
    val maxRadius = 8.dp // Raio máximo das bordas arredondadas

    // Calcula o tamanho relativo do texto em relação ao tamanho máximo
    val textLengthPercentage = (text.length.toFloat() / maxLength.toFloat()).coerceIn(0f, 1f)

    // Calcula o tamanho do arredondamento baseado no tamanho relativo do texto
    val calculatedRadius = maxRadius * (1f - textLengthPercentage)

    // Retorna o tamanho do arredondamento, considerando o valor mínimo
    return (defaultRadius + calculatedRadius).coerceAtLeast(0.dp)
}

@OptIn(BetaOpenAI::class)
@Composable
fun messageBodyShape(role: ChatRole, size: Dp): Shape {
    val roundedCorners = RoundedCornerShape(size)
    return when (role) {
        ChatRole.User -> roundedCorners.copy(topEnd = CornerSize(0))
        else -> roundedCorners.copy(topStart = CornerSize(0))
    }
}


@Composable
fun ChatTextInput(
    modifier: Modifier,
    value: String,
    isNetworkConnected: Boolean,
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceClicked: () -> Unit,
    chatStarted: Boolean,
    isProcessing: Boolean,
) {
    var showSendButton by remember { mutableStateOf(false) }
    var characterCount by remember {
        mutableStateOf(0)
    }

    val maxCharacters = 500

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.onSurface, RoundedCornerShape(3)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .imePadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 100.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Sentences,
                        imeAction = ImeAction.Done,
                    ),
                    value = value,
                    enabled = chatStarted,
                    onValueChange = {
                        if (it.length <= maxCharacters){
                            onValueChange(it)
                        }
                        showSendButton = it.isNotEmpty()
                        characterCount = min(it.length, maxCharacters)
                    },
                    textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.surface),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    shape = RoundedCornerShape(50)
                                )
                                .padding(start = 16.dp, top = 16.dp, end = 24.dp, bottom = 16.dp),
                        ) {
                            if (value.isEmpty()) {
                                Text(
                                    text = stringResource(id = R.string.message),
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.surface.copy(
                                            .2f
                                        )
                                    )
                                )
                            }
                            it()
                        }
                    }
                )

                Button(
                    onClick = {
                        onSendMessage()
//                if (isNetworkConnected) {
                        onValueChange("")
                        showSendButton = false
//                }

                    },
                    shape = RoundedCornerShape(25),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = White,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(.5f)
                    ),
                    modifier = Modifier.size(38.dp),
                    contentPadding = PaddingValues(0.dp),
                    enabled = showSendButton && !isProcessing
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_send_24),
                        contentDescription = null
                    )
                }
            }

            Text(
                text = "$characterCount/$maxCharacters",
                style = MaterialTheme.typography.bodySmall
            )
        }


    }

}


@OptIn(BetaOpenAI::class)
fun getHorizontalAlignment(chatRole: ChatRole) = when (chatRole) {
    ChatRole.User -> Alignment.End
    else -> Alignment.Start
}