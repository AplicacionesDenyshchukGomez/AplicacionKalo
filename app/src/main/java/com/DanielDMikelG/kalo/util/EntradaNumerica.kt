package com.DanielDMikelG.kalo.util

/** Filtros para campos que solo admiten números. Se llaman desde el onValueChange. */
object EntradaNumerica {

    /** Solo dígitos 0-9, con un máximo de cifras. */
    fun entero(texto: String, maxDigitos: Int = 4): String =
        texto.filter { it in '0'..'9' }.take(maxDigitos)

    /**
     * Número con decimales: dígitos y como mucho un separador (coma o punto).
     * No empieza por el separador y limita las cifras antes y después.
     */
    fun decimal(texto: String, maxEnteros: Int = 3, maxDecimales: Int = 1): String {
        val resultado = StringBuilder()
        var hayCifraEntera = false
        var haySeparador = false
        var enteros = 0
        var decimales = 0
        for (c in texto) {
            when {
                c in '0'..'9' && !haySeparador && enteros < maxEnteros -> {
                    resultado.append(c); enteros++; hayCifraEntera = true
                }
                c in '0'..'9' && haySeparador && decimales < maxDecimales -> {
                    resultado.append(c); decimales++
                }
                (c == ',' || c == '.') && hayCifraEntera && !haySeparador && maxDecimales > 0 -> {
                    resultado.append(c); haySeparador = true
                }
            }
        }
        return resultado.toString()
    }
}