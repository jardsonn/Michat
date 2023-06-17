package com.jalloft.michat.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.jalloft.michat.data.toAssistant
import com.jalloft.michat.data.toJson
import com.jalloft.michat.ui.components.NetworkStatus
import com.jalloft.michat.ui.screens.authentication.*
import com.jalloft.michat.ui.screens.chat.ChatScreen
import com.jalloft.michat.ui.screens.home.HomeScreen
import com.jalloft.michat.ui.screens.settings.SettingsScreen
import com.jalloft.michat.utils.connectivityState
import com.google.accompanist.navigation.animation.composable
import com.jalloft.michat.ui.screens.home.HomeViewModel
import timber.log.Timber.Forest.i


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MichatApp(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val navController = rememberAnimatedNavController()
    val connectionState by connectivityState()
    var notifyNetworkWrning by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        NetworkStatus(
            modifier = Modifier.zIndex(1f),
            connectionState = connectionState,
            notifyNetworkWrning = notifyNetworkWrning
        )
        AnimatedNavHost(
            navController = navController,
            startDestination = MichatDestination.HomeDestination.route,
            modifier = Modifier.fillMaxWidth()
        ) {
            composable(MichatDestination.HomeDestination.route) {
                HomeScreen(
                    viewModel = viewModel,
                    onClick = {
                        navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it.toJson()}"))
                    },
                    onSettingClick = {
                        navController.navigate(MichatDestination.SettingsDestination.route)
                    },
                    onSignin = {
                        navController.navigate(MichatDestination.SignInDestination.route.plus("/?assistant=${it.toJson()}"))
                    }
                )
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
            composable(MichatDestination.SettingsDestination.route) {
                SettingsScreen(
                    onBackClicked = { navController.popBackStack() },
                    onProfileClick = { isAuthenticated ->
                        if (isAuthenticated) {
                            navController.navigate(MichatDestination.ProfileDestination.route)
                        } else {
                            navController.navigate(MichatDestination.SignInDestination.route.plus("/?assistant='null'"))
                        }
                    },
                    onFavoritesClick = {},
                    onFeedbackClick = {},
                    onPrivacyPolicyClick = {},
                )
            }
            composable(MichatDestination.ProfileDestination.route) {
                ProfileScreen(onBackClicked = { navController.popBackStack() }, onEditClicked = {
                    navController.navigate(MichatDestination.EditUserDestination.route)
                })
            }
            composable(MichatDestination.EditUserDestination.route) {
                EditUserDataScreen(
                    onBackClicked = { navController.popBackStack() },
                    onReauthenticate = {},
                    onSuccess = {navController.popBackStack()}
                )
            }
            composable(MichatDestination.RedefinePasswordDestination.route) {
                RedefinePasswordScreen(onBackClicked = { navController.popBackStack() })
            }
            composable(
                MichatDestination.SignInDestination.route.plus("/?assistant={assistant}"),
                arguments = listOf(
                    navArgument("assistant") {
                        type = NavType.StringType
                        defaultValue = ""
                    },
                ),
            ) {
                SignInScreen(
                    onBackClicked = {
                        navController.popBackStack()
                    },
                    onSignup = {
                        navController.navigate(MichatDestination.SignUpDestination.route)
                    },
                    onForgotPassword = {
                        navController.navigate(MichatDestination.RedefinePasswordWithEmailDestination.route)
                    },
                    onSignin = {
                        navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it.toJson()}")) {
                            popUpTo(MichatDestination.HomeDestination.route)
//                            launchSingleTop = true
                        }
                    },
//                    onSuccessSignup = {
//
//                    }
                )
            }
            composable(MichatDestination.SignUpDestination.route) {
                SignUpScreen(
                    onBackClicked = { navController.popBackStack() },
                    onSignin = {
                        navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it?.toJson()}")) {
//                        popUpTo(MichatDestination.HomeDestination.route)
                            launchSingleTop = true
                        }
                    },
                    onSuccessSignup = {
                        if (it != null) {
                            navController.navigate(MichatDestination.ChatDestination.route.plus("/?assistant=${it.toJson()}")) {
                                popUpTo(MichatDestination.HomeDestination.route)
                            }
                        } else {
                            navController.navigate(MichatDestination.HomeDestination.route) {
                                popUpTo(MichatDestination.HomeDestination.route)
//                            launchSingleTop = true
                            }
                        }
                    }
                )
            }
            composable(MichatDestination.RedefinePasswordWithEmailDestination.route) {
                RedefinePasswordWithEmailScreen(onBackClicked = { navController.popBackStack() })
            }

        }
    }

}

sealed class MichatDestination(val route: String) {
    object HomeDestination : MichatDestination("home")
    object ChatDestination : MichatDestination("chat")
    object SettingsDestination : MichatDestination("settings")
    object EditUserDestination : MichatDestination("edit_user")
    object ProfileDestination : MichatDestination("profile")
    object RedefinePasswordDestination : MichatDestination("redefine_password")
    object SignInDestination : MichatDestination("signin")
    object SignUpDestination : MichatDestination("signup")
    object RedefinePasswordWithEmailDestination : MichatDestination("redefine_password_with_email")
}