package com.DanielDMikelG.kalo.data.mapper

import com.DanielDMikelG.kalo.data.local.entity.ComidaEntity
import com.DanielDMikelG.kalo.data.local.relation.ComidaConEntradas as ComidaConEntradasRelation
import com.DanielDMikelG.kalo.data.local.relation.EntradaConAlimento as EntradaConAlimentoRelation
import com.DanielDMikelG.kalo.domain.model.Comida
import com.DanielDMikelG.kalo.domain.model.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.EntradaConAlimento
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

fun EntradaConAlimentoRelation.toDomain() = EntradaConAlimento(
    entrada = entrada.toDomain(),      // EntradaComidaEntity -> EntradaComida
    alimento = alimento.toDomain()     // AlimentoEntity -> Alimento
)

fun ComidaConEntradasRelation.toDomain() = ComidaConEntradas(
    comida = comida.toDomain(),        // ComidaEntity -> Comida
    entradas = entradas.map { it.toDomain() }
)