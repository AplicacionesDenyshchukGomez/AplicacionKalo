package com.DanielDMikelG.kalo.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.DanielDMikelG.kalo.data.local.SesionManager
import com.DanielDMikelG.kalo.ui.screens.profile.ProfileRoute
import com.DanielDMikelG.kalo.ui.screens.register.RegistroViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.navArgument
import com.DanielDMikelG.kalo.ui.screens.home.InicioRoute
import com.DanielDMikelG.kalo.ui.screens.product.FichaProductoRoute
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.DanielDMikelG.kalo.data.contenido.CatalogoCocina
import com.DanielDMikelG.kalo.data.local.KaloDatabase
import com.DanielDMikelG.kalo.data.repository.AlimentoRepository
import com.DanielDMikelG.kalo.domain.model.FiltroRecetas
import com.DanielDMikelG.kalo.ui.screens.diary.DiarioRoute
import com.DanielDMikelG.kalo.ui.screens.diary.DiarioViewModel
import com.DanielDMikelG.kalo.ui.screens.kitchen.CocinaRoute
import com.DanielDMikelG.kalo.ui.screens.kitchen.DetalleArticuloScreen
import com.DanielDMikelG.kalo.ui.screens.kitchen.DetalleRecetaScreen
import com.DanielDMikelG.kalo.ui.screens.kitchen.ListaRecetasScreen
import com.DanielDMikelG.kalo.ui.screens.myfoods.MisAlimentosRoute
import com.DanielDMikelG.kalo.ui.screens.myfoods.MisAlimentosViewModel
import com.DanielDMikelG.kalo.ui.screens.product.FichaProductoRoute
import com.DanielDMikelG.kalo.ui.screens.product.FichaProductoViewModel
import com.DanielDMikelG.kalo.ui.screens.product.ModoFicha
import com.DanielDMikelG.kalo.ui.screens.scanner.EscanerScreen
import com.DanielDMikelG.kalo.ui.screens.settings.SettingsScreen
import com.DanielDMikelG.kalo.data.repository.ComidaRepository
import com.DanielDMikelG.kalo.ui.screens.anadir.AnadirComidaRoute
import com.DanielDMikelG.kalo.ui.screens.anadir.AnadirComidaViewModel
import com.DanielDMikelG.kalo.ui.screens.anadir.CantidadRoute
import com.DanielDMikelG.kalo.ui.screens.anadir.CantidadViewModel
import com.DanielDMikelG.kalo.ui.screens.anadir.SeleccionComidaViewModel
import com.DanielDMikelG.kalo.domain.usecase.ElegirRecetaDelDiaUseCase
import com.DanielDMikelG.kalo.data.repository.PesoRepository
import com.DanielDMikelG.kalo.notifications.NotificacionesAgua
import com.DanielDMikelG.kalo.ui.components.PedirPermisoNotificaciones
import com.DanielDMikelG.kalo.ui.screens.weight.AnadirPesoRoute
import com.DanielDMikelG.kalo.ui.screens.weight.AnadirPesoViewModel

