package com.DanielDMikelG.kalo.domain.usecase

import java.time.LocalDate

class CalcularRachaUseCase {
    operator fun invoke(
        fechasConRegistro: Collection<LocalDate>,
        hoy: LocalDate = LocalDate.now()
    ): Int {
        val fechas = fechasConRegistro.filter { !it.isAfter(hoy) }.toSet()

        // Si hoy aún no has registrado nada, la racha sigue viva si ayer sí
        var dia = when {
            hoy in fechas -> hoy
            hoy.minusDays(1) in fechas -> hoy.minusDays(1)
            else -> return 0
        }

        var dias = 0
        while (dia in fechas) {
            dias++
            dia = dia.minusDays(1)
        }
        return dias
    }
}