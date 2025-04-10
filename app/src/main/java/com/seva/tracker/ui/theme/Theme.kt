package com.seva.tracker.ui.theme

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
    primary = Black,
    surface = ToolBarColorDark,
    onSurface = White,
    errorContainer = ErrorContainerColourRedDark,
    onTertiaryContainer = TertiaryRed,
    primaryContainer = Purple40Dark,
    onPrimaryContainer = White,
    surfaceContainer = PurpleCalendarHaveRoutesDark
)

private val LightColorScheme = lightColorScheme(
    primary = White,
    surface = ToolBarColorLight,
    onSurface = Black,
    errorContainer = ErrorContainerColourRedLight,
    onTertiaryContainer = TertiaryRed,
    primaryContainer = Purple80Light,
    onPrimaryContainer = Black,
    surfaceContainer = PurpleCalendarHaveRoutesLight
)

@Composable
fun TrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}