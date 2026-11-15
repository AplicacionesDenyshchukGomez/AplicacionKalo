package com.DanielDMikelG.kalo.ui.screens.anadir

import androidx.lifecycle.ViewModel
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.TipoComida
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SeleccionComida(
    val tipo: TipoComida = TipoComida.DESAYUNO,
    val fecha: LocalDate = LocalDate.now(),
    val alimento: Alimento? = null,
)

/**
 * Lleva entre pantallas qué comida se está rellenando (desayuno, comida...), de qué día
 * y qué alimento se ha elegido. Se crea una sola vez en KaloApp y lo comparten todas.
 */
class SeleccionComidaViewModel : ViewModel() {
    private val _estado = MutableStateFlow(SeleccionComida())
    val estado: StateFlow<SeleccionComida> = _estado.asStateFlow()

    fun empezar(tipo: TipoComida, fecha: LocalDate) {
        _estado.value = SeleccionComida(tipo = tipo, fecha = fecha)
    }

    fun elegirAlimento(alimento: Alimento) {
        _estado.update { it.copy(alimento = alimento) }
    }
}

