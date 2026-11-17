package com.DanielDMikelG.kalo.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PlazoObjetivoTest {
    @Test fun `valido de 1 a 104`() {
        assertTrue(PlazoObjetivo.esValido(1))
        assertTrue(PlazoObjetivo.esValido(104))
        assertTrue(PlazoObjetivo.esValido(52))
    }

    @Test fun `no valido fuera de rango o sin numero`() {
        assertFalse(PlazoObjetivo.esValido(0))
        assertFalse(PlazoObjetivo.esValido(105))
        assertFalse(PlazoObjetivo.esValido(-3))
        assertFalse(PlazoObjetivo.esValido(null))
    }

    @Test fun `ajustar limita al rango`() {
        assertEquals(1, PlazoObjetivo.ajustar(0))
        assertEquals(104, PlazoObjetivo.ajustar(500))
        assertEquals(30, PlazoObjetivo.ajustar(30))
    }

    @Test fun `etiqueta en singular y plural`() {
        assertEquals("1 semana", PlazoObjetivo.etiqueta(1))
        assertEquals("8 semanas", PlazoObjetivo.etiqueta(8))
    }

    @Test fun `equivalencia en meses y anios`() {
        assertNull(PlazoObjetivo.equivalencia(4))
        assertEquals("≈ 3 meses", PlazoObjetivo.equivalencia(12))
        assertEquals("≈ 1 año", PlazoObjetivo.equivalencia(52))
        assertEquals("≈ 1,5 años", PlazoObjetivo.equivalencia(78))
        assertEquals("≈ 2 años", PlazoObjetivo.equivalencia(104))
    }
}