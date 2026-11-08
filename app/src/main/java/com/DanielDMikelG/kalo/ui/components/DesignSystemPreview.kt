package com.DanielDMikelG.kalo.ui.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.ui.theme.KaloTheme
import com.DanielDMikelG.kalo.ui.theme.Spacing
import com.DanielDMikelG.kalo.ui.theme.kaloColors

/** Pantalla de prueba con todos los componentes del sistema de diseño. */
@Composable
fun DesignSystemScreen() {
    var texto by remember { mutableStateOf("") }
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md),
        ) {
            Text("Kalo", style = MaterialTheme.typography.headlineLarge)
            Text("Titulo de seccion", style = MaterialTheme.typography.titleLarge)
            Text("Texto normal de la aplicacion.", style = MaterialTheme.typography.bodyLarge)

            Text("Nutrientes y agua", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                ColorSwatch("Prot.", MaterialTheme.kaloColors.protein, Modifier.weight(1f))
                ColorSwatch("Grasas", MaterialTheme.kaloColors.fat, Modifier.weight(1f))
                ColorSwatch("Carbs", MaterialTheme.kaloColors.carbs, Modifier.weight(1f))
                ColorSwatch("Fibra", MaterialTheme.kaloColors.fiber, Modifier.weight(1f))
                ColorSwatch("Agua", MaterialTheme.kaloColors.water, Modifier.weight(1f))
            }

            KaloButton(text = "Boton principal", onClick = {})
            KaloSecondaryButton(text = "Boton secundario", onClick = {})
            KaloButton(text = "Boton desactivado", onClick = {}, enabled = false)

            KaloTextField(value = texto, onValueChange = { texto = it }, label = "Peso (kg)")
            KaloTextField(value = "abc", onValueChange = {}, label = "Con error", error = "Valor no valido")

            KaloCard {
                Text("Calorias", style = MaterialTheme.typography.titleMedium)
                Text("1.250 / 2.000 kcal", style = MaterialTheme.typography.bodyMedium)
                KaloProgressBar(progress = 0.62f, modifier = Modifier.padding(top = Spacing.sm))
            }
        }
    }
}

@Composable
private fun ColorSwatch(nombre: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(color, RoundedCornerShape(12.dp)),
        )
        Text(nombre, style = MaterialTheme.typography.labelSmall)
    }
}

@Preview(name = "Claro", showBackground = true)
@Composable
private fun DesignSystemLightPreview() {
    KaloTheme(darkTheme = false) { DesignSystemScreen() }
}

@Preview(name = "Oscuro", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun DesignSystemDarkPreview() {
    KaloTheme(darkTheme = true) { DesignSystemScreen() }
}
