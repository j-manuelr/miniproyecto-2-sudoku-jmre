# Sudoku 6×6 — Mini Proyecto #2

**Curso:** 750014C Fundamentos de Programación Orientada a Eventos (FPOE) — 2026-1  
**Integrantes:** Juan Rosero - Natalia Parra  
**Versión:** 1.0  
**Repositorio:** https://github.com/j-manuelr/miniproyecto-2-sudoku-jmre-napp

---

## Descripción

Implementación del juego Sudoku en una cuadrícula de **6×6** dividida en seis bloques de **2×3**. El objetivo es completar el tablero con los números del 1 al 6 sin repetir ninguno en la misma fila, columna o bloque. El tablero es generado dinámicamente en cada inicio garantizando una partida distinta siempre.

---

## Tecnologías y herramientas

| Componente           | Detalle                         |
|----------------------|---------------------------------|
| Lenguaje             | Java SE 17                      |
| Librería gráfica     | JavaFX 17.0.6                   |
| Diseño de interfaz   | Scene Builder + FXML            |
| IDE                  | IntelliJ IDEA                   |
| Build                | Maven (Maven Wrapper incluido)  |
| Control de versiones | Git y GitHub                    |
| Documentación        | Javadoc (HTML exportado)        |

---

## Arquitectura — MVC

```
src/main/java/com/example/sudoku/
│
├── Main.java                          ← Punto de entrada JavaFX
│
├── model/
│   ├── ISudokuModel.java              ← Contrato del modelo
│   ├── SudokuModel.java               ← Lógica del juego (validación, pistas, undo)
│   ├── SudokuBoardGenerator.java      ← Generación aleatoria del tablero (backtracking)
│   └── MoveHistory.java               ← Historial de movimientos (pila de undo)
│
├── view/
│   ├── IGameView.java                 ← Contrato de la vista
│   └── GameView.java                  ← Ventana principal (Singleton)
│
├── controller/
│   ├── IGameController.java           ← Contrato del controlador
│   ├── GameController.java            ← Controlador FXML + inner class CellInputHandler
│   └── GameTimer.java                 ← Temporizador delegado (SRP)
│
└── events/
    ├── ICellEventListener.java        ← Interfaz de eventos de celda
    └── CellEventAdapter.java          ← Adaptador con implementaciones vacías por defecto

src/main/resources/com/example/sudoku/
├── game-view.fxml                     ← Layout FXML (diseñado con Scene Builder)
└── game-style.css                     ← Estilos del tema oscuro
```

---

## Estructuras de datos utilizadas

1. **`LinkedList<Integer>`** (implementa `Deque<Integer>`) — Representación plana del tablero en `SudokuBoardGenerator` y `SudokuModel`. La celda `(row, col)` se mapea al índice `row * 6 + col`. Se usa directamente en la lógica de **construcción del tablero** durante el algoritmo de backtracking, cumpliendo el requisito de la rúbrica. Las operaciones `offerLast`, `set` y `get` del `Deque` se usan en toda la generación.

2. **`ArrayList<Integer>`** — Lista de candidatos aleatorizada dentro de `fillCell()` en `SudokuBoardGenerator`. En cada celda, los números 1–6 se agregan a un `ArrayList` y se mezclan con `Collections.shuffle()` para garantizar un tablero diferente en cada partida.

3. **`ArrayDeque<int[]>`** (como `Deque`) — Pila LIFO de historial de movimientos encapsulada en `MoveHistory`, que soporta la funcionalidad de **Deshacer (Undo)** en tiempo O(1).

4. **`ArrayList<int[]>`** — Usada en `getRandomEmptyCell()` para recolectar y mezclar aleatoriamente las celdas vacías antes de retornar una al azar.

---

## Heurísticas de usabilidad aplicadas (Nielsen)

1. **Visibilidad del estado del sistema** — Resaltado en rojo en tiempo real ante conflictos y cronómetro en vivo.
2. **Correspondencia con el mundo real** — Etiquetas y mensajes en español, vocabulario familiar del Sudoku.
3. **Control y libertad del usuario** — Botones "Deshacer" y "Reiniciar" siempre disponibles.
4. **Prevención de errores** — Solo se aceptan dígitos 1–6; cualquier otra tecla se descarta silenciosamente.
5. **Reconocimiento antes que recuerdo** — Las celdas fijas (pistas iniciales en cian) y las celdas de ayuda (en verde) se distinguen visualmente de las editables normales.

---

## Funcionalidades implementadas

| Historia de usuario | Descripción                                              | Estado |
|---------------------|----------------------------------------------------------|--------|
| HU-1                | Interfaz gráfica intuitiva, tablero 6×6 dinámico         | ✅      |
| HU-2                | Ingreso de números 1–6 por teclado, borrado con Delete   | ✅      |
| HU-3                | Validación en tiempo real, borde rojo en conflictos      | ✅      |
| HU-4                | Botón de ayuda, sugerencia visual en celda vacía         | ✅      |

Funcionalidades adicionales implementadas:

- **Cronómetro** en vivo desde el inicio de cada partida.
- **Deshacer** (botón + atajo `Ctrl + Z`).
- **Nueva Partida** genera un tablero completamente nuevo.
- **Reiniciar** restaura el tablero actual a su estado inicial (sin contar las pistas de ayuda como parte de la solución original).

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

La documentación HTML ya está pre-generada y disponible en la carpeta `javadoc/` del proyecto. Para abrirla, abrir directamente:

```
javadoc/index.html
```

Para regenerarla desde cero:

```bash
./mvnw javadoc:javadoc
# La documentación queda en: target/site/apidocs/index.html
```

---

## Gestión del proyecto — Git y GitHub

El repositorio usa control de versiones con ramas diferenciadas por funcionalidad:

- `main` — versión estable final, etiquetada como `v1.0`
- `rama1` — rama de desarrollo de funcionalidades del modelo
- `feature/help-hint` — rama de la funcionalidad de ayuda (pista)
- `modelo-natalia` — rama de desarrollo del modelo por integrante

Cada funcionalidad fue desarrollada en su propia rama y fusionada a `main` mediante Pull Request. La versión final está marcada con el tag `v1.0`.

---

## Licencia

Proyecto académico — Universidad del Valle, 2026.
