package com.DanielDMikelG.kalo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/** Colores propios de Kalo que Material no tiene: nutrientes y agua. */
@Immutable
data class KaloExtraColors(
    val protein: Color,
    val fat: Color,
    val carbs: Color,
    val fiber: Color,
    val water: Color,
)

private val LightExtraColors = KaloExtraColors(
    protein = ProteinLight,
    fat = FatLight,
    carbs = CarbsLight,
    fiber = FiberLight,
    water = WaterLight,
)

private val DarkExtraColors = KaloExtraColors(
    protein = ProteinDark,
    fat = FatDark,
    carbs = CarbsDark,
    fiber = FiberDark,
    water = WaterDark,
)

val LocalKaloColors = staticCompositionLocalOf { LightExtraColors }

/** Uso: MaterialTheme.kaloColors.protein */
val MaterialTheme.kaloColors: KaloExtraColors
    @Composable
    @ReadOnlyComposable
    get() = LocalKaloColors.current

private val LightColors = lightColorScheme(
    primary = KaloGreen,
    onPrimary = Color.White,
    primaryContainer = KaloGreenContainer,
    onPrimaryContainer = Color(0xFF002106),
    secondary = KaloLime,
    onSecondary = Color.White,
    secondaryContainer = KaloLimeContainer,
    onSecondaryContainer = Color(0xFF1B2A00),
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
    error = LightError,
)

private val DarkColors = darkColorScheme(
    primary = KaloGreenDark,
    onPrimary = Color(0xFF003910),
    primaryContainer = KaloGreenContainerDark,
    onPrimaryContainer = KaloGreenContainer,
    secondary = KaloLimeDark,
    onSecondary = Color(0xFF2A3A00),
    secondaryContainer = KaloLimeContainerDark,
    onSecondaryContainer = KaloLimeContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    error = DarkError,
)

@Composable
fun KaloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalKaloColors provides if (darkTheme) DarkExtraColors else LightExtraColors,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColors else LightColors,
            typography = Typography,
            content = content,
        )
    }
}
