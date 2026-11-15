package com.DanielDMikelG.kalo.ui.navigation

import com.DanielDMikelG.kalo.domain.model.FiltroRecetas

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val OBJETIVO = "objetivo"
    const val AJUSTES = "ajustes"

    const val ESCANER = "escaner/{modo}"

    const val FICHA = "ficha/{codigo}/{modo}"

    const val RECETAS = "recetas/{filtro}"

    const val RECETA = "receta/{id}"

    const val ARTICULO = "articulo/{id}"

    const val MIS_ALIMENTOS = "mis_alimentos"

    const val ANADIR_COMIDA = "anadir_comida"

    const val CANTIDAD = "cantidad"

    fun escaner(modo: String) = "escaner/$modo"

    fun ficha(codigo: String, modo: String) = "ficha/$codigo/$modo"

    fun recetas(filtro: FiltroRecetas) = "recetas/${filtro.clave}"

    fun receta(id: String) = "receta/$id"

    fun articulo(id: String) = "articulo/$id"


}