package com.example.sudoku.model;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the undo history for player moves in the Sudoku game.
 *
 * <p>Each recorded entry is a three-element {@code int[]} array:
 * {@code [row, col, previousValue]}, stored in an {@link ArrayDeque}
 * used as a LIFO stack (the second non-array data structure in the
 * project).</p>
 *
 * <h2>Single Responsibility</h2>
 * <p>This class has exactly one responsibility: remember previous cell
 * values so that moves can be undone in O(1) time.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 * @see SudokuModel
 */
public class MoveHistory {

    /**
     * LIFO stack of previous moves.
     * Each entry: {@code [row, col, previousValue]}.
     */
    private final Deque<int[]> history;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates an empty move history.
     */
    public MoveHistory() {
        history = new ArrayDeque<>();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Records a move by pushing the cell's previous value onto the stack.
     *
     * @param row           row index of the cell that was changed
     * @param col           column index of the cell that was changed
     * @param previousValue the value the cell held before the change
     */
    public void push(int row, int col, int previousValue) {
        history.push(new int[]{row, col, previousValue});
    }

    /**
     * Retrieves and removes the most recent move from the stack.
     *
     * @return a {@code int[3]} array {@code [row, col, previousValue]},
     *         or {@code null} if the history is empty
     */
    public int[] pop() {
        return history.isEmpty() ? null : history.pop();
    }

    /**
     * Returns whether the history stack is empty.
     *
     * @return {@code true} if no moves have been recorded
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }

    /**
     * Removes all recorded moves, resetting the history to its initial state.
     */
    public void clear() {
        history.clear();
    }
}
