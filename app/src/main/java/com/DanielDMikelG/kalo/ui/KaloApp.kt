package com.DanielDMikelG.kalo.ui

import android.widget.Toast
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.DanielDMikelG.kalo.ui.screens.home.InicioRoute
import com.DanielDMikelG.kalo.ui.screens.product.FichaProductoRoute
import com.DanielDMikelG.kalo.ui.screens.product.ModoFicha
import com.DanielDMikelG.kalo.ui.screens.settings.SettingsScreen

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
            composable(Rutas.AJUSTES) {
                SettingsScreen(
                    onCerrar = { navController.popBackStack() },
                    onEditarPerfil = {
                        navController.previousBackStackEntry?.savedStateHandle?.set("editar_perfil", true)
                        navController.popBackStack()
                    },
                    onCerrarSesion = {
                        SesionManager(context).cerrarSesion()
                        navController.navigate(Rutas.LOGIN) {
                            popUpTo(navController.graph.id) { inclusive = true }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(Destino.Home.ruta) {
                InicioRoute(
                    onMisiones = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() },
                    onReceta = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() },
                    onEscanear = { navController.navigate(Rutas.escaner(ModoFicha.CONSULTA)) },
                    onPeso = { navController.navigate(Destino.Profile.ruta) }, // provisional hasta "Añadir peso"
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                Rutas.ESCANER,
                arguments = listOf(navArgument("modo") { type = NavType.StringType }),
            ) { entrada ->
                val modo = ModoFicha.valueOf(entrada.arguments?.getString("modo") ?: ModoFicha.CONSULTA.name)
                EscanerScreen(
                    onCodigo = { codigo ->
                        navController.navigate(Rutas.ficha(codigo, modo)) {
                            popUpTo(Rutas.ESCANER) { inclusive = true } // atrás desde la ficha no vuelve a la cámara
                        }
                    },
                    onCerrar = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                Rutas.FICHA,
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType },
                    navArgument("modo") { type = NavType.StringType },
                ),
            ) { entrada ->
                val codigo = entrada.arguments?.getString("codigo").orEmpty()
                val modo = ModoFicha.valueOf(entrada.arguments?.getString("modo") ?: ModoFicha.CONSULTA.name)
                FichaProductoRoute(
                    codigo = codigo,
                    modo = modo,
                    onCerrar = { navController.popBackStack() },
                    onEscanearOtro = {
                        navController.navigate(Rutas.escaner(modo)) { popUpTo(Rutas.FICHA) { inclusive = true } }
                    },
                    onAnadir = { /* TODO: ruta de Cantidad cuando exista */ navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(Destino.Home.ruta) { InicioRoute(
                onMisiones = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() },
                onReceta = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() },
                onEscanear = { navController.navigate(Rutas.escaner(ModoFicha.CONSULTA)) },
                onPeso = { navController.navigate(Destino.Profile.ruta) }, // provisional hasta "Añadir peso"
                modifier = Modifier.fillMaxSize(),
            )
            }
            composable(Destino.Diary.ruta) { PantallaVacia("Diario") }
            composable(Destino.Kitchen.ruta) { PantallaVacia("Cocina") }
            composable(Destino.Profile.ruta) { entry ->
                val editar by entry.savedStateHandle
                    .getStateFlow("editar_perfil", false)
                    .collectAsStateWithLifecycle()

                ProfileRoute(
                    onAbrirAjustes = { navController.navigate(Rutas.AJUSTES) },
                    editarAlEntrar = editar,
                    onEdicionIniciada = { entry.savedStateHandle["editar_perfil"] = false },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            composable(
                Rutas.ESCANER,
                arguments = listOf(navArgument("modo") { type = NavType.StringType }),
            ) { entrada ->
                val modo = ModoFicha.valueOf(entrada.arguments?.getString("modo") ?: ModoFicha.CONSULTA.name)
                EscanerScreen(
                    onCodigo = { codigo ->
                        navController.navigate(Rutas.ficha(codigo, modo)) {
                            popUpTo(Rutas.ESCANER) { inclusive = true } // atrás desde la ficha no vuelve a la cámara
                        }
                    },
                    onCerrar = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }

            composable(
                Rutas.FICHA,
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType },
                    navArgument("modo") { type = NavType.StringType },
                ),
            ) { entrada ->
                val codigo = entrada.arguments?.getString("codigo").orEmpty()
                val modo = ModoFicha.valueOf(entrada.arguments?.getString("modo") ?: ModoFicha.CONSULTA.name)
                FichaProductoRoute(
                    codigo = codigo,
                    modo = modo,
                    onCerrar = { navController.popBackStack() },
                    onEscanearOtro = {
                        navController.navigate(Rutas.escaner(modo)) { popUpTo(Rutas.FICHA) { inclusive = true } }
                    },
                    onAnadir = { /* TODO: ruta de Cantidad cuando exista */ navController.popBackStack() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}

