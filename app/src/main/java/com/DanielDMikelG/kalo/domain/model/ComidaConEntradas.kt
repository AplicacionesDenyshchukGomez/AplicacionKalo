package com.DanielDMikelG.kalo.domain.model

data class ComidaConEntradas(
    val comida: Comida,
    val entradas: List<EntradaConAlimento>
)