package com.DanielDMikelG.kalo.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.contenido.CatalogoCocina
import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.domain.model.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import com.DanielDMikelG.kalo.domain.model.Usuario
import com.DanielDMikelG.kalo.domain.usecase.CalcularCaloriasUseCase
import com.DanielDMikelG.kalo.domain.usecase.CalcularRachaUseCase
import com.DanielDMikelG.kalo.domain.usecase.ElegirRecetaDelDiaUseCase
import com.DanielDMikelG.kalo.ui.components.EstadoMascota
import com.DanielDMikelG.kalo.ui.components.estadoMascota
import com.DanielDMikelG.kalo.ui.screens.diary.MetasDiarias
import com.DanielDMikelG.kalo.ui.screens.diary.toComidasUi
import com.DanielDMikelG.kalo.ui.screens.diary.toMacrosConsumidos
import java.time.LocalDate
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class InicioViewModel(
    private val usuarios: UsuarioRepository,
    private val usuarioId: Long,
    private val comidas: ComidaRepository,
    private val calcularCalorias: CalcularCaloriasUseCase = CalcularCaloriasUseCase(),
    private val calcularRacha: CalcularRachaUseCase = CalcularRachaUseCase(),
    ) : ViewModel() {

    val estado: StateFlow<InicioUiState> = combine(
        comidas.observarDia(usuarioId, LocalDate.now()),
        usuarios.observarUsuario(usuarioId),
        usuarios.observarUltimoPeso(usuarioId),
        usuarios.observarObjetivo(usuarioId),
        comidas.observarFechasConRegistro(usuarioId),
    ) { lista, usuario, peso, objetivo, fechas ->
        construirEstado(lista, usuario, peso, objetivo, calcularRacha(fechas))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), InicioUiState())
    private fun construirEstado(
        lista: List<ComidaConEntradas>,
        usuario: Usuario?,
        peso: RegistroPeso?,
        objetivo: Objetivo?,
        racha: Int,
    ): InicioUiState {
        // Mismas cuentas que el Diario, para que los números coincidan en las dos pantallas
        val kcalMeta =
            if (usuario != null && peso != null && objetivo != null) {
                calcularCalorias(usuario, peso.pesoKg, objetivo).kcalObjetivo
            } else 0
        val metas = MetasDiarias.desdeKcal(kcalMeta)
        val consumidos = lista.toMacrosConsumidos()
        val kcalConsumidas = lista.toComidasUi().sumOf { it.kcal }

        return InicioUiState(
            nombre = usuario?.nombre.orEmpty(),
            kcalConsumidas = kcalConsumidas,
            kcalMeta = kcalMeta,
            proteina = MacroProgreso(consumidos.proteinaG, metas.proteinaG),
            grasa = MacroProgreso(consumidos.grasaG, metas.grasaG),
            carbohidratos = MacroProgreso(consumidos.carbohidratosG, metas.carbohidratosG),
            fibra = MacroProgreso(consumidos.fibraG, metas.fibraG),
            estadoMascota = if (kcalMeta <= 0) EstadoMascota.CONTENTA
            else estadoMascota(kcalConsumidas.toFloat(), kcalMeta.toFloat()),
            recetaDelDia = ElegirRecetaDelDiaUseCase()(CatalogoCocina.recetas)?.titulo,
            racha = racha,
        )
    }
}