package com.DanielDMikelG.kalo.domain.usecase

import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

object ProximoAviso {
    /** Tiempo hasta la próxima vez que sean [hora]: hoy si aún no ha pasado, y si no, mañana. */
    fun retraso(ahora: LocalDateTime, hora: LocalTime): Duration {
        var objetivo = ahora.toLocalDate().atTime(hora)
        if (!objetivo.isAfter(ahora)) objetivo = objetivo.plusDays(1)
        return Duration.between(ahora, objetivo)
    }
}