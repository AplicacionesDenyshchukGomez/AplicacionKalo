package com.DanielDMikelG.kalo.ui.screens.kitchen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DanielDMikelG.kalo.domain.model.Articulo
import com.DanielDMikelG.kalo.domain.model.Receta

// ---- Lista de recetas (con filtro) ----
@Composable
fun ListaRecetasScreen(
    titulo: String,
    recetas: List<Receta>,
    onReceta: (String) -> Unit,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CocinaFondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BotonVolver(onVolver)
            Text(titulo, color = CocinaTexto, fontSize = 24.sp, fontWeight = FontWeight.Black)
        }
        if (recetas.isEmpty()) {
            Text(
                "Todavía no hay recetas en esta categoría.",
                modifier = Modifier.padding(horizontal = 8.dp),
                color = CocinaTextoSec, fontSize = 15.sp,
            )
        } else {
            recetas.forEach { FilaReceta(it) { onReceta(it.id) } }
        }
    }
}

@Composable
private fun FilaReceta(receta: Receta, onClick: () -> Unit) {
    val estilo = receta.tipo.estiloCocina()
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = Color.White,
        border = BorderStroke(2.dp, CocinaBorde),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(64.dp).background(estilo.fondo, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(30.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    receta.titulo, color = CocinaTexto, fontSize = 17.sp, fontWeight = FontWeight.Black,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${receta.minutos} min · ${receta.kcal} kcal · ${receta.proteinaG} g prot.",
                    color = CocinaTextoSec, fontSize = 13.sp,
                )
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CocinaTextoSec)
        }
    }
}

// ---- Detalle de receta ----
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetalleRecetaScreen(
    receta: Receta,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estilo = receta.tipo.estiloCocina()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CocinaFondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BotonVolver(onVolver, Modifier.padding(start = 0.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .size(width = 0.dp, height = 140.dp)
                .background(estilo.fondo, RoundedCornerShape(28.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(64.dp))
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                receta.tipo.etiqueta.uppercase(),
                color = estilo.color, fontSize = 13.sp, fontWeight = FontWeight.Black,
            )
            Text(receta.titulo, color = CocinaTexto, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text(receta.descripcion, color = CocinaTextoSec, fontSize = 15.sp)
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Pildora("${receta.minutos} min")
            Pildora("${receta.kcal} kcal")
            Pildora("${receta.proteinaG} g proteína")
            Pildora("${receta.fibraG} g fibra")
        }

        Bloque("Ingredientes") {
            receta.ingredientes.forEach { ingrediente ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(Modifier.size(8.dp).background(CocinaVerde, CircleShape))
                    Text(ingrediente, color = CocinaTexto, fontSize = 16.sp)
                }
            }
        }

        Bloque("Preparación") {
            receta.pasos.forEachIndexed { indice, paso ->
                Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        Modifier.size(28.dp).background(CocinaContenedor, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("${indice + 1}", color = CocinaTexto, fontSize = 14.sp, fontWeight = FontWeight.Black)
                    }
                    Text(paso, color = CocinaTexto, fontSize = 16.sp, modifier = Modifier.weight(1f))
                }
            }
        }

        Text(
            "Valores aproximados por ración (1 persona). Son orientativos.",
            color = CocinaTextoSec, fontSize = 12.sp,
        )
    }
}

// ---- Detalle de artículo ----
@Composable
fun DetalleArticuloScreen(
    articulo: Articulo,
    onVolver: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val estilo = articulo.categoria.estiloCocina()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CocinaFondo)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        BotonVolver(onVolver)

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                articulo.categoria.etiqueta.uppercase(),
                color = estilo.color, fontSize = 13.sp, fontWeight = FontWeight.Black,
            )
            Text(articulo.titulo, color = CocinaTexto, fontSize = 26.sp, fontWeight = FontWeight.Black)
            Text("${articulo.minutosLectura} min de lectura", color = CocinaTextoSec, fontSize = 13.sp)
        }

        articulo.parrafos.forEach {
            Text(it, color = CocinaTexto, fontSize = 17.sp, lineHeight = 26.sp)
        }
    }
}

// ---- Piezas ----
@Composable
private fun Pildora(texto: String) {
    Surface(shape = CircleShape, color = CocinaContenedor) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = CocinaTexto, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
        )
    }
}

@Composable
private fun Bloque(titulo: String, contenido: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = Color.White,
        border = BorderStroke(2.dp, CocinaBorde),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(titulo, color = CocinaTexto, fontSize = 20.sp, fontWeight = FontWeight.Black)
            contenido()
        }
    }
}