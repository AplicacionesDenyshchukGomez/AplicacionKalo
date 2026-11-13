package com.DanielDMikelG.kalo.ui.screens.diary

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FreeBreakfast
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.ui.components.KaloWaterGlasses
import com.DanielDMikelG.kalo.ui.components.MascotaKalo
import com.DanielDMikelG.kalo.util.EntradaNumerica
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sin

// ---- Tokens "Menta" ----
private val FONDO = Color(0xFFF4FBF6)
private val CABECERA = Color(0xFFDDF3E5)
private val BORDE = Color(0xFFD3EDDD)
private val TEXTO = Color(0xFF17372A)
private val TEXTO_SEC = Color(0xFF3F5B4C)
private val VERDE = Color(0xFF1B7F4E)
private val OLA_ATRAS = Color(0xFFB5E7C9)
private val OLA_FRENTE = Color(0xFF7FD3A4)
private const val GRAMOS_MAXIMOS = 5000f

private val espanol: Locale = Locale.forLanguageTag("es-ES")

// ---- Datos visuales de cada comida ----
private data class EstiloComida(
    val icono: ImageVector,
    val fondo: Color,
    val color: Color,
    val barra: Color,
    val reparto: Float, // parte de las kcal del día que le corresponde
)

private fun TipoComida.estilo() = when (this) {
    TipoComida.DESAYUNO -> EstiloComida(Icons.Filled.FreeBreakfast, Color(0xFFFFF0C2), Color(0xFF7A5600), Color(0xFFFFD966), 0.25f)
    TipoComida.COMIDA -> EstiloComida(Icons.Filled.Restaurant, Color(0xFFDDEFFF), Color(0xFF1F5E99), Color(0xFF8CCBFF), 0.35f)
    TipoComida.CENA -> EstiloComida(Icons.Filled.NightsStay, Color(0xFFE4E1FF), Color(0xFF4B3FA8), Color(0xFFB9B0FF), 0.30f)
    TipoComida.SNACK -> EstiloComida(Icons.Filled.Cookie, Color(0xFFFFE3E5), Color(0xFF9C2F3C), Color(0xFFFF9AA2), 0.10f)
}

private data class EstiloMacro(
    val letra: String,
    val nombre: String,
    val abreviado: String,
    val color: Color,
    val suave: Color,
    val colorLetra: Color,
)

private val PROTEINA = EstiloMacro("P", "Proteínas", "Prot.", Color(0xFFFF9AA2), Color(0xFFFFE3E5), Color(0xFF9C2F3C))
private val GRASA = EstiloMacro("G", "Grasas", "Grasas", Color(0xFFFFD966), Color(0xFFFFF3C9), Color(0xFF7A5600))
private val CARBOS = EstiloMacro("C", "Carbohidratos", "Carbos", Color(0xFF8CCBFF), Color(0xFFDDEFFF), Color(0xFF1F5E99))
private val FIBRA = EstiloMacro("F", "Fibra", "Fibra", Color(0xFFD9B38C), Color(0xFFF3E7D8), Color(0xFF6B4A25))

// ---- Punto de entrada con ViewModel ----
@Composable
fun DiarioRoute(
    viewModel: DiarioViewModel,
    onAnadirComida: (TipoComida) -> Unit,
    modifier: Modifier = Modifier,
    racha: Int? = null,
    mascota: @Composable () -> Unit = { MascotaKalo() },
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    DiarioScreen(
        estado = estado,
        onDiaAnterior = viewModel::diaAnterior,
        onDiaSiguiente = viewModel::diaSiguiente,
        onAnadirComida = onAnadirComida,
        onPonerVasos = viewModel::ponerVasos,
        onBorrarEntrada = viewModel::borrarEntrada,
        onEditarCantidad = viewModel::editarCantidad,
        modifier = modifier,
        racha = racha,
        mascota = mascota,
    )
}

