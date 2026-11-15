package com.DanielDMikelG.kalo.data.remote

import com.DanielDMikelG.kalo.domain.model.MIN_CARACTERES_BUSQUEDA
import com.DanielDMikelG.kalo.domain.model.ResultadoBusqueda
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay

private const val USER_AGENT = "Kalo/0.1 (https://github.com/AplicacionesDenyshchukGomez/AplicacionKalo)" //Para que no quede publico ninguno de nuestros correos electronicos

private fun crearApi(): OpenFoodFactsApi {
    val cliente = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder().header("User-Agent", USER_AGENT).build(),
            )
        }
        .build()
    return Retrofit.Builder()
        .baseUrl(OpenFoodFactsApi.BASE_URL)
        .client(cliente)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(OpenFoodFactsApi::class.java)
}

class OpenFoodFactsClient(
    private val api: OpenFoodFactsApi = crearApi(),
) {
    suspend fun buscarPorCodigo(codigo: String): ResultadoProducto {
        val limpio = codigo.trim()
        return try {
            val respuesta = api.obtenerProducto(limpio)
            val producto = respuesta.product
            if (respuesta.status != 1 || producto == null) {
                ResultadoProducto.NoEncontrado
            } else {
                producto.toProductoRemoto(limpio)
                    ?.let { ResultadoProducto.Encontrado(it) }
                    ?: ResultadoProducto.DatosIncompletos
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            if (e.code() == 404) ResultadoProducto.NoEncontrado
            else ResultadoProducto.Error("Error del servidor (${e.code()})")
        } catch (e: IOException) {
            ResultadoProducto.SinConexion
        } catch (e: Exception) {
            ResultadoProducto.Error(e.message ?: "Error desconocido")
        }
    }

    suspend fun buscarPorNombre(texto: String): ResultadoBusqueda {
        val limpio = texto.trim()
        if (limpio.length < MIN_CARACTERES_BUSQUEDA) return ResultadoBusqueda.TextoCorto
        return try {
            val alimentos = consultarConReintento(limpio)
                .products
                .orEmpty()
                // Sin código no podemos abrir la ficha, y sin kcal o nombre no sirve de nada.
                .mapNotNull { dto -> dto.code?.let { dto.toProductoRemoto(it) } }
                .map { it.alimento }
                .distinctBy { it.codigoBarras }
            if (alimentos.isEmpty()) ResultadoBusqueda.SinResultados
            else ResultadoBusqueda.Encontrados(alimentos)
        } catch (e: CancellationException) {
            throw e
        } catch (e: HttpException) {
            when (e.code()) {
                429 -> ResultadoBusqueda.Error(
                    "Has hecho demasiadas búsquedas seguidas. Espera un minuto e inténtalo de nuevo.",
                )
                503 -> ResultadoBusqueda.Error(
                    "Open Food Facts está saturado ahora mismo. Inténtalo de nuevo en unos segundos.",
                )
                else -> ResultadoBusqueda.Error("Error del servidor (${e.code()})")
            }
        } catch (e: SocketTimeoutException) {
            ResultadoBusqueda.Error(
                "Open Food Facts tarda en responder. Prueba con un nombre más concreto, por ejemplo \"pan integral\".",
            )
        } catch (e: IOException) {
            ResultadoBusqueda.SinConexion
        } catch (e: Exception) {
            ResultadoBusqueda.Error(e.message ?: "Error desconocido")
        }
    }

    /** Open Food Facts devuelve 503 a ratos por saturación: se reintenta una vez tras una pausa corta. */
    private suspend fun consultarConReintento(texto: String): BusquedaRespuestaDto =
        try {
            api.buscarPorNombre(texto)
        } catch (e: HttpException) {
            if (e.code() != 503) throw e
            delay(2_000)
            api.buscarPorNombre(texto)
        }

}