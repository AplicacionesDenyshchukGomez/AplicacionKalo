package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.model.Usuario
import java.time.LocalDate
import kotlin.math.roundToInt

data class ResultadoCalorias(
    val tmb: Int,            // calorías en reposo
    val mantenimiento: Int,  // calorías para mantener el peso
    val ajusteDiario: Int,   // negativo = déficit, positivo = superávit
    val kcalObjetivo: Int,   // lo que debe comer al día
)

class CalcularCaloriasUseCase {

    operator fun invoke(
        usuario: Usuario,
        pesoKg: Float,
        objetivo: Objetivo,
        hoy: LocalDate = LocalDate.now(),
    ): ResultadoCalorias {
        val tmb = calcularTmb(usuario.sexo, pesoKg, usuario.altura, usuario.edad(hoy))
        val mantenimiento = tmb * factorActividad(usuario.nivelActividad)
        val ajuste = calcularAjusteDiario(objetivo, pesoKg)

        val minimo = if (usuario.sexo == Sexo.HOMBRE) KCAL_MIN_HOMBRE else KCAL_MIN_MUJER
        val kcalObjetivo = (mantenimiento + ajuste).coerceAtLeast(minimo).roundToInt()
        val mantenimientoRedondeado = mantenimiento.roundToInt()

        return ResultadoCalorias(
            tmb = tmb.roundToInt(),
            mantenimiento = mantenimientoRedondeado,
            ajusteDiario = kcalObjetivo - mantenimientoRedondeado,
            kcalObjetivo = kcalObjetivo,
        )
    }

    /** Fórmula de Mifflin-St Jeor. */
    internal fun calcularTmb(sexo: Sexo, pesoKg: Float, alturaCm: Int, edad: Int): Double {
        val base = 10.0 * pesoKg + 6.25 * alturaCm - 5.0 * edad
        return if (sexo == Sexo.HOMBRE) base + 5.0 else base - 161.0
    }

    internal fun factorActividad(nivel: NivelActividad): Double = when (nivel) {
        NivelActividad.SEDENTARIO -> 1.2
        NivelActividad.LIGERO -> 1.375
        NivelActividad.MODERADO -> 1.55
        NivelActividad.ALTO -> 1.725
    }

    /** Calorías diarias a sumar o restar según el ritmo de cambio de peso que pide el objetivo. */
    internal fun calcularAjusteDiario(objetivo: Objetivo, pesoKg: Float): Double {
        val semanas = objetivo.plazo.coerceAtLeast(1)
        val ritmoDeseado = (objetivo.metaKg - pesoKg).toDouble() / semanas // kg por semana

        val ritmo = when (objetivo.tipo) {
            TipoObjetivo.MANTENER -> 0.0
            TipoObjetivo.ADELGAZAR ->
                if (ritmoDeseado < 0) ritmoDeseado.coerceAtLeast(-RITMO_MAX_PERDIDA)
                else -RITMO_DEFECTO_PERDIDA
            TipoObjetivo.GANAR_MASA ->
                if (ritmoDeseado > 0) ritmoDeseado.coerceAtMost(RITMO_MAX_GANANCIA)
                else RITMO_DEFECTO_GANANCIA
        }
        return ritmo * KCAL_POR_KG / 7.0
    }

    private companion object {
        const val KCAL_POR_KG = 7700.0
        const val RITMO_MAX_PERDIDA = 1.0       // kg por semana
        const val RITMO_MAX_GANANCIA = 0.5
        const val RITMO_DEFECTO_PERDIDA = 0.5   // si la meta no es coherente con el tipo
        const val RITMO_DEFECTO_GANANCIA = 0.25
        const val KCAL_MIN_HOMBRE = 1500.0
        const val KCAL_MIN_MUJER = 1200.0
    }
}

