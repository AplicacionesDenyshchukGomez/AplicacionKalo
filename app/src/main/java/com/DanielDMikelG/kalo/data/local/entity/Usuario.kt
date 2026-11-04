package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.Period

@Entity(
    tableName = "usuario",
    indices = [Index(value = ["email"], unique = true)] //impide que haya dos usuarios con el mismo email
)
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, //room genera el id automaticamente
    val email: String,
    val nombre: String,
    val fechaNacimiento: String,   // "2000-05-14"
    val sexo: String,              // "hombre", "mujer" u "otro"
    val altura: Int                // en cm
) {
    fun calcularEdad(): Int =
        Period.between(LocalDate.parse(fechaNacimiento), LocalDate.now()).years
}