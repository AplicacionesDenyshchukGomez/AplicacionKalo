package com.DanielDMikelG.kalo.domain.usecase

sealed interface ResultadoPeso {
    data class Valido(val kg: Float) : ResultadoPeso
    data class Invalido(val mensaje: String) : ResultadoPeso
}

/** Valida el peso escrito por el usuario: entre 20 y 400 kg y un decimal como máximo. No lanza excepciones. */
class ValidarPesoUseCase {

    operator fun invoke(texto: String): ResultadoPeso {
        val limpio = texto.trim().replace(',', '.')
        if (limpio.isEmpty()) return ResultadoPeso.Invalido("Escribe tu peso.")

        val kg = limpio.toFloatOrNull()?.takeIf { it.isFinite() }
            ?: return ResultadoPeso.Invalido("Escribe un número válido.")
        if (limpio.substringAfter('.', "").length > 1) {
            return ResultadoPeso.Invalido("Usa como máximo un decimal.")
        }
        if (kg < PESO_MIN || kg > PESO_MAX) {
            return ResultadoPeso.Invalido("El peso debe estar entre ${PESO_MIN.toInt()} y ${PESO_MAX.toInt()} kg.")
        }
        return ResultadoPeso.Valido(kg)
    }

    companion object {
        const val PESO_MIN = 20f
        const val PESO_MAX = 400f
    }
}