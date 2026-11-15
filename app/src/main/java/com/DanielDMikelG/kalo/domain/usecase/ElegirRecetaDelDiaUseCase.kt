package com.DanielDMikelG.kalo.domain.usecase

import com.DanielDMikelG.kalo.domain.model.Receta
import java.time.LocalDate

/** Elige la receta del día: la misma para todo el día y distinta cada día, rotando por la lista. */
class ElegirRecetaDelDiaUseCase {
    operator fun invoke(recetas: List<Receta>, fecha: LocalDate = LocalDate.now()): Receta? {
        if (recetas.isEmpty()) return null
        val indice = Math.floorMod(fecha.toEpochDay(), recetas.size.toLong()).toInt()
        return recetas[indice]
    }
}