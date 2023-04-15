package com.jalloft.michat.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun LoadingAnimation(modifier: Modifier = Modifier, shape: Shape) {
    val dotScale1 = remember { Animatable(1.0f) }
    val dotScale2 = remember { Animatable(1.0f) }
    val dotScale3 = remember { Animatable(1.0f) }

    val duration = 200

    LaunchedEffect(Unit) {
        while (true) {
            dotScale1.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(durationMillis = duration)
            )
            dotScale1.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = duration)
            )

            delay(20)

            dotScale2.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(durationMillis = duration)
            )
            dotScale2.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = duration)
            )

            delay(20)

            dotScale3.animateTo(
                targetValue = 0.3f,
                animationSpec = tween(durationMillis = duration)
            )
            dotScale3.animateTo(
                targetValue = 1.0f,
                animationSpec = tween(durationMillis = duration)
            )

//            delay(duration.toLong())
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.onSurface,
        shape = shape,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(start = 20.dp, top = 4.dp, end = 20.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Dot(scale = dotScale1.value)
            Spacer(modifier = Modifier.width(4.dp))
            Dot(scale = dotScale2.value)
            Spacer(modifier = Modifier.width(4.dp))
            Dot(scale = dotScale3.value)
        }
    }

}

@Composable
fun Dot(scale: Float) {
    val color = MaterialTheme.colorScheme.onPrimary
    Canvas(
        modifier = Modifier
            .size(6.dp)
            .padding(top = 6.dp * scale)
    ) {
        drawCircle(
            color = color,
            radius = 4.dp.value,/* * scale*/
            center = Offset(size.width / 2, size.height / 2),
        )
    }
}


