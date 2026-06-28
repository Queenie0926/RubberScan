package com.example.rubberscan.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppColorScheme = lightColorScheme(
    primary              = GreenDark,
    onPrimary            = Color.White,
    primaryContainer     = GreenLight,
    onPrimaryContainer   = GreenDeep,

    secondary            = GreenAccent,
    onSecondary          = Color.White,
    secondaryContainer   = GreenLight,
    onSecondaryContainer = GreenDark,

    tertiary             = OrangeDark,
    onTertiary           = Color.White,
    tertiaryContainer    = OrangeLight,
    onTertiaryContainer  = OrangeDark,

    error                = RedDark,
    onError              = Color.White,
    errorContainer       = RedLight,
    onErrorContainer     = RedDark,

    background           = PageBg,
    onBackground         = TextPrimary,
    surface              = CardBg,
    onSurface            = TextPrimary,
    surfaceVariant       = FieldBg,
    onSurfaceVariant     = TextMuted,
    outline              = BorderMedium,
    outlineVariant       = BorderGray,
)

@Composable
fun RubberScanTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    // Dynamic color intentionally disabled — app uses a fixed green-forward palette
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography  = Typography,
        content     = content
    )
}
