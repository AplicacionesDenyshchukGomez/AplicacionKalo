package com.DanielDMikelG.kalo.data.remote

import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val USER_AGENT = "Kalo/0.1 (https://github.com/AplicacionesDenyshchukGomez/AplicacionKalo)" //Para que no quede publico ninguno de nuestros correos electronicos

private fun crearApi(): OpenFoodFactsApi {
    val cliente = OkHttpClient.Builder()
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
}