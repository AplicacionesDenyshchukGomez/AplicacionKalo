package com.DanielDMikelG.kalo.ui.screens.myfoods

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DanielDMikelG.kalo.domain.model.Alimento
import java.util.Locale

private val Fondo = Color(0xFFF4FBF6)
private val Borde = Color(0xFFD3EDDD)
private val Texto = Color(0xFF17372A)
private val TextoSec = Color(0xFF3F5B4C)
private val Verde = Color(0xFF1B7F4E)
private val Error = Color(0xFFB3261E)
private val REGEX_DECIMAL = Regex("^\\d{0,3}([.,]\\d{0,2})?$")

/**
 * Si [onElegir] no es null, la pantalla funciona como selector: tocar un alimento lo elige
 * y crear uno nuevo lo elige también al guardarlo.
 */
@Composable
fun MisAlimentosRoute(
    viewModel: MisAlimentosViewModel,
    onVolver: () -> Unit,
    onElegir: ((Alimento) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val alimentos by viewModel.alimentos.collectAsState()
    val formulario by viewModel.formulario.collectAsState()
    val f = formulario

    BackHandler(enabled = f != null) { viewModel.cancelar() }

    if (f == null) {
        ListaMisAlimentos(
            alimentos = alimentos,
            seleccionable = onElegir != null,
            onNuevo = viewModel::nuevo,
            onElegir = { onElegir?.invoke(it) },
            onVolver = onVolver,
            modifier = modifier,
        )
    } else {
        FormularioMiAlimento(
            formulario = f,
            onEditar = viewModel::editar,
            onGuardar = { viewModel.guardar { creado -> onElegir?.invoke(creado) } },
            onCancelar = viewModel::cancelar,
            modifier = modifier,
        )
    }
}

@Composable
private fun ListaMisAlimentos(
    alimentos: List<Alimento>,
    seleccionable: Boolean,
    onNuevo: () -> Unit,
    onElegir: (Alimento) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Texto)
            }
            Text("Mis alimentos", color = Texto, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        Button(
            onClick = onNuevo,
            colors = ButtonDefaults.buttonColors(containerColor = Verde),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Crear alimento nuevo", fontSize = 17.sp, fontWeight = FontWeight.Black)
        }

        if (alimentos.isEmpty()) {
            Text(
                "Aún no has creado ningún alimento. Crea uno cuando no lo encuentres en el buscador ni al escanearlo.",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
                color = TextoSec, fontSize = 15.sp,
            )
        } else {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                alimentos.forEach { alimento ->
                    FilaAlimento(alimento, seleccionable) { onElegir(alimento) }
                }
            }
        }
    }
}

@Composable
private fun FilaAlimento(alimento: Alimento, seleccionable: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(22.dp),
        color = Color.White,
        border = BorderStroke(2.dp, Borde),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (seleccionable) Modifier.clickable(onClick = onClick) else Modifier),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(alimento.nombre, color = Texto, fontSize = 17.sp, fontWeight = FontWeight.Black)
            Text(
                "${numero(alimento.kcal100g)} kcal por 100 g",
                color = TextoSec, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
            )
            Text(
                "P ${numero(alimento.prot)} g · C ${numero(alimento.carb)} g · " +
                        "G ${numero(alimento.grasa)} g · Fibra ${numero(alimento.fibra)} g",
                color = TextoSec, fontSize = 13.sp,
            )
        }
    }
}

@Composable
private fun FormularioMiAlimento(
    formulario: FormularioAlimento,
    onEditar: ((FormularioAlimento) -> FormularioAlimento) -> Unit,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    modifier: Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Fondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onCancelar) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Texto)
            }
            Text("Alimento nuevo", color = Texto, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = { if (it.length <= 60) onEditar { f -> f.copy(nombre = it) } },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        Text("Valores por 100 g de alimento", color = TextoSec, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)

        CampoNumero(formulario.kcal, "Calorías (kcal)", Modifier.fillMaxWidth()) { onEditar { f -> f.copy(kcal = it) } }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoNumero(formulario.prot, "Proteínas (g)", Modifier.weight(1f)) { onEditar { f -> f.copy(prot = it) } }
            CampoNumero(formulario.carb, "Carbohidratos (g)", Modifier.weight(1f)) { onEditar { f -> f.copy(carb = it) } }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CampoNumero(formulario.grasa, "Grasas (g)", Modifier.weight(1f)) { onEditar { f -> f.copy(grasa = it) } }
            CampoNumero(formulario.fibra, "Fibra (g)", Modifier.weight(1f)) { onEditar { f -> f.copy(fibra = it) } }
        }

        formulario.error?.let { Text(it, color = Error, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold) }

        Button(
            onClick = onGuardar,
            enabled = !formulario.guardando,
            colors = ButtonDefaults.buttonColors(containerColor = Verde),
            modifier = Modifier.fillMaxWidth().height(52.dp),
        ) {
            Text("Guardar alimento", fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
        TextButton(onClick = onCancelar, modifier = Modifier.fillMaxWidth()) {
            Text("Cancelar", color = TextoSec, fontSize = 16.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun CampoNumero(valor: String, etiqueta: String, modifier: Modifier, onCambio: (String) -> Unit) {
    OutlinedTextField(
        value = valor,
        onValueChange = { if (REGEX_DECIMAL.matches(it)) onCambio(it) },
        label = { Text(etiqueta) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        modifier = modifier,
    )
}

private fun numero(valor: Float): String =
    if (valor % 1f == 0f) valor.toInt().toString() else String.format(Locale.getDefault(), "%.1f", valor)