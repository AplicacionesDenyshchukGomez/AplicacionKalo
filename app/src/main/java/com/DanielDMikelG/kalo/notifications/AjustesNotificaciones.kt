package com.DanielDMikelG.kalo.notifications

import android.content.Context

/** Preferencias de avisos. El recordatorio de agua está activado por defecto. */
class AjustesNotificaciones(context: Context) {
    private val prefs = context.getSharedPreferences("kalo_ajustes", Context.MODE_PRIVATE)

    fun recordatorioAguaActivo(): Boolean = prefs.getBoolean(AGUA, true)

    fun guardarRecordatorioAgua(activo: Boolean) = prefs.edit().putBoolean(AGUA, activo).apply()

    fun permisoYaPedido(): Boolean = prefs.getBoolean(PERMISO_PEDIDO, false)

    fun marcarPermisoPedido() = prefs.edit().putBoolean(PERMISO_PEDIDO, true).apply()

    private companion object {
        const val AGUA = "recordatorio_agua"
        const val PERMISO_PEDIDO = "permiso_notificaciones_pedido"
    }
}