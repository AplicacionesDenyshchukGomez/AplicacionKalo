package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Sexo
import java.time.LocalDate
import java.time.Period


@Entity(
    tableName = "usuario",
    indices = [Index(value = ["email"], unique = true)] //impide que haya dos usuarios con el mismo email
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, //room genera el id automaticamente
    val email: String,
    val nombre: String,
    val fechaNacimiento: LocalDate,
    val sexo: Sexo,
    val nivelActividad: NivelActividad,
    val altura: Int
) {
    fun edad(hoy: LocalDate = LocalDate.now()): Int =
        Period.between(fechaNacimiento, hoy).years
}