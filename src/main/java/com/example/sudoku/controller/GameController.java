package com.example.sudoku.controller;

import com.example.sudoku.events.CellEventAdapter;
import com.example.sudoku.model.ISudokuModel;
import com.example.sudoku.model.SudokuModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller for the Sudoku game view ({@code game-view.fxml}).
 *
<<<<<<< HEAD
 * <h2>Responsibilities (after SRP refactoring)</h2>
 * <ul>
 *   <li>Builds the 6×6 cell grid inside the FXML-injected {@link GridPane}.</li>
 *   <li>Keeps the visual cell state (CSS classes) in sync with the model.</li>
 *   <li>Delegates time tracking to {@link GameTimer}.</li>
 *   <li>Delegates cell-event handling to the inner class {@link CellInputHandler}.</li>
 * </ul>
 *
 * <h2>UX/UI heuristics applied (5 required for MP #2)</h2>
 * <ol>
 *   <li><b>Visibility of system status</b> — real-time conflict highlights and
 *       live timer.</li>
 *   <li><b>Match between system and real world</b> — Spanish labels and
 *       familiar Sudoku vocabulary.</li>
 *   <li><b>User control and freedom</b> — "Deshacer" and "Reiniciar" always
 *       available.</li>
 *   <li><b>Error prevention</b> — only digits 1–6 accepted; all other keys
 *       are silently consumed.</li>
 *   <li><b>Recognition over recall</b> — fixed clues (cyan) and hint cells
 *       (green) are visually distinct from normal editable cells.</li>
=======
 * <p>Single responsibility: wire the collaborating components and handle
 * the four user actions exposed by {@link IGameController}. Everything else
 * is delegated:</p>
 * <ul>
 *   <li>{@link BoardBuilder}  — constructs the JavaFX node grid.</li>
 *   <li>{@link CellRenderer}  — applies CSS style classes to cells.</li>
 *   <li>{@link GameTimer}     — manages the countdown label.</li>
 *   <li>{@link SudokuModel}   — owns all game/board state.</li>
 *   <li>{@link CellInputHandler} (inner class) — routes cell input events.</li>
 * </ul>
 *
 * <h2>UX heuristics applied (Nielsen)</h2>
 * <ol>
 *   <li><em>Visibility of system status</em> — real-time conflict highlights and live timer.</li>
 *   <li><em>Match between system and real world</em> — Spanish labels and familiar vocabulary.</li>
 *   <li><em>User control and freedom</em> — Undo (button + Ctrl+Z) and Restart always available.</li>
 *   <li><em>Error prevention</em> — only digits 1–6 accepted; wrong digits auto-reverted.</li>
 *   <li><em>Recognition over recall</em> — fixed clues visually distinct from user entries.</li>
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
 * </ol>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class GameController implements Initializable, IGameController {

    // ── FXML-injected nodes ───────────────────────────────────────────────────
    @FXML private GridPane sudokuGrid;
    @FXML private Label    statusLabel;
    @FXML private Label    timerLabel;

    private static final String STATUS_SUCCESS_CLASS = "status-success";
    private static final String STATUS_ERROR_CLASS = "status-error";

<<<<<<< HEAD
    // -------------------------------------------------------------------------
    // FXML-injected nodes
    // -------------------------------------------------------------------------

    /**
     * Outer GridPane (3 block-rows × 2 block-cols) injected from FXML.
     */
    @FXML
    private GridPane sudokuGrid;

    /**
     * Status / feedback label below the board.
     */
    @FXML
    private Label statusLabel;

    /**
     * Live game-timer label in the header area.
     */
    @FXML
    private Label timerLabel;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    /**
     * Reference to the game model.
     */
=======
    // ── Collaborators ─────────────────────────────────────────────────────────
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    private ISudokuModel model;
    private TextField[][]  cells;
    private CellRenderer   renderer;
    private GameTimer      gameTimer;

<<<<<<< HEAD
    /**
     * 6×6 matrix of the {@link TextField} cells composing the board.
     */
    private TextField[][] cells;

    /**
     * Row index of the currently selected cell (−1 = none).
     */
    private int selectedRow = -1;

    /**
     * Column index of the currently selected cell (−1 = none).
     */
    private int selectedCol = -1;

    /**
     * Tracks which cells were filled by the hint system so their green
     * {@code cell-hint} style is preserved across subsequent refreshes.
     * Cleared on new game, restart, and when the corresponding move is undone.
     */
    private boolean[][] hintCells;

    /**
     * Timer delegate — extracted from this class to honour SRP.
     * Previously this controller managed the {@code Timeline} directly.
     */
    private GameTimer gameTimer;

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    /**
     * Invoked by {@link javafx.fxml.FXMLLoader} after all {@code @FXML} fields
     * are injected.  Creates the model, builds the grid, loads the first
     * puzzle, and starts the timer.
=======
    // ── Selection state ───────────────────────────────────────────────────────
    private int selectedRow = -1;
    private int selectedCol = -1;

    // ── Initializable ─────────────────────────────────────────────────────────

    /**
     * Invoked by {@link javafx.fxml.FXMLLoader} after all {@code @FXML} fields
     * are injected. Creates collaborators, builds the grid, and starts the timer.
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     *
     * @param url            unused
     * @param resourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
<<<<<<< HEAD
        cells = new TextField[ISudokuModel.BOARD_SIZE][ISudokuModel.BOARD_SIZE];
        hintCells = new boolean[ISudokuModel.BOARD_SIZE][ISudokuModel.BOARD_SIZE];
        model = new SudokuModel();
        gameTimer = new GameTimer(timerLabel);

        buildGrid();
        refreshAllCells();
        clearStatus();
        gameTimer.start();

        // Stop the timer when the window closes to prevent resource leaks
        timerLabel.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWin, oldWin, newWin) -> {
                    if (newWin != null) newWin.setOnHidden(e -> gameTimer.stop());
=======
        model     = new SudokuModel();
        cells     = new BoardBuilder().build(sudokuGrid);
        renderer  = new CellRenderer(cells, model);
        gameTimer = new GameTimer(timerLabel);

        attachHandlers();
        renderer.refreshAll(selectedRow, selectedCol);
        clearStatus();
        gameTimer.start();

        // Stop timer cleanly when the window closes
        timerLabel.sceneProperty().addListener((obs, old, scene) -> {
            if (scene != null) {
                scene.windowProperty().addListener((obsW, oldW, window) -> {
                    if (window != null) window.setOnHidden(e -> gameTimer.stop());
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
                });
            }
        });
    }

    /**
<<<<<<< HEAD
     * Populates {@link #sudokuGrid} with six 2×3 block sub-grids.
     */
    private void buildGrid() {
        sudokuGrid.setHgap(6);
        sudokuGrid.setVgap(6);

        int blockRowCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_ROWS;
        int blockColCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_COLS;

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {
                sudokuGrid.add(buildBlockPane(br, bc), bc, br);
            }
        }
    }

    /**
     * Creates a single styled 2×3 block {@link GridPane} for the given
     * block coordinates.
     *
     * @param blockRow block row index (0–2)
     * @param blockCol block column index (0–1)
     * @return the configured block pane containing its cell TextFields
     */
    private GridPane buildBlockPane(int blockRow, int blockCol) {
        GridPane block = new GridPane();
        block.getStyleClass().add("block-pane");
        block.setHgap(3);
        block.setVgap(3);

        for (int r = 0; r < ISudokuModel.BLOCK_ROWS; r++) {
            for (int c = 0; c < ISudokuModel.BLOCK_COLS; c++) {
                int globalRow = blockRow * ISudokuModel.BLOCK_ROWS + r;
                int globalCol = blockCol * ISudokuModel.BLOCK_COLS + c;
                TextField cell = buildCell(globalRow, globalCol);
                cells[globalRow][globalCol] = cell;
                block.add(cell, c, r);
            }
        }
        return block;
    }

    /**
     * Creates and configures a single board {@link TextField} at the given
     * global position, wiring {@link CellInputHandler} for all events.
     *
     * @param row global row index (0–5)
     * @param col global column index (0–5)
     * @return the configured cell
     */
    private TextField buildCell(int row, int col) {
        TextField tf = new TextField();
        tf.setPrefSize(CELL_SIZE, CELL_SIZE);
        tf.setMinSize(CELL_SIZE, CELL_SIZE);
        tf.setMaxSize(CELL_SIZE, CELL_SIZE);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false);  // input handled exclusively via key events

        CellInputHandler handler = new CellInputHandler();
        tf.setOnKeyPressed(e -> handler.onCellKeyPressed(e, row, col));
        tf.setOnMouseClicked(e -> handler.onCellClicked(e, row, col));
        tf.setOnMouseEntered(e -> handler.onCellMouseEntered(e, row, col));
        tf.setOnMouseExited(e -> handler.onCellMouseExited(e, row, col));

        return tf;
    }

    // -------------------------------------------------------------------------
    // Board rendering
    // -------------------------------------------------------------------------

    /**
     * Refreshes all cells without applying any transient hint highlight.
     * Hint cells that were permanently recorded in {@link #hintCells} retain
     * their green {@code cell-hint} style.
     */
    private void refreshAllCells() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                refreshCell(r, c, false);
