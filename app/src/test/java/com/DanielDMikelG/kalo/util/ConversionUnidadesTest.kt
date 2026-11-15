package com.DanielDMikelG.kalo.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ConversionUnidadesTest {

    @Test
    fun kgALibras_70kg() {
        assertEquals(154.32f, ConversionUnidades.kgALibras(70f), 0.01f)
    }

    @Test
    fun librasAKg_ida_y_vuelta() {
        val libras = ConversionUnidades.kgALibras(82.5f)
        assertEquals(82.5f, ConversionUnidades.librasAKg(libras), 0.001f)
    }

    @Test
    fun cmAPulgadas_10cm() {
        assertEquals(3.937f, ConversionUnidades.cmAPulgadas(10f), 0.001f)
    }

    @Test
    fun pulgadasACm_1pulgada() {
        assertEquals(2.54f, ConversionUnidades.pulgadasACm(1f), 0.001f)
    }

    @Test
    fun cmAPiesYPulgadas_180cm() {
        assertEquals(PiesPulgadas(5, 11), ConversionUnidades.cmAPiesYPulgadas(180))
    }

    @Test
    fun cmAPiesYPulgadas_170cm() {
        assertEquals(PiesPulgadas(5, 7), ConversionUnidades.cmAPiesYPulgadas(170))
    }

    @Test
    fun piesYPulgadasACm_5pies11() {
        assertEquals(180, ConversionUnidades.piesYPulgadasACm(5, 11))
    }

    @Test
    fun alturaIdaYVuelta_noSeDesvia_masDe1cm() {
        val pp = ConversionUnidades.cmAPiesYPulgadas(175)
        val vuelta = ConversionUnidades.piesYPulgadasACm(pp.pies, pp.pulgadas)
        assertEquals(175f, vuelta.toFloat(), 1f)
    }

    @Test
    fun pesoParaMostrar_metricoNoCambia_imperialConvierte() {
        assertEquals(70f, ConversionUnidades.pesoParaMostrar(70f, SistemaUnidades.METRICO), 0.001f)
        assertEquals(154.32f, ConversionUnidades.pesoParaMostrar(70f, SistemaUnidades.IMPERIAL), 0.01f)
    }

    @Test
    fun pesoParaGuardar_imperialVuelveAKg() {
        assertEquals(70f, ConversionUnidades.pesoParaGuardar(154.32f, SistemaUnidades.IMPERIAL), 0.01f)
    }
}