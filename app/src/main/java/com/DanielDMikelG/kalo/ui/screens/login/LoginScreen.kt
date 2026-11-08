package com.DanielDMikelG.kalo.ui.screens.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.DanielDMikelG.kalo.ui.components.CampoTexto
import androidx.compose.runtime.collectAsState
@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val estado by viewModel.estado.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Kalo", style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(32.dp))

        CampoTexto(
            valor = estado.usuario,
            onCambio = viewModel::onUsuarioChange,
            etiqueta = "Correo o nombre de usuario"
        )
        estado.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.acceder(onLoginExitoso) },
            enabled = estado.usuario.isNotBlank() && !estado.cargando,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Acceder")
        }
        Spacer(Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?")
            TextButton(onClick = onRegisterClick) {
                Text("Regístrate")
            }
        }
    }
}