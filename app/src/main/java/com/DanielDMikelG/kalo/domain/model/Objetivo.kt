package com.DanielDMikelG.kalo.domain.model

enum class TipoObjetivo(val etiqueta: String) {
    GANAR_MASA("Ganar masa muscular"),
    MANTENER("Mantener peso"),
    ADELGAZAR("Adelgazar"),
}

/** Tabla OBJETIVO. metaKg en kg, plazo en semanas, aguaMeta en ml. kcalDiarias se calcula en la #9. */
data class Objetivo(
    val id: Long = 0,
    val usuarioId: Long,
    val tipo: TipoObjetivo,
    val metaKg: Float,
    val plazo: Int,
    val kcalDiarias: Int?,
    val aguaMeta: Int,
)