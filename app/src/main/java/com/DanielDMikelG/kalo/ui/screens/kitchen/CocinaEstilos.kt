package com.DanielDMikelG.kalo.ui.screens.kitchen

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.domain.model.CategoriaArticulo
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.ui.theme.tonoAmarillo
import com.DanielDMikelG.kalo.ui.theme.tonoAzul
import com.DanielDMikelG.kalo.ui.theme.tonoRosa
import com.DanielDMikelG.kalo.ui.theme.tono

// Colores de Cocina: salen del tema, así cambian solos entre claro y oscuro
internal val CocinaFondo: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

internal val CocinaCabecera: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

internal val CocinaBorde: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

internal val CocinaTexto: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

internal val CocinaTextoSec: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

internal val CocinaVerde: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

internal val CocinaContenedor: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primaryContainer

/** Fondo de las tarjetas (blanco en claro, gris verdoso en oscuro). */
internal val CocinaSuperficie: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

internal data class EstiloTipo(val icono: ImageVector, val fondo: Color, val color: Color)

@Composable
@ReadOnlyComposable
internal fun TipoComida.estiloCocina(): EstiloTipo {
    val t = tono()
    val icono = when (this) {
        TipoComida.DESAYUNO -> Icons.Filled.FreeBreakfast
        TipoComida.COMIDA -> Icons.Filled.Restaurant
        TipoComida.CENA -> Icons.Filled.NightsStay
        TipoComida.SNACK -> Icons.Filled.Cookie
    }
    return EstiloTipo(icono, t.fondo, t.icono)
}

@Composable
@ReadOnlyComposable
internal fun CategoriaArticulo.estiloCocina(): EstiloTipo = when (this) {
    CategoriaArticulo.ETIQUETAS -> tonoAzul().let { EstiloTipo(Icons.Filled.Description, it.fondo, it.icono) }
    CategoriaArticulo.NUTRICION -> tonoRosa().let { EstiloTipo(Icons.Filled.Eco, it.fondo, it.icono) }
    CategoriaArticulo.HABITOS -> tonoAmarillo().let { EstiloTipo(Icons.Filled.Schedule, it.fondo, it.icono) }
}

@Composable
internal fun BotonVolver(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier.padding(4.dp)) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = CocinaTexto)
    }
}