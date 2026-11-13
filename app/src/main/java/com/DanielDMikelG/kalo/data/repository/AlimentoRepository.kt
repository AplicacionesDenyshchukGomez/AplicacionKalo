package com.DanielDMikelG.kalo.data.repository

import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.dao.AlimentoDao
import com.DanielDMikelG.kalo.data.mapper.toDomain
import com.DanielDMikelG.kalo.data.mapper.toEntity
import com.DanielDMikelG.kalo.data.remote.OpenFoodFactsClient
import com.DanielDMikelG.kalo.data.remote.ResultadoProducto
import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento
import com.DanielDMikelG.kalo.domain.model.ResultadoAlimento
import com.DanielDMikelG.kalo.domain.model.ResultadoBusqueda
import com.DanielDMikelG.kalo.util.ValidadorCodigoBarras
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Única puerta de entrada a los alimentos. La pantalla no habla nunca con Room ni con la API:
 * primero se mira en local y solo se llama a Open Food Facts si el alimento no está guardado.
 */
class AlimentoRepository(
    private val dao: AlimentoDao,
    private val remoto: OpenFoodFactsClient = OpenFoodFactsClient(),
) {

    constructor(db: KaloDatabase) : this(db.alimentoDao())

    /** Busca por código de barras: caché local primero, red después, y guarda lo que encuentre. */
    suspend fun buscarPorCodigo(codigo: String): ResultadoAlimento {
        val limpio = ValidadorCodigoBarras.limpiar(codigo)
        if (!ValidadorCodigoBarras.esValido(limpio)) return ResultadoAlimento.CodigoNoValido

        for (candidato in variantes(limpio)) {
            dao.buscarPorCodigoBarras(candidato)?.let {
                return ResultadoAlimento.Encontrado(it.toDomain(), desdeCache = true)
            }
        }

        return when (val resultado = remoto.buscarPorCodigo(limpio)) {
            is ResultadoProducto.Encontrado ->
                ResultadoAlimento.Encontrado(guardar(resultado.producto.alimento), desdeCache = false)
            ResultadoProducto.NoEncontrado -> ResultadoAlimento.NoEncontrado
            ResultadoProducto.DatosIncompletos -> ResultadoAlimento.DatosIncompletos
            ResultadoProducto.SinConexion -> ResultadoAlimento.SinConexion
            is ResultadoProducto.Error -> ResultadoAlimento.Error(resultado.mensaje)
        }
    }

    /** Busca por nombre solo en lo que ya está guardado (alimentos manuales y escaneados). */
    fun buscarPorNombre(texto: String): Flow<List<Alimento>> {
        val limpio = texto.trim()
        if (limpio.isEmpty()) return flowOf(emptyList())
        return dao.buscarPorNombre(limpio).map { lista -> lista.map { it.toDomain() } }
    }

    suspend fun obtener(id: Long): Alimento? = dao.buscarPorId(id)?.toDomain()

    /** Alimento creado por el usuario: siempre origen MANUAL y sin código de barras. */
    suspend fun crearManual(alimento: Alimento): Long =
        dao.insertar(
            alimento.copy(id = 0, codigoBarras = null, origen = OrigenAlimento.MANUAL).toEntity(),
        )

    // Si ya existe un alimento con ese código (por ejemplo, porque OFF lo devolvió en otra forma
    // que la escaneada), nos quedamos con el guardado y no insertamos un duplicado.
    private suspend fun guardar(alimento: Alimento): Alimento {
        alimento.codigoBarras?.let { codigo ->
            dao.buscarPorCodigoBarras(codigo)?.let { return it.toDomain() }
        }
        return alimento.copy(id = dao.insertar(alimento.toEntity()))
    }

    // UPC-A (12 dígitos) y EAN-13 con un cero delante son el mismo producto
    private fun variantes(codigo: String): List<String> = listOfNotNull(
        codigo,
        if (codigo.length == 12) "0$codigo" else null,
        if (codigo.length == 13 && codigo.startsWith("0")) codigo.drop(1) else null,
    )

    suspend fun buscarEnLinea(texto: String): ResultadoBusqueda = remoto.buscarPorNombre(texto)
}