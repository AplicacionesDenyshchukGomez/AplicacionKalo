package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.TipoComida
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularRepartoComidasUseCaseTest {

    private val repartir = CalcularRepartoComidasUseCase()

    @Test
    fun reparto_2300kcal() {
        val r = repartir(2300)
        assertEquals(575, r.getValue(TipoComida.DESAYUNO))
        assertEquals(805, r.getValue(TipoComida.COMIDA))
        assertEquals(690, r.getValue(TipoComida.CENA))
        assertEquals(230, r.getValue(TipoComida.SNACK))
    }

    @Test
    fun siempreSumaExactamenteElObjetivo() {
        for (kcal in 1000..4000 step 37) {
            assertEquals(kcal, repartir(kcal).values.sum())
        }
    }

    @Test
    fun elSobranteDelRedondeoVaALaComida() {
        val r = repartir(2001)
        assertEquals(500, r.getValue(TipoComida.DESAYUNO))
        assertEquals(701, r.getValue(TipoComida.COMIDA))
        assertEquals(600, r.getValue(TipoComida.CENA))
        assertEquals(200, r.getValue(TipoComida.SNACK))
    }

    @Test
    fun elOrdenEsDesayunoComidaCenaSnack() {
        assertEquals(
            listOf(TipoComida.DESAYUNO, TipoComida.COMIDA, TipoComida.CENA, TipoComida.SNACK),
            repartir(2000).keys.toList()
        )
    }

    @Test
    fun conCeroKcal_todoEsCero() {
        assertEquals(0, repartir(0).values.sum())
    }

    @Test(expected = IllegalArgumentException::class)
    fun kcalNegativas_lanzaError() {
        repartir(-1)
    }
}