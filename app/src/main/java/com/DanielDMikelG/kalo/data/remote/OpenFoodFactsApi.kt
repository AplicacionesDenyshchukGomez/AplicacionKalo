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

    companion object {
        const val BASE_URL = "https://world.openfoodfacts.org/"
        const val CAMPOS = "code,product_name,brands,nutriments,allergens_tags"
    }
}