package com.DanielDMikelG.kalo.domain.model

/** Lo que recibe la pantalla al buscar un alimento por código. Nunca lanza excepciones. */
sealed interface ResultadoAlimento {
    /** [desdeCache] es true si salió de la base de datos local sin llamar a la red. */
    data class Encontrado(val alimento: Alimento, val desdeCache: Boolean) : ResultadoAlimento
    data object CodigoNoValido : ResultadoAlimento
    data object NoEncontrado : ResultadoAlimento
    data object DatosIncompletos : ResultadoAlimento
    data object SinConexion : ResultadoAlimento
    data class Error(val mensaje: String) : ResultadoAlimento
}