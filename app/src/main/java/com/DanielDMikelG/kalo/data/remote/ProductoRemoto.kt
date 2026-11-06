package com.DanielDMikelG.kalo.data.remote

import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento

data class ProductoRemoto(
    val alimento: Alimento,
    val alergenos: List<String>,
)

sealed interface ResultadoProducto {
    data class Encontrado(val producto: ProductoRemoto) : ResultadoProducto
    data object NoEncontrado : ResultadoProducto
    data object DatosIncompletos : ResultadoProducto
    data object SinConexion : ResultadoProducto
    data class Error(val mensaje: String) : ResultadoProducto
}

fun ProductoDto.toProductoRemoto(codigoBuscado: String): ProductoRemoto? {
    val n = nutriments ?: return null
    val kcal = n.kcal100g ?: return null
    val nombreBase = nombre?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val marca = brands?.substringBefore(',')?.trim()?.takeIf { it.isNotEmpty() }

    return ProductoRemoto(
        alimento = Alimento(
            codigoBarras = code ?: codigoBuscado,
            nombre = if (marca != null) "$nombreBase ($marca)" else nombreBase,
            kcal100g = kcal,
            prot = n.prot100g ?: 0f,
            carb = n.carb100g ?: 0f,
            grasa = n.grasa100g ?: 0f,
            fibra = n.fibra100g ?: 0f,
            origen = OrigenAlimento.OPEN_FOOD_FACTS,
        ),
        alergenos = alergenos.orEmpty().map { it.substringAfter(':') },
    )
}