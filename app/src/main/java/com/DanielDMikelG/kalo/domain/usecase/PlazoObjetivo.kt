package com.DanielDMikelG.kalo.domain.usecase

import java.util.Locale
import kotlin.math.roundToInt

/** Reglas del plazo del objetivo: de 1 a 104 semanas, también para Mantener. */
object PlazoObjetivo {
    const val MIN_SEMANAS = 1
    const val MAX_SEMANAS = 104
    const val POR_DEFECTO = 4
    val ATAJOS = listOf(4, 12, 26, 52, 104)

    fun esValido(semanas: Int?): Boolean = semanas != null && semanas in MIN_SEMANAS..MAX_SEMANAS

    fun ajustar(semanas: Int): Int = semanas.coerceIn(MIN_SEMANAS, MAX_SEMANAS)

    fun etiqueta(semanas: Int): String = if (semanas == 1) "1 semana" else "$semanas semanas"

    /** Equivalencia aproximada en meses o años; null para plazos cortos. */
    fun equivalencia(semanas: Int): String? = when {
        semanas >= 52 -> {
            val anios = String.format(Locale.forLanguageTag("es-ES"), "%.1f", semanas / 52.0).removeSuffix(",0")
            if (anios == "1") "≈ 1 año" else "≈ $anios años"
        }
        semanas >= 9 -> {
            val meses = (semanas * 7 / 30.44).roundToInt()
            if (meses == 1) "≈ 1 mes" else "≈ $meses meses"
        }
        else -> null
    }
}