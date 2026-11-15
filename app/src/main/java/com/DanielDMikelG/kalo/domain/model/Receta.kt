package com.DanielDMikelG.kalo.domain.model

/** Receta de la sección Cocina. Valores aproximados por ración (1 persona). */
data class Receta(
    val id: String,
    val titulo: String,
    val descripcion: String,
    val tipo: TipoComida,
    val minutos: Int,
    val kcal: Int,
    val proteinaG: Int,
    val fibraG: Int,
    val ingredientes: List<String>,
    val pasos: List<String>,
) {
    val rapida: Boolean get() = minutos <= 15
    val ligera: Boolean get() = kcal < 400
    val altaProteina: Boolean get() = proteinaG >= 25
    val altaFibra: Boolean get() = fibraG >= 7
}

enum class CategoriaArticulo(val etiqueta: String) {
    ETIQUETAS("Etiquetas"),
    NUTRICION("Nutrición"),
    HABITOS("Hábitos"),
}

data class Articulo(
    val id: String,
    val titulo: String,
    val categoria: CategoriaArticulo,
    val parrafos: List<String>,
) {
    /** Tiempo de lectura aproximado a 200 palabras por minuto. */
    val minutosLectura: Int
        get() {
            val palabras = parrafos.sumOf { it.split(" ").size }
            return maxOf(1, (palabras + 199) / 200)
        }
}

enum class FiltroRecetas(val clave: String, val titulo: String) {
    TODAS("todas", "Todas las recetas"),
    DESAYUNO("desayunos", "Desayunos"),
    COMIDA("comidas", "Comidas"),
    CENA("cenas", "Cenas"),
    SNACK("snacks", "Snacks"),
    RAPIDAS("rapidas", "Listas en 15 minutos"),
    ALTA_PROTEINA("proteina", "Altas en proteína"),
    LIGERAS("ligeras", "Ligeras"),
    ALTA_FIBRA("fibra", "Con mucha fibra");

    companion object {
        fun desdeClave(clave: String?): FiltroRecetas =
            entries.firstOrNull { it.clave == clave } ?: TODAS
    }
}