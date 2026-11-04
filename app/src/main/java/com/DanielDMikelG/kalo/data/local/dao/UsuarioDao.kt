package com.DanielDMikelG.kalo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.DanielDMikelG.kalo.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Update
    suspend fun actualizar(usuario: UsuarioEntity)

    @Delete
    suspend fun eliminar(usuario: UsuarioEntity) //Para la opcion de eliminar cuenta

    @Query("SELECT * FROM usuario WHERE id = :id")
    fun observarPorId(id: Long): Flow<UsuarioEntity?>

    @Query("SELECT * FROM usuario WHERE email = :email LIMIT 1")
    suspend fun buscarPorEmail(email: String): UsuarioEntity? //Para comprobar si el email existe durante el login
}