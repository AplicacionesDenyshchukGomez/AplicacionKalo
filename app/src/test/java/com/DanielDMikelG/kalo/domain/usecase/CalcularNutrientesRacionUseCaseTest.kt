package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class CalcularNutrientesRacionUseCaseTest {

    private val calcular = CalcularNutrientesRacionUseCase()

    private val yogur = Alimento(
        codigoBarras = "8412345678905", nombre = "Yogur griego",
        kcal100g = 57f, prot = 9.2f, carb = 3.9f, grasa = 0.4f, fibra = 0f,
        origen = OrigenAlimento.OPEN_FOOD_FACTS,
    )

    @Test
    fun `100 gramos devuelven los valores por 100 g`() {
        val r = calcular(yogur, 100f)!!
        assertEquals(57f, r.kcal, 0.01f)
        assertEquals(9.2f, r.proteina, 0.01f)
        assertEquals(3.9f, r.carbohidratos, 0.01f)
        assertEquals(0.4f, r.grasa, 0.01f)
    }

    @Test
    fun `240 gramos de yogur`() {
        val r = calcular(yogur, 240f)!!
        assertEquals(136.8f, r.kcal, 0.01f)
        assertEquals(22.08f, r.proteina, 0.01f)
        assertEquals(137, r.kcalRedondeadas)
    }

    @Test
    fun `50 gramos son la mitad`() {
        val r = calcular(yogur, 50f)!!
        assertEquals(28.5f, r.kcal, 0.01f)
    }

    @Test
    fun `fibra cero se queda en cero`() {
        assertEquals(0f, calcular(yogur, 300f)!!.fibra, 0.0001f)
    }

    @Test
    fun `gramos cero o negativos no son validos`() {
        assertNull(calcular(yogur, 0f))
        assertNull(calcular(yogur, -10f))
    }

    @Test
    fun `gramos no numericos no son validos`() {
        assertNull(calcular(yogur, Float.NaN))
        assertNull(calcular(yogur, Float.POSITIVE_INFINITY))
    }

    @Test
    fun `el limite maximo es valido y pasarse no`() {
        assertNotNull(calcular(yogur, 5000f))
        assertNull(calcular(yogur, 5000.1f))
    }
}