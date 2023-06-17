package com.jalloft.michat.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.jalloft.michat.R
import com.jalloft.michat.data.*
import com.jalloft.michat.ui.components.HomeTopBar
import com.jalloft.michat.ui.components.RoundedRobotIcon
import com.jalloft.michat.utils.ColorUitls.generateRandomColors
import com.jalloft.michat.utils.connectivityState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber.Forest.i
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.placeholder.placeholder
import kotlinx.coroutines.delay


fun getAssistants() = AssistantsEnum.values().map {
    AssistantIdentifier(it)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onClick: (AssistantIdentifier) -> Unit,
    onSettingClick: () -> Unit,
    onSignin: (AssistantIdentifier) -> Unit,
) {

    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)

    var assistants by remember {
        mutableStateOf<List<AssistantIdentifier>>(mutableListOf())
    }

    val coroutineScope = rememberCoroutineScope()

    val lastMessagesSate = viewModel.lastMessages.collectAsState()

    var lastMessages by remember {
        mutableStateOf<List<FirebaseMessage>>(arrayListOf())
    }

    var loadingLastMessage by remember {
        mutableStateOf(lastMessages.isEmpty())
    }

    when (val state = lastMessagesSate.value) {
        is HomeViewModel.LatestMessagesState.LastMessages -> LaunchedEffect(lastMessagesSate) {
            loadingLastMessage = false
            lastMessages = state.messages
        }
        is HomeViewModel.LatestMessagesState.Loading -> LaunchedEffect(lastMessagesSate) {
            loadingLastMessage = true
        }
        else -> LaunchedEffect(lastMessagesSate) {
            loadingLastMessage = false
            i("LaunchedEffect.state = $state")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            HomeTopBar(
                title = stringResource(id = R.string.app_name),
                scrollBehavior,
                onActionClick = onSettingClick
            )
        },
    ) { values ->
        LazyColumn(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .padding(values)
                .fillMaxSize()
        ) {

            item {
                Text(
                    text = stringResource(id = R.string.assistants),
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.surface

                )

                AssistantsRowList(
                    modifier = Modifier,
                    assistants,
                    onClick = { if (!viewModel.isAuthenticated()) onSignin(it) else onClick(it) })
                FreeTalkPanel(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp)
                        .clickable {
                            if (!viewModel.isAuthenticated()) onSignin(
                                AssistantIdentifier(
                                    AssistantsEnum.FreeChat
                                )
                            ) else onClick(AssistantIdentifier(AssistantsEnum.FreeChat))
                        },
                    stringResource(id = R.string.free_talk),
                    stringResource(id = R.string.talk_about_everything)
                )

                if (lastMessages.isNotEmpty() && viewModel.isAuthenticated()) {
                    Text(
                        text = stringResource(id = R.string.last_conversations),
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(
                                start = 16.dp,
                                top = 8.dp,
                                end = 16.dp,
                                bottom = 8.dp
                            )
                            .placeholder(
                                visible = lastMessages.isEmpty(),
                                highlight = PlaceholderHighlight.shimmer(),
                            ),
                        color = MaterialTheme.colorScheme.surface
                    )
                }
            }
            if (lastMessages.isNotEmpty() && viewModel.isAuthenticated()) {
                items(lastMessages) {
                    LastMessageItem(it, onChatClick = { lastMessage ->
                        if (!viewModel.isAuthenticated()) onSignin(lastMessage.getAssistant()) else onClick(
                            lastMessage.getAssistant()
                        )
                    })
                }
                i("Sumir Shimmer")
            } else if (loadingLastMessage && viewModel.isAuthenticated()) {
                items(5) {
                    ShimmerLastMessageItem()
                }
                i("Mostrar Shimmer")
            }
        }
    }

    LaunchedEffect(coroutineScope) {
        coroutineScope.launch(Dispatchers.IO) {
            assistants = getAssistants()
            delay(1000)
            loadingLastMessage = false
        }
    }
}

@Composable
fun ShimmerLastMessageItem() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(
            modifier = Modifier
                .size(50.dp)
                .clip(RoundedCornerShape(10.dp))
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                )
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .width(100.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Spacer(
                    modifier = Modifier
                        .size(2.dp)
                        .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer()
                        )
                )
                Spacer(
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .width(50.dp)
                        .height(10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .placeholder(
                            visible = true,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                )
            }
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .height(10.dp)
                    .placeholder(
                        visible = true,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            )
        }
    }
}

@Composable
fun LastMessageItem(message: FirebaseMessage, onChatClick: (FirebaseMessage) -> Unit) {
    val assistant = message.getAssistant()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onChatClick(message) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RoundedRobotIcon(assistant, 50.dp, RoundedCornerShape(20))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
                .weight(1f),
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (assistant.assistant == AssistantsEnum.FreeChat) stringResource(id = assistant.assistant.stringId) else assistant.assistant.name,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Spacer(
                    modifier = Modifier
                        .size(2.dp)
                        .background(MaterialTheme.colorScheme.onSecondary, CircleShape)
                )
                Text(
                    text = stringResource(id = assistant.specialtyNameStringId),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSecondary,
                    modifier = Modifier.padding(start = 8.dp)
                )

            }

            Text(
                text = message.content,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
fun FreeTalkPanel(
    modifier: Modifier,
    title: String,
    subtitle: String,
) {

    var ramdonColors by remember {
        mutableStateOf(generateRandomColors())
    }

    val startColor = remember { Animatable(ramdonColors.first) }
    val endColor = remember { Animatable(ramdonColors.second) }
    val listColors = listOf(startColor.value, endColor.value)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(15)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listColors,
                    )
                ),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 32.sp
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
    LaunchedEffect(Unit) {
        val durationMillis = 2000
        while (true) {
            ramdonColors = generateRandomColors()
            startColor.animateTo(ramdonColors.first, animationSpec = tween(durationMillis))
            endColor.animateTo(ramdonColors.second, animationSpec = tween(durationMillis))
        }
    }
}

@Composable
fun AssistantsRowList(
    modifier: Modifier,
    assistants: List<AssistantIdentifier>,
    onClick: (AssistantIdentifier) -> Unit
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(assistants.dropWhile { it.assistant == AssistantsEnum.FreeChat }) { assistant ->
            AssistantRowItem(assistant, onClick = onClick)
        }
    }
}

@Composable
fun AssistantRowItem(assistant: AssistantIdentifier, onClick: (AssistantIdentifier) -> Unit) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable { onClick(assistant) }
    ) {
        RoundedRobotIcon(assistant, 50.dp, CircleShape)
        Text(
            text = assistant.assistant.name,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.surface

        )
        Text(
            text = stringResource(id = assistant.specialtyNameStringId),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary
        )

    }
}
