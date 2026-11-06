package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "objetivo",
    foreignKeys = [ForeignKey(
        entity = UsuarioEntity::class,
        parentColumns = ["id"],
        childColumns = ["usuarioId"],
        onDelete = ForeignKey.CASCADE  //Si borras el usuario se borra el objetivo
    )],
    indices = [Index(value = ["usuarioId"], unique = true)]  //Un solo objetivo por usuario
)
data class ObjetivoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val usuarioId: Long,
    val tipo: String,   // GANAR, MANTENER, ADELGAZAR
    val metaKg: Double,       // peso objetivo en kg
    val plazo: Int,           // en semanas
    val kcalDiarias: Int,
    val aguaMeta: Int         // en ml (ej. 2000)
)