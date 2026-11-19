package com.DanielDMikelG.kalo.ui.screens.objective

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.usecase.PlazoObjetivo
import com.DanielDMikelG.kalo.ui.components.EstadoMascota
import com.DanielDMikelG.kalo.ui.components.KaloCard
import com.DanielDMikelG.kalo.ui.components.KaloMascot
import com.DanielDMikelG.kalo.ui.components.KaloTextField
import com.DanielDMikelG.kalo.ui.components.TecladoKalo
import com.DanielDMikelG.kalo.ui.screens.register.RegistroViewModel
import com.DanielDMikelG.kalo.util.EntradaNumerica
import kotlin.math.roundToInt

// Pasteles fijos (iguales en claro y oscuro), como en Inicio
private val TextoOscuro = Color(0xFF17372A)
private val TextoSuave = Color(0xFF3F5B4C)
private val VerdeAccion = Color(0xFF1B7F4E)
private val VerdeSuave = Color(0xFFCDEFD9)
private val VerdeIcono = Color(0xFF14573A)
private val GrisSuave = Color(0xFFEAF5EE)
private val BordeSuave = Color(0xFFD3EDDD)

private data class EstiloObjetivo(
    val titulo: String,
    val icono: ImageVector,
    val fondo: Color,
    val colorIcono: Color,
    val descripcion: String,
)

private fun TipoObjetivo.estilo() = when (this) {
    TipoObjetivo.ADELGAZAR -> EstiloObjetivo(
        "Adelgazar", Icons.Filled.ArrowDownward, Color(0xFFDDF0FF), Color(0xFF1F6FA8),
        "Comerás un poco menos de lo que gastas cada día.",
    )
    TipoObjetivo.MANTENER -> EstiloObjetivo(
        "Mantener", Icons.Filled.DragHandle, VerdeSuave, VerdeIcono,
        "Comerás lo que gastas cada día para conservar tu peso.",
    )
    TipoObjetivo.GANAR_MASA -> EstiloObjetivo(
        "Ganar masa", Icons.Filled.ArrowUpward, Color(0xFFFFF1BF), Color(0xFF7A5A00),
        "Comerás un poco más de lo que gastas cada día.",
    )
}

private val OPCIONES = listOf(TipoObjetivo.ADELGAZAR, TipoObjetivo.MANTENER, TipoObjetivo.GANAR_MASA)

