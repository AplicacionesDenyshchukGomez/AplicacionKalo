package com.DanielDMikelG.kalo.ui.screens.register

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.ui.components.CampoTexto
import com.DanielDMikelG.kalo.ui.components.KaloTextField
import com.DanielDMikelG.kalo.ui.components.SelectorOpciones
import com.DanielDMikelG.kalo.ui.components.TecladoKalo
import com.DanielDMikelG.kalo.ui.components.comoTexto
import com.DanielDMikelG.kalo.util.EntradaNumerica

@Composable
fun RegisterScreen(
    viewModel: RegistroViewModel,
    onContinuar: () -> Unit,
    onVolver: () -> Unit,
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        IconButton(onClick = onVolver) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
        }
        Text("Crea tu cuenta", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        CampoTexto(estado.nombre, { viewModel.actualizar { s -> s.copy(nombre = it) } }, "Nombre de usuario")
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            estado.email, { viewModel.actualizar { s -> s.copy(email = it) } },
            "Correo electrónico", tipoTeclado = KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            estado.fechaNacimiento, { viewModel.actualizar { s -> s.copy(fechaNacimiento = it) } },
            "Fecha de nacimiento (AAAA-MM-DD)"
        )
        Spacer(Modifier.height(12.dp))
        CampoTexto(
            estado.peso, { viewModel.actualizar { s -> s.copy(peso = EntradaNumerica.decimal(it)) } },
            "Peso (kg)", tipoTeclado = KeyboardType.Decimal
        )
        Spacer(Modifier.height(12.dp))
        KaloTextField(
            estado.altura,
            { viewModel.actualizar { s -> s.copy(altura = EntradaNumerica.entero(it, 3)) } },
            "Altura (cm)",
            keyboardOptions = TecladoKalo.Entero,
        )

        Spacer(Modifier.height(16.dp))

        SelectorOpciones(
            titulo = "Sexo",
            opciones = Sexo.entries,
            seleccionada = estado.sexo,
            etiqueta = { it.comoTexto() },
            onSeleccion = { viewModel.actualizar { s -> s.copy(sexo = it) } }
        )
        Spacer(Modifier.height(16.dp))
        SelectorOpciones(
            titulo = "Nivel de actividad",
            opciones = NivelActividad.entries,
            seleccionada = estado.nivelActividad,
            etiqueta = { it.comoTexto() },
            onSeleccion = { viewModel.actualizar { s -> s.copy(nivelActividad = it) } }
        )

        estado.error?.let {
            Spacer(Modifier.height(12.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))

        estado.avisoDatosPersonales?.let {
            Text(it, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = { viewModel.continuarAObjetivo(onContinuar) },
            enabled = estado.datosPersonalesValidos,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continuar")
        }
    }
}