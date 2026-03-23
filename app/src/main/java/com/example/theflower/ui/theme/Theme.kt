package com.example.theflower.ui.theme

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
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import com.example.theflower.ui.theme.PaperWhite
import com.example.theflower.ui.theme.SoilBrownDark

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

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
