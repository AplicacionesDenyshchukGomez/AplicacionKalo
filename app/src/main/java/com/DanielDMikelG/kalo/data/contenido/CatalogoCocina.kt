package com.DanielDMikelG.kalo.data.contenido

import com.DanielDMikelG.kalo.domain.model.Articulo
import com.DanielDMikelG.kalo.domain.model.CategoriaArticulo
import com.DanielDMikelG.kalo.domain.model.FiltroRecetas
import com.DanielDMikelG.kalo.domain.model.Receta
import com.DanielDMikelG.kalo.domain.model.TipoComida

/** Contenido estático de la sección Cocina. Valores nutricionales aproximados por ración. */
object CatalogoCocina {

    val recetas: List<Receta> = listOf(
        Receta(
            id = "tortilla-espinacas",
            titulo = "Tortilla de espinacas",
            descripcion = "Rápida, ligera y con mucha proteína para empezar el día.",
            tipo = TipoComida.DESAYUNO, minutos = 15, kcal = 310, proteinaG = 22, fibraG = 2,
            ingredientes = listOf(
                "3 huevos", "100 g de espinacas frescas",
                "1 cucharadita de aceite de oliva", "Sal y pimienta",
            ),
            pasos = listOf(
                "Lava las espinacas y trocéalas.",
                "Saltéalas en una sartén con la mitad del aceite durante 2 minutos, hasta que se reduzcan.",
                "Bate los huevos con sal y pimienta y mézclalos con las espinacas.",
                "Vierte la mezcla en la sartén con el resto del aceite a fuego medio y cuájala 2 o 3 minutos por cada lado.",
            ),
        ),
        Receta(
            id = "bowl-pollo-arroz",
            titulo = "Bowl de pollo y arroz",
            descripcion = "Un plato completo y saciante, ideal si te sobró arroz de otro día.",
            tipo = TipoComida.COMIDA, minutos = 12, kcal = 420, proteinaG = 34, fibraG = 3,
            ingredientes = listOf(
                "120 g de pechuga de pollo", "150 g de arroz cocido", "80 g de brócoli",
                "1 cucharadita de aceite de oliva", "1 cucharada de salsa de soja", "Pimienta",
            ),
            pasos = listOf(
                "Corta el pollo en dados y sazónalo con pimienta.",
                "Dóralo en una sartén con el aceite durante 5 o 6 minutos.",
                "Cuece el brócoli al vapor o en agua hirviendo unos 4 minutos.",
                "Calienta el arroz, monta el bowl con el pollo y el brócoli y añade la salsa de soja.",
            ),
        ),
        Receta(
            id = "ensalada-atun",
            titulo = "Ensalada de atún",
            descripcion = "Fresca, sin cocinar y lista en diez minutos.",
            tipo = TipoComida.COMIDA, minutos = 10, kcal = 310, proteinaG = 29, fibraG = 4,
            ingredientes = listOf(
                "1 lata de atún al natural (unos 80 g escurrido)", "2 tomates", "60 g de lechuga",
                "1 huevo cocido", "1 cucharada de aceite de oliva", "Sal y vinagre",
            ),
            pasos = listOf(
                "Lava y trocea los tomates y la lechuga.",
                "Ponlos en un bol y añade el atún escurrido.",
                "Corta el huevo cocido en cuartos y colócalo encima.",
                "Aliña con el aceite, la sal y el vinagre.",
            ),
        ),
        Receta(
            id = "yogur-avena",
            titulo = "Yogur con avena y fresas",
            descripcion = "Un desayuno sin fuego que se hace en cinco minutos.",
            tipo = TipoComida.DESAYUNO, minutos = 5, kcal = 290, proteinaG = 14, fibraG = 5,
            ingredientes = listOf("250 g de yogur natural", "30 g de copos de avena", "80 g de fresas"),
            pasos = listOf(
                "Lava y trocea las fresas.",
                "Mezcla el yogur con la avena en un bol.",
                "Añade las fresas por encima. Si te gusta la avena más blanda, déjalo reposar 2 minutos.",
            ),
        ),
        Receta(
            id = "lentejas-verduras",
            titulo = "Lentejas con verduras",
            descripcion = "Un plato de cuchara con mucha fibra, perfecto para preparar de más.",
            tipo = TipoComida.COMIDA, minutos = 30, kcal = 340, proteinaG = 19, fibraG = 18,
            ingredientes = listOf(
                "200 g de lentejas cocidas", "1 zanahoria", "1/2 cebolla",
                "1 cucharadita de aceite de oliva", "1 vaso de caldo de verduras",
                "Sal, pimentón y comino",
            ),
            pasos = listOf(
                "Pica la cebolla y la zanahoria en trozos pequeños.",
                "Sofríelas con el aceite durante 5 minutos.",
                "Añade las especias, las lentejas y el caldo.",
                "Cocina a fuego suave unos 15 minutos y rectifica de sal.",
            ),
        ),
        Receta(
            id = "salmon-horno",
            titulo = "Salmón con verduras al horno",
            descripcion = "Todo en una bandeja: poca limpieza y mucha proteína.",
            tipo = TipoComida.CENA, minutos = 25, kcal = 400, proteinaG = 31, fibraG = 3,
            ingredientes = listOf(
                "150 g de lomo de salmón", "1 calabacín pequeño", "1 pimiento rojo",
                "1 cucharadita de aceite de oliva", "Limón, sal y pimienta",
            ),
            pasos = listOf(
                "Precalienta el horno a 200 °C.",
                "Corta las verduras y colócalas en una bandeja con el aceite.",
                "Pon el salmón encima, sazónalo con sal y pimienta y añade unas gotas de limón.",
                "Hornea entre 15 y 18 minutos.",
            ),
        ),
        Receta(
            id = "crema-calabacin",
            titulo = "Crema de calabacín",
            descripcion = "Una cena suave y ligera que se prepara con pocos ingredientes.",
            tipo = TipoComida.CENA, minutos = 25, kcal = 190, proteinaG = 5, fibraG = 6,
            ingredientes = listOf(
                "300 g de calabacín", "1 patata pequeña (100 g)", "1/2 cebolla",
                "1 cucharadita de aceite de oliva", "Agua o caldo de verduras", "Sal",
            ),
            pasos = listOf(
                "Trocea el calabacín, la patata y la cebolla.",
                "Sofríe la cebolla con el aceite durante 3 minutos.",
                "Añade el calabacín y la patata y cubre con agua o caldo.",
                "Cuece 15 minutos y tritura hasta que quede fina.",
            ),
        ),
        Receta(
            id = "tostada-aguacate-huevo",
            titulo = "Tostada de aguacate y huevo",
            descripcion = "Grasas saludables y proteína en un desayuno de diez minutos.",
            tipo = TipoComida.DESAYUNO, minutos = 10, kcal = 295, proteinaG = 13, fibraG = 8,
            ingredientes = listOf(
                "1 rebanada de pan integral (40 g)", "1/2 aguacate", "1 huevo", "Limón, sal y pimienta",
            ),
            pasos = listOf(
                "Tuesta el pan.",
                "Cuece o fríe el huevo a tu gusto.",
                "Machaca el aguacate con unas gotas de limón y sal.",
                "Unta el pan con el aguacate y coloca el huevo encima.",
            ),
        ),
        Receta(
            id = "hummus-zanahoria",
            titulo = "Hummus con bastones de zanahoria",
            descripcion = "Un snack crujiente y con fibra que sacia entre horas.",
            tipo = TipoComida.SNACK, minutos = 5, kcal = 145, proteinaG = 5, fibraG = 7,
            ingredientes = listOf("50 g de hummus", "150 g de zanahoria"),
            pasos = listOf(
                "Pela las zanahorias y córtalas en bastones.",
                "Sírvelas con el hummus para mojar.",
            ),
        ),
        Receta(
            id = "manzana-cacahuete",
            titulo = "Manzana con crema de cacahuete",
            descripcion = "Dulce, rápido y fácil de llevar.",
            tipo = TipoComida.SNACK, minutos = 3, kcal = 185, proteinaG = 4, fibraG = 6,
            ingredientes = listOf("1 manzana", "15 g de crema de cacahuete sin azúcar añadido"),
            pasos = listOf(
                "Lava la manzana y córtala en gajos.",
                "Sírvela con la crema de cacahuete.",
            ),
        ),
    )

