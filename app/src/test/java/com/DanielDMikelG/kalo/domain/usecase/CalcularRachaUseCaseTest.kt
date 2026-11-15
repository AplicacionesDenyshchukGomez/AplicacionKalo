package com.DanielDMikelG.kalo.domain.usecase

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularRachaUseCaseTest {
    private val calcular = CalcularRachaUseCase()
    private val hoy = LocalDate.of(2026, 11, 15)

    @Test fun `sin registros la racha es cero`() {
        assertEquals(0, calcular(emptyList(), hoy))
    }

    @Test fun `solo hoy es una racha de uno`() {
        assertEquals(1, calcular(listOf(hoy), hoy))
    }

    @Test fun `dias seguidos hasta hoy`() {
        assertEquals(3, calcular(listOf(hoy, hoy.minusDays(1), hoy.minusDays(2)), hoy))
    }

    @Test fun `si hoy no hay registro cuenta hasta ayer`() {
        assertEquals(2, calcular(listOf(hoy.minusDays(1), hoy.minusDays(2)), hoy))
    }

    @Test fun `si falta ayer y hoy la racha es cero`() {
        assertEquals(0, calcular(listOf(hoy.minusDays(2), hoy.minusDays(3)), hoy))
    }

    @Test fun `un hueco corta la racha`() {
        assertEquals(2, calcular(listOf(hoy, hoy.minusDays(1), hoy.minusDays(3)), hoy))
    }

    @Test fun `ignora fechas futuras y duplicadas`() {
        assertEquals(1, calcular(listOf(hoy, hoy, hoy.plusDays(1)), hoy))
    }
}