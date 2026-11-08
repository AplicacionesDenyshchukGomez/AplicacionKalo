package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.EntradaComidaEntity
import com.DanielDMikelG.kalo.data.local.relation.TotalesDia as TotalesDiaRelation
import com.DanielDMikelG.kalo.domain.model.EntradaComida
import com.DanielDMikelG.kalo.domain.model.TotalesDia

fun EntradaComidaEntity.toDomain() = EntradaComida(
    id = id,
    comidaId = comidaId,
    alimentoId = alimentoId,
    cantidadG = cantidadG,
    kcal = kcal,
    prot = prot,
    carb = carb,
    grasa = grasa,
    fibra = fibra
)

fun EntradaComida.toEntity() = EntradaComidaEntity(
    id = id,
    comidaId = comidaId,
    alimentoId = alimentoId,
    cantidadG = cantidadG,
    kcal = kcal,
    prot = prot,
    carb = carb,
    grasa = grasa,
    fibra = fibra
)

fun TotalesDiaRelation.toDomain() = TotalesDia(
    kcal = kcal,
    prot = prot,
    carb = carb,
    grasa = grasa
)