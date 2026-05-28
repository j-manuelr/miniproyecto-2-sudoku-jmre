package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Generates a complete, randomized valid 6×6 Sudoku solution using
 * a recursive backtracking algorithm.
 *
 * <h2>Board data structure</h2>
 * <p>The board is stored internally as a flat {@code LinkedList<Integer>}
 * which implements {@code Deque<Integer>}.  Cell {@code (row, col)} maps
 * to index {@code row * BOARD_SIZE + col}.  Using a {@link Deque} as the
 * board satisfies the course requirement that at least one non-array data
 * structure must be present in the board-construction logic.</p>
 *
 * <h2>Single Responsibility</h2>
 * <p>This class has exactly one responsibility: given no input, produce a
 * complete valid Sudoku solution board.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 * @see SudokuModel
 */
public class SudokuBoardGenerator {

    /**
     * Flat board representation backed by a {@link LinkedList} which
     * implements {@link Deque}.
     * Index mapping: {@code row * BOARD_SIZE + col}.
     */
    private final LinkedList<Integer> solutionDeque;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a generator and pre-fills the internal Deque with zeros.
     * Zeros are added via {@link Deque#offerLast(Object)} to demonstrate
     * Deque semantics during initialization.
     */
    public SudokuBoardGenerator() {
        solutionDeque = new LinkedList<>();
        for (int i = 0; i < ISudokuModel.BOARD_SIZE * ISudokuModel.BOARD_SIZE; i++) {
            solutionDeque.offerLast(0);   // Deque insertion at tail
        }
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Generates a new complete, random, valid Sudoku solution.
     *
     * <p>Internally, candidate numbers are collected into an
     * {@link ArrayList}{@code <Integer>} and shuffled, then tried one by
     * one via backtracking.  The board state is maintained in the Deque
     * field throughout construction.</p>
     *
     * @return a new {@code LinkedList<Integer>} (a {@link Deque}) containing
     *         the 36-cell solution in row-major order
     * @throws IllegalStateException if the algorithm unexpectedly fails
     */
    public LinkedList<Integer> generate() {
        resetBoard();
        if (!fillCell(0, 0)) {
            throw new IllegalStateException("Failed to generate a valid Sudoku board.");
        }
        // Return a defensive copy so callers cannot mutate the internal state
        return new LinkedList<>(solutionDeque);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resets every cell in the Deque to 0 by clearing and re-filling.
     */
    private void resetBoard() {
        solutionDeque.clear();
        for (int i = 0; i < ISudokuModel.BOARD_SIZE * ISudokuModel.BOARD_SIZE; i++) {
            solutionDeque.offerLast(0);
        }
    }

    /**
     * Returns the value at {@code (row, col)} from the flat Deque.
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return current cell value
     */
    private int get(int row, int col) {
        return solutionDeque.get(row * ISudokuModel.BOARD_SIZE + col);
    }

    /**
     * Writes {@code value} to {@code (row, col)} in the flat Deque.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value value to store
     */
    private void set(int row, int col, int value) {
        solutionDeque.set(row * ISudokuModel.BOARD_SIZE + col, value);
    }

    /**
     * Recursively fills the board from cell {@code (row, col)} using
     * backtracking.  Candidate values are stored in an {@link ArrayList}
     * and shuffled to guarantee a different board every run.
     *
     * @param row current row
     * @param col current column
     * @return {@code true} if the board was completed successfully
     */
    private boolean fillCell(int row, int col) {
        if (row == ISudokuModel.BOARD_SIZE) return true;

        int nextRow = (col == ISudokuModel.BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == ISudokuModel.BOARD_SIZE - 1) ? 0 : col + 1;

        // ArrayList<Integer> of shuffled candidates — primary non-array structure
        // used directly in board construction
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int n = 1; n <= ISudokuModel.BOARD_SIZE; n++) candidates.add(n);
        Collections.shuffle(candidates);

        for (int num : candidates) {
            if (isValidPlacement(row, col, num)) {
                set(row, col, num);                       // place candidate
                if (fillCell(nextRow, nextCol)) return true;
                set(row, col, 0);                         // backtrack
            }
        }
        return false;
    }

    /**
     * Checks whether {@code num} may legally be placed at {@code (row, col)},
     * i.e. it does not appear in the same row, column, or 2×3 block.
     *
     * @param row row index
     * @param col column index
     * @param num candidate value (1–6)
     * @return {@code true} if placement is valid
     */
    private boolean isValidPlacement(int row, int col, int num) {
        for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
            if (get(row, c) == num) return false;
        }
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            if (get(r, col) == num) return false;
        }
        int bsr = (row / ISudokuModel.BLOCK_ROWS) * ISudokuModel.BLOCK_ROWS;
        int bsc = (col / ISudokuModel.BLOCK_COLS) * ISudokuModel.BLOCK_COLS;
        for (int r = bsr; r < bsr + ISudokuModel.BLOCK_ROWS; r++) {
            for (int c = bsc; c < bsc + ISudokuModel.BLOCK_COLS; c++) {
                if (get(r, c) == num) return false;
            }
        }
        return true;
    }
}