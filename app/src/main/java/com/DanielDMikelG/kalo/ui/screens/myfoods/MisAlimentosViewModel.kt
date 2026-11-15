package com.DanielDMikelG.kalo.ui.screens.myfoods

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.AlimentoRepository
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.usecase.ResultadoAlimentoManual
import com.DanielDMikelG.kalo.domain.usecase.ValidarAlimentoManualUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class FormularioAlimento(
    val nombre: String = "",
    val kcal: String = "",
    val prot: String = "",
    val carb: String = "",
    val grasa: String = "",
    val fibra: String = "",
    val error: String? = null,
    val guardando: Boolean = false,
)

class MisAlimentosViewModel(
    private val repositorio: AlimentoRepository,
    private val validar: ValidarAlimentoManualUseCase = ValidarAlimentoManualUseCase(),
) : ViewModel() {

    val alimentos: StateFlow<List<Alimento>> = repositorio.listarManuales()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _formulario = MutableStateFlow<FormularioAlimento?>(null)
    /** null = se ve la lista; no null = se ve el formulario de alimento nuevo. */
    val formulario: StateFlow<FormularioAlimento?> = _formulario.asStateFlow()

    fun nuevo() { _formulario.value = FormularioAlimento() }

    fun cancelar() { _formulario.value = null }

    fun editar(cambio: (FormularioAlimento) -> FormularioAlimento) {
        val actual = _formulario.value ?: return
        _formulario.value = cambio(actual).copy(error = null)
    }

    /** Guarda el alimento; si todo va bien cierra el formulario y avisa con el alimento ya guardado. */
    fun guardar(alCrear: (Alimento) -> Unit) {
        val f = _formulario.value ?: return
        if (f.guardando) return
        when (val r = validar(f.nombre, f.kcal, f.prot, f.carb, f.grasa, f.fibra)) {
            is ResultadoAlimentoManual.Invalido -> _formulario.value = f.copy(error = r.mensaje)
            is ResultadoAlimentoManual.Valido -> {
                _formulario.value = f.copy(guardando = true)
                viewModelScope.launch {
                    val id = repositorio.crearManual(r.alimento)
                    _formulario.value = null
                    alCrear(r.alimento.copy(id = id))
                }
            }
        }
    }

    class Factory(private val repositorio: AlimentoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            MisAlimentosViewModel(repositorio) as T
    }
}