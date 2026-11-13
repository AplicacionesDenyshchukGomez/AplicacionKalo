package com.DanielDMikelG.kalo.domain.model

import java.time.LocalDate

enum class TipoComida(val etiqueta: String) {
    DESAYUNO("Desayuno"),
    COMIDA("Comida"),
    CENA("Cena"),
    SNACK("Snack"),
}

data class Comida(
    val id: Long = 0,
    val usuario_id: Long,
    val fecha: LocalDate,
    val tipo: TipoComida,
)