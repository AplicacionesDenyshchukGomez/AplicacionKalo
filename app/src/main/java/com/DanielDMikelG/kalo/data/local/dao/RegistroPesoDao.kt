package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.DanielDMikelG.kalo.data.local.entity.RegistroPesoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RegistroPesoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(registro: RegistroPesoEntity): Long

    @Delete
    suspend fun eliminar(registro: RegistroPesoEntity)

    @Query("SELECT * FROM registro_peso WHERE usuarioId = :usuarioId ORDER BY fecha ASC")
    fun observarHistorial(usuarioId: Long): Flow<List<RegistroPesoEntity>>

    @Query("SELECT * FROM registro_peso WHERE usuarioId = :usuarioId ORDER BY fecha DESC LIMIT 1")
    fun observarUltimo(usuarioId: Long): Flow<RegistroPesoEntity?>

    @Query("""
        SELECT * FROM registro_peso
        WHERE usuarioId = :usuarioId AND fecha BETWEEN :desde AND :hasta
        ORDER BY fecha ASC
    """)
    fun observarEntreFechas(usuarioId: Long, desde: String, hasta: String): Flow<List<RegistroPesoEntity>>
}