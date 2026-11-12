package com.DanielDMikelG.kalo.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.ui.theme.kaloColors
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.launch

// Colores fijos: la carita va siempre sobre el agua y el contorno es un azul
// oscuro (como el verde oscuro de la mascota), igual en claro y en oscuro.
private val TintaCara = Color(0xFF17372A)
private val RuborCara = Color(0xFFFF9AA2)
private val ContornoTaza = Color(0xFF2B86A8)

// Una taza "llena" llega al 80 % de su altura, para que se vea la ola.
private const val NIVEL_LLENO = 0.8f

// Tiempos de la animacion (en milisegundos). Subelos para ir mas despacio.
private const val DURACION_LLENADO_MS = 1500
private const val DURACION_OLAS_MS = 3600

/**
 * Tacita de agua estilo cartoon.
 * Vacia: contorno discontinuo con un "+". Llena: contorno azul oscuro, agua en dos tonos
 * y carita. Al llenarla el agua sube suavemente y la superficie ondula despacio hasta
 * calmarse. En reposo no anima nada.
 */
@Composable
fun KaloWaterGlass(
    lleno: Boolean,
    onClick: () -> Unit,
    descripcion: String,
    modifier: Modifier = Modifier,
) {
    val colores = MaterialTheme.kaloColors
    val colorMas = MaterialTheme.colorScheme.onSurfaceVariant
    val haptico = LocalHapticFeedback.current

    val nivel = remember { Animatable(if (lleno) 1f else 0f) }
    val chapoteo = remember { Animatable(0f) }
    val fase = remember { Animatable(0f) }
    val escala = remember { Animatable(1f) }
    var anterior by remember { mutableStateOf(lleno) }

    LaunchedEffect(lleno) {
        if (anterior == lleno) return@LaunchedEffect   // primera composicion: sin animar
        anterior = lleno

        // El agua sube (o baja) de forma suave, sin rebote
        launch {
            nivel.animateTo(
                targetValue = if (lleno) 1f else 0f,
                animationSpec = tween(
                    durationMillis = DURACION_LLENADO_MS,
                    easing = FastOutSlowInEasing,
                ),
            )
        }
        // La ola empieza algo mas viva y se va calmando hasta parar
        launch {
            chapoteo.snapTo(1f)
            chapoteo.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = DURACION_OLAS_MS, easing = LinearOutSlowInEasing),
            )
        }
        // Dos vueltas completas de la ola, frenando poco a poco (acaba igual que empezo)
        launch {
            fase.snapTo(0f)
            fase.animateTo(
                targetValue = 4f * PI.toFloat(),
                animationSpec = tween(durationMillis = DURACION_OLAS_MS, easing = FastOutSlowInEasing),
            )
        }
        // Saltito discreto de la taza
        launch {
            escala.snapTo(0.94f)
            escala.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow,
                ),
            )
        }
    }

    Box(
        modifier = modifier
            .height(62.dp)
            .graphicsLayer {
                scaleX = escala.value
                scaleY = escala.value
            }
            .semantics { contentDescription = descripcion }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
            ) {
                haptico.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onClick()
            },
    ) {
        Canvas(Modifier.fillMaxSize()) {
            val n = nivel.value
            val progreso = n.coerceIn(0f, 1f)
            val m = medidas(size.width, size.height, 4.dp.toPx(), 3.dp.toPx())
            val cuerpo = m.trazado()
            val alto = m.bottom - m.top
            val superficie = m.top + alto * (1f - NIVEL_LLENO * n)
            val cx = (m.tl + m.tr + m.bl + m.br) / 4f
            val anchoCuerpo = (m.tr - m.tl + m.br - m.bl) / 2f

            // La ola crece mientras chapotea y se aplana con la taza casi vacia
            val factor = minOf(1f, n * 3f).coerceIn(0f, 1f)
            val amplitud = (3.dp.toPx() + 3.dp.toPx() * chapoteo.value) * factor
            val alfaCara = ((progreso - 0.5f) / 0.5f).coerceIn(0f, 1f)

            // Cristal
            drawPath(cuerpo, colores.waterSoft)

            clipPath(cuerpo) {
                // Agua en dos tonos: la ola de atras mas clara, la de delante mas fuerte
                dibujarSuperficie(
                    superficie - 2.dp.toPx(), amplitud, -fase.value + 1.4f,
                    colores.water.copy(alpha = 0.5f),
                )
                dibujarSuperficie(superficie + 2.dp.toPx(), amplitud, fase.value, colores.water)

                // Brillo del cristal
                drawLine(
                    color = Color.White.copy(alpha = 0.6f * progreso),
                    start = Offset(m.tl + (m.tr - m.tl) * 0.14f, m.top + alto * 0.28f),
                    end = Offset(m.bl + (m.br - m.bl) * 0.12f, m.top + alto * 0.7f),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                )

                dibujarCara(cx, m.top + alto * 0.6f, anchoCuerpo, alfaCara)
            }

            // Contorno: discontinuo si esta vacia, azul oscuro y solido si esta llena
            drawPath(
                path = cuerpo,
                color = colores.water.copy(alpha = 1f - progreso),
                style = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(
                        floatArrayOf(10.dp.toPx(), 7.dp.toPx()),
                    ),
                ),
            )
            drawPath(
                path = cuerpo,
                color = ContornoTaza.copy(alpha = progreso),
                style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round),
            )

            // El "+" se desvanece al llenarse
            val alfaMas = 1f - progreso
            if (alfaMas > 0f) {
                val cy = m.top + alto * 0.55f
                val brazo = 6.dp.toPx()
                val color = colorMas.copy(alpha = alfaMas)
                drawLine(color, Offset(cx - brazo, cy), Offset(cx + brazo, cy), 2.5.dp.toPx(), StrokeCap.Round)
                drawLine(color, Offset(cx, cy - brazo), Offset(cx, cy + brazo), 2.5.dp.toPx(), StrokeCap.Round)
            }
        }
    }
}

