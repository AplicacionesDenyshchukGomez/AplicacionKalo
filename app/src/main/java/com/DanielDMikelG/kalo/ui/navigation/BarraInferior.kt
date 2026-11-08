package com.DanielDMikelG.kalo.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BarraInferior(navController: NavHostController) {
    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route

    NavigationBar {
        Destino.menu.forEach { destino ->
            val seleccionado = rutaActual == destino.ruta

            NavigationBarItem(
                selected = seleccionado,
                onClick = {
                    navController.navigate(destino.ruta) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (seleccionado) destino.iconoSeleccionado() else destino.iconoNormal(),
                        contentDescription = destino.titulo
                    )
                },
                label = {
                    Text(
                        text = destino.titulo,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}