package com.DanielDMikelG.kalo.ui.screens.anadir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.domain.usecase.CalcularNutrientesRacionUseCase
import com.DanielDMikelG.kalo.domain.usecase.NutrientesRacion
import java.time.LocalDate
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CantidadUiState(
    val texto: String,
    val nutrientes: NutrientesRacion?,
    val guardando: Boolean = false,
    val error: String? = null,
)

class CantidadViewModel(
    private val comidas: ComidaRepository,
    private val usuarioId: Long,
    val alimento: Alimento,
    private val tipo: TipoComida,
    private val fecha: LocalDate,
    private val calcular: CalcularNutrientesRacionUseCase = CalcularNutrientesRacionUseCase(),
) : ViewModel() {

    private val _estado = MutableStateFlow(conTexto("100"))
    val estado: StateFlow<CantidadUiState> = _estado.asStateFlow()

    private fun conTexto(texto: String) = CantidadUiState(
        texto = texto,
        nutrientes = texto.toFloatOrNull()?.let { calcular(alimento, it) },
    )

    fun cambiarTexto(texto: String) {
        _estado.update { conTexto(texto).copy(guardando = it.guardando) }
    }

    fun guardar(alGuardar: () -> Unit) {
        val actual = _estado.value
        val gramos = actual.texto.toFloatOrNull() ?: return
        if (actual.nutrientes == null || actual.guardando) return

        _estado.value = actual.copy(guardando = true, error = null)
        viewModelScope.launch {
            try {
                comidas.añadirAlimento(usuarioId, tipo, alimento, gramos, fecha)
                alGuardar()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _estado.update { it.copy(guardando = false, error = "No se ha podido guardar. Inténtalo de nuevo.") }
            }
        }
    }

    class Factory(
        private val comidas: ComidaRepository,
        private val usuarioId: Long,
        private val alimento: Alimento,
        private val tipo: TipoComida,
        private val fecha: LocalDate,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            CantidadViewModel(comidas, usuarioId, alimento, tipo, fecha) as T
    }
}