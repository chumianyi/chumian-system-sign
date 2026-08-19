package com.chumian.systemsign.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NeuLightBackground = Color(0xFFE0E5EC)
val NeuLightShadowDark = Color(0xFFA3B1C6)
val NeuLightShadowLight = Color(0xFFFFFFFF)
val NeuLightText = Color(0xFF3D4A5C)
val NeuLightTextSecondary = Color(0xFF6B7A8F)
val NeuLightAccent = Color(0xFF5B8DEF)
val NeuLightSuccess = Color(0xFF4CAF50)
val NeuLightError = Color(0xFFE57373)

val NeuDarkBackground = Color(0xFF2A2E35)
val NeuDarkShadowDark = Color(0xFF1A1D22)
val NeuDarkShadowLight = Color(0xFF3A3F48)
val NeuDarkText = Color(0xFFE0E5EC)
val NeuDarkTextSecondary = Color(0xFF8B95A5)
val NeuDarkAccent = Color(0xFF7BA7F5)
val NeuDarkSuccess = Color(0xFF81C784)
val NeuDarkError = Color(0xFFEF9A9A)

private val LightColors = lightColorScheme(
    primary = NeuLightAccent,
    background = NeuLightBackground,
    surface = NeuLightBackground,
    onPrimary = Color.White,
    onBackground = NeuLightText,
    onSurface = NeuLightText,
    error = NeuLightError
)

private val DarkColors = darkColorScheme(
    primary = NeuDarkAccent,
    background = NeuDarkBackground,
    surface = NeuDarkBackground,
    onPrimary = Color.White,
    onBackground = NeuDarkText,
    onSurface = NeuDarkText,
    error = NeuDarkError
)

@Composable
fun ChumianTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography,
        content = content
    )
}

object NeuColors {
    @Composable
    fun background(dark: Boolean) = if (dark) NeuDarkBackground else NeuLightBackground
    @Composable
    fun shadowDark(dark: Boolean) = if (dark) NeuDarkShadowDark else NeuLightShadowDark
    @Composable
    fun shadowLight(dark: Boolean) = if (dark) NeuDarkShadowLight else NeuLightShadowLight
    @Composable
    fun text(dark: Boolean) = if (dark) NeuDarkText else NeuLightText
    @Composable
    fun textSecondary(dark: Boolean) = if (dark) NeuDarkTextSecondary else NeuLightTextSecondary
    @Composable
    fun accent(dark: Boolean) = if (dark) NeuDarkAccent else NeuLightAccent
    @Composable
    fun success(dark: Boolean) = if (dark) NeuDarkSuccess else NeuLightSuccess
    @Composable
    fun error(dark: Boolean) = if (dark) NeuDarkError else NeuLightError
}
