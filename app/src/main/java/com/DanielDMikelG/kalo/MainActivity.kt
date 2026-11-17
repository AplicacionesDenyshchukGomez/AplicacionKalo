package com.DanielDMikelG.kalo

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.DanielDMikelG.kalo.notifications.NotificacionesAgua
import com.DanielDMikelG.kalo.notifications.RecordatorioAgua
import com.DanielDMikelG.kalo.ui.KaloApp
import com.DanielDMikelG.kalo.ui.theme.KaloTheme

class MainActivity : ComponentActivity() {
    private var destinoPendiente by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        RecordatorioAgua.sincronizar(this)
        destinoPendiente = intent?.getStringExtra(NotificacionesAgua.EXTRA_DESTINO)
        setContent {
            KaloTheme {
                KaloApp(
                    destinoPendiente = destinoPendiente,
                    onDestinoAtendido = { destinoPendiente = null },
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        destinoPendiente = intent.getStringExtra(NotificacionesAgua.EXTRA_DESTINO)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    KaloTheme {
        Greeting("Android")
    }
}