package com.example.searchmusic.composepresentationlayer.theme

import androidx.annotation.ColorInt
import androidx.annotation.FloatRange
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import kotlin.math.ln

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

fun Color.colorAtElevation(tint: Color, elevation: Dp): Color {
    if (elevation == 0.dp) return this
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return tint.copy(alpha = alpha).compositeOver(this)
}

fun Color.blendWith(
    otherColor: Color,
    @FloatRange(from = 0.0, to = 1.0) percentage: Float
): Color {
    return blendColors(toArgb(), otherColor.toArgb(), percentage).toColor()
}

fun blendColors(
    @ColorInt color: Int,
    @ColorInt otherColor: Int,
    @FloatRange(from = 0.0, to = 1.0) percentage: Float
): Int {
    return ColorUtils.blendARGB(color, otherColor, percentage)
}

fun Int.toColor() = Color(this)

internal val DarkAppColors = appDarkColors(
    primary = Primary,
    secondary = Secondary,
    surfaceVariant = PrimaryVariant,
)
internal val LightAppColors = appLightColors(
    primary = Primary,
    secondary = Primary,
    surfaceVariant = PrimaryVariant,
)
