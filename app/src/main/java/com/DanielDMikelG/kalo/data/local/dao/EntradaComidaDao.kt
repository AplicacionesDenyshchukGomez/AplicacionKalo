package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.DanielDMikelG.kalo.data.local.entity.EntradaComidaEntity
import com.DanielDMikelG.kalo.data.local.relation.TotalesDia
import kotlinx.coroutines.flow.Flow

@Dao
interface EntradaComidaDao {

    @Insert
    suspend fun insertar(entrada: EntradaComidaEntity): Long

    @Update
    suspend fun actualizar(entrada: EntradaComidaEntity)

    @Delete
    suspend fun eliminar(entrada: EntradaComidaEntity)

    @Query("""
        SELECT COALESCE(SUM(e.kcal), 0) AS kcal,
               COALESCE(SUM(e.prot), 0) AS prot,
               COALESCE(SUM(e.carb), 0) AS carb,
               COALESCE(SUM(e.grasa), 0) AS grasa
        FROM entrada_comida e
        INNER JOIN comida c ON c.id = e.comidaId
        WHERE c.usuarioId = :usuarioId AND c.fecha = :fecha
    """)
    fun observarTotalesDia(usuarioId: Long, fecha: String): Flow<TotalesDia>
}