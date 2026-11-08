package com.DanielDMikelG.kalo.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destino(
    val ruta: String,
    val titulo: String,
    val iconoSeleccionado: @Composable () -> ImageVector,
    val iconoNormal: @Composable () -> ImageVector
) {
    data object Home : Destino("home", "Inicio", { Icons.Filled.Home }, { Icons.Outlined.Home })
    data object Diary : Destino("diary", "Diario", { Icons.Filled.Book }, { Icons.Outlined.Book })
    data object Kitchen : Destino("kitchen", "Cocina", { Icons.Filled.Restaurant }, { Icons.Outlined.Restaurant })
    data object Profile : Destino("profile", "Perfil", { Icons.Filled.Person }, { Icons.Outlined.Person })

    companion object {
        val menu: List<Destino>
            get() = listOf(Home, Diary, Kitchen, Profile)
    }
}