package com.DanielDMikelG.kalo.ui.screens.anadir

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.AlimentoRepository
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.ResultadoBusqueda
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface BusquedaEnLinea {
    object Inactiva : BusquedaEnLinea
    object Buscando : BusquedaEnLinea
    data class Resultados(val alimentos: List<Alimento>) : BusquedaEnLinea

    object SinResultados : BusquedaEnLinea
    object SinConexion : BusquedaEnLinea
    data class Error(val mensaje: String) : BusquedaEnLinea
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AnadirComidaViewModel(private val repositorio: AlimentoRepository) : ViewModel() {

    private val _texto = MutableStateFlow("")
    val texto: StateFlow<String> = _texto.asStateFlow()

    /** Alimentos ya guardados en el dispositivo que coinciden con el texto. */
    val locales: StateFlow<List<Alimento>> = _texto
        .debounce(250)
        .flatMapLatest { repositorio.buscarPorNombre(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recientes: StateFlow<List<Alimento>> = repositorio.recientes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _enLinea = MutableStateFlow<BusquedaEnLinea>(BusquedaEnLinea.Inactiva)
    val enLinea: StateFlow<BusquedaEnLinea> = _enLinea.asStateFlow()

    fun cambiarTexto(nuevo: String) {
        _texto.value = nuevo.take(40)
        _enLinea.value = BusquedaEnLinea.Inactiva // los resultados de internet ya no corresponden
    }

    /** Solo se llama al pulsar el botón (o la lupa del teclado), para respetar el límite de la API. */
    fun buscarEnLinea() {
        val consulta = _texto.value.trim()
        if (consulta.length < MIN_CARACTERES || _enLinea.value is BusquedaEnLinea.Buscando) return
        _enLinea.value = BusquedaEnLinea.Buscando
        viewModelScope.launch {
            _enLinea.value = when (val r = repositorio.buscarEnLinea(consulta)) {
                is ResultadoBusqueda.Encontrados ->
                    if (r.alimentos.isEmpty()) BusquedaEnLinea.SinResultados
                    else BusquedaEnLinea.Resultados(r.alimentos)
                is ResultadoBusqueda.SinResultados -> BusquedaEnLinea.SinResultados
                is ResultadoBusqueda.SinConexion -> BusquedaEnLinea.SinConexion
                is ResultadoBusqueda.TextoCorto -> BusquedaEnLinea.Inactiva
                is ResultadoBusqueda.Error -> BusquedaEnLinea.Error(r.mensaje)
            }
        }
    }

    class Factory(private val repositorio: AlimentoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AnadirComidaViewModel(repositorio) as T
    }

    companion object {
        const val MIN_CARACTERES = 3
    }
}

