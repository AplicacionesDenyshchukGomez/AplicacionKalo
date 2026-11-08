package com.DanielDMikelG.kalo.ui.screens.objective

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.ui.components.CampoTexto
import com.DanielDMikelG.kalo.ui.components.SelectorOpciones
import com.DanielDMikelG.kalo.ui.components.comoTexto
import com.DanielDMikelG.kalo.ui.screens.register.RegistroViewModel

@Composable
fun ObjectiveScreen(
    viewModel: RegistroViewModel,
    onTerminar: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()
    val necesitaMeta = estado.tipoObjetivo != null && estado.tipoObjetivo != TipoObjetivo.MANTENER

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("¿Cuál es tu objetivo?", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        SelectorOpciones(
            titulo = "Quiero...",
            opciones = TipoObjetivo.entries,
            seleccionada = estado.tipoObjetivo,
            etiqueta = { it.comoTexto() },
            onSeleccion = { viewModel.actualizar { s -> s.copy(tipoObjetivo = it) } }
        )

        if (necesitaMeta) {
            Spacer(Modifier.height(16.dp))
            CampoTexto(
                estado.metaKg, { viewModel.actualizar { s -> s.copy(metaKg = it) } },
                "Peso objetivo (kg)", tipoTeclado = KeyboardType.Decimal
            )
            Spacer(Modifier.height(12.dp))
            CampoTexto(
                estado.plazo, { viewModel.actualizar { s -> s.copy(plazo = it) } },
                "Plazo (semanas)", tipoTeclado = KeyboardType.Number
            )
        }

        estado.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.finalizar(onTerminar) },
            enabled = estado.objetivoValido && !estado.guardando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Empezar")
        }
    }
}