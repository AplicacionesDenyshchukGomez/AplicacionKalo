package com.DanielDMikelG.kalo.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private val VERDE_OSCURO = Color(0xFF2E8F5D)
private val CUERPO = Color(0xFF9BE0B6)
private val HOJA_IZQ = Color(0xFF3FB37A)
private val HOJA_DER = Color(0xFF5FC58E)
private val TINTA = Color(0xFF17372A)
private val MEJILLA = Color(0xFFFF9AA2)

/** La mascota de Kalo: un bichín verde con una hoja. Se dibuja en una cuadrícula de 64x64. */
@Composable
fun MascotaKalo(modifier: Modifier = Modifier, tamano: Dp = 60.dp) {
    Canvas(modifier.size(tamano)) {
        val s = size.width / 64f
        fun p(x: Float, y: Float) = Offset(x * s, y * s)

        // Tallo
        drawLine(VERDE_OSCURO, p(32f, 18f), p(32f, 10f), strokeWidth = 3f * s, cap = StrokeCap.Round)

        // Hojas
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
        val bordeHoja = Stroke(width = 2f * s, join = StrokeJoin.Round)
        drawPath(hojaIzq, HOJA_IZQ)
        drawPath(hojaIzq, VERDE_OSCURO, style = bordeHoja)
        drawPath(hojaDer, HOJA_DER)
        drawPath(hojaDer, VERDE_OSCURO, style = bordeHoja)

        // Cuerpo
        drawOval(CUERPO, topLeft = p(9f, 19f), size = Size(46f * s, 40f * s))
        drawOval(VERDE_OSCURO, topLeft = p(9f, 19f), size = Size(46f * s, 40f * s), style = Stroke(width = 3f * s))

        // Ojos
        drawCircle(TINTA, radius = 3f * s, center = p(24f, 36f))
        drawCircle(TINTA, radius = 3f * s, center = p(40f, 36f))

        // Sonrisa
        val sonrisa = Path().apply {
            moveTo(27f * s, 44f * s)
            quadraticTo(32f * s, 49f * s, 37f * s, 44f * s)
        }
        drawPath(sonrisa, TINTA, style = Stroke(width = 2.5f * s, cap = StrokeCap.Round))

        // Mejillas
        drawOval(MEJILLA, topLeft = p(14f, 40.5f), size = Size(8f * s, 5f * s), alpha = 0.7f)
        drawOval(MEJILLA, topLeft = p(42f, 40.5f), size = Size(8f * s, 5f * s), alpha = 0.7f)
    }
}

