package com.DanielDMikelG.kalo.domain.model

/** Tabla RACHA. Una fila por usuario. */
data class Racha(
    val usuarioId: Long,
    val diasActuales: Int,
    val mejorRacha: Int,
    val comodines: Int,
)