package com.example.sudoku.model;

/**
 * Contract for the Sudoku game model.
 * Defines all operations for board generation, state management,
 * validation, and hint retrieval.
 *
 * <p>The board is a 6×6 grid divided into six 2×3 blocks.
 * Valid cell values are integers in the range [1, 6]; 0 represents an
 * empty cell.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public interface ISudokuModel {

    /** Total rows (and columns) of the board. */
    int BOARD_SIZE = 6;

    /** Number of rows in each 2×3 block. */
    int BLOCK_ROWS = 2;

    /** Number of columns in each 2×3 block. */
    int BLOCK_COLS = 3;

    /** Fixed clues placed per block when creating a new puzzle. */
    int CLUES_PER_BLOCK = 2;

    /**
     * Generates a fresh random puzzle, replacing any existing state.
     * After this call the board will contain exactly
     * {@value #CLUES_PER_BLOCK} fixed clues per block.
     */
    void generateNewPuzzle();

    /**
     * Resets all user-entered values, restoring the board to its initial
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
     * Stores a player-supplied value at the given cell and pushes the
     * previous value onto the undo history.
     * Has no effect if the cell is fixed or already holds the same value.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value 1–6 to fill the cell, or 0 to clear it
     */
    void setValue(int row, int col, int value);

    /**
     * Returns whether the given cell holds a fixed (puzzle-given) clue
     * that the player must not edit.
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return {@code true} if the cell is immutable
     */
    boolean isFixed(int row, int col);

    /**
     * Determines whether the current value at the given cell violates any
     * Sudoku rule (duplicate in its row, column, or 2×3 block).
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return {@code true} if a conflict exists; {@code false} if the cell
     *         is empty or conflict-free
     */
    boolean hasConflict(int row, int col);

    /**
     * Returns the correct solution value for the given cell, used to
     * provide hints without revealing the full board.
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return solution value (1–6)
     */
    int getHintForCell(int row, int col);

    /**
     * Selects a random empty cell (value = 0) on the current board.
     *
     * @return a two-element array {@code [row, col]}, or {@code null} if
     *         the board has no empty cells
     */
    int[] getRandomEmptyCell();

    /**
     * Checks whether the board is fully and correctly completed (no empty
     * cells and no conflicts).
     *
     * @return {@code true} if every cell is filled and valid
     */
    boolean isSolved();

    /**
     * Undoes the most recent player move by popping the undo-history stack
     * and restoring the previous cell value.
     *
     * @return a two-element array {@code [row, col]} of the cell that was
     *         restored, or {@code null} if the history was empty
     */
    int[] undoMove();

    /**
     * Checks whether {@code value} is the correct solution value for the
     * cell at {@code (row, col)}.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value candidate value to check (1–6)
     * @return {@code true} if {@code value} matches the solution
     */
    boolean isCorrectValue(int row, int col, int value);
}
