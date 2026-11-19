package com.DanielDMikelG.kalo.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/** Fila de un macro: circulito con la letra, nombre, gramos y barra gorda. */
@Composable
fun KaloMacroBar(
    letra: String,
    nombre: String,
    consumido: Float,
    meta: Float,
    color: Color,
    colorSuave: Color,
    modifier: Modifier = Modifier,
) {
    val descripcion = "$nombre: ${consumido.roundToInt()} de ${meta.roundToInt()} gramos"
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { contentDescription = descripcion },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(colorSuave),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = letra,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = nombre,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "${consumido.roundToInt()} / ${meta.roundToInt()} g",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            BarraGorda(consumido, meta, color, colorSuave, alto = 14.dp)
        }
    }
}

/** Version compacta para la tarjeta de macros cerrada: barrita y nombre corto debajo. */
@Composable
fun KaloMacroMiniBar(
    nombre: String,
    consumido: Float,
    meta: Float,
    color: Color,
    colorSuave: Color,
    modifier: Modifier = Modifier,
) {
    val descripcion = "$nombre: ${consumido.roundToInt()} de ${meta.roundToInt()} gramos"
    Column(
        modifier = modifier.semantics(mergeDescendants = true) { contentDescription = descripcion },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        BarraGorda(consumido, meta, color, colorSuave, alto = 12.dp)
        Text(
            text = nombre,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun BarraGorda(
    consumido: Float,
    meta: Float,
    color: Color,
    colorSuave: Color,
    alto: Dp,
    colorExceso: Color = lerp(color, Color.Black, 0.4f),
) {
    val f = FraccionesBarra.desde(consumido, meta)
    val normal by animateFloatAsState(f.normal, tween(600), label = "macro")
    val exceso by animateFloatAsState(f.exceso, tween(600), label = "macroExceso")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(alto)
            .clip(CircleShape)
            .background(colorSuave),
    ) {
        Box(Modifier.fillMaxWidth(normal).fillMaxHeight().clip(CircleShape).background(color))
        if (exceso > 0f) {
            Box(Modifier.fillMaxWidth(exceso).fillMaxHeight().clip(CircleShape).background(colorExceso))
        }
    }
}
