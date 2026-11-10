package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.EntradaComida

/** Consumido frente a meta de un nutriente (o de las kcal). */
data class ProgresoNutriente(
    val consumido: Float,
    val meta: Float
) {
    /** Lo que falta para la meta; nunca negativo. */
    val restante: Float
        get() = (meta - consumido).coerceAtLeast(0f)

    /** De 0 a 1, para barras de progreso. */
    val progreso: Float
        get() = if (meta <= 0f) 0f else (consumido / meta).coerceIn(0f, 1f)

    val superaMeta: Boolean
        get() = consumido > meta
}

data class ResumenDiario(
    val kcal: ProgresoNutriente,
    val proteina: ProgresoNutriente,
    val carbohidratos: ProgresoNutriente,
    val grasa: ProgresoNutriente,
    val fibra: ProgresoNutriente
)

/**
 * Suma las entradas de un día y las compara con las metas
 * (kcal de [CalcularCaloriasUseCase] y macros de [CalcularMacrosUseCase]).
 * No depende de la fecha: sirve para hoy y para cualquier día pasado.
 */
class ResumenDiarioUseCase {

    operator fun invoke(
        entradas: List<EntradaComida>,
        metaKcal: Int,
        metas: ResultadoMacros
    ): ResumenDiario = ResumenDiario(
        kcal = ProgresoNutriente(
            consumido = entradas.sumOf { it.kcal.toDouble() }.toFloat(),
            meta = metaKcal.toFloat()
        ),
        proteina = ProgresoNutriente(
            consumido = entradas.sumOf { it.prot.toDouble() }.toFloat(),
            meta = metas.proteinaG.toFloat()
        ),
        carbohidratos = ProgresoNutriente(
            consumido = entradas.sumOf { it.carb.toDouble() }.toFloat(),
            meta = metas.carbohidratosG.toFloat()
        ),
        grasa = ProgresoNutriente(
            consumido = entradas.sumOf { it.grasa.toDouble() }.toFloat(),
            meta = metas.grasaG.toFloat()
        ),
        fibra = ProgresoNutriente(
            consumido = entradas.sumOf { it.fibra.toDouble() }.toFloat(),
            meta = metas.fibraG.toFloat()
        )
    )
}

