package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "entrada_comida",
    foreignKeys = [
        ForeignKey(
            entity = ComidaEntity::class,
            parentColumns = ["id"],
            childColumns = ["comidaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = AlimentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["alimentoId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("comidaId"), Index("alimentoId")]
)
data class EntradaComidaEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val comidaId: Long,
    val alimentoId: Long,
    val cantidadG: Float,
    val kcal: Float,      // ya calculadas para esa cantidad
    val prot: Float,
    val carb: Float,
    val grasa: Float,
    val fibra: Float
)