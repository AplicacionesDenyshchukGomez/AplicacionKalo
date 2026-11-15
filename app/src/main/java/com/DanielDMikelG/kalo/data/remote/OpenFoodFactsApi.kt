package com.DanielDMikelG.kalo.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface OpenFoodFactsApi {

    @GET("api/v2/product/{codigo}.json")
    suspend fun obtenerProducto(
        @Path("codigo") codigo: String,
        @Query("fields") campos: String = CAMPOS,
    ): ProductoRespuestaDto

    /** Búsqueda por texto (endpoint clásico: la API v2 no tiene búsqueda por texto). */
    @GET("cgi/search.pl")
    suspend fun buscarPorNombre(
        @Query("search_terms") texto: String,
        @Query("search_simple") simple: Int = 1,
        @Query("action") accion: String = "process",
        @Query("json") json: Int = 1,
        @Query("page_size") tamanoPagina: Int = TAMANO_PAGINA_BUSQUEDA,
        @Query("sort_by") orden: String = "unique_scans_n", // los más populares primero
        @Query("fields") campos: String = CAMPOS_BUSQUEDA,
    ): BusquedaRespuestaDto

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
        const val CAMPOS = "code,product_name,brands,nutriments,allergens_tags"
        const val CAMPOS_BUSQUEDA = "code,product_name,brands,nutriments"
        const val TAMANO_PAGINA_BUSQUEDA = 20
    }
}