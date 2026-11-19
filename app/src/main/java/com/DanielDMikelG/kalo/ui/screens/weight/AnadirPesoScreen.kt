package com.DanielDMikelG.kalo.ui.screens.weight

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val FONDO = Color(0xFFF4FBF6)
private val BORDE = Color(0xFFD3EDDD)
private val TEXTO = Color(0xFF17372A)
private val TEXTO_SEC = Color(0xFF3F5B4C)
private val VERDE = Color(0xFF1B7F4E)
private val ERROR = Color(0xFFB3261E)
private val REGEX_PESO = Regex("^\\d{0,3}([.,]\\d?)?$")
private val espanol: Locale = Locale.forLanguageTag("es-ES")

@Composable
fun AnadirPesoRoute(
    viewModel: AnadirPesoViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val formulario by viewModel.formulario.collectAsStateWithLifecycle()
    val ultimo by viewModel.ultimo.collectAsStateWithLifecycle()
    AnadirPesoScreen(
        formulario = formulario,
        ultimo = ultimo,
        onTexto = viewModel::cambiarTexto,
        onGuardar = { viewModel.guardar(onGuardado) },
        onVolver = onVolver,
        modifier = modifier,
    )
}

@Composable
fun AnadirPesoScreen(
    formulario: FormularioPeso,
    ultimo: RegistroPeso?,
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
            Text("Añadir peso", color = TEXTO, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            border = BorderStroke(2.dp, BORDE),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Último peso registrado", color = TEXTO_SEC, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                if (ultimo == null) {
                    Text("Todavía no hay ninguno", color = TEXTO, fontSize = 18.sp, fontWeight = FontWeight.Black)
                } else {
                    Text(
                        "${formatoKg(ultimo.pesoKg)} kg",
                        color = TEXTO, fontSize = 30.sp, fontWeight = FontWeight.Black,
                    )
                    Text(etiquetaFecha(ultimo.fecha), color = TEXTO_SEC, fontSize = 14.sp)
                }
            }
        }

        OutlinedTextField(
            value = formulario.texto,
            onValueChange = { if (REGEX_PESO.matches(it)) onTexto(it) },
            label = { Text("Tu peso de hoy") },
            suffix = { Text("kg") },
            singleLine = true,
            isError = formulario.error != null,
            textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Black, color = TEXTO),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onGuardar() }),
            modifier = Modifier.fillMaxWidth(),
        )

        formulario.error?.let {
            Text(it, color = ERROR, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }

        Text(
            "Si ya has registrado tu peso hoy, se sustituirá por este. " +
                    "Tu meta de calorías se recalcula con tu último peso.",
            color = TEXTO_SEC, fontSize = 13.sp,
        )

        Button(
            onClick = onGuardar,
            enabled = !formulario.guardando,
            colors = ButtonDefaults.buttonColors(containerColor = VERDE),
            modifier = Modifier.fillMaxWidth().height(54.dp),
        ) {
            Text("Guardar peso", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }
    }
}

private fun formatoKg(kg: Float): String = String.format(espanol, "%.1f", kg)

private fun etiquetaFecha(fecha: LocalDate): String {
    val hoy = LocalDate.now()
    return when (fecha) {
        hoy -> "Hoy"
        hoy.minusDays(1) -> "Ayer"
        else -> fecha.format(DateTimeFormatter.ofPattern("d MMM yyyy", espanol))
    }
}