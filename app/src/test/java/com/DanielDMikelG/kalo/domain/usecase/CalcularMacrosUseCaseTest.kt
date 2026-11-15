package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CalcularMacrosUseCaseTest {

    private val calcular = CalcularMacrosUseCase()

    @Test
    fun mantener_80kg_2000kcal() {
        val r = calcular(2000, 80f, TipoObjetivo.MANTENER)
        assertEquals(128, r.proteinaG)
        assertEquals(56, r.grasaG)
        assertEquals(246, r.carbohidratosG)
    }

    @Test
    fun ganarMasa_75kg_3000kcal() {
        val r = calcular(3000, 75f, TipoObjetivo.GANAR_MASA)
        assertEquals(135, r.proteinaG)
        assertEquals(83, r.grasaG)
        assertEquals(428, r.carbohidratosG)
    }

    @Test
    fun adelgazar_usaMasProteinaQueMantener() {
        val adelgazar = calcular(2000, 80f, TipoObjetivo.ADELGAZAR)
        val mantener = calcular(2000, 80f, TipoObjetivo.MANTENER)
        assertTrue(adelgazar.proteinaG > mantener.proteinaG)
    }

    @Test
    fun lasKcalDeLosMacrosSeAcercanAlObjetivo() {
        val r = calcular(2400, 70f, TipoObjetivo.MANTENER)
        assertTrue(kotlin.math.abs(r.kcalTotales - 2400) <= 10)
    }

    @Test
    fun conKcalMuyBajas_losCarbohidratosNuncaSonNegativos() {
        val r = calcular(1000, 100f, TipoObjetivo.ADELGAZAR)
        assertEquals(0, r.carbohidratosG)
    }

    @Test
    fun fibra_2000kcal_son28g() {
        val r = calcular(2000, 80f, TipoObjetivo.MANTENER)
        assertEquals(28, r.fibraG)
    }

    @Test
    fun fibra_crece_con_las_kcal() {
        val baja = calcular(1500, 70f, TipoObjetivo.MANTENER)
        val alta = calcular(3000, 70f, TipoObjetivo.MANTENER)
        assertTrue(alta.fibraG > baja.fibraG)
    }

    @Test
    fun fibra_noCambiaLasKcalTotales() {
        val r = calcular(2000, 80f, TipoObjetivo.MANTENER)
        assertEquals(r.proteinaG * 4 + r.carbohidratosG * 4 + r.grasaG * 9, r.kcalTotales)
    }
}