package com.fyphost.sankhadip.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val SanKhadipColors = darkColorScheme(
    primary = NeonBlue,
    onPrimary = Color.White,
    primaryContainer = Panel2,
    onPrimaryContainer = TextPrimary,
    secondary = NeonBlueBright,
    background = Bg,
    onBackground = TextPrimary,
    surface = Panel,
    onSurface = TextPrimary,
    surfaceVariant = Panel2,
    onSurfaceVariant = TextMuted,
    outline = BorderBlue,
    error = Danger,
)

@Composable
fun SanKhadipTheme(
    @Suppress("UNUSED_PARAMETER") darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = SanKhadipColors,
        typography = Typography(),
        content = content,
    )
}
