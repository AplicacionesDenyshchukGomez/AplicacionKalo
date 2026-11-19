package com.DanielDMikelG.kalo.domain.usecase

object TextoRecordatorioAgua {
    /** Solo se avisa si todavía falta para el objetivo del día. */
    fun debeAvisar(vasos: Int, objetivo: Int): Boolean = objetivo > 0 && vasos < objetivo

    fun mensaje(vasos: Int, objetivo: Int): String =
        if (vasos <= 0) "Aún no has bebido agua hoy. ¡Un vasito ahora!"
        else "Llevas $vasos de $objetivo vasos de agua. ¡Un vasito ahora!"
}