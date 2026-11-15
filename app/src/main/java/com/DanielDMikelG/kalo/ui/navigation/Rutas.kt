package com.DanielDMikelG.kalo.ui.navigation

import com.DanielDMikelG.kalo.ui.screens.product.ModoFicha

object Rutas {
    const val LOGIN = "login"
    const val REGISTRO = "registro"
    const val OBJETIVO = "objetivo"
    const val AJUSTES = "ajustes"

    const val ESCANER = "escaner/{modo}"
    const val FICHA = "ficha/{codigo}/{modo}"
    fun escaner(modo: ModoFicha) = "escaner/${modo.name}"
    fun ficha(codigo: String, modo: ModoFicha) = "ficha/$codigo/${modo.name}"
}