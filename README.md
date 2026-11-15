# Kalo

Aplicación Android nativa para llevar la cuenta de las calorías y los macronutrientes de cada día, con una mascota que reacciona a cómo vas.

## Qué hace

- **Registro y objetivo:** crea tu cuenta, indica tus datos y tu objetivo (adelgazar, mantener o ganar masa) y Kalo calcula tu meta de calorías.
- **Diario:** muestra lo que has comido por desayuno, comida, cena y snack, con calorías restantes, macros (proteínas, grasas, carbohidratos y fibra) y vasos de agua. Se puede navegar entre días y editar o borrar entradas.
- **Añadir comida** de cuatro formas: buscar por nombre (en tu dispositivo y en Open Food Facts), escanear el código de barras, crear tus propios alimentos o elegir entre los guardados recientemente.
- **Cantidad:** eliges los gramos y ves al momento las calorías y los macros antes de guardar.
- **Inicio:** resumen del día, mascota, racha de días seguidos y acceso a la receta del día.
- **Cocina:** receta del día, listas por tipo de comida y por objetivo, detalle de recetas y artículos.
- **Perfil y ajustes.**

## Tecnologías

Kotlin, Jetpack Compose (Material 3), arquitectura MVVM, Room, Retrofit con Gson, CameraX y ML Kit (escáner de códigos de barras) y Navigation Compose. Más detalle en [docs/arquitectura.md](docs/arquitectura.md).

## Cómo probarla

**Con el APK:** descarga `[nombre-del-apk].apk` desde `[enlace o carpeta de la entrega]` e instálalo en un móvil Android (versión mínima: `[minSdk]`). Hay que permitir la instalación de orígenes desconocidos.

**Desde el código:**
1. Clona el repositorio y ábrelo con Android Studio.
2. Sincroniza Gradle y ejecuta la configuración `app` en un emulador o en un móvil.
3. Para el escáner de códigos de barras hace falta una cámara real y conceder el permiso de cámara. En el emulador se puede escribir el código a mano.

**Tests:** `./gradlew test` ejecuta los tests unitarios (cálculos de calorías, macros, nutrientes por ración, racha, receta del día y validación de alimentos).

## Datos de alimentos

Los datos de productos proceden de [Open Food Facts](https://world.openfoodfacts.org), una base de datos colaborativa publicada bajo licencia [ODbL](https://opendatacommons.org/licenses/odbl/1-0/). Kalo solo guarda en el dispositivo los productos que el usuario elige.

## Limitaciones conocidas

- Los valores nutricionales de las recetas de Cocina son **aproximados**, y la información de la app es orientativa y **no sustituye el consejo de un profesional de la salud**.
- Las búsquedas muy genéricas en internet (por ejemplo "pan") pueden tardar o fallar cuando Open Food Facts está saturado; con un nombre más concreto responde mejor.
- Los datos de la app son locales: no hay sincronización entre dispositivos.
- Las misiones y el buscador de recetas quedan fuera de esta entrega.
- `[Si lo has comprobado: la lectura con la cámara se ha probado en ... / pendiente de probar en más dispositivos]`

## Equipo

- Daniel Denyshchuk
- Mikel Gómez

Proyecto de `[asignatura / centro]`, entrega del 15 de noviembre de 2026.
