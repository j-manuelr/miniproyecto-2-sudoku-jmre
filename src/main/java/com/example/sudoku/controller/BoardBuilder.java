package com.example.sudoku.controller;

import com.example.sudoku.model.ISudokuModel;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * Constructs the 6×6 board grid inside the FXML-injected {@link GridPane}.
 *
 * <p>Single responsibility: build the JavaFX node structure (block sub-grids
 * and individual cell {@link TextField} nodes) and return the populated cells
 * matrix. It never attaches event handlers, reads model state, or manages
 * game logic — all of those concerns belong to {@link GameController}.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class BoardBuilder {

    /** Width and height in pixels for each individual cell. */
    private static final int CELL_SIZE = 63;

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Populates {@code sudokuGrid} with six 2×3 block sub-grids, each
     * containing its own inner {@link GridPane} of cells.
     *
     * @param sudokuGrid the outer {@link GridPane} injected from FXML
     * @return the fully constructed {@code TextField[BOARD_SIZE][BOARD_SIZE]}
     *         cell matrix, ready for event-handler attachment
     */
    public TextField[][] build(GridPane sudokuGrid) {
        TextField[][] cells = new TextField[ISudokuModel.BOARD_SIZE][ISudokuModel.BOARD_SIZE];

        sudokuGrid.setHgap(6);
        sudokuGrid.setVgap(6);

        int blockRowCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_ROWS;  // 3
        int blockColCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_COLS;  // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {
                GridPane blockPane = buildBlock(br, bc, cells);
                sudokuGrid.add(blockPane, bc, br);
            }
        }
        return cells;
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Creates a styled 2×3 block sub-grid and populates the corresponding
     * positions in the {@code cells} matrix.
     *
     * @param blockRow global block row index (0–2)
     * @param blockCol global block column index (0–1)
     * @param cells    the matrix to fill with new cell nodes
     * @return the configured block {@link GridPane}
     */
    private GridPane buildBlock(int blockRow, int blockCol, TextField[][] cells) {
        GridPane block = new GridPane();
        block.getStyleClass().add("block-pane");
        block.setHgap(3);
        block.setVgap(3);

        for (int r = 0; r < ISudokuModel.BLOCK_ROWS; r++) {
            for (int c = 0; c < ISudokuModel.BLOCK_COLS; c++) {
                int globalRow = blockRow * ISudokuModel.BLOCK_ROWS + r;
                int globalCol = blockCol * ISudokuModel.BLOCK_COLS + c;
                TextField cell = buildCell();
                cells[globalRow][globalCol] = cell;
                block.add(cell, c, r);
            }
        }
        return block;
    }

    /**
     * Creates a single, unstyled board cell {@link TextField}.
     * The initial style class and event handlers are applied later by
     * {@link GameController} and {@link CellRenderer} respectively.
     *
     * @return a new, configured cell node
     */
    private TextField buildCell() {
        TextField tf = new TextField();
        tf.setPrefSize(CELL_SIZE, CELL_SIZE);
        tf.setMinSize(CELL_SIZE, CELL_SIZE);
        tf.setMaxSize(CELL_SIZE, CELL_SIZE);
        tf.setAlignment(Pos.CENTER);
        tf.setEditable(false);   // all input is routed through key-event handlers
        tf.getStyleClass().add("cell-editable");
        return tf;
    }
}
