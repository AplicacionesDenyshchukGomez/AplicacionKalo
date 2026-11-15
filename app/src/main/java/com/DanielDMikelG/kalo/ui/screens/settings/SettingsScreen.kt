package com.DanielDMikelG.kalo.ui.screens.settings

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.ui.components.KaloIconoRedondo
import com.DanielDMikelG.kalo.ui.components.KaloSettingsRow
import com.DanielDMikelG.kalo.ui.theme.KaloTheme

// Pastel de los círculos de icono (mismo estilo que los macros)
private val FondoVerde = Color(0xFFCDEFD9)
private val IconoVerde = Color(0xFF14573A)
private val FondoTurquesa = Color(0xFFD6F5F8)
private val IconoTurquesa = Color(0xFF17707D)

@Composable
fun SettingsScreen(
    onCerrar: () -> Unit,
    onEditarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var confirmarCierre by rememberSaveable { mutableStateOf(false) }
    var verAcercaDe by rememberSaveable { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Ajustes", style = MaterialTheme.typography.headlineLarge)
                    KaloIconoRedondo(Icons.Default.Close, "Cerrar ajustes", onCerrar)
                }
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(22.dp),
            ) {
                Seccion("Cuenta") {
                    KaloSettingsRow(
                        icono = Icons.Default.Person,
                        colorFondoIcono = FondoVerde,
                        colorIcono = IconoVerde,
                        titulo = "Mi perfil",
                        subtitulo = "Datos, peso objetivo y plazo",
                        onClick = onEditarPerfil,
                    )
                }
                Seccion("Información") {
                    KaloSettingsRow(
                        icono = Icons.Default.Info,
                        colorFondoIcono = FondoTurquesa,
                        colorIcono = IconoTurquesa,
                        titulo = "Acerca de Kalo",
                        subtitulo = "Datos de Open Food Facts",
                        onClick = { verAcercaDe = true },
                    )
                }

                Surface(
                    onClick = { confirmarCierre = true },
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.errorContainer,
                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Text(
                            "Cerrar sesión",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(start = 10.dp),
                        )
                    }
                }
            }
        }
    }

    if (confirmarCierre) {
        AlertDialog(
            onDismissRequest = { confirmarCierre = false },
            title = { Text("Cerrar sesión") },
            text = { Text("¿Seguro? Tus datos se quedan guardados en este móvil.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarCierre = false
                    onCerrarSesion()
                }) { Text("Cerrar sesión") }
            },
            dismissButton = {
                TextButton(onClick = { confirmarCierre = false }) { Text("Cancelar") }
            },
        )
    }

    if (verAcercaDe) {
        AlertDialog(
            onDismissRequest = { verAcercaDe = false },
            title = { Text("Acerca de Kalo") },
            text = {
                Text(
                    "Kalo es un proyecto de clase de Daniel Denyshchuk y Mikel Gómez.\n\n" +
                            "Los datos de los alimentos proceden de Open Food Facts " +
                            "(openfoodfacts.org) y se usan bajo la licencia ODbL.",
                )
            },
            confirmButton = {
                TextButton(onClick = { verAcercaDe = false }) { Text("Cerrar") }
            },
        )
    }
}

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            titulo,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp),
        )
        contenido()
    }
}

@Preview(name = "Ajustes claro", showBackground = true)
@Composable
private fun SettingsLightPreview() {
    KaloTheme(darkTheme = false) { SettingsScreen({}, {}, {}) }
}

@Preview(name = "Ajustes oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsDarkPreview() {
    KaloTheme(darkTheme = true) { SettingsScreen({}, {}, {}) }
}