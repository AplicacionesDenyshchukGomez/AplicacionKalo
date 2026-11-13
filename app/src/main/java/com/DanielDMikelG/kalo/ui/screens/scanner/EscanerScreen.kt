package com.DanielDMikelG.kalo.ui.screens.scanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

private val TEXTO = Color(0xFF17372A)
private val FONDO = Color(0xFFF4FBF6)

/** Código de producto: solo dígitos, 8, 12 o 13 cifras (EAN-8, UPC-A/E, EAN-13). */
private fun parecidoACodigo(codigo: String): Boolean =
    codigo.all { it.isDigit() } && codigo.length in listOf(8, 12, 13)

@OptIn(ExperimentalGetImage::class)
@Composable
fun EscanerScreen(
    onCodigo: (String) -> Unit,
    onCerrar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = context as LifecycleOwner
    val haptic = LocalHapticFeedback.current

    var permiso by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permiso = it }

    LaunchedEffect(Unit) {
        if (!permiso) pedirPermiso.launch(Manifest.permission.CAMERA)
    }

    var manual by remember { mutableStateOf("") }
    // Entrega el código una sola vez aunque el analizador lo vea en varios fotogramas.
    val entregado = remember { AtomicBoolean(false) }
    val ejecutor = remember { Executors.newSingleThreadExecutor() }
    val escaner = remember {
        BarcodeScanning.getClient(
            BarcodeScannerOptions.Builder()
                .setBarcodeFormats(
                    Barcode.FORMAT_EAN_13,
                    Barcode.FORMAT_EAN_8,
                    Barcode.FORMAT_UPC_A,
                    Barcode.FORMAT_UPC_E,
                )
                .build()
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            escaner.close()
            ejecutor.shutdown()
        }
    }

    fun entregar(codigo: String) {
        if (entregado.compareAndSet(false, true)) {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            onCodigo(codigo)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(FONDO)) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (permiso) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        val vista = PreviewView(ctx)
                        val futuro = ProcessCameraProvider.getInstance(ctx)
                        futuro.addListener({
                            val proveedor = futuro.get()
                            val preview = Preview.Builder().build().also {
                                it.surfaceProvider = vista.surfaceProvider
                            }
                            val analisis = ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build()
                            analisis.setAnalyzer(ejecutor) { proxy ->
                                val media = proxy.image
                                if (media == null) {
                                    proxy.close()
                                } else {
                                    val imagen = InputImage.fromMediaImage(
                                        media, proxy.imageInfo.rotationDegrees
                                    )
                                    escaner.process(imagen)
                                        .addOnSuccessListener { codigos ->
                                            codigos.mapNotNull { it.rawValue }
                                                .firstOrNull { parecidoACodigo(it) }
                                                ?.let { entregar(it) }
                                        }
                                        .addOnCompleteListener { proxy.close() }
                                }
                            }
                            proveedor.unbindAll()
                            proveedor.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                preview,
                                analisis,
                            )
                        }, ContextCompat.getMainExecutor(ctx))
                        vista
                    },
                )
                Text(
                    "Apunta al código de barras",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Kalo necesita la cámara para escanear códigos de barras. " +
                                "También puedes escribir el código a mano.",
                        color = TEXTO,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { pedirPermiso.launch(Manifest.permission.CAMERA) }) {
                        Text("Permitir cámara")
                    }
                }
            }

            Surface(
                color = Color.White.copy(alpha = 0.85f),
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
            ) {
                IconButton(onClick = onCerrar) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = TEXTO)
                }
            }
        }

        Surface(color = Color.White, tonalElevation = 4.dp) {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Text(
                    "¿No se lee? Escribe el código",
                    color = TEXTO,
                    style = MaterialTheme.typography.titleSmall,
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = manual,
                        onValueChange = { manual = it.filter(Char::isDigit).take(14) },
                        label = { Text("Código de barras") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                    )
                    Button(
                        onClick = { entregar(manual) },
                        enabled = manual.length >= 8,
                    ) { Text("Buscar") }
                }
            }
        }
    }
}
