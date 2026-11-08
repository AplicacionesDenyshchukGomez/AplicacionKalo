package com.DanielDMikelG.kalo.domain.model

import java.time.LocalDate

enum class TipoComida(val etiqueta: String) {
    DESAYUNO("desayuno"),
    COMIDA("comida"),
    CENA("cena"),
    SNACK("snack"),
}

data class Comida(
    val id: Long = 0,
    val usuario_id: Long,
    val fecha: LocalDate,
    val tipo: TipoComida,
)