=======
     * Attaches a {@link CellInputHandler} instance to each cell returned by
     * {@link BoardBuilder}. Called once after the grid is built.
     */
    private void attachHandlers() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                final int row = r, col = c;
                CellInputHandler handler = new CellInputHandler();
                cells[r][c].setOnKeyPressed(e  -> handler.onCellKeyPressed(e, row, col));
                cells[r][c].setOnMouseClicked(e -> handler.onCellClicked(e, row, col));
                cells[r][c].setOnMouseEntered(e -> handler.onCellMouseEntered(e, row, col));
                cells[r][c].setOnMouseExited(e  -> handler.onCellMouseExited(e, row, col));
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
            }
        }
    }

<<<<<<< HEAD
    /**
     * Updates the text and CSS class of a single cell to reflect the current
     * model state.
     *
     * <p>Style-class priority (highest to lowest):
     * {@code cell-hint} → {@code cell-fixed} → {@code cell-conflict}
     * → {@code cell-selected} → {@code cell-editable}</p>
     *
     * <p>A cell is styled as {@code cell-hint} when {@code isHint} is
     * {@code true} <em>or</em> when it is permanently recorded in
     * {@link #hintCells}, so the green highlight survives subsequent
     * {@link #refreshAllCells()} calls.</p>
     *
     * @param row    row index
     * @param col    column index
     * @param isHint {@code true} forces the {@code cell-hint} style on top of
     *               everything else (e.g. immediately after a hint is placed)
     */
    private void refreshCell(int row, int col, boolean isHint) {
        TextField cell = cells[row][col];
        int val = model.getValue(row, col);
        cell.setText(val == 0 ? "" : String.valueOf(val));

        // Remove all known style classes before re-applying exactly one
        cell.getStyleClass().removeAll(
                "cell-fixed", "cell-editable",
                "cell-conflict", "cell-selected", "cell-hint"
        );

        if (isHint || hintCells[row][col]) {
            cell.getStyleClass().add("cell-hint");
        } else if (model.isFixed(row, col)) {
            cell.getStyleClass().add("cell-fixed");
        } else if (val != 0 && model.hasConflict(row, col)) {
            cell.getStyleClass().add("cell-conflict");
        } else if (row == selectedRow && col == selectedCol) {
            cell.getStyleClass().add("cell-selected");
        } else {
            cell.getStyleClass().add("cell-editable");
        }
    }

    // -------------------------------------------------------------------------
    // Hint tracking helpers
    // -------------------------------------------------------------------------

    /**
     * Resets all hint-cell flags to {@code false}.
     * Called on new game and restart so the hint highlight does not persist
     * across puzzles.
     */
    private void clearHintCells() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                hintCells[r][c] = false;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Selection
    // -------------------------------------------------------------------------

    /**
     * Marks the given cell as selected, refreshes the board, and moves focus.
     *
     * @param row row index of the cell to select
     * @param col column index of the cell to select
     */
    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        refreshAllCells();
        cells[row][col].requestFocus();
    }

    // -------------------------------------------------------------------------
    // IGameController — FXML actions
    // -------------------------------------------------------------------------
