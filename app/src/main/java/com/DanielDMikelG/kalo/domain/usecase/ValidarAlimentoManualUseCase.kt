package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.Alimento
import com.DanielDMikelG.kalo.domain.model.OrigenAlimento

sealed interface ResultadoAlimentoManual {
    data class Valido(val alimento: Alimento) : ResultadoAlimentoManual
    data class Invalido(val mensaje: String) : ResultadoAlimentoManual
}

/** Valida los textos del formulario de alimento propio. Valores por 100 g. No lanza excepciones. */
class ValidarAlimentoManualUseCase {

    operator fun invoke(
        nombre: String,
        kcal: String,
        prot: String,
        carb: String,
        grasa: String,
        fibra: String,
    ): ResultadoAlimentoManual {
        val nombreLimpio = nombre.trim().replace(Regex("\\s+"), " ")
        if (nombreLimpio.length < 2) return invalido("Escribe un nombre de al menos 2 letras.")
        if (nombreLimpio.length > 60) return invalido("El nombre es demasiado largo (máximo 60).")

        if (kcal.isBlank()) return invalido("Indica las calorías por 100 g.")
        val kcalValor = leer(kcal) ?: return invalido("Las calorías no son un número válido.")
        if (kcalValor > MAX_KCAL_100G) return invalido("Más de $MAX_KCAL_100G kcal por 100 g no es posible.")

        val p = if (prot.isBlank()) 0f else leer(prot) ?: return invalido("Las proteínas no son válidas.")
        val c = if (carb.isBlank()) 0f else leer(carb) ?: return invalido("Los carbohidratos no son válidos.")
        val g = if (grasa.isBlank()) 0f else leer(grasa) ?: return invalido("Las grasas no son válidas.")
        val f = if (fibra.isBlank()) 0f else leer(fibra) ?: return invalido("La fibra no es válida.")

        if (p > 100f || c > 100f || g > 100f || f > 100f) {
            return invalido("Cada valor debe ser como máximo 100 g por 100 g de alimento.")
        }
        if (p + c + g > 100f) {
            return invalido("Proteínas, carbohidratos y grasas no pueden sumar más de 100 g.")
        }

        return ResultadoAlimentoManual.Valido(
            Alimento(
                codigoBarras = null,
                nombre = nombreLimpio,
                kcal100g = kcalValor,
                prot = p,
                carb = c,
                grasa = g,
                fibra = f,
                origen = OrigenAlimento.MANUAL,
            ),
        )
    }

    private fun invalido(mensaje: String) = ResultadoAlimentoManual.Invalido(mensaje)

    private fun leer(texto: String): Float? =
        texto.trim().replace(',', '.').toFloatOrNull()?.takeIf { it >= 0f && it.isFinite() }

    private companion object {
        const val MAX_KCAL_100G = 900f // la grasa pura tiene unas 900 kcal por 100 g
    }
}

