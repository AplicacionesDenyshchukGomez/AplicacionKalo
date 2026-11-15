package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import kotlin.math.roundToInt

data class ResultadoMacros(
    val proteinaG: Int,
    val grasaG: Int,
    val carbohidratosG: Int,
    val fibraG: Int
) {
    /** Kcal de proteína, hidratos y grasa. La fibra va incluida en los hidratos. */
    val kcalTotales: Int
        get() = proteinaG * 4 + carbohidratosG * 4 + grasaG * 9
}

/**
 * Reparte las kcal objetivo en proteína, grasa, hidratos y fibra.
 *
 * Son valores orientativos, no consejo médico:
 * - Proteína: g por kg de peso según el objetivo.
 * - Grasa: porcentaje fijo de las kcal.
 * - Hidratos: lo que quede (nunca negativo).
 * - Fibra: meta orientativa de 14 g por cada 1000 kcal (va incluida en los hidratos).
 */
class CalcularMacrosUseCase {

    operator fun invoke(
        kcalObjetivo: Int,
        pesoKg: Float,
        tipo: TipoObjetivo
    ): ResultadoMacros {
        val proteinaG = (pesoKg * proteinaPorKg(tipo)).roundToInt()
        val grasaG = (kcalObjetivo * PORCENTAJE_GRASA / KCAL_POR_G_GRASA).roundToInt()

        val kcalRestantes = kcalObjetivo - proteinaG * KCAL_POR_G_PROTEINA - grasaG * KCAL_POR_G_GRASA
        val carbohidratosG = (kcalRestantes / KCAL_POR_G_CARBOHIDRATO.toFloat())
            .roundToInt()
            .coerceAtLeast(0)

        val fibraG = (kcalObjetivo / 1000f * FIBRA_G_POR_1000_KCAL).roundToInt()

        return ResultadoMacros(proteinaG, grasaG, carbohidratosG, fibraG)
    }

    private fun proteinaPorKg(tipo: TipoObjetivo): Float = when (tipo) {
        TipoObjetivo.ADELGAZAR -> 2.0f
        TipoObjetivo.GANAR_MASA -> 1.8f
        TipoObjetivo.MANTENER -> 1.6f
    }

    private companion object {
        const val PORCENTAJE_GRASA = 0.25f
        const val KCAL_POR_G_PROTEINA = 4
        const val KCAL_POR_G_CARBOHIDRATO = 4
        const val KCAL_POR_G_GRASA = 9
        const val FIBRA_G_POR_1000_KCAL = 14
    }
}