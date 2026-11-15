package com.DanielDMikelG.kalo.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidarAlimentoManualUseCaseTest {
    private val validar = ValidarAlimentoManualUseCase()

    @Test fun `alimento correcto con coma decimal`() {
        val r = validar("  Pan   casero ", "250,5", "8", "50", "2", "3")
        r as ResultadoAlimentoManual.Valido
        assertEquals("Pan casero", r.alimento.nombre)
        assertEquals(250.5f, r.alimento.kcal100g, 0.001f)
    }

    @Test fun `los macros vacios cuentan como cero`() {
        val r = validar("Agua", "0", "", "", "", "")
        r as ResultadoAlimentoManual.Valido
        assertEquals(0f, r.alimento.prot, 0.001f)
    }

    @Test fun `sin calorias es invalido`() {
        assertTrue(validar("Pan", "", "", "", "", "") is ResultadoAlimentoManual.Invalido)
    }

    @Test fun `nombre demasiado corto es invalido`() {
        assertTrue(validar("a", "100", "", "", "", "") is ResultadoAlimentoManual.Invalido)
    }

    @Test fun `mas de 900 kcal por 100 g es invalido`() {
        assertTrue(validar("Raro", "1200", "", "", "", "") is ResultadoAlimentoManual.Invalido)
    }

    @Test fun `macros que suman mas de 100 g son invalidos`() {
        assertTrue(validar("Raro", "300", "60", "50", "10", "") is ResultadoAlimentoManual.Invalido)
    }
}