package com.DanielDMikelG.kalo.ui.screens.anadir

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.domain.usecase.NutrientesRacion
import com.DanielDMikelG.kalo.util.EntradaNumerica
import java.util.Locale

private val FONDO = Color(0xFFF4FBF6)
private val BORDE = Color(0xFFD3EDDD)
private val TEXTO = Color(0xFF17372A)
private val TEXTO_SEC = Color(0xFF3F5B4C)
private val VERDE = Color(0xFF1B7F4E)
private val CONTENEDOR = Color(0xFFCDEFD9)
private val ERROR = Color(0xFFB3261E)

@Composable
fun CantidadRoute(
    viewModel: CantidadViewModel,
    tipo: TipoComida,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estado by viewModel.estado.collectAsStateWithLifecycle()
    CantidadScreen(
        alimento = viewModel.alimento,
        tipo = tipo,
        estado = estado,
        onTexto = viewModel::cambiarTexto,
        onGuardar = { viewModel.guardar(onGuardado) },
        onVolver = onVolver,
        modifier = modifier,
    )
}

@Composable
fun CantidadScreen(
    alimento: Alimento,
    tipo: TipoComida,
    estado: CantidadUiState,
    onTexto: (String) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(FONDO)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = TEXTO)
            }
            Text("Cantidad", color = TEXTO, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(alimento.nombre, color = TEXTO, fontSize = 22.sp, fontWeight = FontWeight.Black)
            Text(
                "${numero(alimento.kcal100g)} kcal por 100 g",
                color = TEXTO_SEC, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
            )
        }

        OutlinedTextField(
            value = estado.texto,
            onValueChange = { onTexto(EntradaNumerica.entero(it, 4)) },
            label = { Text("Cantidad (g)") },
            singleLine = true,
            isError = estado.nutrientes == null,
            supportingText = {
                if (estado.nutrientes == null) Text("Indica entre 1 y 5000 g.")
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("50", "100", "150", "200").forEach { gramos ->
                Surface(
                    shape = CircleShape,
                    color = if (estado.texto == gramos) VERDE else CONTENEDOR,
                    modifier = Modifier.clickable { onTexto(gramos) },
                ) {
                    Text(
                        "$gramos g",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (estado.texto == gramos) Color.White else TEXTO,
                        fontSize = 15.sp, fontWeight = FontWeight.Black,
                    )
                }
            }
        }

        TarjetaNutrientes(estado.nutrientes)

        estado.error?.let { Text(it, color = ERROR, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold) }

        Button(
            onClick = onGuardar,
            enabled = estado.nutrientes != null && !estado.guardando,
            colors = ButtonDefaults.buttonColors(containerColor = VERDE),
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Text("Añadir a ${tipo.etiqueta.lowercase()}", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun TarjetaNutrientes(n: NutrientesRacion?) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(2.dp, BORDE),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                if (n == null) "— kcal" else "${n.kcalRedondeadas} kcal",
                color = TEXTO, fontSize = 32.sp, fontWeight = FontWeight.Black,
            )
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Dato("Proteínas", n?.proteina)
                Dato("Carbos", n?.carbohidratos)
                Dato("Grasas", n?.grasa)
                Dato("Fibra", n?.fibra)
            }
        }
    }
}

@Composable
private fun Dato(nombre: String, valor: Float?) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            if (valor == null) "—" else "${String.format(Locale.getDefault(), "%.1f", valor)} g",
            color = TEXTO, fontSize = 17.sp, fontWeight = FontWeight.Black,
        )
        Text(nombre, color = TEXTO_SEC, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
    }
}

private fun numero(valor: Float): String =
    if (valor % 1f == 0f) valor.toInt().toString() else String.format(Locale.getDefault(), "%.1f", valor)
