package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.RegistroAguaEntity
import com.DanielDMikelG.kalo.domain.model.RegistroAgua

fun RegistroAguaEntity.toDomain() = RegistroAgua(
    id = id,
    usuarioId = usuarioId,
    fecha = fecha,
    vasos = vasos
)

fun RegistroAgua.toEntity() = RegistroAguaEntity(
    id = id,
    usuarioId = usuarioId,
    fecha = fecha,
    vasos = vasos
)