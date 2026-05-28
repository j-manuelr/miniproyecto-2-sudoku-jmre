package com.example.sudoku.model;
/**
 * Validates Sudoku board state against the game rules.
 *
 * <p>Single responsibility: answer three types of read-only queries
 * about the board — conflict detection, completion checking, and
 * solution correctness — without ever modifying any tree.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuValidator {

    /**
     * The working board tree (read-only access).
     */
    private final SudokuBoardTree boardTree;

    /**
     * The complete solution tree (read-only access).
     */
    private final SudokuBoardTree solutionTree;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a validator that reads from the given trees.
     *
     * @param boardTree    the current working board
     * @param solutionTree the pre-generated complete solution
     */
    public SudokuValidator(SudokuBoardTree boardTree,
                           SudokuBoardTree solutionTree) {
        this.boardTree = boardTree;
        this.solutionTree = solutionTree;
    }

    // -------------------------------------------------------------------------
    // Validation queries
    // -------------------------------------------------------------------------

    /**
     * Returns {@code true} if the value at {@code (row, col)} violates any
     * Sudoku rule — i.e. the same digit appears in the same row, column,
     * or 2×3 block.
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return {@code true} if a conflict exists; {@code false} if the cell
     * is empty or its value is locally unique
     */
    public boolean hasConflict(int row, int col) {
        int val = boardTree.getValue(row, col);
        if (val == 0) return false;

        // Check row
        for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
            if (c != col && boardTree.getValue(row, c) == val) return true;
        }
        // Check column
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            if (r != row && boardTree.getValue(r, col) == val) return true;
        }
        // Check 2×3 block
        int bRow = (row / ISudokuModel.BLOCK_ROWS) * ISudokuModel.BLOCK_ROWS;
        int bCol = (col / ISudokuModel.BLOCK_COLS) * ISudokuModel.BLOCK_COLS;
        for (int r = bRow; r < bRow + ISudokuModel.BLOCK_ROWS; r++) {
            for (int c = bCol; c < bCol + ISudokuModel.BLOCK_COLS; c++) {
                if ((r != row || c != col) && boardTree.getValue(r, c) == val) return true;
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if every cell is filled and none has a conflict.
     *
     * @return {@code true} when the puzzle is fully and correctly completed
     */
    public boolean isSolved() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                if (boardTree.getValue(r, c) == 0 || hasConflict(r, c)) return false;
            }
        }
        return true;
    }

    /**
     * Returns {@code true} if {@code value} matches the solution digit for
     * the cell at {@code (row, col)}.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value the digit the player is attempting to place (1–6)
     * @return {@code true} if {@code value} equals the correct solution value
     */
    public boolean isCorrectValue(int row, int col, int value) {
        return solutionTree.getValue(row, col) == value;
    }
}