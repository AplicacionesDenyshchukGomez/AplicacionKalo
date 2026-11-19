package com.DanielDMikelG.kalo.ui.screens.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.model.Usuario
import com.DanielDMikelG.kalo.domain.usecase.CalcularCaloriasUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

// Acepta "70,5" y "70.5", porque el teclado en español suele dar coma
fun String.aNumero(): Double? = replace(',', '.').toDoubleOrNull()

data class RegistroUiState(
    val nombre: String = "",
    val email: String = "",
    val fechaNacimiento: String = "",
    val sexo: Sexo? = null,
    val nivelActividad: NivelActividad? = null,
    val peso: String = "",
    val altura: String = "",
    val tipoObjetivo: TipoObjetivo? = null,
    val metaKg: String = "",
    val plazo: String = "",
    val error: String? = null,
    val guardando: Boolean = false
) {
    val avisoDatosPersonales: String?
        get() = when {
            nombre.isBlank() -> "Escribe un nombre de usuario"
            !email.contains("@") -> "El correo no es válido"
            !runCatching { LocalDate.parse(fechaNacimiento).isBefore(LocalDate.now()) }
                .getOrDefault(false) -> "La fecha debe ser AAAA-MM-DD, por ejemplo 2000-05-14"
            sexo == null -> "Elige tu sexo"
            nivelActividad == null -> "Elige tu nivel de actividad"
            peso.aNumero() == null -> "Escribe tu peso en kg (por ejemplo 70 o 70,5)"
            altura.toIntOrNull() == null -> "La altura va en cm, sin decimales (por ejemplo 175)"
            else -> null
        }

    val datosPersonalesValidos: Boolean
        get() = avisoDatosPersonales == null

    // Para adelgazar la meta debe ser menor que el peso actual, y para ganar masa, mayor
    val objetivoValido: Boolean
        get() {
            val tipo = tipoObjetivo ?: return false
            if (tipo == TipoObjetivo.MANTENER) return true
            val meta = metaKg.aNumero() ?: return false
            val semanas = plazo.toIntOrNull() ?: return false
            val pesoActual = peso.aNumero() ?: return false
            if (semanas < 1) return false
            return when (tipo) {
                TipoObjetivo.ADELGAZAR -> meta < pesoActual
                TipoObjetivo.GANAR_MASA -> meta > pesoActual
                TipoObjetivo.MANTENER -> true
            }
        }

    // Mensaje opcional para mostrar en la pantalla de objetivo
    val avisoObjetivo: String?
        get() {
            val meta = metaKg.aNumero() ?: return null
            val pesoActual = peso.aNumero() ?: return null
            return when (tipoObjetivo) {
                TipoObjetivo.ADELGAZAR ->
                    if (meta >= pesoActual) "Para adelgazar, tu peso objetivo debe ser menor que tu peso actual" else null
                TipoObjetivo.GANAR_MASA ->
                    if (meta <= pesoActual) "Para ganar masa, tu peso objetivo debe ser mayor que tu peso actual" else null
                else -> null
            }
        }
}

class RegistroViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UsuarioRepository(KaloDatabase.obtener(app))
    private val sesion = SesionManager(app)
    private val calcularCalorias = CalcularCaloriasUseCase()

    private val _estado = MutableStateFlow(RegistroUiState())
    val estado: StateFlow<RegistroUiState> = _estado.asStateFlow()

    fun actualizar(cambio: (RegistroUiState) -> RegistroUiState) {
        _estado.update { cambio(it).copy(error = null) }
    }

    // Paso 1 -> 2: comprueba que el correo y el nombre no estén ya cogidos
    fun continuarAObjetivo(onValido: () -> Unit) {
        val datos = _estado.value
        viewModelScope.launch {
            when {
                repo.existeEmail(datos.email.trim()) ->
                    _estado.update { it.copy(error = "Ya existe una cuenta con ese correo") }
                repo.existeNombre(datos.nombre.trim()) ->
                    _estado.update { it.copy(error = "Ese nombre de usuario ya está en uso") }
                else -> onValido()
            }
        }
    }

    // Paso final: guarda usuario, objetivo y primer pesaje
    fun finalizar(onExito: () -> Unit) {
        val datos = _estado.value
        if (!datos.datosPersonalesValidos || !datos.objetivoValido) return
        val tipo = datos.tipoObjetivo ?: return
        val pesoKg = datos.peso.aNumero()?.toFloat() ?: return

        val usuario = Usuario(
            email = datos.email.trim(),
            nombre = datos.nombre.trim(),
            fechaNacimiento = LocalDate.parse(datos.fechaNacimiento),
            sexo = datos.sexo ?: return,
            nivelActividad = datos.nivelActividad ?: return,
            altura = datos.altura.toIntOrNull() ?: return
        )

        val mantener = tipo == TipoObjetivo.MANTENER
        val objetivoBase = Objetivo(
            usuarioId = 0,   // se rellena en el repositorio con el id real
            tipo = tipo,
            metaKg = if (mantener) pesoKg else datos.metaKg.aNumero()?.toFloat() ?: return,
            plazo = if (mantener) 0 else datos.plazo.toIntOrNull() ?: return,
            kcalDiarias = null,
            aguaMeta = AGUA_META_ML
        )
        val kcal = calcularCalorias(usuario, pesoKg, objetivoBase).kcalObjetivo
        val objetivo = objetivoBase.copy(kcalDiarias = kcal)

        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, error = null) }
            try {
                val id = repo.registrar(usuario, objetivo, pesoKg)
                sesion.guardarUsuario(id)
                _estado.value = RegistroUiState()   // limpia el formulario
                onExito()
            } catch (e: Exception) {
                _estado.update {
                    it.copy(guardando = false, error = "No se pudo crear la cuenta. Inténtalo de nuevo.")
                }
            }
        }
    }

    private companion object {
        const val AGUA_META_ML = 2000
    }
}