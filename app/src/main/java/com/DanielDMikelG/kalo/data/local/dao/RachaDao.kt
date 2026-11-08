package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.DanielDMikelG.kalo.data.local.entity.RachaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RachaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(racha: RachaEntity)

    @Query("SELECT * FROM racha WHERE usuarioId = :usuarioId")
    fun observar(usuarioId: Long): Flow<RachaEntity?>

    @Query("SELECT * FROM racha WHERE usuarioId = :usuarioId")
    suspend fun obtener(usuarioId: Long): RachaEntity?
}