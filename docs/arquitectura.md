# Arquitectura de Kalo

Este documento describe la **estructura inicial** de paquetes de la app. No es la estructura final: se ampliará con cada issue, creando los paquetes nuevos solo cuando hagan falta.

## Patrón: MVVM por capas

```
ui  ──►  domain  ◄──  data
```

- **ui** muestra datos y recoge acciones del usuario (Jetpack Compose + ViewModel).
- **domain** contiene los modelos y la lógica de negocio. No depende de Android ni de la base de datos.
- **data** guarda y obtiene datos (base de datos local, API). Implementa lo que necesita `domain`.

### Reglas
1. `ui` nunca accede directamente a la base de datos ni a la red: pasa por `data.repository`.
2. `domain` no importa nada de `ui` ni de `data`.
3. Cada pantalla tiene su propio paquete dentro de `ui.screens`, con su ViewModel y sus composables.
4. Los paquetes nuevos se crean dentro de la issue que los necesita.

## Paquetes actuales

Raíz: `com.DanielDMikelG.kalo`

| Paquete | Para qué sirve | Issue relacionada |
|---|---|---|
| `data.local` | Base de datos local (Room): entidades, DAO y base de datos | #6 |
| `data.repository` | Repositorios que unen las fuentes de datos y las exponen a la app | #7 |
| `domain.model` | Clases del dominio (Usuario, Alimento, etc.) | #9 |
| `ui.theme` | Colores, tipografía y tema de la app | #12 |
| `ui.components` | Componentes reutilizables (botones, tarjetas, campos) | #12 |
| `ui.navigation` | Barra inferior y navegación entre pantallas | #4 |
| `ui.screens.home` | Pantalla de inicio y resumen diario | #5 |
| `ui.screens.diary` | Diario de comidas | #1, #2 |
| `ui.screens.kitchen` | Cocina y recetas (fase posterior) | - |
| `ui.screens.profile` | Perfil y edición de datos | #11 |
| `util` | Funciones auxiliares comunes | - |

## Se añadirá más adelante

- `data.remote`: cliente de Open Food Facts.
- `domain.usecase`: casos de uso, como el cálculo de calorías.
- `ui.screens.water`, `weight`, `scanner`, `achievements`, `onboarding`.
- `di`, `notifications` y `worker`: inyección de dependencias y avisos de ayuno.

## Convenciones

- Ramas: `main` (estable), `dev` (integración) y `feature/nombre` por issue.
- Commits con prefijo: `feat:`, `fix:`, `chore:`, `docs:`, `refactor:`, `test:`.
- Todo entra por pull request a `dev`, revisada por la otra persona.
