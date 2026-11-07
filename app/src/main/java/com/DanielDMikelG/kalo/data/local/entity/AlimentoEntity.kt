package com.DanielDMikelG.kalo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento

@Entity(
    tableName = "alimento",
    indices = [Index(value = ["codigoBarras"], unique = true)]
)
data class AlimentoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val codigoBarras: String? = null,   // null en alimentos creados a mano
    val nombre: String,
    val kcal100g: Float,
    val prot: Float,                   // por 100 g
    val carb: Float,
    val grasa: Float,
    val fibra: Float,
    val origen: OrigenAlimento                  // "MANUAL" u "OPEN_FOOD_FACTS"
)