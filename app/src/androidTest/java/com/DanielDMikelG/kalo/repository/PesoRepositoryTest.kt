package com.DanielDMikelG.kalo.repository

import com.DanielDMikelG.kalo.data.repository.PesoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class PesoRepositoryTest: BaseDbTest() {

    private val hoy = LocalDate.of(2026, 11, 13)

    @Test fun ultimo_sinRegistros_esNulo() = runBlocking {
        assertNull(PesoRepository(db).observarUltimo(usuarioId).first())
    }

    @Test fun registrarPeso_seGuardaYSeLee() = runBlocking {
        val repo = PesoRepository(db)
        repo.registrarPeso(usuarioId, 75f, hoy)
        assertEquals(75f, repo.observarUltimo(usuarioId).first()?.pesoKg)
    }

    @Test fun mismoDia_sustituyeElPesoAnterior() = runBlocking {
        val repo = PesoRepository(db)
        repo.registrarPeso(usuarioId, 75f, hoy)
        repo.registrarPeso(usuarioId, 74.5f, hoy)

        val historial = repo.observarHistorial(usuarioId).first()
        assertEquals(1, historial.size)
        assertEquals(74.5f, historial[0].pesoKg)
    }

    @Test fun historial_vieneOrdenadoPorFecha() = runBlocking {
        val repo = PesoRepository(db)
        repo.registrarPeso(usuarioId, 74f, hoy)
        repo.registrarPeso(usuarioId, 76f, hoy.minusDays(7))

        assertEquals(listOf(76f, 74f), repo.observarHistorial(usuarioId).first().map { it.pesoKg })
    }

    @Test fun eliminar_quitaElRegistro() = runBlocking {
        val repo = PesoRepository(db)
        repo.registrarPeso(usuarioId, 75f, hoy)
        repo.eliminar(repo.observarHistorial(usuarioId).first().single())

        assertEquals(0, repo.observarHistorial(usuarioId).first().size)
    }
}