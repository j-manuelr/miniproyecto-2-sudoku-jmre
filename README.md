# Sudoku 6×6 — Mini Proyecto #2

**Curso:** 750014C Fundamentos de Programación Orientada a Eventos (FPOE) — 2026-1  
**Integrantes:** Juan Rosero  
**Versión:** 1.0

---

## Descripción

Implementación del juego Sudoku en una cuadrícula de **6×6** dividida en seis bloques de **2×3**. El objetivo es completar el tablero con los números del 1 al 6 sin repetir ninguno en la misma fila, columna o bloque. El tablero es generado dinámicamente en cada inicio garantizando una partida distinta siempre.

---

## Tecnologías y herramientas

| Componente          | Detalle                        |
|---------------------|--------------------------------|
| Lenguaje            | Java SE 17                     |
| Librería gráfica    | JavaFX 17.0.6                  |
| Diseño de interfaz  | Scene Builder + FXML           |
| IDE                 | IntelliJ IDEA                  |
| Build               | Maven (Maven Wrapper incluido) |
| Control de versiones| Git y GitHub                   |
| Documentación       | Javadoc (HTML exportado)       |

---

## Arquitectura — MVC

```
src/main/java/com/example/sudoku/
│
├── Main.java                          ← Punto de entrada JavaFX
│
├── model/
│   ├── ISudokuModel.java              ← Contrato del modelo
│   └── SudokuModel.java              ← Lógica del juego (generación, validación, undo)
│
├── view/
│   ├── IGameView.java                 ← Contrato de la vista
│   └── GameStage.java                ← Ventana principal (Singleton)
│
├── controller/
│   ├── IGameController.java          ← Contrato del controlador
│   └── GameController.java           ← Controlador FXML + inner class CellInputHandler
│
└── events/
    ├── ICellEventListener.java        ← Interfaz de eventos de celda
    └── CellEventAdapter.java         ← Adaptador con implementaciones vacías por defecto

src/main/resources/com/example/sudoku/
├── game-view.fxml                     ← Layout FXML (diseñado con Scene Builder)
└── game-style.css                     ← Estilos del tema oscuro
```

---

## Estructuras de datos utilizadas

1. **`ArrayList<Integer>`** — Lista de candidatos aleatorizados dentro de `generateSolution()` para garantizar un tablero diferente en cada partida. También usada en `getRandomEmptyCell()` para recolectar celdas vacías.
2. **`ArrayDeque<int[]>`** (como `Deque`) — Pila de historial de movimientos (`moveHistory`) que soporta la funcionalidad de **Deshacer (Undo)** en tiempo O(1).

Ambas estructuras son distintas a arreglos uni o bidimensionales, y la primera está directamente en la lógica de construcción del tablero, cumpliendo el criterio de la rúbrica.

---

## Heurísticas de usabilidad aplicadas (Nielsen)

1. **Visibilidad del estado del sistema** — Resaltado en rojo en tiempo real ante conflictos y cronómetro en vivo.
2. **Correspondencia con el mundo real** — Etiquetas y mensajes en español, vocabulario familiar del Sudoku.
3. **Control y libertad del usuario** — Botones "Deshacer" y "Reiniciar" siempre disponibles.
4. **Prevención de errores** — Solo se aceptan dígitos 1–6; cualquier otra tecla se descarta silenciosamente.
5. **Reconocimiento antes que recuerdo** — Las celdas fijas (pistas) se distinguen visualmente de las editables y las sugerencias de ayuda tienen su propio estilo destacado.

---

## Funcionalidades implementadas

| Historia de usuario | Descripción                                             | Estado |
|---------------------|---------------------------------------------------------|--------|
| HU-1                | Interfaz gráfica intuitiva, tablero 6×6 dinámico        | ✅      |
| HU-2                | Ingreso de números 1–6 por teclado, borrado con Delete  | ✅      |
| HU-3                | Validación en tiempo real, borde rojo en conflictos     | ✅      |
| HU-4                | Botón de ayuda, sugerencia visual en celda vacía        | ✅      |

Funcionalidades adicionales implementadas:
- **Cronómetro** en vivo desde el inicio de cada partida.
- **Deshacer** (botón + atajo `Ctrl + Z`).
- **Nueva Partida** genera un tablero completamente nuevo.
- **Reiniciar** restaura el tablero actual a su estado inicial.

---

## Cómo ejecutar el proyecto

### Requisitos previos
- JDK 17 o superior instalado
- Maven (o usar el Maven Wrapper incluido)

### Ejecutar con Maven Wrapper

```bash
# En Linux / macOS
./mvnw javafx:run

# En Windows
mvnw.cmd javafx:run
```

### Ejecutar desde IntelliJ IDEA
1. Abrir el proyecto como proyecto Maven.
2. Ejecutar la configuración `Main` o usar el plugin JavaFX.

---

## Documentación Javadoc

El código fuente está completamente documentado en inglés con Javadoc.  
Para generar y abrir la documentación HTML:

```bash
./mvnw javadoc:javadoc
# La documentación queda en: target/site/apidocs/index.html
```

---

## Gestión del proyecto — Git Flow

El repositorio sigue el flujo **GitFlow**:

- `main` — versión estable final etiquetada como `v1.0`
- `develop` — rama de integración continua
- `feature/*` — ramas de desarrollo por funcionalidad
- Pull Requests de cada `feature` hacia `develop`, y de `develop` hacia `main` con el tag de versión final

---

## Licencia

Proyecto académico — Universidad del Valle, 2026.
