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

private val HerCareColorScheme = lightColorScheme(
    primary = PrimaryPink,
    onPrimary = PureWhite,
    primaryContainer = LightPink,
    onPrimaryContainer = DeepPink,
    secondary = RoseAccent,
    onSecondary = PureWhite,
    secondaryContainer = LightPink,
    onSecondaryContainer = PrimaryText,
    tertiary = SoftPurpleAccent,
    onTertiary = PureWhite,
    tertiaryContainer = Color(0xFFF6EEFA),
    onTertiaryContainer = PrimaryText,
    background = VeryLightBackground,
    surface = PureWhite,
    surfaceVariant = LightPink,
    onBackground = PrimaryText,
    onSurface = PrimaryText,
    onSurfaceVariant = SecondaryText,
    outline = CardBorderColor
)

@Composable
fun HerCareTheme(
    darkTheme: Boolean = false, // Strictly feminine light pink theme as per design specification
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = HerCareColorScheme,
        typography = Typography,
        content = content
    )
}
