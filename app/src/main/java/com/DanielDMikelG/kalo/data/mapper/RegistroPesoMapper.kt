package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.RegistroPesoEntity
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import java.time.LocalDate

fun RegistroPesoEntity.toDomain() = RegistroPeso(
    id = id,
    usuarioId = usuarioId,
    fecha = LocalDate.parse(fecha),
    pesoKg = pesoKg
)

fun RegistroPeso.toEntity() = RegistroPesoEntity(
    id = id,
    usuarioId = usuarioId,
    fecha = fecha.toString(),
    pesoKg = pesoKg
)