@Composable
fun KaloApp(
    destinoPendiente: String? = null,
    onDestinoAtendido: () -> Unit = {},
) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val registroViewModel: RegistroViewModel = viewModel()
    val seleccion: SeleccionComidaViewModel = viewModel()

    val rutaInicial = remember {
        if (SesionManager(context).usuarioActual() != null) Destino.Home.ruta else Rutas.LOGIN
    }

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = rutaActual in Destino.menu.map { it.ruta }

    if (mostrarBarra) PedirPermisoNotificaciones()

    LaunchedEffect(destinoPendiente) {
        if (destinoPendiente == NotificacionesAgua.DESTINO_DIARIO) {
            if (SesionManager(context).usuarioActual() != null) {
                navController.navigate(Destino.Diary.ruta) {
                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
            }
            onDestinoAtendido()
        }
    }

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
            composable(
                route = Rutas.ESCANER,
                arguments = listOf(navArgument("modo") { type = NavType.StringType }),
            ) { entry ->
                val modo = entry.arguments?.getString("modo") ?: "consulta"
                EscanerScreen(
                    onCodigo = { codigo -> navController.navigate(Rutas.ficha(codigo, modo)) },
                    onCerrar = { navController.popBackStack() },
                )
            }

            composable(
                route = Rutas.FICHA,
                arguments = listOf(
                    navArgument("codigo") { type = NavType.StringType },
                    navArgument("modo") { type = NavType.StringType },
                ),
            ) { entry ->
                val codigo = entry.arguments?.getString("codigo").orEmpty()
                val modo = if (entry.arguments?.getString("modo") == "anadir") ModoFicha.ANADIR
                else ModoFicha.CONSULTA
                val viewModel: FichaProductoViewModel = viewModel(
                    factory = FichaProductoViewModel.Factory(
                        repositorio = AlimentoRepository(KaloDatabase.obtener(context)),
                        codigo = codigo,
                        modo = modo,
                    )
                )
                FichaProductoRoute(
                    viewModel = viewModel,
                    onAnadir = { alimento ->
                        seleccion.elegirAlimento(alimento)
                        navController.navigate(Rutas.CANTIDAD)
                    },
                    onVolver = { navController.popBackStack() },
                )
            }

            composable(Destino.Home.ruta) {
                InicioRoute(
                    onMisiones = { Toast.makeText(context, "Próximamente", Toast.LENGTH_SHORT).show() },
                    onReceta = {
                        ElegirRecetaDelDiaUseCase()(CatalogoCocina.recetas)
                            ?.let { navController.navigate(Rutas.receta(it.id)) }
                    },
                    onEscanear = { navController.navigate(Rutas.escaner("consulta")) },
                    onPeso = { navController.navigate(Destino.Profile.ruta) }, // provisional hasta "Añadir peso"
                    modifier = Modifier.fillMaxSize(),
                )
            }
            composable(Destino.Diary.ruta) {
                val usuarioId = remember { SesionManager(context).usuarioActual() }
                if (usuarioId == null) {
                    PantallaVacia("Diario")
                } else {
                    val viewModel: DiarioViewModel = viewModel(
                        factory = DiarioViewModel.Factory(usuarioId, KaloDatabase.obtener(context))
                    )
                    DiarioRoute(
                        viewModel = viewModel,
                        // Temporal: hasta que exista "Añadir comida" (#95), el + abre el escáner
                        onAnadirComida = { tipo, fecha ->
                            seleccion.empezar(tipo, fecha)
                            navController.navigate(Rutas.ANADIR_COMIDA)
                        },
                    )
                }
            }
            composable(Destino.Kitchen.ruta) {
                CocinaRoute(
                    onReceta = { navController.navigate(Rutas.receta(it)) },
                    onArticulo = { navController.navigate(Rutas.articulo(it)) },
                    onFiltro = { navController.navigate(Rutas.recetas(it)) },
                )
            }
            composable(
                route = Rutas.RECETAS,
                arguments = listOf(navArgument("filtro") { type = NavType.StringType }),
            ) { entry ->
                val filtro = FiltroRecetas.desdeClave(entry.arguments?.getString("filtro"))
                ListaRecetasScreen(
                    titulo = filtro.titulo,
                    recetas = CatalogoCocina.filtrar(filtro),
                    onReceta = { navController.navigate(Rutas.receta(it)) },
                    onVolver = { navController.popBackStack() },
                )
            }

            composable(
                route = Rutas.RECETA,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val receta = CatalogoCocina.receta(entry.arguments?.getString("id").orEmpty())
                if (receta == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    DetalleRecetaScreen(receta = receta, onVolver = { navController.popBackStack() })
                }
            }

            composable(
                route = Rutas.ARTICULO,
                arguments = listOf(navArgument("id") { type = NavType.StringType }),
            ) { entry ->
                val articulo = CatalogoCocina.articulo(entry.arguments?.getString("id").orEmpty())
                if (articulo == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    DetalleArticuloScreen(articulo = articulo, onVolver = { navController.popBackStack() })
                }
            }

            composable(Rutas.MIS_ALIMENTOS) {
                val context = LocalContext.current
                val vm: MisAlimentosViewModel = viewModel(
                    factory = MisAlimentosViewModel.Factory(AlimentoRepository(KaloDatabase.obtener(context))),
                )
                MisAlimentosRoute(
                    viewModel = vm,
                    onVolver = { navController.popBackStack() },
                    onElegir = { alimento ->
                        seleccion.elegirAlimento(alimento)
                        navController.navigate(Rutas.CANTIDAD)
                    },
                )
            }

            composable(Rutas.ANADIR_COMIDA) {
                val vm: AnadirComidaViewModel = viewModel(
                    factory = AnadirComidaViewModel.Factory(AlimentoRepository(KaloDatabase.obtener(context))),
                )
                val sel by seleccion.estado.collectAsStateWithLifecycle()
                AnadirComidaRoute(
                    viewModel = vm,
                    tipo = sel.tipo,
                    onEscanear = { navController.navigate(Rutas.escaner("anadir")) },
                    onMisAlimentos = { navController.navigate(Rutas.MIS_ALIMENTOS) },
                    onElegirGuardado = { alimento ->
                        seleccion.elegirAlimento(alimento)
                        navController.navigate(Rutas.CANTIDAD)
                    },
                    onElegirEnLinea = { codigo -> navController.navigate(Rutas.ficha(codigo, "anadir")) },
                    onVolver = { navController.popBackStack() },
                )
            }

            composable(Rutas.CANTIDAD) {
                val usuarioId = remember { SesionManager(context).usuarioActual() }
                val sel by seleccion.estado.collectAsStateWithLifecycle()
                val alimento = sel.alimento
                if (usuarioId == null || alimento == null) {
                    LaunchedEffect(Unit) { navController.popBackStack() }
                } else {
                    val vm: CantidadViewModel = viewModel(
                        factory = CantidadViewModel.Factory(
                            comidas = ComidaRepository(KaloDatabase.obtener(context)),
                            usuarioId = usuarioId,
                            alimento = alimento,
                            tipo = sel.tipo,
                            fecha = sel.fecha,
                        ),
                    )
                    CantidadRoute(
                        viewModel = vm,
                        tipo = sel.tipo,
                        onGuardado = { navController.popBackStack(Destino.Diary.ruta, inclusive = false) },
                        onVolver = { navController.popBackStack() },
                    )
                }
            }

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
        }
    }
}