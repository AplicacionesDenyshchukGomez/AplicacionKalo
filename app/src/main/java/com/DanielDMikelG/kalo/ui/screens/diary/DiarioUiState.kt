package com.DanielDMikelG.kalo.ui.screens.diary

import com.DanielDMikelG.kalo.domain.model.TipoComida
import java.time.LocalDate

/** Una línea del diario: "Yogur natural · 240 g · 144 kcal". */
data class EntradaUi(
    val id: Long,
    val nombre: String,
    val cantidadG: Int,
    val kcal: Int,
)

/** Una comida del día (desayuno, comida...) con sus entradas. Puede estar vacía. */
data class ComidaUi(
    val tipo: TipoComida,
    val entradas: List<EntradaUi> = emptyList(),
) {
    val kcal: Int get() = entradas.sumOf { it.kcal }
    val vacia: Boolean get() = entradas.isEmpty()
}

data class MacroDiario(
    val consumidoG: Int = 0,
    val objetivoG: Int = 0,
) {
    /** De 0 a 1, para la barra. Sin objetivo, la barra queda vacía. */
    val progreso: Float
        get() = if (objetivoG <= 0) 0f else (consumidoG.toFloat() / objetivoG).coerceIn(0f, 1f)
}

data class DiarioUiState(
    val fecha: LocalDate = LocalDate.now(),
    val cargando: Boolean = true,

    // Calorías
    val kcalObjetivo: Int = 0,
    val kcalConsumidas: Int = 0,

    // Macros
    val proteina: MacroDiario = MacroDiario(),
    val carbohidratos: MacroDiario = MacroDiario(),
    val grasa: MacroDiario = MacroDiario(),
    val fibra: MacroDiario = MacroDiario(),

    // Siempre las cuatro comidas, en orden, aunque estén vacías
    val comidas: List<ComidaUi> = TipoComida.entries.map { ComidaUi(it) },

    // Agua
    val vasos: Int = 0,
    val vasosObjetivo: Int = VASOS_OBJETIVO_POR_DEFECTO,
) {
    val kcalRestantes: Int get() = kcalObjetivo - kcalConsumidas
    val excedido: Boolean get() = kcalObjetivo > 0 && kcalConsumidas > kcalObjetivo
    val progresoKcal: Float
        get() = if (kcalObjetivo <= 0) 0f
        else (kcalConsumidas.toFloat() / kcalObjetivo).coerceIn(0f, 1f)

    val esHoy: Boolean get() = fecha == LocalDate.now()
    /** No se puede navegar al futuro. */
    val puedeAvanzar: Boolean get() = fecha.isBefore(LocalDate.now())

    val progresoAgua: Float
        get() = if (vasosObjetivo <= 0) 0f else (vasos.toFloat() / vasosObjetivo).coerceIn(0f, 1f)

    companion object {
        const val VASOS_OBJETIVO_POR_DEFECTO = 8

        /** Datos de ejemplo para diseñar la pantalla y los @Preview sin ViewModel. */
        fun ejemplo() = DiarioUiState(
            cargando = false,
            kcalObjetivo = 2100,
            kcalConsumidas = 1240,
            proteina = MacroDiario(62, 130),
            carbohidratos = MacroDiario(140, 240),
            grasa = MacroDiario(38, 70),
            fibra = MacroDiario(14, 30),
            comidas = listOf(
                ComidaUi(
                    TipoComida.DESAYUNO,
                    listOf(
                        EntradaUi(1, "Yogur natural (Danone)", 240, 144),
                        EntradaUi(2, "Avena", 40, 152),
                    ),
                ),
                ComidaUi(
                    TipoComida.COMIDA,
                    listOf(EntradaUi(3, "Arroz con pollo", 350, 520)),
                ),
                ComidaUi(TipoComida.CENA),
                ComidaUi(
                    TipoComida.SNACK,
                    listOf(EntradaUi(4, "Manzana", 180, 94)),
                ),
            ),
            vasos = 5,
        )
    }
}