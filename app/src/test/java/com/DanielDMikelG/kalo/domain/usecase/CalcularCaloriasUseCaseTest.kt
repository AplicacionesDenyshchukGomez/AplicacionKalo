package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.Sexo
import com.DanielDMikelG.kalo.domain.model.TipoObjetivo
import com.DanielDMikelG.kalo.domain.model.Usuario
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

class CalcularCaloriasUseCaseTest {

    private val hoy = LocalDate.of(2026, 11, 6)
    private val calcular = CalcularCaloriasUseCase()

    // Hombre de 30 años, 180 cm, sedentario
    private val hombre = Usuario(
        email = "test@kalo.com",
        nombre = "Test",
        fechaNacimiento = LocalDate.of(1996, 11, 6),
        sexo = Sexo.HOMBRE,
        nivelActividad = NivelActividad.SEDENTARIO,
        altura = 180,
    )

    // Mujer de 25 años, 165 cm, actividad ligera
    private val mujer = Usuario(
        email = "test@kalo.com",
        nombre = "Test",
        fechaNacimiento = LocalDate.of(2001, 11, 6),
        sexo = Sexo.MUJER,
        nivelActividad = NivelActividad.LIGERO,
        altura = 165,
    )

    private fun objetivo(tipo: TipoObjetivo, metaKg: Float, plazo: Int) = Objetivo(
        usuarioId = 1,
        tipo = tipo,
        metaKg = metaKg,
        plazo = plazo,
        kcalDiarias = null,
        aguaMeta = 2000,
    )

    @Test
    fun hombre_mantener() {
        val r = calcular(hombre, 80f, objetivo(TipoObjetivo.MANTENER, 80f, 12), hoy)
        assertEquals(1780, r.tmb)           // 800 + 1125 - 150 + 5
        assertEquals(2136, r.mantenimiento) // 1780 * 1,2
        assertEquals(0, r.ajusteDiario)
        assertEquals(2136, r.kcalObjetivo)
    }

    @Test
    fun mujer_mantener() {
        val r = calcular(mujer, 60f, objetivo(TipoObjetivo.MANTENER, 60f, 12), hoy)
        assertEquals(1345, r.tmb)           // 600 + 1031,25 - 125 - 161
        assertEquals(1850, r.mantenimiento) // 1345,25 * 1,375
        assertEquals(1850, r.kcalObjetivo)
    }

    @Test
    fun hombre_adelgazar_medio_kilo_por_semana() {
        // De 80 a 76 kg en 8 semanas = -0,5 kg/semana = -550 kcal al día
        val r = calcular(hombre, 80f, objetivo(TipoObjetivo.ADELGAZAR, 76f, 8), hoy)
        assertEquals(-550, r.ajusteDiario)
        assertEquals(1586, r.kcalObjetivo)
    }

    @Test
    fun adelgazar_muy_rapido_se_limita_al_suelo() {
        // 10 kg en 5 semanas se limita a 1 kg/semana, y aun así baja de 1.200 kcal
        val sedentaria = mujer.copy(nivelActividad = NivelActividad.SEDENTARIO)
        val r = calcular(sedentaria, 60f, objetivo(TipoObjetivo.ADELGAZAR, 50f, 5), hoy)
        assertEquals(1200, r.kcalObjetivo)
        assertEquals(1200 - r.mantenimiento, r.ajusteDiario)
    }

    @Test
    fun ganar_masa_con_meta_incoherente_usa_ritmo_por_defecto() {
        // Meta menor que el peso actual: se usa +0,25 kg/semana = +275 kcal
        val r = calcular(hombre, 80f, objetivo(TipoObjetivo.GANAR_MASA, 70f, 12), hoy)
        assertEquals(275, r.ajusteDiario)
        assertEquals(2411, r.kcalObjetivo)
    }
}