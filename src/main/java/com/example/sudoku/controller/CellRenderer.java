package com.example.sudoku.controller;

import com.example.sudoku.model.ISudokuModel;
import javafx.scene.control.TextField;

/**
 * Updates the visual state (text content and CSS style classes) of every
 * board cell to reflect the current game model.
 *
 * <p>Single responsibility: translate model data into JavaFX style classes.
 * It reads from the model and writes to the cell nodes; it never handles
 * input events, manages game logic, or controls the timer.</p>
 *
 * <h2>Style-class priority (highest → lowest)</h2>
 * <ol>
 *   <li>{@code cell-hint}     — cell filled by the help button</li>
 *   <li>{@code cell-fixed}    — immutable puzzle clue</li>
 *   <li>{@code cell-conflict} — value violates a Sudoku rule</li>
 *   <li>{@code cell-selected} — currently focused / clicked cell</li>
 *   <li>{@code cell-editable} — normal empty or user-filled cell</li>
 * </ol>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class CellRenderer {

    /** Reference to the 6×6 matrix of cell {@link TextField} nodes. */
    private final TextField[][] cells;

    /** Read-only reference to the game model. */
    private final ISudokuModel model;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a renderer for the given cells and model.
     *
     * @param cells the 6×6 matrix of board {@link TextField} nodes
     * @param model the game model used to read cell values and state
     */
    public CellRenderer(TextField[][] cells, ISudokuModel model) {
        this.cells = cells;
        this.model = model;
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Refreshes every cell on the board using the current model state.
     *
     * @param selectedRow currently selected row index (−1 if no cell is selected)
     * @param selectedCol currently selected column index (−1 if no cell is selected)
     */
    public void refreshAll(int selectedRow, int selectedCol) {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                refresh(r, c, false, selectedRow, selectedCol);
            }
        }
    }

    /**
     * Refreshes a single cell, optionally applying the hint style.
     *
     * @param row         row index of the cell to refresh
     * @param col         column index of the cell to refresh
     * @param isHint      {@code true} to force the {@code cell-hint} style
     * @param selectedRow currently selected row (for {@code cell-selected} style)
     * @param selectedCol currently selected column (for {@code cell-selected} style)
     */
    public void refresh(int row, int col, boolean isHint,
                        int selectedRow, int selectedCol) {
        TextField cell = cells[row][col];
        int val = model.getValue(row, col);

        // ── Text content ────────────────────────────────────────────────────
        cell.setText(val == 0 ? "" : String.valueOf(val));

        // ── CSS style class ─────────────────────────────────────────────────
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
}
