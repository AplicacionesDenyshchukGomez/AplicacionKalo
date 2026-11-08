package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.DanielDMikelG.kalo.data.local.entity.AlimentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AlimentoDao {

    @Insert
    suspend fun insertar(alimento: AlimentoEntity): Long

    @Update
    suspend fun actualizar(alimento: AlimentoEntity)

    @Query("SELECT * FROM alimento WHERE id = :id")
    suspend fun buscarPorId(id: Long): AlimentoEntity?

    @Query("SELECT * FROM alimento WHERE codigoBarras = :codigo LIMIT 1")
    suspend fun buscarPorCodigoBarras(codigo: String): AlimentoEntity?

    @Query("SELECT * FROM alimento WHERE nombre LIKE '%' || :texto || '%' ORDER BY nombre LIMIT 50")
    fun buscarPorNombre(texto: String): Flow<List<AlimentoEntity>>
}