package com.DanielDMikelG.kalo.ui.components

/**
 * Cómo se pinta una barra de macro:
 * [normal] es la primera vuelta (0 a 1) y [exceso] la segunda, más oscura (0 a 1).
 */
data class FraccionesBarra(val normal: Float, val exceso: Float) {
    companion object {
        fun desde(consumido: Float, meta: Float): FraccionesBarra {
            if (meta <= 0f || consumido <= 0f) return FraccionesBarra(0f, 0f)
            val ratio = consumido / meta
            return FraccionesBarra(
                normal = ratio.coerceAtMost(1f),
                exceso = (ratio - 1f).coerceIn(0f, 1f),
            )
        }
    }
}