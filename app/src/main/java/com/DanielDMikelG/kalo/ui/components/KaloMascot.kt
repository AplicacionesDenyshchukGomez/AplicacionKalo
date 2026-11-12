package com.DanielDMikelG.kalo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class EstadoMascota(val descripcion: String) {
    DORMIDA("dormida"),
    CONTENTA("contenta"),
    SORPRENDIDA("sorprendida"),
}

/** Dormida si no has comido nada, sorprendida si te pasas, contenta en el resto de casos. */
fun estadoMascota(kcalConsumidas: Float, kcalMeta: Float): EstadoMascota = when {
    kcalConsumidas <= 0f -> EstadoMascota.DORMIDA
    kcalConsumidas > kcalMeta -> EstadoMascota.SORPRENDIDA
    else -> EstadoMascota.CONTENTA
}

// La mascota tiene sus colores fijos, iguales en claro y oscuro.
private val Contorno = Color(0xFF2E8F5D)
private val Cuerpo = Color(0xFF9BE0B6)
private val HojaIzquierda = Color(0xFF3FB37A)
private val HojaDerecha = Color(0xFF5FC58E)
private val Tinta = Color(0xFF17372A)
private val Rubor = Color(0xFFFF9AA2)

/** Hojita con carita. Dibujada sobre una cuadricula de 64x64. */
@Composable
fun KaloMascot(
    estado: EstadoMascota,
    modifier: Modifier = Modifier,
    tamano: Dp = 60.dp,
) {
    Canvas(
        modifier = modifier
            .size(tamano)
            .semantics { contentDescription = "Mascota de Kalo, ${estado.descripcion}" },
    ) {
        val s = size.minDimension / 64f
        fun p(x: Float, y: Float) = androidx.compose.ui.geometry.Offset(x * s, y * s)
        val trazoFino = Stroke(width = 2.5f * s, cap = StrokeCap.Round)

        // Tallo y hojas
        drawLine(Contorno, p(32f, 18f), p(32f, 10f), strokeWidth = 3f * s, cap = StrokeCap.Round)
        val hojaIzq = Path().apply {
            moveTo(32f * s, 12f * s)
            cubicTo(22f * s, 13f * s, 19f * s, 6f * s, 19f * s, 4f * s)
            cubicTo(27f * s, 2f * s, 32f * s, 6f * s, 32f * s, 12f * s)
            close()
        }
        val hojaDer = Path().apply {
            moveTo(32f * s, 12f * s)
            cubicTo(42f * s, 13f * s, 45f * s, 6f * s, 45f * s, 4f * s)
            cubicTo(37f * s, 2f * s, 32f * s, 6f * s, 32f * s, 12f * s)
            close()
        }
        val contornoHoja = Stroke(width = 2f * s, join = StrokeJoin.Round)
        drawPath(hojaIzq, HojaIzquierda)
        drawPath(hojaIzq, Contorno, style = contornoHoja)
        drawPath(hojaDer, HojaDerecha)
        drawPath(hojaDer, Contorno, style = contornoHoja)

        // Cuerpo
        drawOval(Cuerpo, topLeft = p(9f, 19f), size = Size(46f * s, 40f * s))
        drawOval(Contorno, topLeft = p(9f, 19f), size = Size(46f * s, 40f * s), style = Stroke(3f * s))

        // Rubor
        drawOval(Rubor.copy(alpha = 0.7f), topLeft = p(14f, 40.5f), size = Size(8f * s, 5f * s))
        drawOval(Rubor.copy(alpha = 0.7f), topLeft = p(42f, 40.5f), size = Size(8f * s, 5f * s))

        // Ojos y boca segun el estado
        when (estado) {
            EstadoMascota.CONTENTA -> {
                drawCircle(Tinta, 3f * s, p(24f, 36f))
                drawCircle(Tinta, 3f * s, p(40f, 36f))
                val sonrisa = Path().apply {
                    moveTo(27f * s, 44f * s)
                    cubicTo(30.33f * s, 47.33f * s, 33.67f * s, 47.33f * s, 37f * s, 44f * s)
                }
                drawPath(sonrisa, Tinta, style = trazoFino)
            }
            EstadoMascota.SORPRENDIDA -> {
                drawCircle(Tinta, 4f * s, p(24f, 36f))
                drawCircle(Tinta, 4f * s, p(40f, 36f))
                drawCircle(Color.White, 1.3f * s, p(25.2f, 34.8f))
                drawCircle(Color.White, 1.3f * s, p(41.2f, 34.8f))
                drawCircle(Tinta, 3f * s, p(32f, 46f), style = trazoFino)
            }
            EstadoMascota.DORMIDA -> {
                val ojoIzq = Path().apply {
                    moveTo(21f * s, 36f * s)
                    cubicTo(23f * s, 37.67f * s, 25f * s, 37.67f * s, 27f * s, 36f * s)
                }
                val ojoDer = Path().apply {
                    moveTo(37f * s, 36f * s)
                    cubicTo(39f * s, 37.67f * s, 41f * s, 37.67f * s, 43f * s, 36f * s)
                }
                drawPath(ojoIzq, Tinta, style = trazoFino)
                drawPath(ojoDer, Tinta, style = trazoFino)
                drawLine(Tinta, p(29f, 45f), p(35f, 45f), strokeWidth = 2.5f * s, cap = StrokeCap.Round)
            }
        }
    }
}