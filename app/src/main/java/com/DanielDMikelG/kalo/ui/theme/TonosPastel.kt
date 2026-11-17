package com.DanielDMikelG.kalo.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.DanielDMikelG.kalo.domain.model.TipoComida

/** true si el tema activo es el oscuro (también funciona en los @Preview). */
@Composable
@ReadOnlyComposable
fun esTemaOscuro(): Boolean = MaterialTheme.colorScheme.background.luminance() < 0.5f

/**
 * Un tono pastel con su pareja legible: [fondo] para cajas e iconos, [icono] para el icono o el texto
 * que va encima y [barra] para barras de progreso. Cambian solos entre tema claro y oscuro.
 */
@Immutable
data class TonoPastel(val fondo: Color, val icono: Color, val barra: Color)

@Composable
@ReadOnlyComposable
fun tonoAmarillo() =
    if (esTemaOscuro()) TonoPastel(KaloSunContainerDark, KaloSunDark, KaloSunDark)
    else TonoPastel(Color(0xFFFFF0C2), Color(0xFF7A5600), Color(0xFFFFD966))

@Composable
@ReadOnlyComposable
fun tonoAzul() =
    if (esTemaOscuro()) TonoPastel(CarbsSoftDark, CarbsDark, CarbsDark)
    else TonoPastel(Color(0xFFDDEFFF), Color(0xFF1F5E99), Color(0xFF8CCBFF))

@Composable
@ReadOnlyComposable
fun tonoLila() =
    if (esTemaOscuro()) TonoPastel(Color(0xFF2B2850), Color(0xFFB9B0FF), Color(0xFFB9B0FF))
    else TonoPastel(Color(0xFFE4E1FF), Color(0xFF4B3FA8), Color(0xFFB9B0FF))

@Composable
@ReadOnlyComposable
fun tonoRosa() =
    if (esTemaOscuro()) TonoPastel(ProteinSoftDark, ProteinDark, ProteinDark)
    else TonoPastel(Color(0xFFFFE3E5), Color(0xFF9C2F3C), Color(0xFFFF9AA2))

@Composable
@ReadOnlyComposable
fun TipoComida.tono(): TonoPastel = when (this) {
    TipoComida.DESAYUNO -> tonoAmarillo()
    TipoComida.COMIDA -> tonoAzul()
    TipoComida.CENA -> tonoLila()
    TipoComida.SNACK -> tonoRosa()
}
