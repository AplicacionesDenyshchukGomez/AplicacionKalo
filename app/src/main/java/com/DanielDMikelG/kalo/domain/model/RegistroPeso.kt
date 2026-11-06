package com.DanielDMikelG.kalo.domain.model

import java.time.LocalDate

/** Tabla REGISTRO_PESO. Un pesaje por fecha, en kg. */
data class RegistroPeso(
    val id: Long = 0,
    val usuarioId: Long,
    val fecha: LocalDate,
    val pesoKg: Float,
)