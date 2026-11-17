package com.DanielDMikelG.kalo.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarPesoUseCaseTest {
    private val validar = ValidarPesoUseCase()

    @Test fun `acepta coma decimal`() {
        val r = validar("72,5") as ResultadoPeso.Valido
        assertEquals(72.5f, r.kg, 0.001f)
    }

    @Test fun `acepta entero y punto`() {
        assertEquals(80f, (validar("80") as ResultadoPeso.Valido).kg, 0.001f)
        assertEquals(80.4f, (validar("80.4") as ResultadoPeso.Valido).kg, 0.001f)
    }

    @Test fun `vacio es invalido`() {
        assertTrue(validar("  ") is ResultadoPeso.Invalido)
    }

    @Test fun `texto no numerico es invalido`() {
        assertTrue(validar("abc") is ResultadoPeso.Invalido)
    }

    @Test fun `fuera de rango es invalido`() {
        assertTrue(validar("19,9") is ResultadoPeso.Invalido)
        assertTrue(validar("401") is ResultadoPeso.Invalido)
    }

    @Test fun `mas de un decimal es invalido`() {
        assertTrue(validar("72,55") is ResultadoPeso.Invalido)
    }
}