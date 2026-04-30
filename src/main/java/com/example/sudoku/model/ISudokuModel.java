package com.example.sudoku.model;

/**
 * Contract for the Sudoku game model.
 * Defines all operations for board generation, state management,
 * validation, and hint retrieval.
 *
 * <p>The board is a 6×6 grid divided into six 2×3 blocks.
 * Valid cell values are integers in the range [1, 6].</p>
 *
 * @author Juan Rosero
 * @version 1.0
 */
public interface ISudokuModel {

    /** Total rows (and columns) of the board. */
    int BOARD_SIZE = 6;

    /** Number of rows in each block. */
    int BLOCK_ROWS = 2;

    /** Number of columns in each block. */
    int BLOCK_COLS = 3;

    /** Number of fixed clues placed per block when creating a puzzle. */
    int CLUES_PER_BLOCK = 2;

    /**
     * Generates a fresh random puzzle, replacing any existing state.
     * After this call the board will have exactly {@value #CLUES_PER_BLOCK}
     * fixed clues per block.
     */
    void generateNewPuzzle();

    /**
     * Resets user-entered values, restoring the board to its initial
     * puzzle state (fixed clues only).
     */
    void resetPuzzle();

    /**
     * Returns the current value stored at the given cell.
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return value 1–6, or 0 if the cell is empty
     */
    int getValue(int row, int col);

    /**
     * Stores a user-supplied value at the given cell and pushes the
     * previous value onto the undo history.
     * Has no effect if the cell is fixed.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value 1–6 to fill the cell, or 0 to clear it
     */
    void setValue(int row, int col, int value);

    /**
     * Returns whether the given cell holds a fixed (puzzle-given) clue.
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if the cell is fixed and must not be edited
     */
    boolean isFixed(int row, int col);

    /**
     * Determines whether the current value at the given cell violates
     * any Sudoku rule (duplicate in row, column, or block).
     *
     * @param row row index
     * @param col column index
     * @return {@code true} if a conflict exists; {@code false} otherwise
     *         or if the cell is empty
     */
    boolean hasConflict(int row, int col);

    /**
     * Returns the correct solution value for the given cell.
     *
     * @param row row index
     * @param col column index
     * @return the solution value (1–6)
     */
    int getHintForCell(int row, int col);

    /**
     * Selects a random empty (value = 0) cell on the current board.
     *
     * @return an int array {@code [row, col]}, or {@code null} if the
     *         board has no empty cells
     */
    int[] getRandomEmptyCell();

    /**
     * Checks whether the board is fully and correctly completed.
     *
     * @return {@code true} if every cell is filled without conflicts
     */
    boolean isSolved();

    /**
     * Undoes the most recent user move by popping the undo history stack.
     * Has no effect if the history is empty.
     */
    void undoMove();
}