@Composable
fun DiarioScreen(
    estado: DiarioUiState,
    onDiaAnterior: () -> Unit,
    onDiaSiguiente: () -> Unit,
    onAnadirComida: (TipoComida) -> Unit,
    onPonerVasos: (Int) -> Unit,
    onBorrarEntrada: (Long) -> Unit,
    onEditarCantidad: (Long, Float) -> Unit,
    modifier: Modifier = Modifier,
    racha: Int? = null,
    mascota: @Composable () -> Unit = { MascotaKalo() },
) {
    var entradaEditando by remember { mutableStateOf<EntradaUi?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FONDO)
            .verticalScroll(rememberScrollState()),
    ) {
        Cabecera(estado, onDiaAnterior, onDiaSiguiente, racha, mascota)

        Column(
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            TarjetaMacros(estado)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Tus comidas",
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = TEXTO, fontSize = 21.sp, fontWeight = FontWeight.Black,
                )
                estado.comidas.forEach { comida ->
                    TarjetaComida(
                        comida = comida,
                        kcalDia = estado.kcalObjetivo,
                        onAnadir = { onAnadirComida(comida.tipo) },
                        onEditar = { entradaEditando = it },
                        onBorrar = onBorrarEntrada,
                    )
                }
            }

            TarjetaAgua(estado, onPonerVasos)
        }
    }

    entradaEditando?.let { entrada ->
        DialogoCantidad(
            entrada = entrada,
            onConfirmar = { gramos ->
                onEditarCantidad(entrada.id, gramos)
                entradaEditando = null
            },
            onCancelar = { entradaEditando = null },
        )
    }
}

// ---- Cabecera ----
@Composable
private fun Cabecera(
    estado: DiarioUiState,
    onDiaAnterior: () -> Unit,
    onDiaSiguiente: () -> Unit,
    racha: Int?,
    mascota: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CABECERA, RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
            .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text("Diario", color = TEXTO, fontSize = 30.sp, fontWeight = FontWeight.Black)
            if (racha != null) ChipRacha(racha)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            DatoLateral("Ingeridas", estado.kcalConsumidas.toString(), Alignment.Start)
            TazaCalorias(estado)
            DatoLateral("Meta", estado.kcalObjetivo.toString(), Alignment.End)
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.size(60.dp), contentAlignment = Alignment.Center) { mascota() }
            Surface(shape = RoundedCornerShape(20.dp), color = Color.White, modifier = Modifier.weight(1f)) {
                Text(
                    mensajeDiario(estado),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    color = TEXTO, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            BotonCircular(Icons.AutoMirrored.Filled.KeyboardArrowLeft, "Día anterior", true, onDiaAnterior)
            Surface(shape = CircleShape, color = Color.White, modifier = Modifier.weight(1f).height(48.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(Icons.Filled.CalendarMonth, contentDescription = null, tint = TEXTO)
                    Spacer(Modifier.width(10.dp))
                    Text(etiquetaFecha(estado.fecha), color = TEXTO, fontSize = 20.sp, fontWeight = FontWeight.Black)
                }
            }
            BotonCircular(Icons.AutoMirrored.Filled.KeyboardArrowRight, "Día siguiente", estado.puedeAvanzar, onDiaSiguiente)
        }
    }
}

