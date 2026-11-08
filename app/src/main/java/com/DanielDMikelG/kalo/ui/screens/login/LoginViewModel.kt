package com.DanielDMikelG.kalo.ui.screens.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val usuario: String = "",
    val error: String? = null,
    val cargando: Boolean = false
)

class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = UsuarioRepository(KaloDatabase.obtener(app))
    private val sesion = SesionManager(app)

    private val _estado = MutableStateFlow(LoginUiState())
    val estado: StateFlow<LoginUiState> = _estado.asStateFlow()

    fun onUsuarioChange(texto: String) {
        _estado.update { it.copy(usuario = texto, error = null) }
    }

    fun acceder(onExito: () -> Unit) {
        val texto = _estado.value.usuario.trim()
        if (texto.isBlank()) return

        viewModelScope.launch {
            _estado.update { it.copy(cargando = true) }
            val usuario = repo.buscarPorEmailONombre(texto)
            if (usuario == null) {
                _estado.update {
                    it.copy(cargando = false, error = "No existe ningún usuario con ese correo o nombre")
                }
            } else {
                sesion.guardarUsuario(usuario.id)
                _estado.update { it.copy(cargando = false) }
                onExito()
            }
        }
    }
}