package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.EntradaComida
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ResumenDiarioUseCaseTest {

    private val resumir = ResumenDiarioUseCase()
    private val metas = ResultadoMacros(
        proteinaG = 128, grasaG = 56, carbohidratosG = 246, fibraG = 28
    )

    private fun entrada(
        kcal: Float, prot: Float, carb: Float, grasa: Float, fibra: Float
    ) = EntradaComida(
        comidaId = 1, alimentoId = 1, cantidadG = 100f,
        kcal = kcal, prot = prot, carb = carb, grasa = grasa, fibra = fibra
    )

    @Test
    fun diaVacio_todoEnCero_yRestanteEsLaMeta() {
        val r = resumir(emptyList(), 2000, metas)
        assertEquals(0f, r.kcal.consumido, 0.01f)
        assertEquals(2000f, r.kcal.restante, 0.01f)
        assertEquals(0f, r.kcal.progreso, 0.01f)
    }

    @Test
    fun sumaLasEntradasDelDia() {
        val entradas = listOf(
            entrada(500f, 30f, 60f, 15f, 5f),
            entrada(700f, 40f, 80f, 25f, 8f)
        )
        val r = resumir(entradas, 2000, metas)
        assertEquals(1200f, r.kcal.consumido, 0.01f)
        assertEquals(70f, r.proteina.consumido, 0.01f)
        assertEquals(140f, r.carbohidratos.consumido, 0.01f)
        assertEquals(40f, r.grasa.consumido, 0.01f)
        assertEquals(13f, r.fibra.consumido, 0.01f)
    }

    @Test
    fun calculaRestanteYProgreso() {
        val r = resumir(listOf(entrada(500f, 0f, 0f, 0f, 0f)), 2000, metas)
        assertEquals(1500f, r.kcal.restante, 0.01f)
        assertEquals(0.25f, r.kcal.progreso, 0.001f)
        assertFalse(r.kcal.superaMeta)
    }

    @Test
    fun siSePasaDeLaMeta_progresoSeQuedaEn1_yRestanteEn0() {
        val r = resumir(listOf(entrada(2500f, 0f, 0f, 0f, 0f)), 2000, metas)
        assertEquals(1f, r.kcal.progreso, 0.001f)
        assertEquals(0f, r.kcal.restante, 0.001f)
        assertTrue(r.kcal.superaMeta)
    }

    @Test
    fun metaCero_noDivideEntreCero() {
        val sinMetas = ResultadoMacros(0, 0, 0, 0)
        val r = resumir(listOf(entrada(100f, 5f, 5f, 5f, 5f)), 0, sinMetas)
        assertEquals(0f, r.kcal.progreso, 0.001f)
    }
}