@Composable
private fun ChipRacha(dias: Int) {
    Surface(
        shape = CircleShape, color = Color.White,
        border = BorderStroke(2.dp, Color(0xFFFFD966)),
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 14.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(Icons.Filled.LocalFireDepartment, contentDescription = "Racha", tint = Color(0xFFFFB84D))
            Text(dias.toString(), color = TEXTO, fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun BotonCircular(icono: ImageVector, descripcion: String, activo: Boolean, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = Color.White.copy(alpha = if (activo) 1f else 0.5f),
        modifier = Modifier.size(48.dp),
    ) {
        IconButton(onClick = onClick, enabled = activo) {
            Icon(icono, contentDescription = descripcion, tint = TEXTO.copy(alpha = if (activo) 1f else 0.35f))
        }
    }
}

@Composable
private fun DatoLateral(titulo: String, valor: String, alineacion: Alignment.Horizontal) {
    Column(modifier = Modifier.width(84.dp), horizontalAlignment = alineacion) {
        Text(titulo, color = TEXTO_SEC, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        Text(valor, color = TEXTO, fontSize = 28.sp, fontWeight = FontWeight.Black)
        Text("kcal", color = TEXTO_SEC, fontSize = 13.sp)
    }
}

// ---- Taza con olas: se vacía a medida que comes ----
@Composable
private fun TazaCalorias(estado: DiarioUiState) {
    val transicion = rememberInfiniteTransition(label = "ola")
    val fase1 by transicion.animateFloat(
        0f, 1f, infiniteRepeatable(tween(4000, easing = LinearEasing)), label = "fase1",
    )
    val fase2 by transicion.animateFloat(
        1f, 0f, infiniteRepeatable(tween(6000, easing = LinearEasing)), label = "fase2",
    )
    val nivel by animateFloatAsState(1f - estado.progresoKcal, tween(800), label = "nivel")

    Box(modifier = Modifier.size(176.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val radio = size.minDimension / 2f
            drawCircle(FONDO, radius = radio - 2.dp.toPx())
            val recorte = Path().apply { addOval(Rect(center = center, radius = radio - 4.dp.toPx())) }
            clipPath(recorte) {
                ola(nivel, fase2, 8.dp.toPx(), OLA_ATRAS)
                ola(nivel, fase1, 8.dp.toPx(), OLA_FRENTE)
            }
            drawCircle(
                Color(0xFFB8E6CA), radius = radio - 2.dp.toPx(),
                style = Stroke(width = 4.dp.toPx()),
            )
        }
        val numero = if (estado.excedido) abs(estado.kcalRestantes) else estado.kcalRestantes
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(numero.toString(), color = TEXTO, fontSize = 42.sp, fontWeight = FontWeight.Black)
            Text(
                if (estado.excedido) "kcal de más" else "kcal restantes",
                color = TEXTO, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

private fun DrawScope.ola(nivel: Float, fase: Float, amplitud: Float, color: Color) {
    val base = size.height * (1f - nivel.coerceIn(0f, 1f))
    val longitudOnda = 88.dp.toPx()
    val camino = Path().apply {
        moveTo(0f, size.height)
        lineTo(0f, base)
        var x = 0f
        while (x <= size.width) {
            lineTo(x, base + amplitud * sin((x / longitudOnda + fase) * 2f * PI.toFloat()))
            x += 4f
        }
        lineTo(size.width, size.height)
        close()
    }
    drawPath(camino, color)
}

private fun mensajeDiario(e: DiarioUiState): String = when {
    e.cargando -> "Un momento..."
    e.kcalObjetivo <= 0 -> "Completa tu objetivo en el perfil para calcular tu meta."
    !e.esHoy -> "Ese día comiste ${e.kcalConsumidas} kcal."
    e.excedido -> "Hoy te has pasado ${-e.kcalRestantes} kcal. ¡Mañana seguimos!"
    e.kcalConsumidas == 0 -> "¡Buenos días! Añade tu primera comida."
    else -> "¡Vas genial! Aún te caben ${e.kcalRestantes} kcal hoy."
}

private fun etiquetaFecha(fecha: LocalDate): String {
    val hoy = LocalDate.now()
    return when (fecha) {
        hoy -> "Hoy"
        hoy.minusDays(1) -> "Ayer"
        else -> fecha.format(DateTimeFormatter.ofPattern("d MMM", espanol))
    }
}

// ---- Macros ----
@Composable
private fun TarjetaMacros(estado: DiarioUiState) {
    var abiertas by rememberSaveable { mutableStateOf(false) }
    val macros = listOf(
        PROTEINA to estado.proteina,
        GRASA to estado.grasa,
        CARBOS to estado.carbohidratos,
        FIBRA to estado.fibra,
    )

    Tarjeta(radio = 28.dp, padding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().clickable { abiertas = !abiertas },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    if (estado.esHoy) "Macros de hoy" else "Macros del día",
                    color = TEXTO, fontSize = 21.sp, fontWeight = FontWeight.Black,
                )
                Box(
                    Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEAF5EE)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        if (abiertas) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (abiertas) "Ocultar detalle" else "Ver detalle",
                        tint = TEXTO,
                    )
                }
            }

            if (!abiertas) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    macros.forEach { (estilo, macro) ->
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Barra(macro.progreso, estilo.color, estilo.suave, 12.dp)
                            Text(
                                estilo.abreviado, color = TEXTO_SEC, fontSize = 12.sp,
                                fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    macros.forEach { (estilo, macro) ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(
                                Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(estilo.suave),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(estilo.letra, color = estilo.colorLetra, fontSize = 18.sp, fontWeight = FontWeight.Black)
                            }
                            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(estilo.nombre, color = TEXTO, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                                    Text(
                                        if (macro.objetivoG > 0) "${macro.consumidoG} / ${macro.objetivoG} g"
                                        else "${macro.consumidoG} g",
                                        color = TEXTO_SEC, fontSize = 14.sp,
                                    )
                                }
                                Barra(macro.progreso, estilo.color, estilo.suave, 14.dp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Barra(progreso: Float, color: Color, fondo: Color, alto: Dp) {
    Box(Modifier.fillMaxWidth().height(alto).clip(CircleShape).background(fondo)) {
        Box(
            Modifier.fillMaxWidth(progreso.coerceIn(0f, 1f)).fillMaxHeight()
                .clip(CircleShape).background(color),
        )
    }
}

// ---- Comidas ----
@Composable
private fun TarjetaComida(
    comida: ComidaUi,
    kcalDia: Int,
    onAnadir: () -> Unit,
    onEditar: (EntradaUi) -> Unit,
    onBorrar: (Long) -> Unit,
) {
    val estilo = comida.tipo.estilo()
    var abierta by rememberSaveable(comida.tipo.name) { mutableStateOf(false) }
    val presupuesto = (kcalDia * estilo.reparto).roundToInt()
    val progreso = if (presupuesto > 0) comida.kcal.toFloat() / presupuesto else 0f

    Tarjeta(radio = 26.dp, padding = 0.dp) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(enabled = !comida.vacia) { abierta = !abierta }
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    Modifier.size(56.dp).clip(RoundedCornerShape(20.dp)).background(estilo.fondo),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(28.dp))
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(comida.tipo.etiqueta, color = TEXTO, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(
                        if (presupuesto > 0) "${comida.kcal} / $presupuesto kcal" else "${comida.kcal} kcal",
                        color = TEXTO_SEC, fontSize = 14.sp,
                    )
                    Barra(progreso, estilo.barra, Color(0xFFEAF5EE), 8.dp)
                }
                Surface(shape = CircleShape, color = VERDE, modifier = Modifier.size(48.dp)) {
                    IconButton(onClick = onAnadir) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Añadir alimento a ${comida.tipo.etiqueta.lowercase()}",
                            tint = Color.White,
                        )
                    }
                }
            }

            if (abierta && !comida.vacia) {
                Column(
                    modifier = Modifier.padding(start = 16.dp, end = 8.dp, bottom = 12.dp),
                ) {
                    comida.entradas.forEach { entrada ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onEditar(entrada) }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(entrada.nombre, color = TEXTO, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                                Text(
                                    "${entrada.cantidadG} g · ${entrada.kcal} kcal",
                                    color = TEXTO_SEC, fontSize = 13.sp,
                                )
                            }
                            IconButton(onClick = { onBorrar(entrada.id) }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Borrar ${entrada.nombre}", tint = TEXTO_SEC)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DialogoCantidad(
    entrada: EntradaUi,
    onConfirmar: (Float) -> Unit,
    onCancelar: () -> Unit,
) {
    var texto by remember { mutableStateOf(entrada.cantidadG.toString()) }
    val gramos = texto.toFloatOrNull()
    val valido = gramos != null && gramos > 0f && gramos <= GRAMOS_MAXIMOS

    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text(entrada.nombre) },
        text = {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = EntradaNumerica.entero(it, 4) },
                label = { Text("Cantidad (g)") },
                singleLine = true,
                isError = !valido,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        },
        confirmButton = {
            TextButton(onClick = { gramos?.let(onConfirmar) }, enabled = valido) { Text("Guardar") }
        },
        dismissButton = { TextButton(onClick = onCancelar) { Text("Cancelar") } },
    )
}

// ---- Agua ----
@Composable
private fun TarjetaAgua(estado: DiarioUiState, onPonerVasos: (Int) -> Unit) {
    val formato = remember { DecimalFormat("0.##", DecimalFormatSymbols(espanol)) }

    Tarjeta(radio = 28.dp, padding = 20.dp) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text("Agua", color = TEXTO, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text(
                        "${estado.vasos} de ${estado.vasosObjetivo} vasos de 250 ml",
                        color = TEXTO_SEC, fontSize = 14.sp,
                    )
                }
                Text(
                    "${formato.format(estado.vasos * 0.25)} L / ${formato.format(estado.vasosObjetivo * 0.25)} L",
                    color = TEXTO, fontSize = 16.sp, fontWeight = FontWeight.Black,
                )
            }

            KaloWaterGlasses(
                vasosBebidos = estado.vasos,
                vasosTotales = maxOf(estado.vasosObjetivo, estado.vasos),
                onVasoClick = { indice ->
                    // Tocar el último lleno lo quita; tocar cualquier otro deja el total en indice + 1
                    val nuevoTotal = if (indice == estado.vasos - 1) indice else indice + 1
                    onPonerVasos(nuevoTotal)
                },
            )
        }
    }
}

// ---- Tarjeta base ----
@Composable
private fun Tarjeta(radio: Dp, padding: Dp, contenido: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(radio),
        color = Color.White,
        border = BorderStroke(2.dp, BORDE),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(Modifier.padding(padding)) { contenido() }
    }
}

@Preview(showBackground = true, heightDp = 1500)
@Composable
private fun DiarioScreenPreview() {
    DiarioScreen(
        estado = DiarioUiState.ejemplo(),
        onDiaAnterior = {}, onDiaSiguiente = {}, onAnadirComida = {},
        onPonerVasos = {}, onBorrarEntrada = {}, onEditarCantidad = { _, _ -> },
        racha = 12,
    )
}
