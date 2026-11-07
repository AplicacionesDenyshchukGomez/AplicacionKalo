package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.DanielDMikelG.kalo.domain.model.TipoComida

@Entity(
    tableName = "comida",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["usuarioId", "fecha", "tipo"], unique = true)]
)
data class ComidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val fecha: String,       // "2026-11-07"
    val tipo: TipoComida
)