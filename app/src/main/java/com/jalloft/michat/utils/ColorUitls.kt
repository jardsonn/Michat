package com.jalloft.michat.utils

import androidx.compose.ui.graphics.Color
import kotlin.math.abs
import kotlin.random.Random

object ColorUitls {
    fun generateRandomColors(): Pair<Color, Color> {
        val color1 = generateRandomColor()
        val color2 = generateRandomColor()
        return Pair(color1, color2)
    }

    private fun generateRandomColor(): Color {
        val hue = Random.nextFloat() * 360f
        val saturation = Random.nextFloat()
        val lightness = Random.nextFloat() * 0.8f + 0.1f

        return hslToRgb(hue, saturation, lightness)
    }

    private fun hslToRgb(hue: Float, saturation: Float, lightness: Float): Color {
        val c = (1f - abs(2f * lightness - 1f)) * saturation
        val h = hue / 60f
        val x = c * (1f - abs(h % 2f - 1f))
        val m = lightness - c / 2f

        val rgb = when {
            h < 1f -> Triple(c, x, 0f)
            h < 2f -> Triple(x, c, 0f)
            h < 3f -> Triple(0f, c, x)
            h < 4f -> Triple(0f, x, c)
            h < 5f -> Triple(x, 0f, c)
            else -> Triple(c, 0f, x)
        }

        val (r, g, b) = rgb
        return Color(
            (r + m).coerceIn(0f, 1f),
            (g + m).coerceIn(0f, 1f),
            (b + m).coerceIn(0f, 1f)
        )
    }
}
