package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.Alimento
import kotlin.math.roundToInt

/** Kcal y macros de una cantidad concreta de un alimento. */
data class NutrientesRacion(
    val kcal: Float,
    val proteina: Float,
    val carbohidratos: Float,
    val grasa: Float,
    val fibra: Float,
) {
    val kcalRedondeadas: Int get() = kcal.roundToInt()
}

/**
 * Pasa los valores "por 100 g" de un alimento a los de la cantidad que se come.
 * Devuelve null si los gramos no son válidos: así la pantalla puede mostrar "—" sin romperse.
 */
class CalcularNutrientesRacionUseCase {

    operator fun invoke(alimento: Alimento, gramos: Float): NutrientesRacion? {
        if (!gramos.isFinite() || gramos <= 0f || gramos > GRAMOS_MAXIMOS) return null
        val factor = gramos / 100f
        return NutrientesRacion(
            kcal = alimento.kcal100g * factor,
            proteina = alimento.prot * factor,
            carbohidratos = alimento.carb * factor,
            grasa = alimento.grasa * factor,
            fibra = alimento.fibra * factor,
        )
    }

    companion object {
        const val GRAMOS_MAXIMOS = 5000f
    }
}