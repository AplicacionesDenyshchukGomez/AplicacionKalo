package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.RegistroPesoEntity
import com.DanielDMikelG.kalo.domain.model.RegistroPeso

fun RegistroPesoEntity.toDomain() = RegistroPeso(
    id = id,
    usuarioId = usuarioId,
    fecha = fecha,
    pesoKg = pesoKg
)

fun RegistroPeso.toEntity() = RegistroPesoEntity(
    id = id,
    usuarioId = usuarioId,
    fecha = fecha,
    pesoKg = pesoKg
)