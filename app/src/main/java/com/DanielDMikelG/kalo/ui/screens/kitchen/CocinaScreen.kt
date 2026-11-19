package com.DanielDMikelG.kalo.ui.screens.kitchen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.DanielDMikelG.kalo.data.contenido.CatalogoCocina
import com.DanielDMikelG.kalo.domain.model.Articulo
import com.DanielDMikelG.kalo.domain.model.FiltroRecetas
import com.DanielDMikelG.kalo.domain.model.Receta
import com.DanielDMikelG.kalo.domain.model.TipoComida
import com.DanielDMikelG.kalo.domain.usecase.ElegirRecetaDelDiaUseCase
import com.DanielDMikelG.kalo.ui.theme.esTemaOscuro
import com.DanielDMikelG.kalo.ui.theme.kaloColors

@Composable
fun CocinaRoute(
    onReceta: (String) -> Unit,
    onArticulo: (String) -> Unit,
    onFiltro: (FiltroRecetas) -> Unit,
    modifier: Modifier = Modifier,
) {
    val recetaDelDia = remember { ElegirRecetaDelDiaUseCase()(CatalogoCocina.recetas) }
    val rapidas = remember { CatalogoCocina.filtrar(FiltroRecetas.RAPIDAS).take(6) }
    CocinaScreen(
        recetaDelDia = recetaDelDia,
        rapidas = rapidas,
        articulos = CatalogoCocina.articulos,
        onReceta = onReceta,
        onArticulo = onArticulo,
        onFiltro = onFiltro,
        modifier = modifier,
    )
}

@Composable
fun CocinaScreen(
    recetaDelDia: Receta?,
    rapidas: List<Receta>,
    articulos: List<Articulo>,
    onReceta: (String) -> Unit,
    onArticulo: (String) -> Unit,
    onFiltro: (FiltroRecetas) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CocinaFondo)
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CocinaCabecera, RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp))
                .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text("Cocina", color = CocinaTexto, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text(
                "Ideas sencillas para comer bien hoy",
                color = CocinaTextoSec, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold,
            )
        }

        Column(
            modifier = Modifier.padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(28.dp),
        ) {
            if (recetaDelDia != null) {
                Seccion("Receta del día") { TarjetaRecetaDelDia(recetaDelDia) { onReceta(recetaDelDia.id) } }
            }

            Seccion("Por tipo de comida") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotonTipo("Desayunos", TipoComida.DESAYUNO, Modifier.weight(1f)) { onFiltro(FiltroRecetas.DESAYUNO) }
                        BotonTipo("Comidas", TipoComida.COMIDA, Modifier.weight(1f)) { onFiltro(FiltroRecetas.COMIDA) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        BotonTipo("Cenas", TipoComida.CENA, Modifier.weight(1f)) { onFiltro(FiltroRecetas.CENA) }
                        BotonTipo("Snacks", TipoComida.SNACK, Modifier.weight(1f)) { onFiltro(FiltroRecetas.SNACK) }
                    }
                }
            }

            if (rapidas.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    TituloSeccion(
                        "Listas en 15 minutos",
                        accion = "Ver todas",
                        onAccion = { onFiltro(FiltroRecetas.RAPIDAS) },
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        items(rapidas, key = { it.id }) { TarjetaRapida(it) { onReceta(it.id) } }
                    }
                }
            }

            Seccion("Según tu objetivo") {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilaObjetivo(
                        "Altas en proteína", "Para ganar masa o sentirte saciado",
                        MaterialTheme.kaloColors.protein, MaterialTheme.kaloColors.proteinSoft,
                    ) { onFiltro(FiltroRecetas.ALTA_PROTEINA) }
                    FilaObjetivo(
                        "Ligeras", "Menos de 400 kcal por ración",
                        Color(0xFF7FD3A4), MaterialTheme.colorScheme.surfaceVariant,
                    ) { onFiltro(FiltroRecetas.LIGERAS) }
                    FilaObjetivo(
                        "Con mucha fibra", "Legumbres, verduras y cereales integrales",
                        MaterialTheme.kaloColors.fiber, MaterialTheme.kaloColors.fiberSoft,
                    ) { onFiltro(FiltroRecetas.ALTA_FIBRA) }
                }
            }

            if (articulos.isNotEmpty()) {
                Seccion("Para leer") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        articulos.forEach { TarjetaArticulo(it) { onArticulo(it.id) } }
                    }
                }
            }
        }
    }
}

// ---- Estructura de las secciones ----

@Composable
private fun Seccion(titulo: String, contenido: @Composable () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            titulo,
            modifier = Modifier.padding(start = 4.dp),
            color = CocinaTexto, fontSize = 21.sp, fontWeight = FontWeight.Black,
        )
        contenido()
    }
}

