package com.DanielDMikelG.kalo.ui.screens.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.PesoRepository
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import com.DanielDMikelG.kalo.domain.usecase.ResultadoPeso
import com.DanielDMikelG.kalo.domain.usecase.ValidarPesoUseCase
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioPeso(
    val texto: String = "",
    val error: String? = null,
    val guardando: Boolean = false,
)

class AnadirPesoViewModel(
    private val usuarioId: Long,
    private val pesos: PesoRepository,
    private val validar: ValidarPesoUseCase = ValidarPesoUseCase(),
) : ViewModel() {

    val ultimo: StateFlow<RegistroPeso?> = pesos.observarUltimo(usuarioId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _formulario = MutableStateFlow(FormularioPeso())
    val formulario: StateFlow<FormularioPeso> = _formulario.asStateFlow()

    init {
        // El campo empieza con el último peso, para ajustarlo en vez de escribirlo desde cero
        viewModelScope.launch {
            val anterior = pesos.observarUltimo(usuarioId).first() ?: return@launch
            _formulario.update { if (it.texto.isBlank()) it.copy(texto = formatear(anterior.pesoKg)) else it }
        }
    }

    fun cambiarTexto(texto: String) {
        _formulario.update { it.copy(texto = texto, error = null) }
    }

    fun guardar(alGuardar: () -> Unit) {
        val actual = _formulario.value
        if (actual.guardando) return
        when (val r = validar(actual.texto)) {
            is ResultadoPeso.Invalido -> _formulario.value = actual.copy(error = r.mensaje)
            is ResultadoPeso.Valido -> {
                _formulario.value = actual.copy(guardando = true, error = null)
                viewModelScope.launch {
                    try {
                        pesos.registrarPeso(usuarioId, r.kg)
                        alGuardar()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        _formulario.update {
                            it.copy(guardando = false, error = "No se ha podido guardar. Inténtalo de nuevo.")
                        }
                    }
                }
            }
        }
    }

    private fun formatear(kg: Float): String =
        String.format(Locale.forLanguageTag("es-ES"), "%.1f", kg)

    class Factory(
        private val usuarioId: Long,
        private val pesos: PesoRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnadirPesoViewModel(usuarioId, pesos) as T
    }
}