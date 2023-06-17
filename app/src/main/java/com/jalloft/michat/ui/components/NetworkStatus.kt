package com.jalloft.michat.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkStatus(
    modifier: Modifier = Modifier,
    connectionState: ConnectionState,
    notifyNetworkWrning: Boolean
) {
    val isNetworkConnected = connectionState == ConnectionState.Available
    var showNetworkStatus by remember { mutableStateOf(false) }

    var isFirstTime by remember { mutableStateOf(true) }

    Box(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(
            visible = showNetworkStatus,
            enter = expandIn(
                expandFrom = Alignment.TopCenter,
                animationSpec = tween(300, easing = FastOutSlowInEasing)
            ),
            exit = shrinkVertically(
                shrinkTowards = Alignment.Top,
                animationSpec = tween(300, easing = FastOutLinearInEasing)
            ),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.systemBarsPadding()
//                    .windowInsetsPadding(
//                        WindowInsets.systemBars//.only(WindowInsetsSides.Horizontal)
//                    )
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

    LaunchedEffect(connectionState, notifyNetworkWrning) {
        if (isFirstTime && isNetworkConnected) {
            isFirstTime = false
        } else {
            showNetworkStatus = true
        }

    }

    LaunchedEffect(showNetworkStatus) {
        delay(3000)
        if (showNetworkStatus && isFirstTime) {
            isFirstTime = false
        }
        showNetworkStatus = false
    }
}
