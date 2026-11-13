package com.DanielDMikelG.kalo.ui.navigation

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val OBJETIVO = "objetivo"
    const val AJUSTES = "ajustes"

    const val ESCANER = "escaner/{modo}"

    const val FICHA = "ficha/{codigo}/{modo}"

    fun escaner(modo: String) = "escaner/$modo"

    fun ficha(codigo: String, modo: String) = "ficha/$codigo/$modo"
}