=======
    // ── IGameController ───────────────────────────────────────────────────────
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986

    /**
     * {@inheritDoc}
     * Wired to the "Nueva Partida" button.
     */
    @FXML @Override
    public void handleNewGame() {
        model.generateNewPuzzle();
<<<<<<< HEAD
        selectedRow = -1;
        selectedCol = -1;
        clearHintCells();
        clearStatus();
        refreshAllCells();
=======
        selectedRow = selectedCol = -1;
        clearStatus();
        renderer.refreshAll(selectedRow, selectedCol);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        gameTimer.restart();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Reiniciar" button.
     */
    @FXML @Override
    public void handleRestart() {
        model.resetPuzzle();
<<<<<<< HEAD
        selectedRow = -1;
        selectedCol = -1;
        clearHintCells();
        clearStatus();
        refreshAllCells();
=======
        selectedRow = selectedCol = -1;
        clearStatus();
        renderer.refreshAll(selectedRow, selectedCol);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        gameTimer.restart();
    }

    /**
     * {@inheritDoc}
<<<<<<< HEAD
     * Wired to the "Ayuda" button in the FXML.
     *
     * <p>Writes the hint value to the model, records the cell in
     * {@link #hintCells} so its green style persists across subsequent
     * refreshes, and shows a descriptive message
     * (HU-4: hint entered automatically AND shown in message).</p>
=======
     * Wired to the "Ayuda" button. Prefers the selected cell if it is empty;
     * otherwise picks a random empty cell.
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    @FXML @Override
    public void handleHelp() {
<<<<<<< HEAD
        // Prefer the selected cell if it is empty and editable
        int[] target;
=======
        int[] target = null;
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        if (selectedRow >= 0 && selectedCol >= 0
                && !model.isFixed(selectedRow, selectedCol)
                && model.getValue(selectedRow, selectedCol) == 0) {
            target = new int[]{selectedRow, selectedCol};
        } else {
            target = model.getRandomEmptyCell();
        }

        if (target == null) {
            showStatus("El tablero ya está completo.", false);
            return;
        }

        int r = target[0], c = target[1];
        int hint = model.getHintForCell(r, c);

<<<<<<< HEAD
        //model.setValue(r, c, hint);
        selectCell(r,c);
        hintCells[r][c] = true;   // persist the green highlight across refreshes

        refreshAllCells();
        selectCell(r,c);
        showStatus("AYUDA: Coloca el " + hint
                + " en fila " + (r + 1) + ", columna " + (c + 1) + ".", true);
=======
        renderer.refreshAll(selectedRow, selectedCol);
        renderer.refresh(r, c, true, selectedRow, selectedCol);
        showStatus("SUGERENCIA: Pon el número " + hint
                + " en la fila " + (r + 1) + ", columna: " + (c + 1), true);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986

        if (model.isSolved()) showWinMessage();
    }

    /**
     * {@inheritDoc}
<<<<<<< HEAD
     * Wired to the "↩ Deshacer" button in the FXML.
     *
     * <p>If the undone move was a hint-placed cell, its hint flag is cleared
     * so the green highlight is correctly removed.</p>
=======
     * Wired to the "Deshacer" button.
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    @FXML @Override
    public void handleUndo() {
<<<<<<< HEAD
        int[] undone = model.undoMove();
        if (undone != null) {
            hintCells[undone[0]][undone[1]] = false;
        }
        refreshAllCells();
        clearStatus();
    }

    // -------------------------------------------------------------------------
    // Status helpers
    // -------------------------------------------------------------------------

    /**
     * Clears any status message and removes all status style classes.
     */
=======
        model.undoMove();
        renderer.refreshAll(selectedRow, selectedCol);
        clearStatus();
    }

    // ── Selection ─────────────────────────────────────────────────────────────

    /**
     * Marks the given cell as selected, refreshes the board, and moves focus.
     *
     * @param row row index to select
     * @param col column index to select
     */
    private void selectCell(int row, int col) {
        selectedRow = row;
        selectedCol = col;
        renderer.refreshAll(selectedRow, selectedCol);
        cells[row][col].requestFocus();
    }

    // ── Status helpers ────────────────────────────────────────────────────────

