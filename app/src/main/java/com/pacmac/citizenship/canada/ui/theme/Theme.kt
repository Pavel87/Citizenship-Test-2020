package com.pacmac.citizenship.canada.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = RedPrimary,
    onPrimary = Neutral99,
    primaryContainer = Red90,
    onPrimaryContainer = Red10,
    secondary = Slate40,
    onSecondary = Neutral99,
    secondaryContainer = Slate90,
    onSecondaryContainer = Slate10,
    tertiary = Green40,
    onTertiary = Neutral99,
    tertiaryContainer = Green90,
    onTertiaryContainer = Green40,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    error = Red40,
    errorContainer = Red90,
    onError = Neutral99,
    onErrorContainer = Red10,
)

private val DarkColorScheme = darkColorScheme(
    primary = Red80,
    onPrimary = Red20,
    primaryContainer = Red30,
    onPrimaryContainer = Red90,
    secondary = Slate80,
    onSecondary = Slate20,
    secondaryContainer = Slate30,
    onSecondaryContainer = Slate90,
    tertiary = Green80,
    onTertiary = Green40,
    tertiaryContainer = Green40,
    onTertiaryContainer = Green90,
    background = Neutral10,
    onBackground = Neutral90,
    surface = Neutral10,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    error = Red80,
    errorContainer = Red30,
    onError = Red20,
    onErrorContainer = Red90,
)

@Composable
fun CitizenshipTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = CitizenshipTypography,
        content = content
    )
}
