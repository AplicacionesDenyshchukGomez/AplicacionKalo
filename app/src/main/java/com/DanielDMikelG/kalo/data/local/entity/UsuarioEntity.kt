package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.DanielDMikelG.kalo.domain.model.NivelActividad
import com.DanielDMikelG.kalo.domain.model.Sexo
import java.time.LocalDate
import java.time.Period


@Entity(
    tableName = "usuario",
    indices = [Index(value = ["email"], unique = true),
        Index(value = ["nombre"], unique = true)
    ] //impide que haya dos usuarios con el mismo email y mismo nombre
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, //room genera el id automaticamente
    @ColumnInfo(collate = ColumnInfo.NOCASE) val email: String,
    @ColumnInfo(collate = ColumnInfo.NOCASE) val nombre: String,
    val fechaNacimiento: String,
    val sexo: Sexo,
    val nivelActividad: NivelActividad,
    val altura: Int
) {
    fun calcularEdad(): Int =
        Period.between(LocalDate.parse(fechaNacimiento), LocalDate.now()).years
}