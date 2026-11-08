package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.DanielDMikelG.kalo.data.local.entity.ObjetivoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ObjetivoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(objetivo: ObjetivoEntity): Long

    @Update
    suspend fun actualizar(objetivo: ObjetivoEntity)

    @Query("SELECT * FROM objetivo WHERE usuarioId = :usuarioId LIMIT 1")
    fun observarPorUsuario(usuarioId: Long): Flow<ObjetivoEntity?>

    @Query("SELECT * FROM objetivo WHERE usuarioId = :usuarioId LIMIT 1")
    suspend fun obtenerPorUsuario(usuarioId: Long): ObjetivoEntity?

    @Query("DELETE FROM objetivo WHERE usuarioId = :usuarioId")
    suspend fun eliminarPorUsuario(usuarioId: Long)
}