package com.DanielDMikelG.kalo.ui.screens.profile

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.model.Usuario
import com.DanielDMikelG.kalo.ui.components.KaloButton
import com.DanielDMikelG.kalo.ui.components.KaloCard
import com.DanielDMikelG.kalo.ui.components.KaloSecondaryButton
import com.DanielDMikelG.kalo.ui.components.KaloTextField
import com.DanielDMikelG.kalo.ui.theme.KaloTheme
import com.DanielDMikelG.kalo.ui.theme.Spacing
import java.time.LocalDate

/** Punto de entrada: es lo que se llama desde la navegación. */
@Composable
fun ProfileRoute(
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current.applicationContext
    val usuarioId = remember { SesionManager(context).usuarioActual() }

    if (usuarioId == null) {
        Surface(modifier = modifier) {
            Text("No hay ninguna sesión iniciada.", modifier = Modifier.padding(Spacing.md))
        }
        return
    }

    val viewModel: ProfileViewModel = viewModel(
        key = "perfil_$usuarioId",
        factory = viewModelFactory {
            initializer {
                ProfileViewModel(UsuarioRepository(KaloDatabase.obtener(context)), usuarioId)
            }
        },
    )
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileContent(
        state = state,
        onEditar = viewModel::empezarEdicion,
        onCancelar = viewModel::cancelarEdicion,
        onGuardar = viewModel::guardar,
        onFormChange = viewModel::actualizarForm,
        onCerrarSesion = {
            SesionManager(context).cerrarSesion()
            onCerrarSesion()
        },
        modifier = modifier,
    )
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    onEditar: () -> Unit,
    onCancelar: () -> Unit,
    onGuardar: () -> Unit,
    onFormChange: (ProfileFormState) -> Unit,
    onCerrarSesion: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Perfil", style = MaterialTheme.typography.headlineMedium)

            val usuario = state.usuario
            val objetivo = state.objetivo
            when {
                state.cargando -> Text("Cargando...", style = MaterialTheme.typography.bodyLarge)
                usuario == null || objetivo == null ->
                    Text("No se encontró el perfil.", style = MaterialTheme.typography.bodyLarge)
                state.editando -> FormContent(state, onGuardar, onCancelar, onFormChange)
                else -> InfoContent(state, usuario, objetivo, onEditar, onCerrarSesion)
            }
        }
    }
}

@Composable
private fun InfoContent(
    state: ProfileUiState,
    usuario: Usuario,
    objetivo: Objetivo,
    onEditar: () -> Unit,
    onCerrarSesion: () -> Unit,
) {
    state.mensaje?.let {
        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
    }
    KaloCard {
        Text("Tus datos", style = MaterialTheme.typography.titleMedium)
        InfoRow("Nombre", usuario.nombre)
        InfoRow("Email", usuario.email)
        InfoRow("Nacimiento", "${usuario.fechaNacimiento.formatear()} (${usuario.edad()} años)")
        InfoRow("Sexo", usuario.sexo.etiqueta)
        InfoRow("Altura", "${usuario.altura} cm")
        InfoRow(
            "Peso actual",
            state.pesoActualKg?.let { "${it.sinDecimalesInnecesarios()} kg" } ?: "— (sin registros)",
        )
    }
    KaloCard {
        Text("Tus objetivos", style = MaterialTheme.typography.titleMedium)
        InfoRow("Objetivo", objetivo.tipo.etiqueta)
        InfoRow("Actividad", usuario.nivelActividad.etiqueta)
        InfoRow("Meta de peso", "${objetivo.metaKg.sinDecimalesInnecesarios()} kg")
        InfoRow("Plazo", "${objetivo.plazo} semanas")
        InfoRow("Meta de agua", "${objetivo.aguaMeta} ml")
        InfoRow("Calorías objetivo", objetivo.kcalDiarias?.let { "$it kcal" } ?: "— (pendiente)")
    }
    KaloButton(text = "Editar datos", onClick = onEditar)

    var confirmarCierre by rememberSaveable { mutableStateOf(false) }
    KaloSecondaryButton(text = "Cerrar sesión", onClick = { confirmarCierre = true })
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
}

