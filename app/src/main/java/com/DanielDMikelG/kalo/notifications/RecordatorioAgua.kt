package com.DanielDMikelG.kalo.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.data.repository.AguaRepository
import com.DanielDMikelG.kalo.domain.usecase.ProximoAviso
import com.DanielDMikelG.kalo.domain.usecase.TextoRecordatorioAgua
import com.DanielDMikelG.kalo.ui.screens.diary.DiarioUiState
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.first

class RecordatorioAguaWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val contexto = applicationContext
        if (!AjustesNotificaciones(contexto).recordatorioAguaActivo()) return Result.success()
        val usuarioId = SesionManager(contexto).usuarioActual() ?: return Result.success()

        val vasos = AguaRepository(KaloDatabase.obtener(contexto)).observarVasos(usuarioId).first()
        // El mismo objetivo de vasos que enseña el Diario
        val objetivo = DiarioUiState().vasosObjetivo

        if (TextoRecordatorioAgua.debeAvisar(vasos, objetivo)) {
            NotificacionesAgua.mostrar(contexto, TextoRecordatorioAgua.mensaje(vasos, objetivo))
        }
        return Result.success()
    }
}

/** Programa o cancela los dos avisos diarios de agua. */
object RecordatorioAgua {
    private val AVISOS = listOf(
        "agua_mediodia" to LocalTime.of(12, 0),
        "agua_tarde" to LocalTime.of(18, 0),
    )

    /** Se llama al abrir la app: programa o cancela según la preferencia. Es barato y no repite avisos. */
    fun sincronizar(context: Context) {
        if (AjustesNotificaciones(context).recordatorioAguaActivo()) programar(context) else cancelar(context)
    }

    fun programar(context: Context) {
        val trabajo = WorkManager.getInstance(context)
        AVISOS.forEach { (nombre, hora) ->
            val retraso = ProximoAviso.retraso(LocalDateTime.now(), hora)
            val peticion = PeriodicWorkRequestBuilder<RecordatorioAguaWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(retraso.toMinutes(), TimeUnit.MINUTES)
                .build()
            // KEEP: si ya estaba programado no se reinicia la hora
            trabajo.enqueueUniquePeriodicWork(nombre, ExistingPeriodicWorkPolicy.KEEP, peticion)
        }
    }

    fun cancelar(context: Context) {
        val trabajo = WorkManager.getInstance(context)
        AVISOS.forEach { (nombre, _) -> trabajo.cancelUniqueWork(nombre) }
    }
}