/**
 * Cuadricula de tazas. Quien la usa decide que pasa al tocar una
 * (por ejemplo, poner el total en indice + 1, o quitar una si era la ultima llena).
 */
@Composable
fun KaloWaterGlasses(
    vasosBebidos: Int,
    vasosTotales: Int,
    onVasoClick: (indice: Int) -> Unit,
    modifier: Modifier = Modifier,
    porFila: Int = 5,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        (0 until vasosTotales).chunked(porFila).forEach { fila ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                fila.forEach { indice ->
                    val bebido = indice < vasosBebidos
                    KaloWaterGlass(
                        lleno = bebido,
                        onClick = { onVasoClick(indice) },
                        descripcion = if (bebido) "Vaso ${indice + 1}, bebido" else "Añadir vaso ${indice + 1}",
                        modifier = Modifier.weight(1f),
                    )
                }
                repeat(porFila - fila.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

// ---------- Geometria ----------

private class Medidas(
    val tl: Float, val tr: Float,      // x de las esquinas de arriba
    val bl: Float, val br: Float,      // x de las esquinas de abajo
    val top: Float, val bottom: Float,
    val radio: Float,
)

/** Taza: cuerpo ancho, paredes casi rectas y fondo muy redondeado. */
private fun medidas(ancho: Float, alto: Float, margenSup: Float, margenInf: Float) = Medidas(
    tl = ancho * 0.06f, tr = ancho * 0.94f,
    bl = ancho * 0.10f, br = ancho * 0.90f,
    top = margenSup, bottom = alto - margenInf, radio = ancho * 0.2f,
)

private fun Medidas.trazado(): Path = Path().apply {
    moveTo(tl, top)
    lineTo(bl, bottom - radio)
    cubicTo(bl, bottom, bl, bottom, bl + radio, bottom)
    lineTo(br - radio, bottom)
    cubicTo(br, bottom, br, bottom, br, bottom - radio)
    lineTo(tr, top)
    close()
}

private fun DrawScope.dibujarSuperficie(superficie: Float, amplitud: Float, fase: Float, color: Color) {
    val periodo = size.width * 1.5f   // ola larga y suave
    val ola = Path().apply {
        moveTo(0f, size.height)
        var x = 0f
        while (x <= size.width) {
            lineTo(x, superficie + amplitud * sin(x / periodo * 2f * PI.toFloat() + fase))
            x += 3f
        }
        lineTo(size.width, size.height)
        close()
    }
    drawPath(ola, color)
}

/** Ojos felices, sonrisa y mejillas. [ancho] es el ancho de la taza a esa altura. */
private fun DrawScope.dibujarCara(cx: Float, cy: Float, ancho: Float, alfa: Float) {
    if (alfa <= 0f) return
    val trazo = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
    val tinta = TintaCara.copy(alpha = alfa)

    for (lado in listOf(-1f, 1f)) {
        val ex = cx + lado * ancho * 0.2f
        val ojo = Path().apply {
            moveTo(ex - ancho * 0.07f, cy + ancho * 0.03f)
            cubicTo(
                ex - ancho * 0.04f, cy - ancho * 0.06f,
                ex + ancho * 0.04f, cy - ancho * 0.06f,
                ex + ancho * 0.07f, cy + ancho * 0.03f,
            )
        }
        drawPath(ojo, tinta, style = trazo)
        drawOval(
            color = RuborCara.copy(alpha = 0.75f * alfa),
            topLeft = Offset(cx + lado * ancho * 0.32f - ancho * 0.08f, cy + ancho * 0.07f),
            size = Size(ancho * 0.16f, ancho * 0.1f),
        )
    }
    val sonrisa = Path().apply {
        moveTo(cx - ancho * 0.07f, cy + ancho * 0.11f)
        cubicTo(
            cx - ancho * 0.04f, cy + ancho * 0.19f,
            cx + ancho * 0.04f, cy + ancho * 0.19f,
            cx + ancho * 0.07f, cy + ancho * 0.11f,
        )
    }
    drawPath(sonrisa, tinta, style = trazo)
}