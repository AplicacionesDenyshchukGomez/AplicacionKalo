package com.DanielDMikelG.kalo.domain.model

import java.time.LocalDate

/** Tabla REGISTRO_AGUA. Vasos de 250 ml bebidos ese día. */
data class RegistroAgua(
    val id: Long = 0,
    val usuarioId: Long,
    val fecha: LocalDate,
    val vasos: Int,
)