@Composable
private fun TituloSeccion(titulo: String, accion: String, onAccion: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(titulo, color = CocinaTexto, fontSize = 21.sp, fontWeight = FontWeight.Black)
        TextButton(onClick = onAccion) {
            Text(accion, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}

// ---- Receta del día ----

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TarjetaRecetaDelDia(receta: Receta, onVer: () -> Unit) {
    Surface(shape = RoundedCornerShape(32.dp), color = CocinaContenedor, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(CocinaSuperficie, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Plato()
                Surface(
                    shape = CircleShape,
                    color = CocinaTexto,
                    modifier = Modifier.align(Alignment.TopStart).padding(12.dp),
                ) {
                    Text(
                        receta.tipo.etiqueta,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.background, fontSize = 13.sp, fontWeight = FontWeight.Black,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(receta.titulo, color = CocinaTexto, fontSize = 25.sp, fontWeight = FontWeight.Black)
                Text(receta.descripcion, color = CocinaTextoSec, fontSize = 15.sp)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Dato("${receta.minutos} min")
                Dato("${receta.kcal} kcal")
                Dato("${receta.proteinaG} g proteína")
            }
            Button(
                onClick = onVer,
                colors = ButtonDefaults.buttonColors(containerColor = CocinaVerde),
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Text("Ver receta", fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun Dato(texto: String) {
    Surface(shape = CircleShape, color = CocinaSuperficie) {
        Text(
            texto,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
            color = CocinaTexto, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold,
        )
    }
}

/** Plato con una tortilla y hojas, dibujado con Canvas. */
@Composable
private fun Plato() {
    val sombra = if (esTemaOscuro()) Color(0x33000000) else Color(0xFFDDF3E5)
    Canvas(Modifier.size(width = 150.dp, height = 130.dp)) {
        val s = size.width / 150f
        val centro = Offset(75f * s, 62f * s)
        drawOval(sombra, topLeft = Offset(17f * s, 104f * s), size = Size(116f * s, 16f * s))
        drawCircle(Color(0xFFF4FBF6), radius = 52f * s, center = centro)
        drawCircle(Color(0xFFB8E6CA), radius = 52f * s, center = centro, style = Stroke(width = 4f * s))
        drawCircle(Color(0xFFFFF1BF), radius = 38f * s, center = centro)
        drawCircle(Color(0xFFFFD966), radius = 38f * s, center = centro, style = Stroke(width = 3f * s))
        hoja(Offset(62f * s, 70f * s), 9f * s, 5f * s, Color(0xFF7FD3A4), -20f)
        hoja(Offset(86f * s, 52f * s), 9f * s, 5f * s, Color(0xFF3FB37A), 25f)
        hoja(Offset(90f * s, 76f * s), 8f * s, 4.5f * s, Color(0xFF7FD3A4), -10f)
        hoja(Offset(66f * s, 48f * s), 7f * s, 4f * s, Color(0xFF5FC58E), 15f)
    }
}

private fun DrawScope.hoja(centro: Offset, radioX: Float, radioY: Float, color: Color, grados: Float) {
    rotate(grados, pivot = centro) {
        drawOval(
            color,
            topLeft = Offset(centro.x - radioX, centro.y - radioY),
            size = Size(radioX * 2, radioY * 2),
        )
    }
}

// ---- Tipos, rápidas, objetivos, artículos ----

@Composable
private fun BotonTipo(titulo: String, tipo: TipoComida, modifier: Modifier, onClick: () -> Unit) {
    val estilo = tipo.estiloCocina()
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = estilo.fondo,
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                Modifier.size(44.dp).background(CocinaSuperficie, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(24.dp))
            }
            Text(titulo, color = CocinaTexto, fontSize = 17.sp, fontWeight = FontWeight.Black)
        }
    }
}

@Composable
private fun TarjetaRapida(receta: Receta, onClick: () -> Unit) {
    val estilo = receta.tipo.estiloCocina()
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = CocinaSuperficie,
        border = BorderStroke(2.dp, CocinaBorde),
        modifier = Modifier.width(168.dp).clickable(onClick = onClick),
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(92.dp)
                    .background(estilo.fondo, RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(44.dp))
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    receta.titulo, color = CocinaTexto, fontSize = 16.sp, fontWeight = FontWeight.Black,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
                Text("${receta.minutos} min · ${receta.kcal} kcal", color = CocinaTextoSec, fontSize = 13.sp)
            }
        }
    }
}

@Composable
private fun FilaObjetivo(titulo: String, detalle: String, punto: Color, fondo: Color, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = CocinaSuperficie,
        border = BorderStroke(2.dp, CocinaBorde),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(Modifier.size(52.dp).background(fondo, CircleShape), contentAlignment = Alignment.Center) {
                Box(Modifier.size(20.dp).background(punto, CircleShape))
            }
            Column(Modifier.weight(1f)) {
                Text(titulo, color = CocinaTexto, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(detalle, color = CocinaTextoSec, fontSize = 14.sp)
            }
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = CocinaTextoSec)
        }
    }
}

@Composable
private fun TarjetaArticulo(articulo: Articulo, onClick: () -> Unit) {
    val estilo = articulo.categoria.estiloCocina()
    Surface(
        shape = RoundedCornerShape(26.dp),
        color = CocinaSuperficie,
        border = BorderStroke(2.dp, CocinaBorde),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Box(
                Modifier.size(88.dp).background(estilo.fondo, RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(estilo.icono, contentDescription = null, tint = estilo.color, modifier = Modifier.size(40.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    articulo.categoria.etiqueta.uppercase(),
                    color = estilo.color, fontSize = 12.sp, fontWeight = FontWeight.Black,
                )
                Text(
                    articulo.titulo, color = CocinaTexto, fontSize = 17.sp, fontWeight = FontWeight.Black,
                    maxLines = 3, overflow = TextOverflow.Ellipsis,
                )
                Text("${articulo.minutosLectura} min de lectura", color = CocinaTextoSec, fontSize = 13.sp)
            }
        }
    }
}