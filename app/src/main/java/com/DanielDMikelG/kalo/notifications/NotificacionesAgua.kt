package com.DanielDMikelG.kalo.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.DanielDMikelG.kalo.MainActivity
import com.DanielDMikelG.kalo.R

object NotificacionesAgua {
    const val EXTRA_DESTINO = "destino"
    const val DESTINO_DIARIO = "diario"

    private const val CANAL_ID = "recordatorio_agua"
    private const val NOTIFICACION_ID = 1001

    @SuppressLint("MissingPermission") // se comprueba con areNotificationsEnabled()
    fun mostrar(context: Context, mensaje: String) {
        val manager = NotificationManagerCompat.from(context)
        if (!manager.areNotificationsEnabled()) return
        crearCanal(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_DESTINO, DESTINO_DIARIO)
        }
        val pendiente = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(R.drawable.ic_notificacion_agua)
            .setContentTitle("Hora de beber agua")
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendiente)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICACION_ID, notificacion)
    }

    private fun crearCanal(context: Context) {
        val canal = NotificationChannel(CANAL_ID, "Recordatorio de agua", NotificationManager.IMPORTANCE_DEFAULT)
            .apply { description = "Avisos para beber agua durante el día" }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(canal)
    }
}