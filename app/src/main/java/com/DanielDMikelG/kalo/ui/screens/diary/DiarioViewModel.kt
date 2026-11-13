package com.DanielDMikelG.kalo.ui.screens.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.repository.AguaRepository
import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.data.repository.UsuarioRepository
import com.DanielDMikelG.kalo.domain.model.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.EntradaComida
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import com.DanielDMikelG.kalo.domain.model.Usuario
import com.DanielDMikelG.kalo.domain.usecase.CalcularCaloriasUseCase
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class DiarioViewModel(
    private val usuarioId: Long,
    private val usuarios: UsuarioRepository,
    private val comidas: ComidaRepository,
    private val agua: AguaRepository,
    private val calcularCalorias: CalcularCaloriasUseCase = CalcularCaloriasUseCase(),
) : ViewModel() {

    private val fecha = MutableStateFlow(LocalDate.now())

    // Últimos datos del día mostrado, para localizar una entrada por su id al borrarla o editarla
    private var comidasDelDia: List<ComidaConEntradas> = emptyList()

    val estado: StateFlow<DiarioUiState> = fecha
        .flatMapLatest { dia ->
            combine(
                comidas.observarDia(usuarioId, dia),
                agua.observarVasos(usuarioId, dia),
                usuarios.observarUsuario(usuarioId),
                usuarios.observarUltimoPeso(usuarioId),
                usuarios.observarObjetivo(usuarioId),
            ) { lista, vasos, usuario, peso, objetivo ->
                comidasDelDia = lista
                construirEstado(dia, lista, vasos, usuario, peso, objetivo)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DiarioUiState())

    // ---- Navegación entre días ----

    fun diaAnterior() {
        fecha.value = fecha.value.minusDays(1)
    }

    fun diaSiguiente() {
        if (fecha.value.isBefore(LocalDate.now())) fecha.value = fecha.value.plusDays(1)
    }

    // ---- Agua ----

    fun anadirVaso() {
        viewModelScope.launch { agua.añadirVaso(usuarioId, fecha.value) }
    }

    fun quitarVaso() {
        viewModelScope.launch { agua.quitarVaso(usuarioId, fecha.value) }
    }

    // ---- Entradas ----

    fun borrarEntrada(entradaId: Long) {
        val entrada = buscarEntrada(entradaId) ?: return
        viewModelScope.launch { comidas.eliminarEntrada(entrada) }
    }

    fun editarCantidad(entradaId: Long, nuevosGramos: Float) {
        val entrada = buscarEntrada(entradaId) ?: return
        viewModelScope.launch { comidas.editarCantidad(entrada, nuevosGramos) }
    }

    private fun buscarEntrada(id: Long): EntradaComida? =
        comidasDelDia.flatMap { it.entradas }.map { it.entrada }.firstOrNull { it.id == id }

    // ---- Construcción del estado ----

    private fun construirEstado(
        dia: LocalDate,
        lista: List<ComidaConEntradas>,
        vasos: Int,
        usuario: Usuario?,
        peso: RegistroPeso?,
        objetivo: Objetivo?,
    ): DiarioUiState {
        val comidasUi = lista.toComidasUi()
        val consumidos = lista.toMacrosConsumidos()

        // Sin usuario, peso u objetivo todavía no hay meta: se muestra 0 en vez de inventarla
        val kcalObjetivo =
            if (usuario != null && peso != null && objetivo != null) {
                calcularCalorias(usuario, peso.pesoKg, objetivo).kcalObjetivo
            } else 0
        val metas = MetasDiarias.desdeKcal(kcalObjetivo)

        return DiarioUiState(
            fecha = dia,
            cargando = false,
            kcalObjetivo = kcalObjetivo,
            kcalConsumidas = comidasUi.sumOf { it.kcal },
            proteina = MacroDiario(consumidos.proteinaG, metas.proteinaG),
            carbohidratos = MacroDiario(consumidos.carbohidratosG, metas.carbohidratosG),
            grasa = MacroDiario(consumidos.grasaG, metas.grasaG),
            fibra = MacroDiario(consumidos.fibraG, metas.fibraG),
            comidas = comidasUi,
            vasos = vasos,
        )
    }

    class Factory(
        private val usuarioId: Long,
        private val db: KaloDatabase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DiarioViewModel(
                usuarioId = usuarioId,
                usuarios = UsuarioRepository(db),
                comidas = ComidaRepository(db),
                agua = AguaRepository(db),
            ) as T
    }
}

