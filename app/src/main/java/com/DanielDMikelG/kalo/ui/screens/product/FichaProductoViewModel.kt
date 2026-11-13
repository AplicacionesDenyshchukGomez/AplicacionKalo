package com.DanielDMikelG.kalo.ui.screens.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.AlimentoRepository
import com.DanielDMikelG.kalo.domain.model.ResultadoAlimento
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ModoFicha { CONSULTA, ANADIR }

class FichaProductoViewModel(
    private val repositorio: AlimentoRepository,
    private val codigo: String,
    val modo: ModoFicha,
) : ViewModel() {

    /** null = cargando. */
    private val _estado = MutableStateFlow<ResultadoAlimento?>(null)
    val estado: StateFlow<ResultadoAlimento?> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        _estado.value = null
        viewModelScope.launch {
            _estado.value = repositorio.buscarPorCodigo(codigo)
        }
    }

    class Factory(
        private val repositorio: AlimentoRepository,
        private val codigo: String,
        private val modo: ModoFicha,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            FichaProductoViewModel(repositorio, codigo, modo) as T
    }
}