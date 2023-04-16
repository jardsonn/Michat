package com.jalloft.michat.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.data.toJson
import com.jalloft.michat.ui.screens.chat.ChatScreen
import com.jalloft.michat.ui.screens.home.HomeScreen

@Composable
fun MichatApp(

) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = MichatDestination.HomeDestination.route
    ) {
        composable(MichatDestination.HomeDestination.route) {
            HomeScreen(onClick = {
                navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it.toJson()}"))
            })
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
                ChatScreen(assistant = assistant, onBackClicked = { navController.popBackStack() })
            }
        }
    }

}

sealed class MichatDestination(val route: String) {
    object HomeDestination : MichatDestination("home")
    object ChatDestination : MichatDestination("chat")
}