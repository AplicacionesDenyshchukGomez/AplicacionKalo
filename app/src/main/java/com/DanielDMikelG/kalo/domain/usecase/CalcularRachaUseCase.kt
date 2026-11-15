package com.DanielDMikelG.kalo.domain.usecase

import java.time.LocalDate

class CalcularRachaUseCase {
    operator fun invoke(
        fechasConRegistro: Collection<LocalDate>,
        hoy: LocalDate = LocalDate.now()
    ): Int {
        val fechas = fechasConRegistro.toSet()
        var dia = if (hoy in fechas) hoy else hoy.minusDays(1)
        var racha = 0
        while (dia in fechas) {
            racha++
            dia = dia.minusDays(1)
        }
        return racha
    }
}