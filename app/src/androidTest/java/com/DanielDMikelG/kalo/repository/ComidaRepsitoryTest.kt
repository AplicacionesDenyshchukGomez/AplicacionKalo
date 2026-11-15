package com.DanielDMikelG.kalo.repository

import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.data.repository.RachaRepository
import com.DanielDMikelG.kalo.domain.model.TipoComida
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class ComidaRepositoryTest : BaseDbTest() {

    private val hoy = LocalDate.of(2026, 11, 13)

    @Test fun añadirAlimento_creaComidaYCalculaTotales() = runBlocking {
        val repo = ComidaRepository(db)
        repo.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo(), 150f, hoy)

        val dia = repo.observarDia(usuarioId, hoy).first()
        assertEquals(1, dia.size)
        assertEquals(1, dia[0].entradas.size)
        assertEquals("Pollo", dia[0].entradas[0].alimento.nombre)

        val totales = repo.observarTotalesDia(usuarioId, hoy).first()
        assertEquals(300.0, totales.kcal, 0.01)   // 200 kcal/100 g * 150 g
        assertEquals(30.0, totales.prot, 0.01)
    }

    @Test fun mismoTipoYDia_reutilizaLaComida() = runBlocking {
        val repo = ComidaRepository(db)
        val alimento = pollo(codigoBarras = "111")
        repo.añadirAlimento(usuarioId, TipoComida.CENA, alimento, 100f, hoy)
        repo.añadirAlimento(usuarioId, TipoComida.CENA, alimento, 50f, hoy)

        val dia = repo.observarDia(usuarioId, hoy).first()
        assertEquals(1, dia.size)
        assertEquals(2, dia[0].entradas.size)
    }

    @Test fun tiposDistintos_creanComidasDistintas() = runBlocking {
        val repo = ComidaRepository(db)
        repo.añadirAlimento(usuarioId, TipoComida.DESAYUNO, pollo("111"), 100f, hoy)
        repo.añadirAlimento(usuarioId, TipoComida.CENA, pollo("111"), 100f, hoy)

        assertEquals(2, repo.observarDia(usuarioId, hoy).first().size)
    }

    @Test fun alimentoConMismoCodigoDeBarras_noSeDuplica() = runBlocking {
        val repo = ComidaRepository(db)
        repo.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo("999"), 100f, hoy)
        repo.añadirAlimento(usuarioId, TipoComida.CENA, pollo("999"), 100f, hoy)

        assertEquals(1, repo.buscarAlimentos("Pollo").first().size)
    }

    @Test fun eliminarEntrada_actualizaLosTotales() = runBlocking {
        val repo = ComidaRepository(db)
        repo.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo(), 100f, hoy)

        val entrada = repo.observarDia(usuarioId, hoy).first()[0].entradas[0].entrada
        repo.eliminarEntrada(entrada)

        assertEquals(0.0, repo.observarTotalesDia(usuarioId, hoy).first().kcal, 0.01)
    }

    @Test fun totalesDeOtroDia_noSeMezclan() = runBlocking {
        val repo = ComidaRepository(db)
        repo.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo(), 100f, hoy.minusDays(1))

        assertEquals(0.0, repo.observarTotalesDia(usuarioId, hoy).first().kcal, 0.01)
    }

    @Test fun racha_cuentaDiasConComidaRegistrada() = runBlocking {
        val comidas = ComidaRepository(db)
        val hoyReal = LocalDate.now()
        comidas.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo("1"), 100f, hoyReal)
        comidas.añadirAlimento(usuarioId, TipoComida.COMIDA, pollo("1"), 100f, hoyReal.minusDays(1))

        val racha = RachaRepository(db).observarRacha(usuarioId).first()
        assertEquals(2, racha.diasActuales)
        assertEquals(2, racha.mejorRacha)
    }
}