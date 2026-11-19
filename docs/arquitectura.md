# Arquitectura de Kalo

Este documento describe la estructura de paquetes de la app a fecha de la entrega.

## Patrón: MVVM por capas

```
ui  ──►  domain  ◄──  data
```

- **ui** muestra datos y recoge acciones del usuario (Jetpack Compose + ViewModel).
- **domain** contiene los modelos y la lógica de negocio. No depende de Android ni de la base de datos.
- **data** guarda y obtiene datos (base de datos local, API, contenido estático). Implementa lo que necesita `domain`.

### Reglas
1. `ui` nunca accede directamente a la base de datos ni a la red: pasa por `data.repository`.
2. `domain` no importa nada de `ui` ni de `data`.
3. Cada pantalla tiene su propio paquete dentro de `ui.screens`, con su ViewModel y sus composables.
4. La lógica de cálculo (calorías, macros, nutrientes por ración, racha, receta del día) vive en `domain.usecase` como funciones puras, para poder probarla con tests unitarios.

## Paquetes

Raíz: `com.DanielDMikelG.kalo`

| Paquete | Para qué sirve |
|---|---|
| `data.local` | Base de datos local (Room): `KaloDatabase` y `SesionManager` |
| `data.local.entity` / `dao` / `relation` | Entidades, DAO y relaciones de Room |
| `data.mapper` | Conversión entre entidades de Room y modelos del dominio |
| `data.remote` | Cliente de Open Food Facts (búsqueda por código y por nombre) |
| `data.repository` | Repositorios que unen las fuentes de datos y las exponen a la app |
| `data.contenido` | Contenido estático de Cocina (recetas y artículos) |
| `domain.model` | Clases del dominio (Usuario, Alimento, Receta, etc.) |
| `domain.usecase` | Casos de uso: calorías, macros, nutrientes por ración, racha, receta del día |
| `ui.theme` | Colores, tipografía y tema de la app |
| `ui.components` | Componentes reutilizables (tarjetas, campos, mascota, vasos de agua) |
| `ui.navigation` | Barra inferior y rutas de navegación |
| `ui.screens.login` / `register` / `objective` | Inicio de sesión, registro y objetivo |
| `ui.screens.home` | Inicio y resumen diario |
| `ui.screens.diary` | Diario de comidas, macros y agua |
| `ui.screens.anadir` | Añadir comida (buscar, escanear, mis alimentos) y cantidad |
| `ui.screens.scanner` | Escáner de códigos de barras (CameraX + ML Kit) |
| `ui.screens.product` | Ficha de producto |
| `ui.screens.myfoods` | Alimentos creados por el usuario |
| `ui.screens.kitchen` | Cocina: receta del día, listas, recetas y artículos |
| `ui.screens.profile` / `settings` | Perfil y ajustes |
| `util` | Funciones auxiliares comunes |

## Pendiente (fuera de la entrega)

- Inyección de dependencias (`di`), avisos de ayuno (`notifications`, `worker`).
- Logros y misiones.
- Añadir recetas al Diario y buscador de recetas.

## Datos externos

Los datos de productos proceden de [Open Food Facts](https://world.openfoodfacts.org), bajo licencia ODbL. Solo se guardan en el dispositivo los productos que el usuario elige.

## Convenciones

- Ramas: `main` (estable), `dev` (integración) y `feature/nombre_con_guion_bajo` por issue.
- Commits: mensaje corto en español que empieza por la pantalla o el área, por ejemplo `Mis alimentos: crear y listar alimentos propios (#86)`.
- Todo entra por pull request a `dev`, revisada por la otra persona, con merge commit.
- Los issues se cierran a mano al fusionar.
