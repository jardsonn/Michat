package com.jalloft.michat.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalloft.michat.R
import com.jalloft.michat.data.AssistantIdentifier
import com.jalloft.michat.data.AssistantsEnum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior?,
    onActionClick: () -> Unit,
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 26.sp,
                color = MaterialTheme.colorScheme.surface
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
        scrollBehavior = scrollBehavior,
        actions = {
            IconButton(
                onClick = onActionClick,
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.surface)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_settings),
                    contentDescription = stringResource(id = R.string.options_menu),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    )
}

@Composable
fun ChatTopBar(
    assistant: AssistantIdentifier,
    isProcessing: Boolean,
    onBackClicked: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 8.dp)
    ) {
        val isFreeChat = assistant.assistant == AssistantsEnum.FreeChat
        IconButton(
            onClick = { onBackClicked() },
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack, contentDescription = null,
                tint = MaterialTheme.colorScheme.surface
            )
        }

        RoundedRobotIcon(assistant, 42.dp, CircleShape)

        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = if (isFreeChat) stringResource(id = assistant.assistant.stringId) else assistant.assistant.name,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.surface
            )
            Text(
                text = stringResource(id = if (isProcessing) R.string.typing else R.string.online),
                style = MaterialTheme.typography.titleSmall,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.outline
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreensTopBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onBackClicked: () -> Unit = {},
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.surface
            )
        },
        colors = colors,
        scrollBehavior = scrollBehavior,
        actions = actions,
        navigationIcon = {
            IconButton(
                onClick = { onBackClicked() },
            ) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack, contentDescription = null,
                    tint = MaterialTheme.colorScheme.surface
                )
            }
        }
    )
}