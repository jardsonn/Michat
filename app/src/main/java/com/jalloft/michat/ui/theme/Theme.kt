package com.jalloft.michat.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// NEWER
private val MichatLightColorScheme = lightColorScheme(
    primary = Red,
    secondary = RaisinBlack,
    onPrimary = White,
    onSecondary = SilverChalice,
    background = White,
    onSurface = BrightGray,
    surface = SmokyBlack,
    outline = OnlineWhiteColor,
    surfaceVariant = SmokyBlack.copy(.4f)

)

private val MichatDarkColorScheme = lightColorScheme(
    primary = Red,
    secondary = Red,
    onPrimary = White,
    onSecondary = SilverChalice,
    background = RaisinBlack,
    onSurface = SmokyBlack,
    surface = White,

    outline = OnlineBlackColor,
    surfaceVariant = SmokyBlack.copy(.6f)

)


@Composable
fun MichatTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    isDynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {

    val dynamicColor = isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> MichatDarkColorScheme
        else -> MichatLightColorScheme
    }
//    val view = LocalView.current
//    if (!view.isInEditMode) {
//        SideEffect {
//            (view.context as Activity).window.statusBarColor = colorScheme.primary.toArgb()
//            ViewCompat.getWindowInsetsController(view)?.isAppearanceLightStatusBars = darkTheme
//        }
//    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val currentWindow = (view.context as? Activity)?.window
            ?: throw Exception("Not in an activity - unable to get Window reference")

        SideEffect {
            (view.context as Activity).window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(currentWindow, view).isAppearanceLightStatusBars = !darkTheme
//            WindowCompat.getInsetsController(currentWindow, view).isAppearanceLightStatusBars = true
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}