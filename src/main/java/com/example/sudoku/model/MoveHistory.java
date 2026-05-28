package com.example.sudoku.model;
<<<<<<< HEAD

=======
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
import java.util.ArrayDeque;
import java.util.Deque;

/**
<<<<<<< HEAD
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
=======
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
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
 */
public class MoveHistory {

    /**
<<<<<<< HEAD
     * LIFO stack of previous moves.
     * Each entry: {@code [row, col, previousValue]}.
=======
     * Internal deque used as a stack.
     * Each entry is {@code int[3]}: {@code [row, col, previousValue]}.
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    private final Deque<int[]> history;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
<<<<<<< HEAD
     * Creates an empty move history.
     */
    public MoveHistory() {
        history = new ArrayDeque<>();
    }

    // -------------------------------------------------------------------------
    // Public API
=======
     * Creates a new, empty move history.
     */
    public MoveHistory() {
        this.history = new ArrayDeque<>();
    }

    // -------------------------------------------------------------------------
    // Stack operations
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    // -------------------------------------------------------------------------

    /**
     * Records a move by pushing the cell's previous value onto the stack.
     *
<<<<<<< HEAD
     * @param row           row index of the cell that was changed
     * @param col           column index of the cell that was changed
     * @param previousValue the value the cell held before the change
=======
     * @param row           row index of the modified cell (0–5)
     * @param col           column index of the modified cell (0–5)
     * @param previousValue the value the cell held before this move
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    public void push(int row, int col, int previousValue) {
        history.push(new int[]{row, col, previousValue});
    }

    /**
<<<<<<< HEAD
     * Retrieves and removes the most recent move from the stack.
     *
     * @return a {@code int[3]} array {@code [row, col, previousValue]},
     *         or {@code null} if the history is empty
=======
     * Removes and returns the most recent move.
     *
     * @return {@code int[3]} array {@code [row, col, previousValue]},
     *         or {@code null} if there are no moves to undo
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    public int[] pop() {
        return history.isEmpty() ? null : history.pop();
    }

    /**
<<<<<<< HEAD
     * Returns whether the history stack is empty.
     *
     * @return {@code true} if no moves have been recorded
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }

    /**
     * Removes all recorded moves, resetting the history to its initial state.
=======
     * Removes all recorded moves from the history.
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    public void clear() {
        history.clear();
    }
<<<<<<< HEAD
=======

    /**
     * Returns {@code true} if there are no moves recorded.
     *
     * @return {@code true} when the stack is empty
     */
    public boolean isEmpty() {
        return history.isEmpty();
    }
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
}
