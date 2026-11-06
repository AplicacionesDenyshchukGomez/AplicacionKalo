package com.DanielDMikelG.kalo.domain.model

/**
 * Tabla ENTRADA_COMIDA. Un alimento dentro de una comida.
 * kcal, prot, carb y grasa son los valores YA calculados para cantidadG,
 * así el historial no cambia si luego se edita el alimento.
 */
data class EntradaComida(
    val id: Long = 0,
    val comidaId: Long,
    val alimentoId: Long,
    val cantidadG: Float,
    val kcal: Float,
    val prot: Float,
    val carb: Float,
    val grasa: Float,
    val fibra: Float,
) {
    companion object {
        /** Crea una entrada calculando los valores a partir de los de 100 g del alimento. */

        //La función desde(...) es la que usará la pantalla de añadir comida:
        //le pasas el alimento y los gramos, y devuelve la entrada con las
        //calorías y macros ya calculados.

        fun desde(comidaId: Long, alimento: Alimento, cantidadG: Float) = EntradaComida(
            comidaId = comidaId,
            alimentoId = alimento.id,
            cantidadG = cantidadG,
            kcal = alimento.kcal100g * cantidadG / 100f,
            prot = alimento.prot * cantidadG / 100f,
            carb = alimento.carb * cantidadG / 100f,
            grasa = alimento.grasa * cantidadG / 100f,
            fibra = alimento.fibra * cantidadG / 100f,
        )
    }
}

