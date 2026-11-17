package com.DanielDMikelG.kalo.ui.screens.profile

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
import com.DanielDMikelG.kalo.domain.usecase.CalcularMetaUseCase
import com.DanielDMikelG.kalo.ui.components.EstadoMascota
import com.DanielDMikelG.kalo.ui.components.KaloButton
import com.DanielDMikelG.kalo.ui.components.KaloCard
import com.DanielDMikelG.kalo.ui.components.KaloIconoRedondo
import com.DanielDMikelG.kalo.ui.components.KaloMascot
import com.DanielDMikelG.kalo.ui.components.KaloSecondaryButton
import com.DanielDMikelG.kalo.ui.components.KaloTextField
import com.DanielDMikelG.kalo.ui.theme.KaloTheme
import com.DanielDMikelG.kalo.ui.theme.Spacing
import java.time.LocalDate
import java.util.Locale
import kotlin.math.abs
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.DanielDMikelG.kalo.ui.components.TecladoKalo
import com.DanielDMikelG.kalo.util.EntradaNumerica

/**
 * Punto de entrada: es lo que se llama desde la navegación.
 *
 * [editarAlEntrar] lo pone Ajustes ("Mi perfil") para que al volver se abra el formulario;
 * cuando ya se ha abierto avisamos con [onEdicionIniciada] para que no se repita.
 */
@Composable
fun ProfileRoute(
    onAbrirAjustes: () -> Unit,
    modifier: Modifier = Modifier,
    editarAlEntrar: Boolean = false,
    onEdicionIniciada: () -> Unit = {},
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

    // La señal se consume una sola vez, aunque quien la pone tarde en limpiarla
    var pendiente by remember(editarAlEntrar) { mutableStateOf(editarAlEntrar) }
    LaunchedEffect(pendiente, state.usuario, state.objetivo) {
        if (pendiente && state.usuario != null && state.objetivo != null) {
            pendiente = false
            onEdicionIniciada()
            viewModel.empezarEdicion()
        }
    }

    ProfileContent(
        state = state,
        onCancelar = viewModel::cancelarEdicion,
        onGuardar = viewModel::guardar,
        onFormChange = viewModel::actualizarForm,
        onAbrirAjustes = onAbrirAjustes,
        modifier = modifier,
    )
}

@Composable
fun ProfileContent(
    state: ProfileUiState,
    onCancelar: () -> Unit,
    onGuardar: () -> Unit,
    onFormChange: (ProfileFormState) -> Unit,
    onAbrirAjustes: () -> Unit = {},
    modifier: Modifier = Modifier,

) {
    BackHandler(enabled = state.editando, onBack = onCancelar)
    Surface(color = MaterialTheme.colorScheme.background, modifier = modifier) {
        val usuario = state.usuario
        val objetivo = state.objetivo
        when {
            state.cargando -> TextoCentrado("Cargando...")
            usuario == null || objetivo == null -> TextoCentrado("No se encontró el perfil.")
            state.editando -> Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .statusBarsPadding()
                    .padding(Spacing.md),
                verticalArrangement = Arrangement.spacedBy(Spacing.md),
            ) {
                Text("Editar perfil", style = MaterialTheme.typography.headlineMedium)
                FormContent(state, onGuardar, onCancelar, onFormChange)
            }
            else -> InfoContent(state, usuario, objetivo, onAbrirAjustes)
        }
    }
}

@Composable
private fun TextoCentrado(texto: String) {
    Text(
        texto,
        style = MaterialTheme.typography.bodyLarge,
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(Spacing.md),
    )
}

@Composable
private fun InfoContent(
    state: ProfileUiState,
    usuario: Usuario,
    objetivo: Objetivo,
    onAbrirAjustes: () -> Unit,
) {
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        CabeceraPerfil(usuario, objetivo, onAbrirAjustes)

        Column(
            modifier = Modifier.padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            state.mensaje?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            TarjetaPeso(state.pesoActualKg, objetivo.metaKg)
            state.pesoActualKg?.let { TarjetaMeta(it, objetivo) }

            KaloCard {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                    Text("Tus datos", style = MaterialTheme.typography.titleMedium)
                    InfoRow("Email", usuario.email)
                    InfoRow("Nacimiento", "${usuario.fechaNacimiento.formatear()} (${usuario.edad()} años)")
                    InfoRow("Sexo", usuario.sexo.etiqueta)
                    InfoRow("Altura", "${usuario.altura} cm")
                    InfoRow("Actividad", usuario.nivelActividad.etiqueta)
                    InfoRow("Meta de agua", "${objetivo.aguaMeta} ml")
                }
            }
        }
    }
}

