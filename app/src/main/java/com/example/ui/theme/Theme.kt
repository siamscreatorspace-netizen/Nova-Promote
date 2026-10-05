package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CyanGlow,
    onPrimary = Slate950,
    primaryContainer = Slate800,
    onPrimaryContainer = CyanGlow,
    secondary = IndigoAccent,
    onSecondary = Color.White,
    secondaryContainer = Slate850,
    onSecondaryContainer = Slate100,
    tertiary = EmeraldVerify,
    onTertiary = Slate950,
    background = Slate950,
    onBackground = Slate100,
    surface = Slate900,
    onSurface = Slate100,
    surfaceVariant = Slate800,
    onSurfaceVariant = Slate300,
    error = RedProhibited,
    onError = Color.White
)

private val LightColorScheme = darkColorScheme( // Enforce high-tech dark theme for consistent brand trust
    primary = CyanNeon,
    onPrimary = Color.White,
    primaryContainer = Slate800,
    onPrimaryContainer = CyanGlow,
    secondary = IndigoAccent,
    onSecondary = Color.White,
    background = Slate950,
    surface = Slate900,
    onBackground = Slate100,
    onSurface = Slate100
)

@Composable
fun NovaPromoteTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
