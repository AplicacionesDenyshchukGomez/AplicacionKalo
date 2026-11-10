package com.DanielDMikelG.kalo.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularMetaUseCaseTest {

    private val calcular = CalcularMetaUseCase()

    @Test
    fun adelgazar_80a76_en8semanas_esSeguro() {
        val r = calcular(80f, 76f, 8)
        assertEquals(-0.5f, r.kgPorSemana, 0.001f)
        assertEquals(NivelRitmo.SEGURO, r.nivel)
    }

    @Test
    fun adelgazar_80a72_en8semanas_esExigente_yPideDeficit() {
        val r = calcular(80f, 72f, 8)
        assertEquals(-1f, r.kgPorSemana, 0.001f)
        assertEquals(NivelRitmo.ALTO, r.nivel)
        assertEquals(-1100, r.ajusteKcalDiario)
        assertEquals(76f, r.pesoEn4Semanas, 0.001f)
        assertEquals(-4f, r.kgPorMes, 0.001f)
    }

    @Test
    fun adelgazar_muyRapido_esMuyAlto() {
        val r = calcular(80f, 70f, 4)
        assertEquals(NivelRitmo.MUY_ALTO, r.nivel)
    }

    @Test
    fun ganar_70a73_en12semanas_esSeguro() {
        val r = calcular(70f, 73f, 12)
        assertEquals(0.25f, r.kgPorSemana, 0.001f)
        assertEquals(NivelRitmo.SEGURO, r.nivel)
    }

    @Test
    fun ganar_70a72_en4semanas_esExigente() {
        val r = calcular(70f, 72f, 4)
        assertEquals(NivelRitmo.ALTO, r.nivel)
    }

    @Test
    fun ganar_70a76_en8semanas_esMuyAlto() {
        val r = calcular(70f, 76f, 8)
        assertEquals(NivelRitmo.MUY_ALTO, r.nivel)
    }

    @Test
    fun mantener_sinCambio_esSeguro_yAjusteCero() {
        val r = calcular(75f, 75f, 8)
        assertEquals(0f, r.kgPorSemana, 0.001f)
        assertEquals(NivelRitmo.SEGURO, r.nivel)
        assertEquals(0, r.ajusteKcalDiario)
    }

    @Test
    fun plazoCorto_laProyeccionNoSePasaDeLaMeta() {
        val r = calcular(80f, 78f, 2)
        assertEquals(78f, r.pesoEn4Semanas, 0.001f)
    }

    @Test
    fun hitosSemanales_tienenUnoPorSemana_yTerminanEnLaMeta() {
        val r = calcular(80f, 72f, 8)
        assertEquals(8, r.hitosSemanales.size)
        assertEquals(72f, r.hitosSemanales.last(), 0.001f)
    }

    @Test
    fun hitosMensuales_sonUnoCada4Semanas() {
        val r = calcular(80f, 68f, 12)
        assertEquals(3, r.hitosMensuales.size)
        assertEquals(68f, r.hitosMensuales.last(), 0.001f)
    }

    @Test(expected = IllegalArgumentException::class)
    fun plazoCero_lanzaError() {
        calcular(80f, 75f, 0)
    }
}