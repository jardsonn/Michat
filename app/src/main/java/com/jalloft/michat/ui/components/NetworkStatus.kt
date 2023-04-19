package com.jalloft.michat.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jalloft.michat.R
import com.jalloft.michat.ui.theme.Malachite
import com.jalloft.michat.ui.theme.Red
import com.jalloft.michat.utils.ConnectionState
import kotlinx.coroutines.delay


@Composable
fun NetworkStatus(connectionState: ConnectionState) {
    val isNetworkConnected = connectionState == ConnectionState.Available

    var showNetworkStatus by remember { mutableStateOf(false) }

    var isFirstTime by remember { mutableStateOf(true) }

    Box(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = showNetworkStatus,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300, easing = FastOutLinearInEasing)
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .background(color = if (!isNetworkConnected) Red else Malachite)
                    .fillMaxWidth()
                    .animateContentSize(),
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(id = if (isNetworkConnected) R.drawable.ic_network else R.drawable.ic_no_network),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        text = stringResource(id = if (isNetworkConnected) R.string.network_connected else R.string.network_not_connected),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(start = 16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        }
    }

    LaunchedEffect(connectionState) {
        if (isFirstTime && isNetworkConnected) {
            isFirstTime = false
        } else {
            showNetworkStatus = true
        }

    }

    LaunchedEffect(showNetworkStatus) {
        delay(5000)
        if (showNetworkStatus && isFirstTime) {
            isFirstTime = false
        }
        showNetworkStatus = false
    }
}
