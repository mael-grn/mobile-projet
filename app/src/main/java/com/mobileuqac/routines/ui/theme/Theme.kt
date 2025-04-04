package com.mobileuqac.routines.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDarkColor,
    secondary = SecondaryDarkColor,
    background = DarkColor,
    onBackground = LightColor,
    surfaceVariant = DarkSecondaryColor,
    surface = DarkColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLightColor,
    secondary = SecondaryLightColor,
    background = LightColor,
    onBackground = DarkColor,
    surfaceVariant = LightSecondaryColor,
    surface = LightColor
)

@Composable
fun RoutinesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {


        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}