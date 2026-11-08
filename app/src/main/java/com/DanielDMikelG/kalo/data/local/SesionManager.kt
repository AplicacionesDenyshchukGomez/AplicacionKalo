package com.DanielDMikelG.kalo.data.local

import android.content.Context

class SesionManager(context: Context) {
    private val prefs = context.getSharedPreferences("kalo_sesion", Context.MODE_PRIVATE)

    fun guardarUsuario(id: Long) = prefs.edit().putLong(CLAVE, id).apply()

    fun usuarioActual(): Long? = prefs.getLong(CLAVE, -1L).takeIf { it != -1L }

    fun cerrarSesion() = prefs.edit().remove(CLAVE).apply()

    private companion object {
        const val CLAVE = "usuario_id"
    }
}