@Composable
private fun FormContent(
    state: ProfileUiState,
    onGuardar: () -> Unit,
    onCancelar: () -> Unit,
    onFormChange: (ProfileFormState) -> Unit,
) {
    val f = state.form
    val e = state.errores

    KaloTextField(f.nombre, { onFormChange(f.copy(nombre = it)) }, "Nombre", error = e.nombre)
    KaloTextField(
        f.fechaNacimiento, { onFormChange(f.copy(fechaNacimiento = it)) },
        "Fecha de nacimiento (dd/mm/aaaa)", error = e.fechaNacimiento,
    )
    KaloTextField(f.altura, { onFormChange(f.copy(altura = it)) }, "Altura (cm)", error = e.altura)
    KaloTextField(f.metaKg, { onFormChange(f.copy(metaKg = it)) }, "Meta de peso (kg)", error = e.metaKg)
    KaloTextField(f.plazo, { onFormChange(f.copy(plazo = it)) }, "Plazo (semanas)", error = e.plazo)
    KaloTextField(f.aguaMeta, { onFormChange(f.copy(aguaMeta = it)) }, "Meta de agua (ml)", error = e.aguaMeta)

    OpcionesRadio("Sexo", Sexo.entries, f.sexo, { it.etiqueta }) { onFormChange(f.copy(sexo = it)) }
    OpcionesRadio("Objetivo", TipoObjetivo.entries, f.tipo, { it.etiqueta }) { onFormChange(f.copy(tipo = it)) }
    OpcionesRadio("Nivel de actividad", NivelActividad.entries, f.nivelActividad, { it.etiqueta }) {
        onFormChange(f.copy(nivelActividad = it))
    }

    state.mensaje?.let {
        Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
    }
    KaloButton(
        text = if (state.guardando) "Guardando..." else "Guardar",
        onClick = onGuardar,
        enabled = !state.guardando,
    )
    KaloSecondaryButton(text = "Cancelar", onClick = onCancelar, enabled = !state.guardando)
}

@Composable
private fun InfoRow(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun <T> OpcionesRadio(
    titulo: String,
    opciones: List<T>,
    seleccionada: T,
    etiqueta: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column {
        Text(titulo, style = MaterialTheme.typography.titleMedium)
        opciones.forEach { opcion ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = opcion == seleccionada,
                        onClick = { onSelect(opcion) },
                        role = Role.RadioButton,
                    )
                    .padding(vertical = Spacing.xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = opcion == seleccionada, onClick = null)
                Text(
                    etiqueta(opcion),
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(start = Spacing.sm),
                )
            }
        }
    }
}

private val usuarioEjemplo = Usuario(
    id = 1, email = "daniel@kalo.com", nombre = "Daniel",
    fechaNacimiento = LocalDate.of(2000, 1, 15), sexo = Sexo.HOMBRE,
    nivelActividad = NivelActividad.MODERADO, altura = 178,
)
private val objetivoEjemplo = Objetivo(
    id = 1, usuarioId = 1, tipo = TipoObjetivo.MANTENER,
    metaKg = 75f, plazo = 12, kcalDiarias = 2400, aguaMeta = 2000,
)
private val estadoEjemplo = ProfileUiState(
    cargando = false, usuario = usuarioEjemplo, objetivo = objetivoEjemplo, pesoActualKg = 75f,
)

@Preview(name = "Perfil claro", showBackground = true)
@Composable
private fun ProfileLightPreview() {
    KaloTheme(darkTheme = false) { ProfileContent(estadoEjemplo, {}, {}, {}, {}) }
}

@Preview(name = "Edicion oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileEditDarkPreview() {
    KaloTheme(darkTheme = true) {
        ProfileContent(
            estadoEjemplo.copy(
                editando = true,
                form = ProfileFormState(
                    nombre = "Daniel", fechaNacimiento = "15/01/2000", altura = "178",
                    metaKg = "75", plazo = "12", aguaMeta = "2000",
                ),
            ),
            {}, {}, {}, {},
        )
    }
}

