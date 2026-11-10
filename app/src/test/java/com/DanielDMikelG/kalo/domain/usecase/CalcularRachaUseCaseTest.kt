package com.DanielDMikelG.kalo.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class CalcularRachaUseCaseTest {

    private val calcular = CalcularRachaUseCase()
    private val hoy = LocalDate.of(2026, 11, 10)

    @Test fun sinRegistros_es0() =
        assertEquals(0, calcular(emptyList(), hoy))

    @Test fun soloHoy_es1() =
        assertEquals(1, calcular(listOf(hoy), hoy))

    @Test fun diasConsecutivos_suman() =
        assertEquals(3, calcular(listOf(hoy, hoy.minusDays(1), hoy.minusDays(2)), hoy))

    @Test fun soloAyer_mantieneLaRacha() =
        assertEquals(2, calcular(listOf(hoy.minusDays(1), hoy.minusDays(2)), hoy))

    @Test fun hueco_reiniciaLaRacha() =
        assertEquals(0, calcular(listOf(hoy.minusDays(2), hoy.minusDays(3)), hoy))

    @Test fun huecoEnMedio_cuentaSoloLaRachaActual() =
        assertEquals(2, calcular(listOf(hoy, hoy.minusDays(1), hoy.minusDays(3)), hoy))

    @Test fun duplicados_noSumanDeMas() =
        assertEquals(1, calcular(listOf(hoy, hoy, hoy), hoy))

    @Test fun fechasFuturas_seIgnoran() =
        assertEquals(1, calcular(listOf(hoy, hoy.plusDays(1)), hoy))
}