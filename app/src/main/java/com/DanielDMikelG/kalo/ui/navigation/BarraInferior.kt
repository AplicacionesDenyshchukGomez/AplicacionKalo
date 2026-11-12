package com.DanielDMikelG.kalo.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kotlin.math.PI
import kotlin.math.sin

/**
 * Barra inferior verde menta con el borde de arriba en forma de ola (quieta).
 * Dos olas superpuestas: una mas clara detras y otra mas fuerte delante, que sujeta los iconos.
 */
@Composable
fun BarraInferior(navController: NavHostController) {
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    // Para volver a la barra blanca: ola frontal = surface y ola trasera = primaryContainer,
    // y en los colores del item, indicatorColor = primaryContainer.
    val colorOlaTrasera = MaterialTheme.colorScheme.surfaceVariant
    val colorOlaFrontal = MaterialTheme.colorScheme.primaryContainer
    val colorBorde = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                // Ola de atras: mas alta y mas clara, en contrafase
                drawPath(
                    path = construirOla(6.dp.toPx(), 5.dp.toPx(), -FASE, cerrar = true),
                    color = colorOlaTrasera,
                )
                // Ola de delante: la que sujeta los iconos
                drawPath(
                    path = construirOla(14.dp.toPx(), 5.dp.toPx(), FASE, cerrar = true),
                    color = colorOlaFrontal,
                )
                // Borde fino solo en la parte de arriba de la ola de delante
                drawPath(
                    path = construirOla(14.dp.toPx(), 5.dp.toPx(), FASE, cerrar = false),
                    color = colorBorde,
                    style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
                )
            },
    ) {
        NavigationBar(
            modifier = Modifier.padding(top = 12.dp),
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
        ) {
            Destino.menu.forEach { destino ->
                val seleccionado = rutaActual == destino.ruta

                NavigationBarItem(
                    selected = seleccionado,
                    onClick = {
                        navController.navigate(destino.ruta) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = if (seleccionado) destino.iconoSeleccionado() else destino.iconoNormal(),
                            contentDescription = destino.titulo
                        )
                    },
                    label = {
                        Text(
                            text = destino.titulo,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (seleccionado) FontWeight.ExtraBold else FontWeight.Bold
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        // Pildora blanca sobre la barra verde
                        indicatorColor = MaterialTheme.colorScheme.surface,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

// Dos ondas a lo ancho de la barra; con esta fase las curvas caen entre los iconos.
private const val ONDAS = 2f
private const val FASE = (PI / 2).toFloat()

/**
 * Camino de una ola que recorre todo el ancho.
 * Si [cerrar] es true, baja por los lados y se cierra por abajo (para rellenarla);
 * si es false, es solo la linea de arriba (para dibujar el borde).
 */
private fun DrawScope.construirOla(
    baseY: Float,
    amplitud: Float,
    fase: Float,
    cerrar: Boolean,
): Path = Path().apply {
    fun y(x: Float) = baseY + amplitud * sin(x / size.width * ONDAS * 2f * PI.toFloat() + fase)

    moveTo(0f, y(0f))
    var x = 0f
    while (x <= size.width) {
        lineTo(x, y(x))
        x += 4f
    }
    lineTo(size.width, y(size.width))
    if (cerrar) {
        lineTo(size.width, size.height)
        lineTo(0f, size.height)
        close()
    }
}