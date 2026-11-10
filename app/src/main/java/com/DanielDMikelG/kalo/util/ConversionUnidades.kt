package com.DanielDMikelG.kalo.util

import kotlin.math.roundToInt

enum class SistemaUnidades(val etiqueta: String) {
    METRICO("Métrico (kg, cm)"),
    IMPERIAL("Imperial (lb, ft)")
}

data class PiesPulgadas(val pies: Int, val pulgadas: Int)

/**
 * Conversiones entre sistemas. Internamente la app guarda siempre kg y cm;
 * estas funciones solo se usan al mostrar o al leer lo que escribe el usuario.
 */
object ConversionUnidades {

    private const val LIBRAS_POR_KG = 2.20462262f
    private const val CM_POR_PULGADA = 2.54f
    private const val PULGADAS_POR_PIE = 12

    fun kgALibras(kg: Float): Float = kg * LIBRAS_POR_KG

    fun librasAKg(libras: Float): Float = libras / LIBRAS_POR_KG

    fun cmAPulgadas(cm: Float): Float = cm / CM_POR_PULGADA

    fun pulgadasACm(pulgadas: Float): Float = pulgadas * CM_POR_PULGADA

    /** Redondea a la pulgada más cercana: 180 cm -> 5 ft 11 in. */
    fun cmAPiesYPulgadas(cm: Int): PiesPulgadas {
        val totalPulgadas = (cm / CM_POR_PULGADA).roundToInt()
        return PiesPulgadas(
            pies = totalPulgadas / PULGADAS_POR_PIE,
            pulgadas = totalPulgadas % PULGADAS_POR_PIE
        )
    }

    fun piesYPulgadasACm(pies: Int, pulgadas: Int): Int =
        ((pies * PULGADAS_POR_PIE + pulgadas) * CM_POR_PULGADA).roundToInt()

    /** Peso (guardado en kg) en la unidad del sistema elegido: kg o lb. */
    fun pesoParaMostrar(kg: Float, sistema: SistemaUnidades): Float = when (sistema) {
        SistemaUnidades.METRICO -> kg
        SistemaUnidades.IMPERIAL -> kgALibras(kg)
    }

    /** Peso escrito por el usuario (en kg o lb según el sistema) pasado a kg para guardar. */
    fun pesoParaGuardar(valor: Float, sistema: SistemaUnidades): Float = when (sistema) {
        SistemaUnidades.METRICO -> valor
        SistemaUnidades.IMPERIAL -> librasAKg(valor)
    }
}