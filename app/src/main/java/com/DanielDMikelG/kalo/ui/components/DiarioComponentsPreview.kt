package com.DanielDMikelG.kalo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.DanielDMikelG.kalo.ui.theme.KaloTheme
import com.DanielDMikelG.kalo.ui.theme.kaloColors

@Composable
private fun DiarioComponentsDemo() {
    var vasos by remember { mutableIntStateOf(5) }
    val colores = MaterialTheme.kaloColors

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        KaloLiquidCircle(progress = 0.52f, kcalRestantes = 1110)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            KaloMascot(EstadoMascota.DORMIDA)
            KaloMascot(EstadoMascota.CONTENTA)
            KaloMascot(EstadoMascota.SORPRENDIDA)
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            KaloMacroBar("P", "Proteínas", 62f, 128f, colores.protein, colores.proteinSoft)
            KaloMacroBar("G", "Grasas", 38f, 64f, colores.fat, colores.fatSoft)
            KaloMacroBar("C", "Carbohidratos", 150f, 303f, colores.carbs, colores.carbsSoft)
            KaloMacroBar("F", "Fibra", 11f, 32f, colores.fiber, colores.fiberSoft)
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            KaloMacroMiniBar("Prot.", 62f, 128f, colores.protein, colores.proteinSoft, Modifier.weight(1f))
            KaloMacroMiniBar("Grasas", 38f, 64f, colores.fat, colores.fatSoft, Modifier.weight(1f))
            KaloMacroMiniBar("Carbos", 150f, 303f, colores.carbs, colores.carbsSoft, Modifier.weight(1f))
            KaloMacroMiniBar("Fibra", 11f, 32f, colores.fiber, colores.fiberSoft, Modifier.weight(1f))
        }

        KaloWaterGlasses(
            vasosBebidos = vasos,
            vasosTotales = 10,
            onVasoClick = { indice -> vasos = if (indice + 1 == vasos) indice else indice + 1 },
        )
    }
}

@Preview(name = "Claro", widthDp = 390, heightDp = 1000)
@Composable
private fun DiarioComponentsClaro() {
    KaloTheme(darkTheme = false) { DiarioComponentsDemo() }
}

@Preview(name = "Oscuro", widthDp = 390, heightDp = 1000)
@Composable
private fun DiarioComponentsOscuro() {
    KaloTheme(darkTheme = true) { DiarioComponentsDemo() }
}