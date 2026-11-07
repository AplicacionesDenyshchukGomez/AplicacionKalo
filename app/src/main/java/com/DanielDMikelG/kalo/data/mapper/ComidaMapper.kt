package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.ComidaEntity
import com.DanielDMikelG.kalo.data.local.relation.ComidaConEntradas
import com.DanielDMikelG.kalo.data.local.relation.EntradaConAlimento
import com.DanielDMikelG.kalo.domain.model.Comida
import java.time.LocalDate

fun ComidaEntity.toDomain() = Comida(
    id = id,
    usuario_id = usuarioId,
    fecha = LocalDate.parse(fecha),
    tipo = tipo
)

fun Comida.toEntity() = ComidaEntity(
    id = id,
    usuarioId = usuario_id,
    fecha = fecha.toString(),
    tipo = tipo
)

fun EntradaConAlimento.toDomain() = EntradaConAlimento(
    entrada = entrada,
    alimento = alimento
)

fun ComidaConEntradas.toDomain() = ComidaConEntradas(
    comida = comida,
    entradas = entradas.map { it.toDomain() }
)