package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.TipoComida

/**
 * Reparte las kcal objetivo del día entre las comidas.
 * Reparto orientativo: desayuno 25 %, comida 35 %, cena 30 % y snack 10 %.
 * Las kcal que sobran por el redondeo se suman a la comida,
 * para que el total sea siempre exactamente el objetivo.
 */
class CalcularRepartoComidasUseCase {

    operator fun invoke(kcalObjetivo: Int): Map<TipoComida, Int> {
        require(kcalObjetivo >= 0) { "Las kcal objetivo no pueden ser negativas" }

        val reparto = PORCENTAJES.mapValues { (_, porcentaje) ->
            kcalObjetivo * porcentaje / 100
        }
        val sobrante = kcalObjetivo - reparto.values.sum()

        return reparto + (TipoComida.COMIDA to reparto.getValue(TipoComida.COMIDA) + sobrante)
    }

    private companion object {
        val PORCENTAJES = linkedMapOf(
            TipoComida.DESAYUNO to 25,
            TipoComida.COMIDA to 35,
            TipoComida.CENA to 30,
            TipoComida.SNACK to 10
        )
    }
}