>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    private void clearStatus() {
        statusLabel.getStyleClass().removeAll(STATUS_SUCCESS_CLASS, STATUS_ERROR_CLASS);
        statusLabel.setText("");
    }

    /**
     * Shows a status message with appropriate visual styling.
     *
     * @param message text to display
     * @param success {@code true} → green success style;
     *                {@code false} → red error style
     */
    private void showStatus(String message, boolean success) {
<<<<<<< HEAD
        statusLabel.getStyleClass().removeAll(STATUS_SUCCESS_CLASS, STATUS_ERROR_CLASS);
        statusLabel.getStyleClass().add(success ? STATUS_SUCCESS_CLASS : STATUS_ERROR_CLASS);
        statusLabel.setText(message);
    }

    // -------------------------------------------------------------------------
    // Win message
    // -------------------------------------------------------------------------

    /**
     * Stops the timer and shows a congratulatory message with the elapsed time.
     */
    private void showWinMessage() {
        gameTimer.stop();
        showStatus("¡FELICIDADES! Completaste el Sudoku en "
=======
        statusLabel.getStyleClass().remove(STATUS_SUCCESS_CLASS);
        if (success) statusLabel.getStyleClass().add(STATUS_SUCCESS_CLASS);
        statusLabel.setText(message);
    }

    private void showWinMessage() {
        gameTimer.stop();
        showStatus("¡FELICIDADES! Completaste el sudoku en "
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
                + gameTimer.getFormattedTime() + ".", true);
    }

    // =========================================================================
    // Named inner class — CellInputHandler
    // =========================================================================

    /**
<<<<<<< HEAD
     * Handles keyboard and mouse events for a single board cell.
     *
     * <p>This <em>named inner class</em> extends {@link CellEventAdapter} and
     * overrides only the required callbacks, following the Adapter design
     * pattern.  Each cell receives its own {@code CellInputHandler} instance
     * whose coordinates are captured via the event-binding lambdas in
     * {@link #buildCell(int, int)}.</p>
=======
     * Routes keyboard and mouse events for a single board cell.
     *
     * <p>Follows the <em>Adapter design pattern</em> by extending
     * {@link CellEventAdapter} and overriding only the required callbacks,
     * avoiding empty method bodies throughout the controller.</p>
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     *
     * @author Juan Rosero, Natalia Parra
     * @version 1.0
     */
    private class CellInputHandler extends CellEventAdapter {

        /**
         * Handles key-press events on the cell:
         * <ul>
<<<<<<< HEAD
         *   <li>Digits 1–6 → write to model; show success or conflict feedback.</li>
         *   <li>BACKSPACE / DELETE → clear the cell.</li>
         *   <li>Ctrl+Z → trigger undo.</li>
         *   <li>All other keys → silently consumed (error prevention heuristic).</li>
         * </ul>
         *
         * @param event key event
         * @param row   cell row
         * @param col   cell column
=======
         *   <li>1–6 → validates against solution; correct values are kept
         *       with a green highlight. Wrong values show a red border
         *       <strong>without</strong> being written to the model — this
         *       avoids the previous bug where model.setValue + model.undoMove
         *       left the model and the UI in an inconsistent state (the cell
         *       displayed the rejected digit even though the model had rolled
         *       it back, because JavaFX renders only after the handler returns).
         *       </li>
         *   <li>BACKSPACE / DELETE → clear the cell.</li>
         *   <li>Ctrl+Z → undo last move.</li>
         *   <li>All other keys → silently consumed.</li>
         * </ul>
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
         */
        @Override
        public void onCellKeyPressed(KeyEvent event, int row, int col) {
            if (model.isFixed(row, col)) { event.consume(); return; }

            String text = event.getText();

            if (text != null && text.matches("[1-6]")) {
                int value = Integer.parseInt(text);

<<<<<<< HEAD
                // Clear hint flag if the player overwrites a hint cell manually
                hintCells[row][col] = false;
                model.setValue(row, col, value);

                if (model.isCorrectValue(row, col, value)) {
                    refreshAllCells();
                    showStatus("¡Número correcto!", true);
                    if (model.isSolved()) showWinMessage();
                } else {
                    refreshAllCells();
                    showStatus("Ese número viola las reglas del Sudoku.", false);
=======
                if (model.isCorrectValue(row, col, value)) {
                    // ── Correct digit ─────────────────────────────────────────
                    model.setValue(row, col, value);
                    renderer.refreshAll(selectedRow, selectedCol);
                    cells[row][col].getStyleClass().add("cell-hint");
                    showStatus("¡NÚMERO CORRECTO!", true);
                    if (model.isSolved()) showWinMessage();
                } else {
                    // ── Wrong digit (BUG FIX) ──────────────────────────────────
                    // Only apply visual feedback; do NOT touch the model or the
                    // undo history. The old code called model.setValue followed
                    // by model.undoMove in the same JavaFX event, which caused
                    // the cell to display the wrong number while the model already
                    // held the original value — a model / view state mismatch.
                    cells[row][col].getStyleClass().removeAll(
                            "cell-fixed", "cell-editable", "cell-selected", "cell-hint");
                    if (!cells[row][col].getStyleClass().contains("cell-conflict")) {
                        cells[row][col].getStyleClass().add("cell-conflict");
                    }
                    showStatus("El número ingresado es incorrecto en el sudoku.", false);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
                }

            } else if (event.getCode() == KeyCode.BACK_SPACE
                    || event.getCode() == KeyCode.DELETE) {
                hintCells[row][col] = false;
                model.setValue(row, col, 0);
                renderer.refreshAll(selectedRow, selectedCol);
                clearStatus();

            } else if (event.getCode() == KeyCode.Z && event.isControlDown()) {
                handleUndo();
            }
            event.consume();
        }

<<<<<<< HEAD
        /**
         * Selects the clicked cell (heuristic: recognition over recall —
         * makes the active cell obvious).
         *
         * @param event mouse event
         * @param row   cell row
         * @param col   cell column
         */
=======
        /** Selects the clicked cell. */
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        @Override
        public void onCellClicked(MouseEvent event, int row, int col) {
            selectCell(row, col);
        }

<<<<<<< HEAD
        /**
         * Restores the cell's computed style when the mouse exits.
         * Because {@link #refreshCell} now respects {@link #hintCells},
         * hint-placed cells keep their green border after the mouse leaves.
         *
         * @param event mouse event
         * @param row   cell row
         * @param col   cell column
         */
=======
        /** Restores normal cell style on mouse exit. */
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        @Override
        public void onCellMouseExited(MouseEvent event, int row, int col) {
            renderer.refresh(row, col, false, selectedRow, selectedCol);
        }
    }

}