package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.DanielDMikelG.kalo.data.local.entity.ComidaEntity
import com.DanielDMikelG.kalo.data.local.relation.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.TipoComida
import kotlinx.coroutines.flow.Flow

@Dao
interface ComidaDao {

    @Insert
    suspend fun insertar(comida: ComidaEntity): Long

    @Query("""
        SELECT * FROM comida
        WHERE usuarioId = :usuarioId AND fecha = :fecha AND tipo = :tipo
    """)
    suspend fun obtener(usuarioId: Long, fecha: String, tipo: TipoComida): ComidaEntity?

    @Transaction
    @Query("SELECT * FROM comida WHERE usuarioId = :usuarioId AND fecha = :fecha")
    fun observarDia(usuarioId: Long, fecha: String): Flow<List<ComidaConEntradas>>

    @Delete
    suspend fun eliminar(comida: ComidaEntity)
}