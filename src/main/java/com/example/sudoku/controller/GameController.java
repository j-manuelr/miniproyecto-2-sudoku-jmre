package com.example.sudoku.controller;

import com.example.sudoku.events.CellEventAdapter;
import com.example.sudoku.model.ISudokuModel;
import com.example.sudoku.model.SudokuModel;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * JavaFX controller for the Sudoku game view ({@code game-view.fxml}).
 *
 * <p>Responsibilities:
 * <ul>
 *   <li>Builds the 6×6 cell grid programmatically inside the FXML-injected
 *       {@link GridPane}, using 2×3 block sub-grids.</li>
 *   <li>Delegates input events to the named inner class
 *       {@link CellInputHandler}, which extends {@link CellEventAdapter}.</li>
 *   <li>Keeps the visual state (CSS classes) in sync with the model after
 *       every user action.</li>
 *   <li>Manages a live game timer via a {@link Timeline}.</li>
 * </ul>
 *
 * <p><b>UX/UI heuristics applied:</b>
 * <ol>
 *   <li><em>Visibility of system status</em> — real-time conflict highlights
 *       (red border) and a live timer.</li>
 *   <li><em>Match between system and real world</em> — Spanish labels and
 *       familiar Sudoku vocabulary.</li>
 *   <li><em>User control and freedom</em> — Undo and Restart actions.</li>
 *   <li><em>Error prevention</em> — only digits 1–6 accepted; out-of-range
 *       keys are silently consumed.</li>
 *   <li><em>Recognition over recall</em> — fixed clues visually distinct
 *       (cyan colour) from user entries.</li>
 * </ol>
 *
 * @author Juan Rosero
 * @version 1.0
 */
public class GameController implements Initializable, IGameController {

    private static final int CELL_SIZE = 63;
    private static final String STATUS_SUCCESS_CLASS = "status-success";

    // -------------------------------------------------------------------------
    // FXML-injected nodes
    // -------------------------------------------------------------------------

    /** Outer GridPane (3 block-rows × 2 block-cols) injected from FXML. */
    @FXML private GridPane sudokuGrid;

    /** Status/feedback label below the board. */
    @FXML private Label statusLabel;

    /** Live game-timer label in the header area. */
    @FXML private Label timerLabel;

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    /** Reference to the game model. */
    private ISudokuModel model;

    /** 6×6 matrix of the TextField cells composing the board. */
    private TextField[][] cells;

    /** Row index of the currently selected cell (−1 = none selected). */
    private int selectedRow = -1;

    /** Column index of the currently selected cell (−1 = none selected). */
    private int selectedCol = -1;

    /** JavaFX animation timeline used as the game timer. */
    private Timeline timer;

    /** Elapsed seconds since the last (re)start. */
    private int secondsElapsed;

    // -------------------------------------------------------------------------
    // Initializable
    // -------------------------------------------------------------------------