    val articulos: List<Articulo> = listOf(
        Articulo(
            id = "leer-etiquetas",
            titulo = "Cómo leer la etiqueta de un alimento",
            categoria = CategoriaArticulo.ETIQUETAS,
            parrafos = listOf(
                "Casi toda la información útil está en la tabla nutricional. Empieza por mirar si los valores son por 100 g o por ración: es la causa más habitual de confusión al comparar dos productos.",
                "Compara siempre con la columna de 100 g. Así dos productos de distinto tamaño se pueden comparar de forma justa.",
                "Después fíjate en las calorías, los azúcares, las grasas saturadas y la sal. Ningún alimento es bueno o malo por un solo número, pero estos datos ayudan a elegir entre opciones parecidas.",
                "La lista de ingredientes va ordenada de mayor a menor cantidad. Si el primer ingrediente es azúcar, o aparece varias veces con nombres distintos, el producto lleva más de lo que parece.",
                "En Kalo puedes escanear el código de barras de un producto para ver sus valores por 100 g y calcular lo que aportan los gramos que vas a comer.",
            ),
        ),
        Articulo(
            id = "proteinas-cada-dia",
            titulo = "Proteínas: de dónde sacarlas cada día",
            categoria = CategoriaArticulo.NUTRICION,
            parrafos = listOf(
                "Las proteínas ayudan a mantener y reparar la masa muscular y dan sensación de saciedad. Se encuentran tanto en alimentos de origen animal como vegetal.",
                "Entre las fuentes más habituales están los huevos, el pescado, el pollo y el pavo, los lácteos como el yogur y el queso fresco, las legumbres, el tofu y los frutos secos.",
                "Repartirlas entre las comidas del día suele resultar más fácil que concentrarlas en una sola: un huevo o un yogur en el desayuno, una legumbre o un pescado en la comida y algo ligero en la cena.",
                "Las necesidades dependen de cada persona. Como referencia general, muchas guías para adultos sanos hablan de unos 0,8 g por kilo de peso al día como mínimo, y quien entrena o busca ganar masa puede necesitar más. Son datos orientativos, no un consejo médico.",
                "En el Diario de Kalo puedes ver cuántos gramos de proteína llevas hoy frente a tu meta.",
            ),
        ),
        Articulo(
            id = "preparar-la-semana",
            titulo = "Cómo preparar la semana en una tarde",
            categoria = CategoriaArticulo.HABITOS,
            parrafos = listOf(
                "Dedicar un rato a preparar comida para varios días te ahorra decisiones entre semana y, con ellas, muchas comidas improvisadas.",
                "Empieza por planificar: elige dos o tres platos principales, apunta lo que necesitas y haz una sola compra.",
                "Cocina en tandas: una base de arroz o legumbres, una proteína al horno o a la plancha y verduras troceadas o asadas. Combinadas de formas distintas dan comidas variadas.",
                "Guarda todo en recipientes cerrados dentro de la nevera. Como pauta general, la comida cocinada se conserva unos tres o cuatro días refrigerada; lo que no vayas a comer en ese plazo, congélalo.",
                "Etiqueta cada recipiente con la fecha y, si puedes, pesa las raciones: así registrarlas en el Diario es mucho más fácil.",
            ),
        ),
    )

    fun receta(id: String): Receta? = recetas.firstOrNull { it.id == id }

    fun articulo(id: String): Articulo? = articulos.firstOrNull { it.id == id }

    fun filtrar(filtro: FiltroRecetas): List<Receta> = when (filtro) {
        FiltroRecetas.TODAS -> recetas
        FiltroRecetas.DESAYUNO -> recetas.filter { it.tipo == TipoComida.DESAYUNO }
        FiltroRecetas.COMIDA -> recetas.filter { it.tipo == TipoComida.COMIDA }
        FiltroRecetas.CENA -> recetas.filter { it.tipo == TipoComida.CENA }
        FiltroRecetas.SNACK -> recetas.filter { it.tipo == TipoComida.SNACK }
        FiltroRecetas.RAPIDAS -> recetas.filter { it.rapida }.sortedBy { it.minutos }
        FiltroRecetas.ALTA_PROTEINA -> recetas.filter { it.altaProteina }
        FiltroRecetas.LIGERAS -> recetas.filter { it.ligera }
        FiltroRecetas.ALTA_FIBRA -> recetas.filter { it.altaFibra }
    }
}