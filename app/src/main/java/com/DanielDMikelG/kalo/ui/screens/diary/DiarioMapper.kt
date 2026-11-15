package com.DanielDMikelG.kalo.ui.screens.diary

import com.DanielDMikelG.kalo.domain.model.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.TipoComida
import kotlin.math.roundToInt

/** Gramos consumidos en el día, ya redondeados para mostrar. */
data class MacrosConsumidos(
    val proteinaG: Int,
    val carbohidratosG: Int,
    val grasaG: Int,
    val fibraG: Int,
)

/** Metas diarias de macros a partir de las kcal objetivo. Reparto orientativo 25/50/25. */
data class MetasDiarias(
    val proteinaG: Int,
    val carbohidratosG: Int,
    val grasaG: Int,
    val fibraG: Int,
) {
    companion object {
        const val FIBRA_G = 30

        fun desdeKcal(kcal: Int): MetasDiarias =
            if (kcal <= 0) MetasDiarias(0, 0, 0, 0)
            else MetasDiarias(
                proteinaG = (kcal * 0.25f / 4f).roundToInt(),
                carbohidratosG = (kcal * 0.50f / 4f).roundToInt(),
                grasaG = (kcal * 0.25f / 9f).roundToInt(),
                fibraG = FIBRA_G,
            )
    }
}

/**
 * Siempre devuelve las cuatro comidas, en el orden de [TipoComida], aunque no tengan entradas.
 * Las entradas salen en el orden en que se añadieron.
 */
fun List<ComidaConEntradas>.toComidasUi(): List<ComidaUi> =
    TipoComida.entries.map { tipo ->
        val entradas = firstOrNull { it.comida.tipo == tipo }
            ?.entradas
            .orEmpty()
            .sortedBy { it.entrada.id }
            .map {
                EntradaUi(
                    id = it.entrada.id,
                    nombre = it.alimento.nombre,
                    cantidadG = it.entrada.cantidadG.roundToInt(),
                    kcal = it.entrada.kcal.roundToInt(),
                )
            }
        ComidaUi(tipo, entradas)
    }

/** Suma de macros de todas las entradas del día (la fibra no viene en TotalesDia). */
fun List<ComidaConEntradas>.toMacrosConsumidos(): MacrosConsumidos {
    val entradas = flatMap { it.entradas }.map { it.entrada }
    return MacrosConsumidos(
        proteinaG = entradas.sumOf { it.prot.toDouble() }.roundToInt(),
        carbohidratosG = entradas.sumOf { it.carb.toDouble() }.roundToInt(),
        grasaG = entradas.sumOf { it.grasa.toDouble() }.roundToInt(),
        fibraG = entradas.sumOf { it.fibra.toDouble() }.roundToInt(),
    )
}