@Composable
fun ObjectiveScreen(
    viewModel: RegistroViewModel,
    onTerminar: () -> Unit,
) {
    val estado by viewModel.estado.collectAsState()
    val tipo = estado.tipoObjetivo
    val necesitaMeta = tipo != null && tipo != TipoObjetivo.MANTENER
    val semanas = estado.plazo.toIntOrNull()?.let(PlazoObjetivo::ajustar) ?: PlazoObjetivo.POR_DEFECTO

    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Cabecera()

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Seccion("¿Cuál es tu objetivo?") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OPCIONES.forEach { opcion ->
                                TarjetaObjetivo(
                                    opcion = opcion,
                                    seleccionada = tipo == opcion,
                                    onClick = { viewModel.actualizar { s -> s.copy(tipoObjetivo = opcion) } },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                        }
                        tipo?.let { DescripcionObjetivo(it) }
                    }
                }

                if (necesitaMeta) {
                    Seccion("¿Cuál es tu meta de peso?") {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            KaloTextField(
                                estado.metaKg,
                                { viewModel.actualizar { s -> s.copy(metaKg = EntradaNumerica.decimal(it)) } },
                                "Meta de peso (kg)",
                                keyboardOptions = TecladoKalo.Decimal,
                            )
                            estado.avisoObjetivo?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                Seccion("¿En cuántas semanas?") {
                    SelectorPlazo(
                        semanas = semanas,
                        onCambiar = { n ->
                            viewModel.actualizar { s -> s.copy(plazo = PlazoObjetivo.ajustar(n).toString()) }
                        },
                        esMantener = tipo == TipoObjetivo.MANTENER,
                    )
                }

                estado.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

                Button(
                    onClick = { viewModel.finalizar(onTerminar) },
                    enabled = estado.objetivoValido && !estado.guardando,
                    colors = ButtonDefaults.buttonColors(containerColor = VerdeAccion),
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                ) {
                    Text("Empezar", fontSize = 18.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }
}

@Composable
private fun Cabecera() {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.statusBarsPadding().padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Text("Tu objetivo", style = MaterialTheme.typography.headlineMedium)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KaloMascot(estado = EstadoMascota.CONTENTA, tamano = 72.dp)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(1f),
                ) {
                    Text(
                        "Cuéntame qué quieres conseguir y en cuánto tiempo.",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    )
                }
            }
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
private fun TarjetaObjetivo(
    opcion: TipoObjetivo,
    seleccionada: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estilo = opcion.estilo()
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(28.dp),
        color = estilo.fondo,
        border = BorderStroke(3.dp, if (seleccionada) VerdeAccion else Color.Transparent),
        modifier = modifier.semantics {
            contentDescription = if (seleccionada) "${estilo.titulo}, seleccionado" else estilo.titulo
        },
    ) {
        Box {
            Column(
                modifier = Modifier.padding(12.dp).heightIn(min = 150.dp),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color.White, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(estilo.icono, contentDescription = null, tint = estilo.colorIcono)
                }
                Text(estilo.titulo, color = TextoOscuro, fontSize = 15.sp, fontWeight = FontWeight.Black)
            }
            if (seleccionada) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .background(VerdeAccion, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
private fun DescripcionObjetivo(tipo: TipoObjetivo) {
    val estilo = tipo.estilo()
    KaloCard {
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(estilo.titulo, style = MaterialTheme.typography.titleMedium)
            Text(estilo.descripcion, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SelectorPlazo(
    semanas: Int,
    onCambiar: (Int) -> Unit,
    esMantener: Boolean,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        KaloCard {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    BotonRedondo(
                        icono = Icons.Filled.Remove,
                        descripcion = "Una semana menos",
                        activo = semanas > PlazoObjetivo.MIN_SEMANAS,
                        onClick = { onCambiar(semanas - 1) },
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$semanas", fontSize = 44.sp, fontWeight = FontWeight.Black)
                        Text(
                            if (semanas == 1) "semana" else "semanas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    BotonRedondo(
                        icono = Icons.Filled.Add,
                        descripcion = "Una semana más",
                        activo = semanas < PlazoObjetivo.MAX_SEMANAS,
                        onClick = { onCambiar(semanas + 1) },
                    )
                }

                Column {
                    Slider(
                        value = semanas.toFloat(),
                        onValueChange = { onCambiar(it.roundToInt()) },
                        valueRange = PlazoObjetivo.MIN_SEMANAS.toFloat()..PlazoObjetivo.MAX_SEMANAS.toFloat(),
                        steps = PlazoObjetivo.MAX_SEMANAS - PlazoObjetivo.MIN_SEMANAS - 1,
                        colors = SliderDefaults.colors(
                            thumbColor = VerdeAccion,
                            activeTrackColor = VerdeAccion,
                            activeTickColor = Color.Transparent,
                            inactiveTickColor = Color.Transparent,
                        ),
                        modifier = Modifier.semantics { contentDescription = "Plazo en semanas" },
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("${PlazoObjetivo.MIN_SEMANAS}", style = MaterialTheme.typography.bodySmall)
                        Text("${PlazoObjetivo.MAX_SEMANAS}", style = MaterialTheme.typography.bodySmall)
                    }
                }

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PlazoObjetivo.ATAJOS.forEach { atajo ->
                        val elegido = semanas == atajo
                        Surface(
                            onClick = { onCambiar(atajo) },
                            shape = CircleShape,
                            color = if (elegido) VerdeAccion else GrisSuave,
                            border = BorderStroke(2.dp, if (elegido) VerdeAccion else BordeSuave),
                        ) {
                            Text(
                                "$atajo sem.",
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                                color = if (elegido) Color.White else TextoOscuro,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Plazo elegido: ${PlazoObjetivo.etiqueta(semanas)}", style = MaterialTheme.typography.titleSmall)
            PlazoObjetivo.equivalencia(semanas)?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = VerdeIcono, fontWeight = FontWeight.ExtraBold)
            }
            Text(
                "Elige entre ${PlazoObjetivo.MIN_SEMANAS} y ${PlazoObjetivo.MAX_SEMANAS} semanas.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (esMantener) {
                Text(
                    "Con Mantener también eliges un plazo: es el tiempo tras el cual revisaremos cómo vas.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun BotonRedondo(icono: ImageVector, descripcion: String, activo: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        enabled = activo,
        shape = CircleShape,
        color = if (activo) VerdeSuave else GrisSuave,
        modifier = Modifier.size(52.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icono,
                contentDescription = descripcion,
                tint = if (activo) VerdeIcono else Color(0xFF8AA597),
            )
        }
    }
}