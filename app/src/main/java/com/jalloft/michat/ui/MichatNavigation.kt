package com.jalloft.michat.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.data.toJson
import com.jalloft.michat.ui.components.NetworkStatus
import com.jalloft.michat.ui.screens.chat.ChatScreen
import com.jalloft.michat.ui.screens.home.HomeScreen
import com.jalloft.michat.ui.screens.settings.SettingsScreen
import com.jalloft.michat.utils.connectivityState
import okhttp3.internal.notify

@Composable
fun MichatApp() {
    val navController = rememberNavController()
    val connectionState by connectivityState()
    var notifyNetworkWrning by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        NetworkStatus(connectionState = connectionState, notifyNetworkWrning = notifyNetworkWrning)
        NavHost(
            navController = navController,
            startDestination = MichatDestination.HomeDestination.route,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            composable(MichatDestination.HomeDestination.route) {
                HomeScreen(
                    onClick = {
                        navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it.toJson()}"))
                    },
                    onSettingClick = {
                        navController.navigate(MichatDestination.SettingsDestination.route)
                    }
                )
            }
            composable(MichatDestination.SettingsDestination.route) {
                SettingsScreen(onBackClicked = { navController.popBackStack() })
            }
            composable(
                MichatDestination.ChatDestination.route.plus("/?assistant={assistant}"),
                arguments = listOf(
                    navArgument("assistant") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                )
            ) {
                it.arguments?.getString("assistant")?.toAssistant()?.let { assistant ->
                    ChatScreen(
                        assistant = assistant,
                        onNotifyNetworkWrning = {
                            notifyNetworkWrning = !notifyNetworkWrning
                        },
                        onBackClicked = { navController.popBackStack() })
                }
            }
        }
    }

}

sealed class MichatDestination(val route: String) {
    object HomeDestination : MichatDestination("home")
    object ChatDestination : MichatDestination("chat")
    object SettingsDestination : MichatDestination("settings")
}