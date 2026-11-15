package com.DanielDMikelG.kalo.util

enum class TipoCodigoBarras(val longitud: Int) {
    EAN_8(8),
    UPC_A(12),
    EAN_13(13)
}

/**
 * Valida códigos de barras EAN-8, UPC-A y EAN-13 comprobando longitud,
 * que sean solo dígitos y que el dígito de control sea correcto.
 */
object ValidadorCodigoBarras {

    /** Quita espacios y guiones, por si el código viene de un campo escrito a mano. */
    fun limpiar(entrada: String): String =
        entrada.filter { !it.isWhitespace() && it != '-' }

    /** Tipo del código, o null si no es válido. */
    fun tipo(codigo: String): TipoCodigoBarras? {
        if (codigo.isEmpty() || !codigo.all { it in '0'..'9' }) return null
        val tipo = TipoCodigoBarras.entries.firstOrNull { it.longitud == codigo.length }
            ?: return null
        return if (digitoControlCorrecto(codigo)) tipo else null
    }

    fun esValido(codigo: String): Boolean = tipo(codigo) != null

    /**
     * Devuelve el código en su forma canónica o null si no es válido.
     * Un UPC-A de 12 dígitos equivale a un EAN-13 con un 0 delante; se unifica
     * para no guardar dos veces el mismo producto en la caché.
     */
    fun normalizar(codigo: String): String? = when (tipo(codigo)) {
        null -> null
        TipoCodigoBarras.UPC_A -> "0$codigo"
        else -> codigo
    }

    // Algoritmo GS1: desde la derecha, sin contar el dígito de control,
    // los pesos alternan 3, 1, 3, 1...
    private fun digitoControlCorrecto(codigo: String): Boolean {
        val digitos = codigo.map { it - '0' }
        val sumaPonderada = digitos
            .dropLast(1)
            .reversed()
            .mapIndexed { indice, digito -> if (indice % 2 == 0) digito * 3 else digito }
            .sum()
        val esperado = (10 - sumaPonderada % 10) % 10
        return esperado == digitos.last()
    }
}