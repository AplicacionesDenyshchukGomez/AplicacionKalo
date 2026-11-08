package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.DanielDMikelG.kalo.data.local.entity.RegistroAguaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroAguaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroAguaEntity): Long

    @Query("SELECT * FROM registro_agua WHERE usuarioId = :usuarioId AND fecha = :fecha")
    fun observarDia(usuarioId: Long, fecha: String): Flow<RegistroAguaEntity?>

    @Query("SELECT * FROM registro_agua WHERE usuarioId = :usuarioId AND fecha = :fecha")
    suspend fun obtenerDia(usuarioId: Long, fecha: String): RegistroAguaEntity?

    @Query("""
        SELECT * FROM registro_agua
        WHERE usuarioId = :usuarioId AND fecha BETWEEN :desde AND :hasta
        ORDER BY fecha ASC
    """)
    fun observarEntreFechas(usuarioId: Long, desde: String, hasta: String): Flow<List<RegistroAguaEntity>>
}