package com.purina.feedright.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val FeedRightColorScheme = lightColorScheme(
    primary = PurinaRed,
    onPrimary = OnPurinaRed,
    primaryContainer = PurinaRedContainer,
    onPrimaryContainer = OnPurinaRedContainer,
    secondary = PurinaRedDark,
    onSecondary = OnPurinaRed,
    background = BackgroundGrey,
    surface = SurfaceWhite,
    error = ErrorRed,
    outline = OutlineGrey
)

@Composable
fun FeedRightTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FeedRightColorScheme,
        typography = FeedRightTypography,
        content = content
    )
}
