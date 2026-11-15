package com.DanielDMikelG.kalo.ui.screens.home

import android.content.res.Configuration
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.ui.components.EstadoMascota
import com.DanielDMikelG.kalo.ui.components.KaloCard
import com.DanielDMikelG.kalo.ui.components.KaloMascot
import com.DanielDMikelG.kalo.ui.theme.KaloTheme
import com.DanielDMikelG.kalo.ui.theme.Spacing

// Pasteles fijos (iguales en claro y oscuro): fondo de las fichas y de las macros
private val TextoOscuro = Color(0xFF17372A)
private val TextoSuave = Color(0xFF3F5B4C)

data class MacroProgreso(val consumido: Int = 0, val meta: Int = 0) {
    val fraccion: Float get() = if (meta <= 0) 0f else (consumido.toFloat() / meta).coerceIn(0f, 1f)
}

data class InicioUiState(
    val nombre: String = "",
    val kcalConsumidas: Int = 0,
    val kcalMeta: Int = 0,
    val proteina: MacroProgreso = MacroProgreso(),
    val grasa: MacroProgreso = MacroProgreso(),
    val carbohidratos: MacroProgreso = MacroProgreso(),
    val fibra: MacroProgreso = MacroProgreso(),
    val racha: Int = 0,
    val estadoMascota: EstadoMascota = EstadoMascota.CONTENTA,
    val misionesCompletadas: Int = 0,
    val misionesTotales: Int = 0,
    val recetaDelDia: String? = null,
) {
    val kcalRestantes: Int get() = (kcalMeta - kcalConsumidas).coerceAtLeast(0)
}

/** Lo que dice la mascota en la cabecera. Función pura: fácil de probar. */
internal fun mensajeMascota(estado: InicioUiState): String = when {
    estado.kcalMeta <= 0 -> "¡Hola! Completa tu objetivo para empezar."
    estado.kcalConsumidas > estado.kcalMeta ->
        "Hoy te has pasado ${estado.kcalConsumidas - estado.kcalMeta} kcal. ¡Mañana seguimos!"
    estado.kcalConsumidas == 0 -> "Aún no has registrado nada hoy. ¡Empezamos!"
    else -> "Hoy te quedan ${estado.kcalRestantes} kcal. ¡Vas genial!"
}

/** Punto de entrada: es lo que se llama desde la navegación. */
@Composable
fun InicioRoute(
    onMisiones: () -> Unit,
    onReceta: () -> Unit,
    onEscanear: () -> Unit,
    onPeso: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current.applicationContext
    val usuarioId = remember { SesionManager(context).usuarioActual() }

    if (usuarioId == null) {
        Surface(modifier = modifier) {
            Text("No hay ninguna sesión iniciada.", modifier = Modifier.padding(Spacing.md))
        }
        return
    }

    val db = remember { KaloDatabase.obtener(context) }
    val viewModel: InicioViewModel = viewModel(
        key = "inicio_$usuarioId",
        factory = viewModelFactory {
            initializer { InicioViewModel(UsuarioRepository(db), usuarioId, ComidaRepository(db)) }
        },
    )
    val estado by viewModel.estado.collectAsStateWithLifecycle()

    InicioContent(estado, onMisiones, onReceta, onEscanear, onPeso, modifier)
}

