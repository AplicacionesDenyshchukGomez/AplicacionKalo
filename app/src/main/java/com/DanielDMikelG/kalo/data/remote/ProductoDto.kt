package com.DanielDMikelG.kalo.data.remote

import com.google.gson.annotations.SerializedName

data class ProductoRespuestaDto(
    val status: Int?,
    val product: ProductoDto?,
)

data class ProductoDto(
    val code: String?,
    @SerializedName("product_name") val nombre: String?,
    val brands: String?,
    val nutriments: NutrimentosDto?,
    @SerializedName("allergens_tags") val alergenos: List<String>?,
)

data class NutrimentosDto(
    @SerializedName("energy-kcal_100g") val kcal100g: Float?,
    @SerializedName("proteins_100g") val prot100g: Float?,
    @SerializedName("carbohydrates_100g") val carb100g: Float?,
    @SerializedName("fat_100g") val grasa100g: Float?,
    @SerializedName("fiber_100g") val fibra100g: Float?,
)

data class BusquedaRespuestaDto(
    val count: Int?,
    val products: List<ProductoDto>?,
)