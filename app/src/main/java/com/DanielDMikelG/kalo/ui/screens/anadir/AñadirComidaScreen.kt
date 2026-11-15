package com.DanielDMikelG.kalo.ui.screens.anadir

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.TipoComida
import java.util.Locale


private val FONDO = Color(0xFFF4FBF6)
private val BORDE = Color(0xFFD3EDDD)
private val TEXTO = Color(0xFF17372A)
private val TEXTO_SEC = Color(0xFF3F5B4C)
private val VERDE = Color(0xFF1B7F4E)
private val CONTENEDOR = Color(0xFFCDEFD9)

@Composable
fun AnadirComidaRoute(
    viewModel: AnadirComidaViewModel,
    tipo: TipoComida,
    onEscanear: () -> Unit,
    onMisAlimentos: () -> Unit,
    onElegirGuardado: (Alimento) -> Unit,
    onElegirEnLinea: (String) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val texto by viewModel.texto.collectAsStateWithLifecycle()
    val locales by viewModel.locales.collectAsStateWithLifecycle()
    val recientes by viewModel.recientes.collectAsStateWithLifecycle()
    val enLinea by viewModel.enLinea.collectAsStateWithLifecycle()

    AnadirComidaScreen(
        tipo = tipo,
        texto = texto,
        locales = locales,
        recientes = recientes,
        enLinea = enLinea,
        onTexto = viewModel::cambiarTexto,
        onBuscarEnLinea = viewModel::buscarEnLinea,
        onEscanear = onEscanear,
        onMisAlimentos = onMisAlimentos,
        onElegirGuardado = onElegirGuardado,
        onElegirEnLinea = onElegirEnLinea,
        onVolver = onVolver,
        modifier = modifier,
    )
}

@Composable
fun AnadirComidaScreen(
    tipo: TipoComida,
    texto: String,
    locales: List<Alimento>,
    recientes: List<Alimento>,
    enLinea: BusquedaEnLinea,
    onTexto: (String) -> Unit,
    onBuscarEnLinea: () -> Unit,
    onEscanear: () -> Unit,
    onMisAlimentos: () -> Unit,
    onElegirGuardado: (Alimento) -> Unit,
    onElegirEnLinea: (String) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val hayTexto = texto.isNotBlank()

    LazyColumn(
        modifier = modifier.fillMaxSize().background(FONDO).statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onVolver) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TEXTO)
                }
                Text(
                    "Añadir a ${tipo.etiqueta.lowercase()}",
                    color = TEXTO, fontSize = 24.sp, fontWeight = FontWeight.Black,
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                BotonMetodo("Escanear", Icons.Filled.QrCodeScanner, Modifier.weight(1f), onEscanear)
                BotonMetodo("Mis alimentos", Icons.Filled.Star, Modifier.weight(1f), onMisAlimentos)
            }
        }

        item {
            OutlinedTextField(
                value = texto,
                onValueChange = onTexto,
                label = { Text("Buscar alimento") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onBuscarEnLinea() }),
                modifier = Modifier.fillMaxWidth(),
            )
        }

        if (!hayTexto) {
            item { Titulo("Guardados recientemente") }
            if (recientes.isEmpty()) {
                item { Nota("Todavía no hay alimentos guardados. Busca uno, escanéalo o créalo en Mis alimentos.") }
            } else {
                items(recientes, key = { "r${it.id}" }) { FilaAlimento(it) { onElegirGuardado(it) } }
            }
        } else {
            item { Titulo("En tu dispositivo") }
            if (locales.isEmpty()) {
                item { Nota("No hay coincidencias guardadas.") }
            } else {
                items(locales, key = { "l${it.id}" }) { FilaAlimento(it) { onElegirGuardado(it) } }
            }
            bloqueEnLinea(texto, enLinea, onBuscarEnLinea, onElegirEnLinea)
        }
    }
}

private fun LazyListScope.bloqueEnLinea(
    texto: String,
    enLinea: BusquedaEnLinea,
    onBuscar: () -> Unit,
    onElegir: (String) -> Unit,
) {
    item { Titulo("En internet") }

    if (texto.trim().length < AnadirComidaViewModel.MIN_CARACTERES) {
        item { Nota("Escribe al menos ${AnadirComidaViewModel.MIN_CARACTERES} letras para buscar en internet.") }
        return
    }

    when (enLinea) {
        BusquedaEnLinea.Inactiva -> item { BotonBuscar("Buscar en Open Food Facts", onBuscar) }
        BusquedaEnLinea.Buscando -> item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = VERDE, strokeWidth = 3.dp)
                Text("Buscando...", color = TEXTO_SEC, fontSize = 15.sp)
            }
        }
        is BusquedaEnLinea.Resultados -> {
            val validos = enLinea.alimentos.filter { it.codigoBarras != null }
            items(validos) { alimento -> FilaAlimento(alimento) { onElegir(alimento.codigoBarras.orEmpty()) } }
            item { Nota("Datos de Open Food Facts (licencia ODbL).") }
        }
        BusquedaEnLinea.SinResultados -> item { Nota("No se ha encontrado nada con ese nombre.") }
        BusquedaEnLinea.SinConexion -> {
            item { Nota("Sin conexión. Comprueba internet e inténtalo de nuevo.") }
            item { BotonBuscar("Reintentar", onBuscar) }
        }
        is BusquedaEnLinea.Error -> {
            item { Nota(enLinea.mensaje) }
            item { BotonBuscar("Reintentar", onBuscar) }
        }
    }
}

@Composable
private fun BotonMetodo(titulo: String, icono: ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = CONTENEDOR,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Icon(icono, contentDescription = null, tint = TEXTO, modifier = Modifier.size(24.dp))
            Text(titulo, color = TEXTO, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun BotonBuscar(texto: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = VERDE),
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) { Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Black) }
}

@Composable
private fun Titulo(texto: String) {
    Text(
        texto,
        modifier = Modifier.padding(start = 4.dp, top = 8.dp),
        color = TEXTO, fontSize = 18.sp, fontWeight = FontWeight.Black,
    )
}

@Composable
private fun Nota(texto: String) {
    Text(texto, modifier = Modifier.padding(horizontal = 4.dp), color = TEXTO_SEC, fontSize = 14.sp)
}

@Composable
private fun FilaAlimento(alimento: Alimento, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(2.dp, BORDE),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(alimento.nombre, color = TEXTO, fontSize = 16.sp, fontWeight = FontWeight.Black)
            Text(
                "${numero(alimento.kcal100g)} kcal por 100 g",
                color = TEXTO_SEC, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

private fun numero(valor: Float): String =
    if (valor % 1f == 0f) valor.toInt().toString() else String.format(Locale.getDefault(), "%.1f", valor)

