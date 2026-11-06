package com.DanielDMikelG.kalo.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.DanielDMikelG.kalo.ui.components.PantallaVacia
import com.DanielDMikelG.kalo.ui.navigation.BarraInferior
import com.DanielDMikelG.kalo.ui.navigation.Destino

@Composable
fun KaloApp() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BarraInferior(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Destino.Home.ruta,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destino.Home.ruta) { PantallaVacia("Inicio") }
            composable(Destino.Diary.ruta) { PantallaVacia("Diario") }
            composable(Destino.Kitchen.ruta) { PantallaVacia("Cocina") }
            composable(Destino.Profile.ruta) { PantallaVacia("Perfil") }
        }
    }
}