    /**
     * Invoked automatically by the {@link javafx.fxml.FXMLLoader} after all
     * {@code @FXML} fields have been injected.
     * Creates the model, builds the board grid, loads the first puzzle, and
     * starts the timer.
     *
     * @param url            unused
     * @param resourceBundle unused
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cells = new TextField[ISudokuModel.BOARD_SIZE][ISudokuModel.BOARD_SIZE];
        model = new SudokuModel();
        buildGrid();
        loadBoard();
        clearStatus();
        startTimer();

        // Prevent running timeline leaks when the window is closed.
        timerLabel.sceneProperty().addListener((obsScene, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obsWindow, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        newWindow.setOnHidden(event -> stopTimer());
                    }
                });
            }
        });
    }

    // -------------------------------------------------------------------------
    // Grid construction
    // -------------------------------------------------------------------------

    /**
     * Populates {@link #sudokuGrid} with six 2×3 block sub-grids.
     * Each block is a styled {@link GridPane} containing the individual
     * {@link TextField} cells.
     */
    private void buildGrid() {
        sudokuGrid.setHgap(6);
        sudokuGrid.setVgap(6);

        int blockRowCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_ROWS; // 3
        int blockColCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_COLS; // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {
                GridPane blockPane = buildBlockPane(br, bc);
                sudokuGrid.add(blockPane, bc, br);
            }
        }
    }

    /**
     * Creates a single 2×3 block sub-grid for the given block coordinates.
     *
     * @param blockRow block row index (0–2)
     * @param blockCol block column index (0–1)
     * @return a styled {@link GridPane} containing the cells for this block
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
     * global board position.
     * Registers {@link CellInputHandler} (inner class) for key and mouse events.
     *
     * @param row global row index (0–5)
     * @param col global column index (0–5)
     * @return the fully configured cell
     */
    private TextField buildCell(int row, int col) {
        TextField tf = new TextField();
        tf.setPrefSize(CELL_SIZE, CELL_SIZE);
        tf.setMinSize(CELL_SIZE, CELL_SIZE);
        tf.setMaxSize(CELL_SIZE, CELL_SIZE);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false);   // input is handled exclusively via key events

        // Attach the named inner-class handler for this cell
        CellInputHandler handler = new CellInputHandler();
        tf.setOnKeyPressed(e  -> handler.onCellKeyPressed(e, row, col));
        tf.setOnMouseClicked(e -> handler.onCellClicked(e, row, col));
        tf.setOnMouseEntered(e -> handler.onCellMouseEntered(e, row, col));
        tf.setOnMouseExited(e  -> handler.onCellMouseExited(e, row, col));

        return tf;
    }

    // -------------------------------------------------------------------------
    // Board rendering
    // -------------------------------------------------------------------------

    /**
     * Reads the model and updates every cell's text and CSS style class.
     */
    private void loadBoard() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                refreshCell(r, c, false);
            }
        }
    }

    /**
     * Refreshes all cells, optionally preserving the hint highlight on one cell.
     */
    private void refreshAllCells() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                refreshCell(r, c, false);
            }
        }
    }

    /**
     * Updates the text content and CSS style class of a single cell to reflect
     * the current model state.
     *
     * <p>Style-class priority (highest to lowest):
     * <ol>
     *   <li>{@code cell-hint}    — hint-filled, no conflict</li>
     *   <li>{@code cell-fixed}   — immutable puzzle clue</li>
     *   <li>{@code cell-conflict}— user value violates a rule</li>
     *   <li>{@code cell-selected}— currently focused cell</li>
     *   <li>{@code cell-editable}— normal editable cell</li>
     * </ol>
     *
     * @param row      row index
     * @param col      column index
     * @param isHint   {@code true} keeps the {@code cell-hint} style
     */
    private void refreshCell(int row, int col, boolean isHint) {
        TextField cell = cells[row][col];
        int val = model.getValue(row, col);
        cell.setText(val == 0 ? "" : String.valueOf(val));

        cell.getStyleClass().removeAll(
                "cell-fixed", "cell-editable",
                "cell-conflict", "cell-selected", "cell-hint"
        );

        if (isHint) {
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
    // Selection
    // -------------------------------------------------------------------------

    /**
     * Marks the given cell as selected, refreshes the board visuals, and
     * moves keyboard focus to that cell.
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

    /**
     * {@inheritDoc}
     * Wired to the "Nueva Partida" button in the FXML.
     */
    @FXML
    @Override
    public void handleNewGame() {
        model.generateNewPuzzle();
        selectedRow = -1;
        selectedCol = -1;
        clearStatus();
        loadBoard();
        restartTimer();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Reiniciar" button in the FXML.
     */
    @FXML
    @Override
    public void handleRestart() {
        model.resetPuzzle();
        selectedRow = -1;
        selectedCol = -1;
        clearStatus();
        loadBoard();
        restartTimer();
    }

    /**
     * {@inheritDoc}
     * Wired to the "Ayuda" button in the FXML.
     * Fills one empty cell automatically with the correct value and
     * highlights it with the {@code cell-hint} style class.
     */
    @FXML
    @Override
    public void handleHelp() {
        // Prefer the currently selected cell if it is empty
        int[] target;
        if (selectedRow >= 0 && selectedCol >= 0
                && !model.isFixed(selectedRow, selectedCol)
                && model.getValue(selectedRow, selectedCol) == 0) {
            target = new int[]{selectedRow, selectedCol};
        } else {
            target = model.getRandomEmptyCell();
        }

        if (target == null) {
            showStatus("El tablero ya esta completo.", false);
            return;
        }

        int r = target[0];
        int c = target[1];
        int hint = model.getHintForCell(r, c);
        model.setValue(r, c, hint);

        // Refresh all cells first, then apply the hint highlight separately
        refreshAllCells();
        refreshCell(r, c, true);

        showStatus("Sugerencia: " + hint + " -> fila " + (r + 1) + ", columna " + (c + 1), true);

        if (model.isSolved()) showWinMessage();
    }

    /**
     * {@inheritDoc}
     * Wired to the "↩ Deshacer" button in the FXML.
     */
    @FXML
    @Override
    public void handleUndo() {
        model.undoMove();
        refreshAllCells();
        clearStatus();
    }

    // -------------------------------------------------------------------------
    // Timer
    // -------------------------------------------------------------------------

    /**
     * Creates and starts a one-second interval {@link Timeline} that
     * increments {@link #secondsElapsed} and updates {@link #timerLabel}.
     */
    private void startTimer() {
        secondsElapsed = 0;
        timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed++;
            int m = secondsElapsed / 60;
            int s = secondsElapsed % 60;
            timerLabel.setText(String.format("%02d:%02d", m, s));
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    /**
     * Stops the running timer, resets the display to {@code 00:00}, and
     * starts a fresh timeline.
     */
    private void restartTimer() {
        stopTimer();
        timerLabel.setText("00:00");
        startTimer();
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void clearStatus() {
        statusLabel.getStyleClass().remove(STATUS_SUCCESS_CLASS);
        statusLabel.setText("");
    }

    private void showStatus(String message, boolean success) {
        statusLabel.getStyleClass().remove(STATUS_SUCCESS_CLASS);
        if (success) {
            statusLabel.getStyleClass().add(STATUS_SUCCESS_CLASS);
        }
        statusLabel.setText(message);
    }

    // -------------------------------------------------------------------------
    // Win message
    // -------------------------------------------------------------------------

    /**
     * Stops the timer and shows a congratulatory message in the status label
     * with the elapsed time.
     */
    private void showWinMessage() {
        stopTimer();
        int m = secondsElapsed / 60;
        int s = secondsElapsed % 60;
        showStatus("Felicitaciones. Completado en " + String.format("%02d:%02d", m, s) + ".", true);
    }

    // =========================================================================
    // Named inner class — CellInputHandler
    // =========================================================================

    /**
     * Handles keyboard and mouse events for a specific board cell.
     *
     * <p>This <em>named inner class</em> extends {@link CellEventAdapter} and
     * overrides only the required callback methods, following the Adapter
     * design pattern. Each cell in the board gets its own {@code CellInputHandler}
     * instance and receives coordinates from the event binding.</p>
     *
     * @author Juan Rosero
     * @version 1.0
     */
    private class CellInputHandler extends CellEventAdapter {

        /**
         * Handles key-press events:
         * <ul>
         *   <li>Digits 1–6 → write value to model and validate board.</li>
         *   <li>BACKSPACE / DELETE → clear the cell.</li>
         *   <li>Ctrl+Z → trigger undo.</li>
         *   <li>All other keys → silently consumed (no default text insert).</li>
         * </ul>
         *
         * @param event the originating {@link KeyEvent}
         * @param row   the cell's row
         * @param col   the cell's column
         */
        @Override
        public void onCellKeyPressed(KeyEvent event, int row, int col) {
            if (model.isFixed(row, col)) {
                event.consume();
                return;
            }

            String text = event.getText();

            if (text != null && text.matches("[1-6]")) {
                int value = Integer.parseInt(text);
                model.setValue(row, col, value);
                refreshAllCells();
                clearStatus();
                if (model.isSolved()) showWinMessage();

            } else if (event.getCode() == KeyCode.BACK_SPACE
                    || event.getCode() == KeyCode.DELETE) {
                model.setValue(row, col, 0);
                refreshAllCells();
                clearStatus();

            } else if (event.getCode() == KeyCode.Z && event.isControlDown()) {
                handleUndo();
            }

            event.consume();
        }

        /**
         * Handles mouse-click events by selecting the clicked cell.
         *
         * @param event the originating {@link MouseEvent}
         * @param row   the cell's row index
         * @param col   the cell's column index
         */
        @Override
        public void onCellClicked(MouseEvent event, int row, int col) {
            selectCell(row, col);
        }

        /**
         * Applies a subtle hover style to non-fixed, non-conflict cells when
         * the mouse enters, providing visual feedback (heuristic: affordance).
         *
         * @param event the originating {@link MouseEvent}
         * @param row   the cell's row index
         * @param col   the cell's column index
         */
        @Override
        public void onCellMouseEntered(MouseEvent event, int row, int col) {
            TextField cell = cells[row][col];
            if (!model.isFixed(row, col)
                    && !cell.getStyleClass().contains("cell-conflict")
                    && !cell.getStyleClass().contains("cell-hint")) {
                cell.setStyle("-fx-background-color: #181840;");
            }
        }

        /**
         * Removes the hover style when the mouse exits the cell.
         *
         * @param event the originating {@link MouseEvent}
         * @param row   the cell's row index
         * @param col   the cell's column index
         */
        @Override
        public void onCellMouseExited(MouseEvent event, int row, int col) {
            refreshCell(row, col, false);
        }
    }
}
