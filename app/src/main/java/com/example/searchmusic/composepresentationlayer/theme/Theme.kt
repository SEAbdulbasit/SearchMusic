package com.example.searchmusic.composepresentationlayer.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat


val Primary = Color(0xFF0D0C0E)
val PrimaryVariant = Color(0xFF131313)
val Secondary = Color(0xFFFF3C8F)
val SecondaryVariant = Color(0xFFef0076)

val WhiteTransparent = Color(0x80FFFFFF)

val Red = Color(0xFFFF3B30)
val Red700 = Color(0xFFC0392b)
val Orange = Color(0xFFFF9500)
val Yellow = Color(0xFFFFCC00)
val Yellow500 = Color(0xFFFBBC04)
val Green = Color(0xFF4CD964)
val Blue300 = Color(0xFF5AC8FA)
val Blue = Color(0xFF007AFF)
val Purple = Color(0xFF5856D6)
val Asphalt = Color(0xFF2c3e50)

val Gray1000 = Color(0xFF121212)
val BlueGrey = Color(0xFF263238)
val Green600 = Color(0xFF1DB954)
val Green900 = Color(0xFF468847)


fun appDarkColors(
    primary: Color,
    secondary: Color,
    tertiary: Color = secondary,
    background: Color = primary,
    surface: Color = primary,
    surfaceTint: Color = secondary,
    surfaceVariant: Color = surface,
    onPrimary: Color = Color.White,
    onSecondary: Color = Color.White,
    onTertiary: Color = onSecondary,
    onSurface: Color = Color.White,
    onSurfaceVariant: Color = onSurface,
) = AppColors(
    _isLightTheme = false,
    _colorScheme = darkColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        tertiary = tertiary,
        onTertiary = onTertiary,
        background = background,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = surfaceTint,
    )
)

fun appLightColors(
    primary: Color,
    secondary: Color,
    tertiary: Color = secondary,
    background: Color = Color.White,
    surface: Color = Color.White,
    surfaceTint: Color = secondary,
    surfaceVariant: Color = surface,
    onPrimary: Color = Color.White,
    onSecondary: Color = Color.White,
    onTertiary: Color = onSecondary,
    onSurface: Color = Color.Black,
    onSurfaceVariant: Color = onSurface,
) = AppColors(
    _isLightTheme = true,
    _colorScheme = lightColorScheme(
        primary = primary,
        onPrimary = onPrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        background = background,
        tertiary = tertiary,
        onTertiary = onTertiary,
        surfaceTint = surfaceTint,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
    )
)

@Stable
data class AppColors(
    val _isLightTheme: Boolean,
    private val _colorScheme: ColorScheme,
) {
    var colorScheme by mutableStateOf(_colorScheme)
        private set
    var isLightTheme by mutableStateOf(_isLightTheme)
        private set

    val elevatedSurface: Color @Composable get() = elevatedSurface()

    fun update(other: AppColors) {
        isLightTheme = other.isLightTheme
        colorScheme = other.colorScheme
    }
}

internal fun AppColors.elevatedSurface(
    tint: Color = if (isLightTheme) Color.Black else Color.White,
    tintBlendPercentage: Float = if (isLightTheme) 0.5f else 0.75f,
    elevation: Dp = if (isLightTheme) 4.dp else 8.dp,
) = colorScheme.surface.colorAtElevation(
    colorScheme.surface.blendWith(tint, tintBlendPercentage),
    elevation
)


@Composable
fun SearchMusicTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) DarkAppColors else LightAppColors
        }

        darkTheme -> DarkAppColors
        else -> LightAppColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colors.colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colors.colorScheme,
        content = content,
    )
}

