package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.RachaEntity
import com.DanielDMikelG.kalo.domain.model.Racha

fun RachaEntity.toDomain() = Racha(
    usuarioId = usuarioId,
    diasActuales = diasActuales,
    mejorRacha = mejorRacha,
    comodines = comodines
)

fun Racha.toEntity() = RachaEntity(
    usuarioId = usuarioId,
    diasActuales = diasActuales,
    mejorRacha = mejorRacha,
    comodines = comodines
)