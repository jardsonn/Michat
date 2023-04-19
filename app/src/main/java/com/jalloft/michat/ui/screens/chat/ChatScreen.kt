package com.jalloft.michat.ui.screens.chat

import android.content.Intent
import android.media.MediaPlayer
import android.os.Build
import android.speech.RecognizerIntent
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsAnimationCompat
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
import com.jalloft.michat.utils.keyboardAsState
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import java.util.*
import kotlin.math.max

@OptIn(BetaOpenAI::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = hiltViewModel(),
    assistant: AssistantIdentifier,
    onBackClicked: () -> Unit
) {

    val scrollState = rememberLazyListState()

    //    val isProcessing = viewModel.processing.observeAsState()

//    var isProcessing by remember {
//        mutableStateOf(false)
//    }

    val currentMessages by viewModel.currentMessages.observeAsState()

    val chatStarted = currentMessages?.isNotEmpty() ?: false

    val isProcessing by viewModel.isProcessing.observeAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ChatTopBar(
                assistant,
                isProcessing = isProcessing ?: false,
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
                isProcessing ?: false,
                onSendMessage = {
                    viewModel.sendMessage(it, ChatRole.User)
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
                        viewModel.sendMessage(systemMessage, ChatRole.System)
//                        chatStarted = true
                    }
                )
            }
        }

    }

//    LaunchedEffect(messages.value?.size) {
//        coroutineScope.launch {
//            scrollState.animateScrollToItem(max(messages.value?.size ?: 0, 1) - 1)
//        }
//    }

}

@OptIn(BetaOpenAI::class)
@Composable
fun ChatBody(
    modifier: Modifier,
    scrollState: LazyListState,
    chatStarted: Boolean,
    currentMessages: List<ChatMessage>?,
    isProcessing: Boolean,
    onSendMessage: (String) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()

    val isKeyboardOpen by keyboardAsState()

    val context = LocalContext.current

    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.chat_bot_toogle) }
    var waintingAnswer by remember { mutableStateOf(false) }

    if (!isProcessing && waintingAnswer) {
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
            state = scrollState
        ) {
            currentMessages?.let { chatMessage ->
                items(chatMessage.dropWhile { it.role == ChatRole.System }) {
                    MessageBody(it)
                }
            }

//            items(messages.dropWhile { it.role == ChatRole.System }) {
//                MessageBody(it)
//            }

            item {
                if (isProcessing) {
                    LoadingAnimation(
                        shape = RoundedCornerShape(100),
                        modifier = Modifier
                            .constrainAs(circleTyping) { top.linkTo(messagesList.bottom) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }

        }

        ChatTextInput(
            modifier = Modifier.constrainAs(chatTextinput) {
                bottom.linkTo(parent.bottom)
            },
            value = text,
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
//                    viewModel.sendMessage(text)
//                val message = ChatMessage(
//                    if (messages.size % 2 == 0) ChatRole.User else ChatRole.Assistant,
//                    text
//                )
//                messages.add(message)
            },
        )
    }

    LaunchedEffect(currentMessages?.size, isKeyboardOpen) {
        coroutineScope.launch {
            scrollState.animateScrollToItem(max(currentMessages?.size ?: 0, 1) - 1)
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
    onValueChange: (String) -> Unit,
    onSendMessage: () -> Unit,
    onVoiceClicked: () -> Unit,
    chatStarted: Boolean,
    isProcessing: Boolean,
) {
    var showSendButton by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BasicTextField(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 100.dp)
                .weight(1f)
                .padding(end = 8.dp),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            value = value,
            enabled = chatStarted,
            onValueChange = {
                onValueChange(it)
                showSendButton = it.isNotEmpty()
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
                        .padding(horizontal = 24.dp, vertical = 16.dp),
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
                onValueChange("")
                showSendButton = false

//                if (showSendButton) {
//                    onSendMessage()
//                    onValueChange("")
//                    showSendButton = false
//                } else {
//                    onVoiceClicked()
//                }
            },
            shape = RoundedCornerShape(25),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = White,
                disabledContainerColor = MaterialTheme.colorScheme.primary.copy(.5f)
            ),
            modifier = Modifier.size(50.dp),
            contentPadding = PaddingValues(0.dp),
            enabled = showSendButton && !isProcessing
        ) {
            Icon(
                painter = painterResource(id = R.drawable.round_send_24),
                contentDescription = null
            )


//            AnimatedVisibility(
//                visible = showSendButton,
//                enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
//                exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.round_send_24),
//                    contentDescription = null
//                )
//            }
//            AnimatedVisibility(
//                visible = !showSendButton,
//                enter = scaleIn() + expandVertically(expandFrom = Alignment.CenterVertically),
//                exit = scaleOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.round_mic_24),
//                    contentDescription = null
//                )
//            }
        }
    }
}


@OptIn(BetaOpenAI::class)
fun getHorizontalAlignment(chatRole: ChatRole) = when (chatRole) {
    ChatRole.User -> Alignment.End
    else -> Alignment.Start
}