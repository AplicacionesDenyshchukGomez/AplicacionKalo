package com.DanielDMikelG.kalo.ui.screens.settings

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.DanielDMikelG.kalo.notifications.AjustesNotificaciones
import com.DanielDMikelG.kalo.notifications.RecordatorioAgua

/** Ajustes con el estado de las notificaciones; mismos parámetros que SettingsScreen. */
@Composable
fun AjustesRoute(
    onCerrar: () -> Unit,
    onEditarPerfil: () -> Unit,
    onCerrarSesion: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val ajustes = remember { AjustesNotificaciones(context) }
    var aguaActiva by remember { mutableStateOf(ajustes.recordatorioAguaActivo()) }
    var bloqueadas by remember { mutableStateOf(!NotificationManagerCompat.from(context).areNotificationsEnabled()) }

    // Al volver de los ajustes del móvil se vuelve a comprobar
    LifecycleResumeEffect(Unit) {
        bloqueadas = !NotificationManagerCompat.from(context).areNotificationsEnabled()
        onPauseOrDispose { }
    }

    SettingsScreen(
        onCerrar = onCerrar,
        onEditarPerfil = onEditarPerfil,
        onCerrarSesion = onCerrarSesion,
        modifier = modifier,
        recordatorioAgua = aguaActiva,
        notificacionesBloqueadas = bloqueadas,
        onRecordatorioAgua = { activar ->
            aguaActiva = activar
            ajustes.guardarRecordatorioAgua(activar)
            if (activar) RecordatorioAgua.programar(context) else RecordatorioAgua.cancelar(context)
        },
        onAbrirAjustesMovil = {
            context.startActivity(
                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    .putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
            )
        },
    )
}