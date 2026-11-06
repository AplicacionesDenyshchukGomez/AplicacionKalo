package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.RegistroPesoEntity

fun RegistroPesoEntity.toDomain() = RegistroPesoEntity(
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