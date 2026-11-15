package com.DanielDMikelG.kalo.data.repository

import androidx.room.withTransaction
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.RegistroAgua
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class AguaRepository(private val db: KaloDatabase) {

    private val dao = db.registroAguaDao()

    // Vasos bebidos ese día (0 si todavía no hay registro)
    fun observarVasos(usuarioId: Long, fecha: LocalDate = LocalDate.now()): Flow<Int> =
        dao.observarDia(usuarioId, fecha.toString()).map { it?.vasos ?: 0 }

    fun observarEntreFechas(usuarioId: Long, desde: LocalDate, hasta: LocalDate): Flow<List<RegistroAgua>> =
        dao.observarEntreFechas(usuarioId, desde.toString(), hasta.toString())
            .map { lista -> lista.map { it.toDomain() } }

    suspend fun añadirVaso(usuarioId: Long, fecha: LocalDate = LocalDate.now()) =
        cambiarVasos(usuarioId, fecha, +1)

    suspend fun quitarVaso(usuarioId: Long, fecha: LocalDate = LocalDate.now()) =
        cambiarVasos(usuarioId, fecha, -1)

    // Leer y escribir en una transacción para que dos toques seguidos no se pisen
    private suspend fun cambiarVasos(usuarioId: Long, fecha: LocalDate, delta: Int) {
        db.withTransaction {
            val actual = dao.obtenerDia(usuarioId, fecha.toString())?.toDomain()
                ?: RegistroAgua(usuarioId = usuarioId, fecha = fecha, vasos = 0)
            val nuevo = actual.copy(vasos = (actual.vasos + delta).coerceAtLeast(0))
            dao.guardar(nuevo.toEntity())
        }
    }
}