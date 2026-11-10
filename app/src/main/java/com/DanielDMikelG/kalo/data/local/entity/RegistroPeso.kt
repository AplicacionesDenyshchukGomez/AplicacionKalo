package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

class RegistroPeso {

    @Entity(
        tableName = "registro_peso",
        foreignKeys = [ForeignKey(
            entity = UsuarioEntity::class,
            parentColumns = ["id"],
            childColumns = ["usuarioId"],
            onDelete = ForeignKey.CASCADE
        )],
        indices = [Index(value = ["usuarioId", "fecha"], unique = true)]
    )
    data class RegistroPesoEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val usuarioId: Long,
        val fecha: String,    // "2026-11-06"
        val pesoKg: Double
    )
}