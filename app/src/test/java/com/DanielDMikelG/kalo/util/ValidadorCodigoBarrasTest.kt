package com.DanielDMikelG.kalo.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidadorCodigoBarrasTest {

    @Test
    fun ean13_valido() {
        assertTrue(ValidadorCodigoBarras.esValido("4006381333931"))
        assertTrue(ValidadorCodigoBarras.esValido("5449000000996"))
        assertEquals(TipoCodigoBarras.EAN_13, ValidadorCodigoBarras.tipo("4006381333931"))
    }

    @Test
    fun ean8_valido() {
        assertTrue(ValidadorCodigoBarras.esValido("96385074"))
        assertEquals(TipoCodigoBarras.EAN_8, ValidadorCodigoBarras.tipo("96385074"))
    }

    @Test
    fun upcA_valido() {
        assertTrue(ValidadorCodigoBarras.esValido("036000291452"))
        assertEquals(TipoCodigoBarras.UPC_A, ValidadorCodigoBarras.tipo("036000291452"))
    }

    @Test
    fun digitoDeControlIncorrecto_noEsValido() {
        assertFalse(ValidadorCodigoBarras.esValido("4006381333932"))
        assertFalse(ValidadorCodigoBarras.esValido("96385075"))
        assertFalse(ValidadorCodigoBarras.esValido("036000291453"))
    }

    @Test
    fun longitudIncorrecta_noEsValido() {
        assertFalse(ValidadorCodigoBarras.esValido("12345"))
        assertFalse(ValidadorCodigoBarras.esValido("40063813339311"))
    }

    @Test
    fun conLetrasOVacio_noEsValido() {
        assertFalse(ValidadorCodigoBarras.esValido("40063813339A1"))
        assertFalse(ValidadorCodigoBarras.esValido(""))
    }

    @Test
    fun limpiar_quitaEspaciosYGuiones() {
        assertEquals("4006381333931", ValidadorCodigoBarras.limpiar(" 4006 3813-33931 "))
    }

    @Test
    fun normalizar_upcAGanaUnCeroDelante() {
        assertEquals("0036000291452", ValidadorCodigoBarras.normalizar("036000291452"))
    }

    @Test
    fun normalizar_ean13YEan8NoCambian() {
        assertEquals("4006381333931", ValidadorCodigoBarras.normalizar("4006381333931"))
        assertEquals("96385074", ValidadorCodigoBarras.normalizar("96385074"))
    }

    @Test
    fun normalizar_codigoInvalidoDevuelveNull() {
        assertNull(ValidadorCodigoBarras.normalizar("1234"))
    }

    @Test
    fun upcANormalizadoSigueSiendoEan13Valido() {
        val normalizado = ValidadorCodigoBarras.normalizar("036000291452")!!
        assertTrue(ValidadorCodigoBarras.esValido(normalizado))
    }
}