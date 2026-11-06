package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.ObjetivoEntity
import com.DanielDMikelG.kalo.domain.model.Objetivo

fun ObjetivoEntity.toDomain() = Objetivo(
    id = id,
    usuarioId = usuarioId,
    tipo = tipo,
    metaKg = metaKg,
    plazo = plazo,
    kcalDiarias = kcalDiarias,
    aguaMeta = aguaMeta
)

fun Objetivo.toEntity() = ObjetivoEntity(
    id = id,
    usuarioId = usuarioId,
    tipo = tipo,
    metaKg = metaKg,
    plazo = plazo,
    kcalDiarias = kcalDiarias,
    aguaMeta = aguaMeta
)