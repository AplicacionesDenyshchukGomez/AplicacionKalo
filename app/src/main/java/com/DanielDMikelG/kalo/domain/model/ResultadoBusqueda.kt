package com.DanielDMikelG.kalo.domain.model

/** Mínimo de caracteres para lanzar una búsqueda online. */
const val MIN_CARACTERES_BUSQUEDA = 3

sealed interface ResultadoBusqueda {
    data class Encontrados(val alimentos: List<Alimento>) : ResultadoBusqueda
    data object SinResultados : ResultadoBusqueda
    data object TextoCorto : ResultadoBusqueda
    data object SinConexion : ResultadoBusqueda
    data class Error(val mensaje: String) : ResultadoBusqueda
}