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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.domain.model.CategoriaArticulo
import com.DanielDMikelG.kalo.domain.model.TipoComida

internal val CocinaFondo = Color(0xFFF4FBF6)
internal val CocinaCabecera = Color(0xFFDDF3E5)
internal val CocinaBorde = Color(0xFFD3EDDD)
internal val CocinaTexto = Color(0xFF17372A)
internal val CocinaTextoSec = Color(0xFF3F5B4C)
internal val CocinaVerde = Color(0xFF1B7F4E)
internal val CocinaContenedor = Color(0xFFCDEFD9)

internal data class EstiloTipo(val icono: ImageVector, val fondo: Color, val color: Color)

internal fun TipoComida.estiloCocina() = when (this) {
    TipoComida.DESAYUNO -> EstiloTipo(Icons.Filled.FreeBreakfast, Color(0xFFFFF0C2), Color(0xFF7A5600))
    TipoComida.COMIDA -> EstiloTipo(Icons.Filled.Restaurant, Color(0xFFDDEFFF), Color(0xFF1F5E99))
    TipoComida.CENA -> EstiloTipo(Icons.Filled.NightsStay, Color(0xFFE4E1FF), Color(0xFF4B3FA8))
    TipoComida.SNACK -> EstiloTipo(Icons.Filled.Cookie, Color(0xFFFFE3E5), Color(0xFF9C2F3C))
}

internal fun CategoriaArticulo.estiloCocina() = when (this) {
    CategoriaArticulo.ETIQUETAS -> EstiloTipo(Icons.Filled.Description, Color(0xFFDDEFFF), Color(0xFF1F5E99))
    CategoriaArticulo.NUTRICION -> EstiloTipo(Icons.Filled.Eco, Color(0xFFFFE3E5), Color(0xFF9C2F3C))
    CategoriaArticulo.HABITOS -> EstiloTipo(Icons.Filled.Schedule, Color(0xFFFFF0C2), Color(0xFF7A5600))
}

@Composable
internal fun BotonVolver(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(onClick = onClick, modifier = modifier.padding(4.dp)) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = CocinaTexto)
    }
}