@Composable
fun InicioContent(
    state: InicioUiState,
    onMisiones: () -> Unit,
    onReceta: () -> Unit,
    onEscanear: () -> Unit,
    onPeso: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Cabecera(state)
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Seccion("Mi objetivo de hoy") {
                    KaloCard { MacrosGrid(state) }
                }
                Seccion("Para hoy") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            FichaInicio(
                                Icons.Default.Flag, Color(0xFFFFE1E4), Color(0xFFA62A36),
                                "Misiones",
                                if (state.misionesTotales > 0) {
                                    "${state.misionesCompletadas} de ${state.misionesTotales} completadas"
                                } else {
                                    "Próximamente"
                                },
                                onMisiones, Modifier.weight(1f).fillMaxHeight(),
                            )
                            FichaInicio(
                                Icons.Default.Restaurant, Color(0xFFCDEFD9), Color(0xFF14573A),
                                "Receta del día", state.recetaDelDia ?: "Próximamente",
                                onReceta, Modifier.weight(1f).fillMaxHeight(),
                            )
                        }
                        Row(
                            modifier = Modifier.height(IntrinsicSize.Min),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            FichaInicio(
                                Icons.Default.QrCodeScanner, Color(0xFFFFF1BF), Color(0xFF7A5A00),
                                "Escanear", "Mira los datos de un producto",
                                onEscanear, Modifier.weight(1f).fillMaxHeight(),
                            )
                            FichaInicio(
                                Icons.Default.MonitorWeight, Color(0xFFDDF0FF), Color(0xFF1F6FA8),
                                "Mi peso", "Añade tu peso de hoy",
                                onPeso, Modifier.weight(1f).fillMaxHeight(),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Cabecera(state: InicioUiState) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (state.nombre.isBlank()) "¡Hola!" else "¡Hola, ${state.nombre}!",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                )
                PastillaRacha(state.racha)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                KaloMascot(estado = state.estadoMascota, tamano = 72.dp)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        mensajeMascota(state),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun PastillaRacha(racha: Int) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(2.dp, Color(0xFFFFD966)),
        modifier = Modifier.semantics { contentDescription = "Racha de $racha días" },
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, top = 6.dp, end = 14.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = Color(0xFFFFB84D))
            Text("$racha", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(start = 4.dp))
        contenido()
    }
}

@Composable
private fun MacrosGrid(state: InicioUiState) {
    Column(verticalArrangement = Arrangement.spacedBy(18.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            MacroBarra("Proteínas", state.proteina, Color(0xFFFF9AA2), Color(0xFFFFE1E4), Modifier.weight(1f))
            MacroBarra("Grasas", state.grasa, Color(0xFFFFD966), Color(0xFFFFF1BF), Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
            MacroBarra("Carbohidratos", state.carbohidratos, Color(0xFF8CCBFF), Color(0xFFDDF0FF), Modifier.weight(1f))
            MacroBarra("Fibra", state.fibra, Color(0xFFD9B38C), Color(0xFFF1E3D3), Modifier.weight(1f))
        }
    }
}

@Composable
private fun MacroBarra(
    nombre: String,
    progreso: MacroProgreso,
    color: Color,
    colorSuave: Color,
    modifier: Modifier = Modifier,
) {
    val fraccion by animateFloatAsState(progreso.fraccion, label = "macro")
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {
            contentDescription = "$nombre: ${progreso.consumido} de ${progreso.meta} gramos"
        },
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(nombre, style = MaterialTheme.typography.titleSmall)
        }
        Text(
            "${progreso.consumido} / ${progreso.meta} g",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(CircleShape)
                .background(colorSuave),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraccion)
                    .fillMaxHeight()
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}

@Composable
private fun FichaInicio(
    icono: ImageVector,
    fondo: Color,
    colorIcono: Color,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(onClick = onClick, shape = RoundedCornerShape(28.dp), color = fondo, modifier = modifier) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .heightIn(min = 118.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icono, contentDescription = null, tint = colorIcono)
            }
            Column(modifier = Modifier.padding(top = 12.dp)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium, color = TextoOscuro)
                Text(subtitulo, style = MaterialTheme.typography.bodyMedium, color = TextoSuave)
            }
        }
    }
}

private val estadoEjemplo = InicioUiState(
    nombre = "Daniel", kcalConsumidas = 1190, kcalMeta = 2300,
    proteina = MacroProgreso(62, 144), grasa = MacroProgreso(31, 64),
    carbohidratos = MacroProgreso(140, 276), fibra = MacroProgreso(9, 23),
    racha = 12, misionesCompletadas = 2, misionesTotales = 5, recetaDelDia = "Tortilla de espinacas",
)

@Preview(name = "Inicio claro", showBackground = true, heightDp = 900)
@Composable
private fun InicioLightPreview() {
    KaloTheme(darkTheme = false) { InicioContent(estadoEjemplo, {}, {}, {}, {}) }
}

@Preview(name = "Inicio oscuro", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun InicioDarkPreview() {
    KaloTheme(darkTheme = true) { InicioContent(estadoEjemplo, {}, {}, {}, {}) }
}