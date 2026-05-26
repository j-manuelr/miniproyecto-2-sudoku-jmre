package com.example.sudoku.model;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Manages the undo history for the Sudoku game.
 *
 * <p>Single responsibility: act as a last-in-first-out stack of moves,
 * where each move records the cell coordinates and the value that was
 * overwritten, so it can be restored on undo.</p>
 *
 * <p>Uses an {@link ArrayDeque} as the underlying stack (push/pop on the
 * head), giving O(1) push and pop operations.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class MoveHistory {

    /**
     * Internal deque used as a stack.
     * Each entry is {@code int[3]}: {@code [row, col, previousValue]}.
     */
    private final Deque<int[]> history;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a new, empty move history.
     */
    public MoveHistory() {
        this.history = new ArrayDeque<>();
    }

    // -------------------------------------------------------------------------
    // Stack operations
    // -------------------------------------------------------------------------

    /**
     * Records a move by pushing the cell's previous value onto the stack.
     *
     * @param row           row index of the modified cell (0–5)
     * @param col           column index of the modified cell (0–5)
     * @param previousValue the value the cell held before this move
     */
    public void push(int row, int col, int previousValue) {
        history.push(new int[]{row, col, previousValue});
    }

    /**
     * Removes and returns the most recent move.
     *
     * @return {@code int[3]} array {@code [row, col, previousValue]},
     *         or {@code null} if there are no moves to undo
     */
    public int[] pop() {
        return history.isEmpty() ? null : history.pop();
    }

    /**
     * Removes all recorded moves from the history.
     */
    public void clear() {
        history.clear();
    }

    /**
     * Returns {@code true} if there are no moves recorded.
     *
     * @return {@code true} when the stack is empty
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }
}
