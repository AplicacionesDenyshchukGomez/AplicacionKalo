package com.DanielDMikelG.kalo.ui.screens.register

import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObjetivoValidoTest {
    private fun estado(tipo: TipoObjetivo?, plazo: String, meta: String = "", peso: String = "80") =
        RegistroUiState(tipoObjetivo = tipo, plazo = plazo, metaKg = meta, peso = peso)

    @Test fun `mantener con plazo valido es valido`() {
        assertTrue(estado(TipoObjetivo.MANTENER, "4").objetivoValido)
    }

    @Test fun `mantener con plazo cero no es valido`() {
        assertFalse(estado(TipoObjetivo.MANTENER, "0").objetivoValido)
    }

    @Test fun `mantener con plazo vacio o pasado de rango no es valido`() {
        assertFalse(estado(TipoObjetivo.MANTENER, "").objetivoValido)
        assertFalse(estado(TipoObjetivo.MANTENER, "105").objetivoValido)
    }

    @Test fun `adelgazar necesita meta menor y plazo valido`() {
        assertTrue(estado(TipoObjetivo.ADELGAZAR, "8", meta = "75").objetivoValido)
        assertFalse(estado(TipoObjetivo.ADELGAZAR, "8", meta = "85").objetivoValido)
    }

    @Test fun `ganar masa acepta plazo de 104 semanas`() {
        assertTrue(estado(TipoObjetivo.GANAR_MASA, "104", meta = "85").objetivoValido)
    }

    @Test fun `sin tipo de objetivo no es valido`() {
        assertFalse(estado(null, "4").objetivoValido)
    }
}