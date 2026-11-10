package com.DanielDMikelG.kalo.ui.screens.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.RachaRepository
import com.DanielDMikelG.kalo.domain.model.Racha
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val usuarioId = SesionManager(app).usuarioActual()
    private val repo = RachaRepository(KaloDatabase.obtener(app))

    val racha: StateFlow<Racha?> =
        (usuarioId?.let { repo.observarRacha(it) } ?: flowOf<Racha?>(null))
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}