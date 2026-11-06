package com.DanielDMikelG.kalo.domain.model

enum class OrigenAlimento(val etiqueta: String) {
    MANUAL("Creado por el usuario"),
    OPEN_FOOD_FACTS("Open Food Facts"),
}

/** Tabla ALIMENTO. Valores nutricionales por 100 g. */
data class Alimento(
    val id: Long = 0,
    val codigoBarras: String?,
    val nombre: String,
    val kcal100g: Float,
    val prot: Float,
    val carb: Float,
    val grasa: Float,
    val fibra: Float,
    val origen: OrigenAlimento,
)