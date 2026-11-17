package com.DanielDMikelG.kalo.domain.usecase

import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RecordatorioAguaTest {
    @Test fun `avisa solo si falta para el objetivo`() {
        assertTrue(TextoRecordatorioAgua.debeAvisar(3, 10))
        assertFalse(TextoRecordatorioAgua.debeAvisar(10, 10))
        assertFalse(TextoRecordatorioAgua.debeAvisar(12, 10))
        assertFalse(TextoRecordatorioAgua.debeAvisar(0, 0))
    }

    @Test fun `mensaje con vasos y sin vasos`() {
        assertEquals("Llevas 3 de 10 vasos de agua. ¡Un vasito ahora!", TextoRecordatorioAgua.mensaje(3, 10))
        assertEquals("Aún no has bebido agua hoy. ¡Un vasito ahora!", TextoRecordatorioAgua.mensaje(0, 10))
    }

    @Test fun `la hora de hoy que aun no ha pasado`() {
        val ahora = LocalDateTime.of(2026, 11, 17, 10, 0)
        assertEquals(120L, ProximoAviso.retraso(ahora, LocalTime.of(12, 0)).toMinutes())
    }

    @Test fun `si la hora ya paso se programa para mañana`() {
        val ahora = LocalDateTime.of(2026, 11, 17, 18, 30)
        assertEquals(17 * 60 + 30L, ProximoAviso.retraso(ahora, LocalTime.of(12, 0)).toMinutes())
    }

    @Test fun `a la hora exacta se programa para mañana`() {
        val ahora = LocalDateTime.of(2026, 11, 17, 12, 0)
        assertEquals(24 * 60L, ProximoAviso.retraso(ahora, LocalTime.of(12, 0)).toMinutes())
    }
}