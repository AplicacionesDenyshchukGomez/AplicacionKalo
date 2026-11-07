package com.DanielDMikelG.kalo.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.DanielDMikelG.kalo.data.local.entity.AlimentoEntity
import com.DanielDMikelG.kalo.data.local.entity.ComidaEntity
import com.DanielDMikelG.kalo.data.local.entity.EntradaComidaEntity

data class EntradaConAlimento(
    @Embedded val entrada: EntradaComidaEntity,
    @Relation(parentColumn = "alimentoId", entityColumn = "id")
    val alimento: AlimentoEntity
)

data class ComidaConEntradas(
    @Embedded val comida:   ComidaEntity,
    @Relation(
        entity = EntradaComidaEntity::class,
        parentColumn = "id",
        entityColumn = "comidaId"
    )
    val entradas: List<EntradaConAlimento>
)