@Composable
private fun CabeceraPerfil(usuario: Usuario, objetivo: Objetivo, onAbrirAjustes: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .statusBarsPadding()
                .padding(start = 20.dp, top = 24.dp, end = 20.dp, bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text("Perfil", style = MaterialTheme.typography.headlineLarge)
                KaloIconoRedondo(Icons.Default.Settings, "Abrir ajustes", onAbrirAjustes)
            }

            Box(
                modifier = Modifier
                    .size(128.dp)
                    .background(MaterialTheme.colorScheme.background, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                KaloMascot(estado = EstadoMascota.CONTENTA, tamano = 96.dp)
            }

            Text(usuario.nombre, style = MaterialTheme.typography.headlineMedium)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Pastilla(objetivo.tipo.etiqueta)
                Pastilla(
                    texto = objetivo.kcalDiarias?.let { "$it kcal al día" } ?: "Kcal pendientes",
                    borde = BorderStroke(2.dp, Color(0xFFFFD966)),
                )
            }
        }
    }
}

@Composable
private fun Pastilla(texto: String, borde: BorderStroke? = null) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = borde,
    ) {
        Text(
            texto,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun TarjetaPeso(pesoActualKg: Float?, metaKg: Float) {
    KaloCard {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text("Tu peso", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                TilePeso(
                    etiqueta = "Actual",
                    valor = pesoActualKg?.aTexto() ?: "—",
                    fondo = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.weight(1f),
                )
                TilePeso(
                    etiqueta = "Objetivo",
                    valor = metaKg.aTexto(),
                    fondo = Color(0xFFDDF0FF), // azul pastel (mismo que los carbohidratos)
                    modifier = Modifier.weight(1f),
                )
            }
            if (pesoActualKg == null) {
                Text(
                    "Aún no has registrado tu peso.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun TilePeso(etiqueta: String, valor: String, fondo: Color, modifier: Modifier = Modifier) {
    Surface(color = fondo, shape = MaterialTheme.shapes.medium, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(etiqueta, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(valor, style = MaterialTheme.typography.headlineSmall)
            Text("kg", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun TarjetaMeta(pesoActualKg: Float, objetivo: Objetivo) {
    val meta = remember(pesoActualKg, objetivo.metaKg, objetivo.plazo) {
        // El caso de uso lanza excepción con datos no válidos (por ejemplo, plazo 0):
        // en ese caso no mostramos la tarjeta en vez de tirar la app.
        if (objetivo.plazo < 1) {
            null
        } else {
            try {
                CalcularMetaUseCase()(pesoActualKg, objetivo.metaKg, objetivo.plazo)
            } catch (e: IllegalArgumentException) {
                null
            }
        }
    } ?: return

    KaloCard {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Text("Tu meta", style = MaterialTheme.typography.titleMedium)
            InfoRow("Ritmo semanal", "${abs(meta.kgPorSemana).aTexto(2)} kg / semana")
            InfoRow("Plazo", "${objetivo.plazo} semanas")
            // Si NivelRitmo tiene su propia etiqueta, úsala aquí en vez del nombre del enum
            InfoRow("Ritmo", meta.nivel.name.lowercase().replace('_', ' ').replaceFirstChar { it.uppercase() })
        }
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
    KaloTextField(f.altura, { onFormChange(f.copy(altura = EntradaNumerica.entero(it, 3))) }, "Altura (cm)", error = e.altura, keyboardOptions = TecladoKalo.Entero)
    KaloTextField(f.metaKg, { onFormChange(f.copy(metaKg = EntradaNumerica.decimal(it))) }, "Meta de peso (kg)", error = e.metaKg, keyboardOptions = TecladoKalo.Decimal)
    KaloTextField(f.plazo, { onFormChange(f.copy(plazo = EntradaNumerica.entero(it, 3))) }, "Plazo (semanas)", error = e.plazo, keyboardOptions = TecladoKalo.Entero)
    KaloTextField(f.aguaMeta, { onFormChange(f.copy(aguaMeta = EntradaNumerica.entero(it, 4))) }, "Meta de agua (ml)", error = e.aguaMeta, keyboardOptions = TecladoKalo.Entero)

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
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(etiqueta, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(valor, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.End)
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

private fun Float.aTexto(decimales: Int = 1): String =
    String.format(Locale.getDefault(), "%.${decimales}f", this)

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
    cargando = false, usuario = usuarioEjemplo, objetivo = objetivoEjemplo, pesoActualKg = 78.4f,
)

@Preview(name = "Perfil claro", showBackground = true, heightDp = 900)
@Composable
private fun ProfileLightPreview() {
    KaloTheme(darkTheme = false) { ProfileContent(estadoEjemplo, {}, {}, {}) }
}

@Preview(name = "Perfil oscuro", showBackground = true, heightDp = 900, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ProfileDarkPreview() {
    KaloTheme(darkTheme = true) { ProfileContent(estadoEjemplo, {}, {}, {}) }
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
            {}, {}, {},
        )
    }
}

