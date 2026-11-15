package com.DanielDMikelG.kalo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InicioViewModel(
    private val usuarios: UsuarioRepository,
    private val usuarioId: Long,
) : ViewModel() {

    private val _estado = MutableStateFlow(InicioUiState())
    val estado: StateFlow<InicioUiState> = _estado.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                usuarios.observarUsuario(usuarioId),
                usuarios.observarObjetivo(usuarioId),
            ) { usuario, objetivo -> usuario to objetivo }
                .collect { (usuario, objetivo) ->
                    _estado.update {
                        it.copy(
                            nombre = usuario?.nombre.orEmpty(),
                            kcalMeta = objetivo?.kcalDiarias ?: 0,
                        )
                    }
                }
        }

        // TODO (Mikel): conectar con tus repositorios de comidas y la racha:
        //  - kcalConsumidas y macros consumidos: suma de las entradas de hoy
        //    (ResumenDiarioUseCase ya lo calcula a partir de la lista de EntradaComida).
        //  - metas de macros: CalcularMacrosUseCase con las kcal y el objetivo del usuario.
        //  - racha: caso de uso de la issue "Racha de días".
        //  - estadoMascota: estadoMascota(consumidas, meta), la función de KaloMascot.
    }
}