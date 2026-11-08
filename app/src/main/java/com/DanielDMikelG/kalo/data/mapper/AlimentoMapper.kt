package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.AlimentoEntity
import com.DanielDMikelG.kalo.domain.model.Alimento

fun AlimentoEntity.toDomain() = Alimento(
    id = id,
    codigoBarras = codigoBarras,
    nombre = nombre,
    kcal100g = kcal100g,
    prot = prot,
    carb = carb,
    grasa = grasa,
    fibra = fibra,
    origen = origen
)

fun Alimento.toEntity() = AlimentoEntity(
    id = id,
    codigoBarras = codigoBarras,
    nombre = nombre,
    kcal100g = kcal100g,
    prot = prot,
    carb = carb,
    grasa = grasa,
    fibra = fibra,
    origen = origen
)