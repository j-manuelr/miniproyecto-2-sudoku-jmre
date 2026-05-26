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

    // ── Collaborators ─────────────────────────────────────────────────────────
    private ISudokuModel model;
    private TextField[][]  cells;
    private CellRenderer   renderer;
    private GameTimer      gameTimer;

    // ── Selection state ───────────────────────────────────────────────────────
    private int selectedRow = -1;
    private int selectedCol = -1;

    // ── Initializable ─────────────────────────────────────────────────────────

    /**
     * Invoked by {@link javafx.fxml.FXMLLoader} after all {@code @FXML} fields
     * are injected. Creates collaborators, builds the grid, and starts the timer.
     *
     * @param url            unused
     * @param resourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
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
                });
            }
        });
    }

    /**
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
            }
        }
    }

    // ── IGameController ───────────────────────────────────────────────────────

    /**
     * {@inheritDoc}
     * Wired to the "Nueva Partida" button.
     */
    @FXML @Override
    public void handleNewGame() {
        model.generateNewPuzzle();
        selectedRow = selectedCol = -1;
        clearStatus();
        renderer.refreshAll(selectedRow, selectedCol);
        gameTimer.restart();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Reiniciar" button.
     */
    @FXML @Override
    public void handleRestart() {
        model.resetPuzzle();
        selectedRow = selectedCol = -1;
        clearStatus();
        renderer.refreshAll(selectedRow, selectedCol);
        gameTimer.restart();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Ayuda" button. Prefers the selected cell if it is empty;
     * otherwise picks a random empty cell.
     */
    @FXML @Override
    public void handleHelp() {
        int[] target = null;
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
        model.setValue(r, c, hint);

        renderer.refreshAll(selectedRow, selectedCol);
        renderer.refresh(r, c, true, selectedRow, selectedCol);
        showStatus("SUGERENCIA: Pon el número " + hint
                + " en la fila " + (r + 1) + ", columna: " + (c + 1), true);

        if (model.isSolved()) showWinMessage();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Deshacer" button.
     */
    @FXML @Override
    public void handleUndo() {
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

    private void clearStatus() {
        statusLabel.getStyleClass().remove(STATUS_SUCCESS_CLASS);
        statusLabel.setText("");
    }

    private void showStatus(String message, boolean success) {
        statusLabel.getStyleClass().remove(STATUS_SUCCESS_CLASS);
        if (success) statusLabel.getStyleClass().add(STATUS_SUCCESS_CLASS);
        statusLabel.setText(message);
    }

    private void showWinMessage() {
        gameTimer.stop();
        showStatus("¡FELICIDADES! Completaste el sudoku en "
                + gameTimer.getFormattedTime() + ".", true);
    }

    // =========================================================================
    // Named inner class — CellInputHandler
    // =========================================================================

    /**
     * Routes keyboard and mouse events for a single board cell.
     *
     * <p>Follows the <em>Adapter design pattern</em> by extending
     * {@link CellEventAdapter} and overriding only the required callbacks,
     * avoiding empty method bodies throughout the controller.</p>
     *
     * @author Juan Rosero, Natalia Parra
     * @version 1.0
     */
    private class CellInputHandler extends CellEventAdapter {

        /**
         * Handles key-press events:
         * <ul>
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
         */
        @Override
        public void onCellKeyPressed(KeyEvent event, int row, int col) {
            if (model.isFixed(row, col)) { event.consume(); return; }

            String text = event.getText();

            if (text != null && text.matches("[1-6]")) {
                int value = Integer.parseInt(text);

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
                }

            } else if (event.getCode() == KeyCode.BACK_SPACE
                    || event.getCode() == KeyCode.DELETE) {
                model.setValue(row, col, 0);
                renderer.refreshAll(selectedRow, selectedCol);
                clearStatus();

            } else if (event.getCode() == KeyCode.Z && event.isControlDown()) {
                handleUndo();
            }
            event.consume();
        }

        /** Selects the clicked cell. */
        @Override
        public void onCellClicked(MouseEvent event, int row, int col) {
            selectCell(row, col);
        }

        /** Restores normal cell style on mouse exit. */
        @Override
        public void onCellMouseExited(MouseEvent event, int row, int col) {
            renderer.refresh(row, col, false, selectedRow, selectedCol);
        }
    }
}
