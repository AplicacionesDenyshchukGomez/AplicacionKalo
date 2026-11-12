package com.DanielDMikelG.kalo.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Circulo que se llena como un liquido con una ondita suave.
 *
 * @param progress de 0 a 1: lo consumido respecto a la meta.
 * @param kcalRestantes numero grande del centro; pasa 0 si te has pasado de la meta.
 */
@Composable
fun KaloLiquidCircle(
    progress: Float,
    kcalRestantes: Int,
    modifier: Modifier = Modifier,
    diametro: Dp = 176.dp,
) {
    val nivel by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 900),
        label = "nivel",
    )
    val olas = rememberInfiniteTransition(label = "olas")
    val fase by olas.animateFloat(
        initialValue = 0f,
        targetValue = (2.0 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
        ),
        label = "fase",
    )

    val colorFondo = MaterialTheme.colorScheme.background
    val colorBorde = MaterialTheme.colorScheme.primaryContainer
    val colorOlaTrasera = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
    val colorOlaFrontal = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
    val colorTexto = MaterialTheme.colorScheme.onSurface
    val descripcion = "$kcalRestantes kilocalorías restantes"

    Box(
        modifier = modifier
            .size(diametro)
            .semantics(mergeDescendants = true) { contentDescription = descripcion },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val grosorBorde = 4.dp.toPx()
            val radio = size.minDimension / 2f
            val interior = Path().apply { addOval(Rect(center, radio - grosorBorde)) }

            drawCircle(color = colorFondo, radius = radio - grosorBorde)
            clipPath(interior) {
                // La ola se aplana cuando el circulo esta casi vacio o casi lleno
                val amplitud = 6.dp.toPx() * minOf(1f, nivel * 4f, (1f - nivel) * 4f)
                val y = size.height * (1f - nivel)
                dibujarOla(y, amplitud, -fase + 1.5f, colorOlaTrasera)
                dibujarOla(y + 3.dp.toPx(), amplitud, fase, colorOlaFrontal)
            }
            drawCircle(
                color = colorBorde,
                radius = radio - grosorBorde / 2f,
                style = Stroke(width = grosorBorde),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = kcalRestantes.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = colorTexto,
            )
            Text(
                text = "kcal restantes",
                style = MaterialTheme.typography.labelLarge,
                color = colorTexto,
            )
        }
    }
}

private fun DrawScope.dibujarOla(nivelY: Float, amplitud: Float, fase: Float, color: Color) {
    val ancho = size.width
    val ola = Path().apply {
        moveTo(0f, size.height)
        var x = 0f
        while (x <= ancho) {
            lineTo(x, nivelY + amplitud * sin(x / ancho * 2f * PI.toFloat() + fase))
            x += 4f
        }
        lineTo(ancho, size.height)
        close()
    }
    drawPath(ola, color)
}