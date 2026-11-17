package com.DanielDMikelG.kalo.data.repository

import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class PesoRepository(db: KaloDatabase) {

    private val dao = db.registroPesoDao()

    fun observarHistorial(usuarioId: Long): Flow<List<RegistroPeso>> =
        dao.observarHistorial(usuarioId).map { lista -> lista.map { it.toDomain() } }

    fun observarUltimo(usuarioId: Long): Flow<RegistroPeso?> =
        dao.observarUltimo(usuarioId).map { it?.toDomain() }

    fun observarEntreFechas(usuarioId: Long, desde: LocalDate, hasta: LocalDate): Flow<List<RegistroPeso>> =
        dao.observarEntreFechas(usuarioId, desde.toString(), hasta.toString())
            .map { lista -> lista.map { it.toDomain() } }

    // Un pesaje por día: si ya existe uno en esa fecha, el índice único + REPLACE lo sustituye
    suspend fun registrarPeso(usuarioId: Long, pesoKg: Float, fecha: LocalDate = LocalDate.now()) {
        dao.guardar(RegistroPeso(usuarioId = usuarioId, fecha = fecha, pesoKg = pesoKg).toEntity())
    }

    suspend fun eliminar(registro: RegistroPeso) = dao.eliminar(registro.toEntity())
}