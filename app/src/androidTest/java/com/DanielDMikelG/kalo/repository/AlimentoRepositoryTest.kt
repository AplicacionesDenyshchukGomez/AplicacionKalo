package com.DanielDMikelG.kalo.data.repository

import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento
import com.DanielDMikelG.kalo.domain.model.ResultadoAlimento
import com.DanielDMikelG.kalo.repository.BaseDbTest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AlimentoRepositoryTest : BaseDbTest() {

    // Códigos con dígito de control válido
    private val ean13 = "5449000000996"
    private val upcA = "036000291452"
    private val upcAComoEan13 = "0036000291452"

    private fun repo() = AlimentoRepository(db)

    @Test fun codigoNoValido_devuelveCodigoNoValido() = runBlocking {
        assertEquals(ResultadoAlimento.CodigoNoValido, repo().buscarPorCodigo("123"))
    }

    @Test fun alimentoGuardado_seEncuentraPorCodigo_desdeCache() = runBlocking {
        db.alimentoDao().insertar(pollo(codigoBarras = ean13).toEntity())

        val r = repo().buscarPorCodigo(ean13) as ResultadoAlimento.Encontrado
        assertEquals(true, r.desdeCache)
        assertEquals("Pollo", r.alimento.nombre)
    }

    @Test fun upcA_encuentraElProductoGuardadoComoEan13() = runBlocking {
        db.alimentoDao().insertar(pollo(codigoBarras = upcAComoEan13).toEntity())

        val r = repo().buscarPorCodigo(upcA) as ResultadoAlimento.Encontrado
        assertEquals(true, r.desdeCache)
    }

    @Test fun ean13ConCeroDelante_encuentraElProductoGuardadoComoUpcA() = runBlocking {
        db.alimentoDao().insertar(pollo(codigoBarras = upcA).toEntity())

        val r = repo().buscarPorCodigo(upcAComoEan13) as ResultadoAlimento.Encontrado
        assertEquals(true, r.desdeCache)
    }

    @Test fun crearManual_guardaConIdReal() = runBlocking {
        val id = repo().crearManual(pollo())

        assertNotEquals(0L, id)
        assertEquals("Pollo", repo().obtener(id)?.nombre)
    }

    @Test fun crearManual_fuerzaOrigenManualYSinCodigo() = runBlocking {
        val id = repo().crearManual(
            pollo(codigoBarras = "999").copy(origen = OrigenAlimento.OPEN_FOOD_FACTS)
        )
        val guardado = repo().obtener(id)

        assertEquals(OrigenAlimento.MANUAL, guardado?.origen)
        assertNull(guardado?.codigoBarras)
    }

    @Test fun obtener_idInexistente_devuelveNulo() = runBlocking {
        assertNull(repo().obtener(12345L))
    }

    @Test fun buscarPorNombre_textoVacio_devuelveListaVacia() = runBlocking {
        repo().crearManual(pollo())

        assertEquals(0, repo().buscarPorNombre("   ").first().size)
    }

    @Test fun buscarPorNombre_encuentraPorParteDelNombre() = runBlocking {
        repo().crearManual(pollo())

        assertEquals(1, repo().buscarPorNombre("poll").first().size)
        assertEquals(0, repo().buscarPorNombre("pescado").first().size)
    }
}