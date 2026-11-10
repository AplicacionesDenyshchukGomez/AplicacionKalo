package com.DanielDMikelG.kalo.domain.usecase

import kotlin.math.abs
import kotlin.math.min

enum class NivelRitmo(val etiqueta: String) {
    SEGURO("Ritmo adecuado"),
    ALTO("Ritmo exigente"),
    MUY_ALTO("Ritmo demasiado agresivo")
}

data class ResultadoMeta(
    /** Cambio de peso por semana; negativo si se quiere bajar. */
    val kgPorSemana: Float,
    /** Cambio de peso proyectado en un mes (4 semanas). */
    val kgPorMes: Float,
    /** Porcentaje del peso actual que cambia cada semana (en valor absoluto). */
    val porcentajePesoPorSemana: Float,
    val nivel: NivelRitmo,
    /** Déficit (negativo) o superávit (positivo) diario de kcal que exige este ritmo. */
    val ajusteKcalDiario: Int,
    /** Peso esperado dentro de 4 semanas (o al final del plazo si es menor). */
    val pesoEn4Semanas: Float,
    /** Peso esperado al final de cada semana del plazo. */
    val hitosSemanales: List<Float>
) {
    /** Peso esperado al final de cada mes (cada 4 semanas). */
    val hitosMensuales: List<Float>
        get() = hitosSemanales.filterIndexed { indice, _ -> (indice + 1) % SEMANAS_POR_MES == 0 }

    private companion object {
        const val SEMANAS_POR_MES = 4
    }
}

/**
 * Convierte una meta de peso y un plazo en ritmo semanal, aviso de agresividad,
 * ajuste calórico y proyección. Son valores orientativos, no consejo médico.
 */
class CalcularMetaUseCase {

    operator fun invoke(
        pesoActualKg: Float,
        pesoMetaKg: Float,
        plazoSemanas: Int
    ): ResultadoMeta {
        require(pesoActualKg > 0f) { "El peso actual debe ser positivo" }
        require(plazoSemanas > 0) { "El plazo debe ser de al menos 1 semana" }

        val kgPorSemana = (pesoMetaKg - pesoActualKg) / plazoSemanas
        val porcentaje = abs(kgPorSemana) / pesoActualKg * 100f
        val semanasProyeccion = min(SEMANAS_PROYECCION, plazoSemanas)

        return ResultadoMeta(
            kgPorSemana = kgPorSemana,
            kgPorMes = kgPorSemana * SEMANAS_PROYECCION,
            porcentajePesoPorSemana = porcentaje,
            nivel = nivelDelRitmo(kgPorSemana, porcentaje),
            ajusteKcalDiario = (kgPorSemana * KCAL_POR_KG / DIAS_SEMANA).toInt(),
            pesoEn4Semanas = pesoActualKg + kgPorSemana * semanasProyeccion,
            hitosSemanales = (1..plazoSemanas).map { semana ->
                pesoActualKg + kgPorSemana * semana
            }
        )
    }

    private fun nivelDelRitmo(kgPorSemana: Float, porcentaje: Float): NivelRitmo {
        // Bajar de peso admite un ritmo mayor que subirlo
        val (limiteSeguro, limiteAlto) =
            if (kgPorSemana < 0f) LIMITES_PERDER else LIMITES_GANAR
        return when {
            porcentaje <= limiteSeguro -> NivelRitmo.SEGURO
            porcentaje <= limiteAlto -> NivelRitmo.ALTO
            else -> NivelRitmo.MUY_ALTO
        }
    }

    private companion object {
        const val SEMANAS_PROYECCION = 4
        const val KCAL_POR_KG = 7700f
        const val DIAS_SEMANA = 7f

        // Porcentaje del peso corporal por semana: (seguro hasta, alto hasta)
        val LIMITES_PERDER = 1.0f to 1.5f
        val LIMITES_GANAR = 0.5f to 0.75f
    }
}

