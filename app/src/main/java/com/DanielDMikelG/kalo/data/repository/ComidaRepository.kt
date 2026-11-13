package com.DanielDMikelG.kalo.data.repository

import androidx.room.withTransaction
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.Comida
import com.DanielDMikelG.kalo.domain.model.ComidaConEntradas
import com.DanielDMikelG.kalo.domain.model.EntradaComida
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.domain.model.TotalesDia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

class ComidaRepository(private val db: KaloDatabase) {

    private val comidaDao = db.comidaDao()
    private val entradaDao = db.entradaComidaDao()
    private val alimentoDao = db.alimentoDao()

    // ---- Lectura ----

    fun observarDia(usuarioId: Long, fecha: LocalDate = LocalDate.now()): Flow<List<ComidaConEntradas>> =
        comidaDao.observarDia(usuarioId, fecha.toString()).map { lista -> lista.map { it.toDomain() } }

    fun observarTotalesDia(usuarioId: Long, fecha: LocalDate = LocalDate.now()): Flow<TotalesDia> =
        entradaDao.observarTotalesDia(usuarioId, fecha.toString()).map { it.toDomain() }

    fun buscarAlimentos(texto: String): Flow<List<Alimento>> =
        alimentoDao.buscarPorNombre(texto).map { lista -> lista.map { it.toDomain() } }

    suspend fun buscarAlimentoPorCodigo(codigo: String): Alimento? =
        alimentoDao.buscarPorCodigoBarras(codigo)?.toDomain()

    // ---- Escritura ----

    /**
     * Añade un alimento a la comida (desayuno, comida...) de ese día.
     * Crea la comida si no existía y el alimento si todavía no estaba en la base de datos
     * (por ejemplo, uno recién traído de Open Food Facts). Todo en una transacción.
     */
    suspend fun añadirAlimento(
        usuarioId: Long,
        tipo: TipoComida,
        alimento: Alimento,
        cantidadG: Float,
        fecha: LocalDate = LocalDate.now()
    ): Long = db.withTransaction {
        val alimentoGuardado = asegurarAlimento(alimento)

        val comidaId = comidaDao.obtener(usuarioId, fecha.toString(), tipo)?.id
            ?: comidaDao.insertar(Comida(usuario_id = usuarioId, fecha = fecha, tipo = tipo).toEntity())

        entradaDao.insertar(EntradaComida.desde(comidaId, alimentoGuardado, cantidadG).toEntity())
    }

    suspend fun eliminarEntrada(entrada: EntradaComida) =
        entradaDao.eliminar(entrada.toEntity())

    /** Cambia la cantidad de una entrada y recalcula kcal y macros con los datos del alimento. */
    suspend fun editarCantidad(entrada: EntradaComida, nuevaCantidadG: Float) {
        if (!nuevaCantidadG.isFinite() || nuevaCantidadG <= 0f) return
        val alimento = alimentoDao.buscarPorId(entrada.alimentoId)?.toDomain() ?: return
        val nueva = EntradaComida
            .desde(entrada.comidaId, alimento, nuevaCantidadG)
            .copy(id = entrada.id)
        entradaDao.actualizar(nueva.toEntity())
    }

    // Crea un alimento nuevo de forma manual (devuelve el alimento con su id real)
    suspend fun crearAlimento(alimento: Alimento): Alimento = asegurarAlimento(alimento)

    private suspend fun asegurarAlimento(alimento: Alimento): Alimento {
        if (alimento.id != 0L) return alimento
        val existente = alimento.codigoBarras?.let { alimentoDao.buscarPorCodigoBarras(it) }
        if (existente != null) return existente.toDomain()
        return alimento.copy(id = alimentoDao.insertar(alimento.toEntity()))
    }
}