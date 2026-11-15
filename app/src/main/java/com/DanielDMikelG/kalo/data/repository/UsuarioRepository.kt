package com.DanielDMikelG.kalo.data.repository

import androidx.room.withTransaction
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.Objetivo
import com.DanielDMikelG.kalo.domain.model.RegistroPeso
import com.DanielDMikelG.kalo.domain.model.Usuario
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class UsuarioRepository(private val db: KaloDatabase) {

    private val usuarioDao = db.usuarioDao()
    private val objetivoDao = db.objetivoDao()
    private val registroPesoDao = db.registroPesoDao()

    suspend fun buscarPorEmailONombre(texto: String): Usuario? =
        usuarioDao.buscarPorEmailONombre(texto)?.toDomain()

    suspend fun existeEmail(email: String): Boolean =
        usuarioDao.buscarPorEmail(email) != null

    suspend fun existeNombre(nombre: String): Boolean =
        usuarioDao.buscarPorNombre(nombre) != null

    // Guarda usuario, objetivo y primer pesaje juntos: o se guardan los tres, o ninguno
    suspend fun registrar(usuario: Usuario, objetivo: Objetivo, pesoInicialKg: Float): Long =
        db.withTransaction {
            val id = usuarioDao.insertar(usuario.toEntity())
            objetivoDao.guardar(objetivo.copy(usuarioId = id).toEntity())
            registroPesoDao.guardar(
                RegistroPeso(usuarioId = id, fecha = LocalDate.now(), pesoKg = pesoInicialKg).toEntity()
            )
            id
        }

    fun observarUsuario(id: Long): Flow<Usuario?> =
        usuarioDao.observarPorId(id).map { it?.toDomain() }

    fun observarObjetivo(usuarioId: Long): Flow<Objetivo?> =
        objetivoDao.observarPorUsuario(usuarioId).map { it?.toDomain() }

    fun observarUltimoPeso(usuarioId: Long): Flow<RegistroPeso?> =
        registroPesoDao.observarUltimo(usuarioId).map { it?.toDomain() }

    // Usuario y objetivo se actualizan juntos: o se guardan los dos, o ninguno
    suspend fun actualizarPerfil(usuario: Usuario, objetivo: Objetivo) =
        db.withTransaction {
            usuarioDao.actualizar(usuario.toEntity())
            objetivoDao.actualizar(objetivo.toEntity())
        }
}