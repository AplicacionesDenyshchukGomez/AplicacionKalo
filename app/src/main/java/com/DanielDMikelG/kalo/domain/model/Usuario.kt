package com.DanielDMikelG.kalo.domain.model

import java.time.LocalDate
import java.time.Period

enum class Sexo(val etiqueta: String) {
    HOMBRE("Hombre"),
    MUJER("Mujer"),
}

enum class NivelActividad(val etiqueta: String) {
    SEDENTARIO("Sedentario (poco o ningún ejercicio)"),
    LIGERO("Ligero (1-3 días por semana)"),
    MODERADO("Moderado (3-5 días por semana)"),
    ALTO("Alto (6-7 días por semana)"),
}

/** Tabla USUARIO. Altura en cm. La edad se calcula; el peso es el último RegistroPeso. */
data class Usuario(
    val id: Long = 0,
    val email: String,
    val nombre: String,
    val fechaNacimiento: LocalDate,
    val sexo: Sexo,
    val nivelActividad: NivelActividad,
    val altura: Int,
) {
    fun edad(hoy: LocalDate = LocalDate.now()): Int =
        Period.between(fechaNacimiento, hoy).years
}