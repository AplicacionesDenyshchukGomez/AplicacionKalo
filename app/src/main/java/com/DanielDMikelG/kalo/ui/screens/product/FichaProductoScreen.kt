package com.DanielDMikelG.kalo.ui.screens.product

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.ResultadoAlimento
import kotlin.math.roundToInt

private val FONDO = Color(0xFFF4FBF6)
private val BORDE = Color(0xFFD3EDDD)
private val TEXTO = Color(0xFF17372A)
private val TEXTO_SEC = Color(0xFF3F5B4C)
private val VERDE_ACCION = Color(0xFF1B7F4E)
private val PROTEINA = Color(0xFFFF9AA2)
private val GRASA = Color(0xFFFFD966)
private val CARBOS = Color(0xFF8CCBFF)
private val FIBRA = Color(0xFFD9B38C)

@Composable
fun FichaProductoRoute(
    viewModel: FichaProductoViewModel,
    onAnadir: (Alimento) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estado by viewModel.estado.collectAsState()
    FichaProductoScreen(
        estado = estado,
        modo = viewModel.modo,
        onAnadir = onAnadir,
        onReintentar = viewModel::cargar,
        onVolver = onVolver,
        modifier = modifier,
    )
}

@Composable
fun FichaProductoScreen(
    estado: ResultadoAlimento?,
    modo: ModoFicha,
    onAnadir: (Alimento) -> Unit,
    onReintentar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxSize().background(FONDO)) {
        IconButton(onClick = onVolver, modifier = Modifier.padding(8.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TEXTO)
        }

        when (estado) {
            null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = VERDE_ACCION)
            }

            is ResultadoAlimento.Encontrado -> Contenido(
                alimento = estado.alimento,
                desdeCache = estado.desdeCache,
                modo = modo,
                onAnadir = onAnadir,
            )

            ResultadoAlimento.CodigoNoValido -> Mensaje(
                "Ese código no es válido", "Revisa que tenga 8, 12 o 13 cifras.",
                onVolver, "Volver a escanear",
            )

            ResultadoAlimento.NoEncontrado -> Mensaje(
                "No hemos encontrado este producto",
                "Todavía no está en Open Food Facts.",
                onVolver, "Volver a escanear",
            )

            ResultadoAlimento.DatosIncompletos -> Mensaje(
                "El producto no tiene datos nutricionales",
                "Existe en Open Food Facts, pero sin calorías ni macros.",
                onVolver, "Volver a escanear",
            )

            ResultadoAlimento.SinConexion -> Mensaje(
                "Sin conexión",
                "Necesitamos internet para buscar productos nuevos.",
                onReintentar, "Reintentar",
            )

            is ResultadoAlimento.Error -> Mensaje(
                "Algo ha ido mal", estado.mensaje, onReintentar, "Reintentar",
            )
        }
    }
}

@Composable
private fun Contenido(
    alimento: Alimento,
    desdeCache: Boolean,
    modo: ModoFicha,
    onAnadir: (Alimento) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
    ) {
        Text(alimento.nombre, style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold, color = TEXTO)
        Spacer(Modifier.height(16.dp))

        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BORDE),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Por 100 g", color = TEXTO_SEC, style = MaterialTheme.typography.labelLarge)
                Text(
                    "${alimento.kcal100g.roundToInt()} kcal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold, color = TEXTO,
                )
                Spacer(Modifier.height(12.dp))
                Fila("Proteínas", alimento.prot, PROTEINA)
                Fila("Carbohidratos", alimento.carb, CARBOS)
                Fila("Grasas", alimento.grasa, GRASA)
                Fila("Fibra", alimento.fibra, FIBRA)
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            if (desdeCache) "Guardado en tu dispositivo" else "Datos de Open Food Facts",
            color = TEXTO_SEC, style = MaterialTheme.typography.bodySmall,
        )

        if (modo == ModoFicha.ANADIR) {
            Spacer(Modifier.height(24.dp))
            Button(
                onClick = { onAnadir(alimento) },
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = VERDE_ACCION),
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Añadir al diario") }
        }
    }
}

@Composable
private fun Fila(nombre: String, gramos: Float, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.padding(end = 8.dp).height(12.dp).fillMaxWidth(0.04f).background(color))
            Text(nombre, color = TEXTO)
        }
        Text("${"%.1f".format(gramos)} g", color = TEXTO, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun Mensaje(titulo: String, detalle: String, onAccion: () -> Unit, textoAccion: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(titulo, style = MaterialTheme.typography.titleLarge, color = TEXTO,
            textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(detalle, color = TEXTO_SEC, textAlign = TextAlign.Center)
        Spacer(Modifier.height(20.dp))
        OutlinedButton(onClick = onAccion) { Text(textoAccion) }
    }
}
