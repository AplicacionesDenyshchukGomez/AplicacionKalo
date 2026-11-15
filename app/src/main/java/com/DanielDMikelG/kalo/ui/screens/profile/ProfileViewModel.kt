package com.DanielDMikelG.kalo.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.model.Usuario
import com.DanielDMikelG.kalo.domain.usecase.CalcularCaloriasUseCase
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.format.ResolverStyle
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val FORMATO_FECHA: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT)

internal fun LocalDate.formatear(): String = format(FORMATO_FECHA)

internal fun Float.sinDecimalesInnecesarios(): String =
    if (this % 1f == 0f) toInt().toString() else toString()

private fun String.aFloat(): Float? = trim().replace(',', '.').toFloatOrNull()

/** Lo que escribe el usuario en el formulario, como texto. */
data class ProfileFormState(
    val nombre: String = "",
    val fechaNacimiento: String = "",
    val altura: String = "",
    val metaKg: String = "",
    val plazo: String = "",
    val aguaMeta: String = "",
    val sexo: Sexo = Sexo.HOMBRE,
    val tipo: TipoObjetivo = TipoObjetivo.MANTENER,
    val nivelActividad: NivelActividad = NivelActividad.MODERADO,
)

data class ProfileErrors(
    val nombre: String? = null,
    val fechaNacimiento: String? = null,
    val altura: String? = null,
    val metaKg: String? = null,
    val plazo: String? = null,
    val aguaMeta: String? = null,
) {
    val hayErrores: Boolean
        get() = listOf(nombre, fechaNacimiento, altura, metaKg, plazo, aguaMeta).any { it != null }
}

data class ProfileUiState(
    val cargando: Boolean = true,
    val usuario: Usuario? = null,
    val objetivo: Objetivo? = null,
    val pesoActualKg: Float? = null,
    val editando: Boolean = false,
    val guardando: Boolean = false,
    val form: ProfileFormState = ProfileFormState(),
    val errores: ProfileErrors = ProfileErrors(),
    val mensaje: String? = null,
)

private fun formDe(usuario: Usuario, objetivo: Objetivo) = ProfileFormState(
    nombre = usuario.nombre,
    fechaNacimiento = usuario.fechaNacimiento.formatear(),
    altura = usuario.altura.toString(),
    metaKg = objetivo.metaKg.sinDecimalesInnecesarios(),
    plazo = objetivo.plazo.toString(),
    aguaMeta = objetivo.aguaMeta.toString(),
    sexo = usuario.sexo,
    tipo = objetivo.tipo,
    nivelActividad = usuario.nivelActividad,
)

class ProfileViewModel(
    private val repositorio: UsuarioRepository,
    private val usuarioId: Long,
    private val calcularCalorias: CalcularCaloriasUseCase = CalcularCaloriasUseCase(),
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repositorio.observarUsuario(usuarioId),
                repositorio.observarObjetivo(usuarioId),
                repositorio.observarUltimoPeso(usuarioId),
            ) { usuario, objetivo, peso -> Triple(usuario, objetivo, peso?.pesoKg) }
                .collect { (usuario, objetivo, peso) ->
                    // El formulario no se toca: si el usuario está editando, no perdemos lo que escribe
                    _uiState.update {
                        it.copy(cargando = false, usuario = usuario, objetivo = objetivo, pesoActualKg = peso)
                    }
                }
        }
    }

    fun empezarEdicion() {
        val estado = _uiState.value
        val usuario = estado.usuario ?: return
        val objetivo = estado.objetivo ?: return
        _uiState.update {
            it.copy(editando = true, form = formDe(usuario, objetivo), errores = ProfileErrors(), mensaje = null)
        }
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(editando = false, errores = ProfileErrors()) }
    }

    fun actualizarForm(form: ProfileFormState) {
        _uiState.update { it.copy(form = form) }
    }

    fun guardar() {
        val estado = _uiState.value
        val actual = estado.usuario ?: return
        val objetivoActual = estado.objetivo ?: return
        if (estado.guardando) return
        val f = estado.form

        val nombre = f.nombre.trim()
        val fecha = try {
            LocalDate.parse(f.fechaNacimiento.trim(), FORMATO_FECHA)
        } catch (e: DateTimeParseException) {
            null
        }
        val edad = fecha?.let { Period.between(it, LocalDate.now()).years }
        val altura = f.altura.trim().toIntOrNull()
        val metaKg = f.metaKg.aFloat()
        val plazo = f.plazo.trim().toIntOrNull()
        val aguaMeta = f.aguaMeta.trim().toIntOrNull()

        val errores = ProfileErrors(
            nombre = if (nombre.isEmpty() || nombre.length > 40) "Escribe un nombre (máximo 40 letras)" else null,
            fechaNacimiento = if (edad == null || edad !in 14..100) "Fecha válida (dd/mm/aaaa), entre 14 y 100 años" else null,
            altura = if (altura == null || altura !in 100..250) "Altura entre 100 y 250 cm" else null,
            metaKg = if (metaKg == null || metaKg !in 30f..300f) "Meta entre 30 y 300 kg" else null,
            plazo = if (plazo == null || plazo !in 1..104) "Plazo entre 1 y 104 semanas" else null,
            aguaMeta = if (aguaMeta == null || aguaMeta !in 500..6000) "Entre 500 y 6000 ml" else null,
        )
        _uiState.update { it.copy(errores = errores) }
        if (errores.hayErrores || fecha == null || altura == null ||
            metaKg == null || plazo == null || aguaMeta == null
        ) return

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, mensaje = null) }
            try {
                if (nombre != actual.nombre && repositorio.existeNombre(nombre)) {
                    _uiState.update {
                        it.copy(guardando = false, errores = errores.copy(nombre = "Ese nombre ya está en uso"))
                    }
                    return@launch
                }
                // El email no se edita aquí: se conserva el del usuario cargado
                val usuario = actual.copy(
                    nombre = nombre,
                    fechaNacimiento = fecha,
                    sexo = f.sexo,
                    nivelActividad = f.nivelActividad,
                    altura = altura,
                )
                var objetivo = objetivoActual.copy(
                    tipo = f.tipo,
                    metaKg = metaKg,
                    plazo = plazo,
                    aguaMeta = aguaMeta,
                )
                // Recalcula las kcal si hay un peso registrado; si no, deja las que había
                estado.pesoActualKg?.let { peso ->
                    objetivo = objetivo.copy(kcalDiarias = calcularCalorias(usuario, peso, objetivo).kcalObjetivo)
                }
                repositorio.actualizarPerfil(usuario, objetivo)
                _uiState.update { it.copy(guardando = false, editando = false, mensaje = "Perfil guardado") }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(guardando = false, mensaje = "No se pudo guardar. Inténtalo de nuevo.") }
            }
        }
    }
}

