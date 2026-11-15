package com.DanielDMikelG.kalo.repository

import com.DanielDMikelG.kalo.data.repository.AguaRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class AguaRepositoryTest : BaseDbTest() {

    private val hoy = LocalDate.of(2026, 11, 13)

    @Test fun sinRegistro_son0Vasos() = runBlocking {
        assertEquals(0, AguaRepository(db).observarVasos(usuarioId, hoy).first())
    }

    @Test fun añadirVaso_sumaDeUnoEnUno() = runBlocking {
        val repo = AguaRepository(db)
        repo.añadirVaso(usuarioId, hoy)
        repo.añadirVaso(usuarioId, hoy)
        assertEquals(2, repo.observarVasos(usuarioId, hoy).first())
    }

    @Test fun quitarVaso_nuncaBajaDeCero() = runBlocking {
        val repo = AguaRepository(db)
        repo.quitarVaso(usuarioId, hoy)
        assertEquals(0, repo.observarVasos(usuarioId, hoy).first())
    }

    @Test fun cadaDiaTieneSuPropioContador() = runBlocking {
        val repo = AguaRepository(db)
        repo.añadirVaso(usuarioId, hoy)
        assertEquals(0, repo.observarVasos(usuarioId, hoy.minusDays(1)).first())
    }
}