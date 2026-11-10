package com.DanielDMikelG.kalo.ui

import androidx.compose.foundation.layout.fillMaxSize
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
import com.DanielDMikelG.kalo.ui.navigation.Rutas
import com.DanielDMikelG.kalo.ui.screens.login.LoginScreen
import com.DanielDMikelG.kalo.ui.screens.objective.ObjectiveScreen
import com.DanielDMikelG.kalo.ui.screens.register.RegisterScreen
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.ui.screens.profile.ProfileRoute
import com.DanielDMikelG.kalo.ui.screens.register.RegistroViewModel

@Composable
fun KaloApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val registroViewModel: RegistroViewModel = viewModel()

    val rutaInicial = remember {
        if (SesionManager(context).usuarioActual() != null) Destino.Home.ruta else Rutas.LOGIN
    }

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = rutaActual in Destino.menu.map { it.ruta }

    Scaffold(
        bottomBar = { if (mostrarBarra) BarraInferior(navController) }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = rutaInicial,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.LOGIN) {
                LoginScreen(
                    onLoginExitoso = {
                        navController.navigate(Destino.Home.ruta) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    },
                    onRegisterClick = { navController.navigate(Rutas.REGISTRO) }
                )
            }
            composable(Rutas.REGISTRO) {
                RegisterScreen(
                    viewModel = registroViewModel,
                    onContinuar = { navController.navigate(Rutas.OBJETIVO) },
                    onVolver = { navController.popBackStack() }
                )
            }
            composable(Rutas.OBJETIVO) {
                ObjectiveScreen(
                    viewModel = registroViewModel,
                    onTerminar = {
                        navController.navigate(Destino.Home.ruta) {
                            popUpTo(Rutas.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(Destino.Home.ruta) { PantallaVacia("Inicio") }
            composable(Destino.Diary.ruta) { PantallaVacia("Diario") }
            composable(Destino.Kitchen.ruta) { PantallaVacia("Cocina") }
            composable(Destino.Profile.ruta) {
                ProfileRoute(
                    onCerrarSesion = {
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}