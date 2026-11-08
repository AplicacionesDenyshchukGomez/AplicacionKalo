package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "racha",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class RachaEntity(
    @PrimaryKey val usuarioId: Long,   // es clave primaria y foránea a la vez
    val diasActuales: Int,
    val mejorRacha: Int,
    val comodines: Int
)