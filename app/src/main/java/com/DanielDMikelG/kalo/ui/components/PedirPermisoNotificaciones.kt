package com.DanielDMikelG.kalo.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.DanielDMikelG.kalo.notifications.AjustesNotificaciones

/** Pide el permiso de notificaciones una sola vez (Android 13 o superior). */
@Composable
fun PedirPermisoNotificaciones() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return
    val context = LocalContext.current
    val ajustes = remember { AjustesNotificaciones(context) }
    val lanzador = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(Unit) {
        val concedido = ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (!concedido && !ajustes.permisoYaPedido()) {
            ajustes.marcarPermisoPedido()
            lanzador.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}