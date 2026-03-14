package com.example.myapplication.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val BotanicalLightColorScheme = lightColorScheme(
    primary = MossGreen,
    secondary = WarmPeach,
    tertiary = SoilBrown,
    background = PaperWhite,
    surface = Sand,
    onPrimary = PaperWhite,
    onSecondary = SoilBrown,
    onTertiary = PaperWhite,
    onBackground = SoilBrown,
    onSurface = SoilBrown,
    outline = SoilBrown
)

private val BotanicalDarkColorScheme = darkColorScheme(
    primary = MossGreenLight,
    secondary = PeachLight,
    tertiary = SandDark,
    background = SoilBrownDark,
    surface = SoilBrown,
    onPrimary = PaperWhite,
    onSecondary = SoilBrown,
    onTertiary = PaperWhite,
    onBackground = PaperWhite,
    onSurface = PaperWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled for botanical palette control
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        BotanicalDarkColorScheme
    } else {
        BotanicalLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}