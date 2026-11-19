package com.DanielDMikelG.kalo.data.repository

import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.entity.RachaEntity
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.domain.model.Racha
import com.DanielDMikelG.kalo.domain.usecase.CalcularRachaUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class RachaRepository(private val db: KaloDatabase) {

    private val calcularRacha = CalcularRachaUseCase()

    fun observarRacha(usuarioId: Long): Flow<Racha> =
        db.comidaDao().observarFechasConRegistros(usuarioId).map { fechas ->
            val actual = calcularRacha(fechas.map { LocalDate.parse(it) })
            val previa = db.rachaDao().obtener(usuarioId)

            val nueva = RachaEntity(
                usuarioId = usuarioId,
                diasActuales = actual,
                mejorRacha = maxOf(previa?.mejorRacha ?: 0, actual),
                comodines = previa?.comodines ?: 0
            )
            if (nueva != previa) db.rachaDao().guardar(nueva)
            nueva.toDomain()
        }
}