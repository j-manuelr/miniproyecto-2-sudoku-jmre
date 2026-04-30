package com.example.sudoku.events;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Listener interface for cell-level interaction events in the Sudoku board.
 * Classes that need to react to user input on individual cells should
 * implement this interface (or extend {@link CellEventAdapter}).
 *
 * @author Juan Rosero
 * @version 1.0
 */
public interface ICellEventListener {

    /**
     * Called when a key is pressed while the given cell has keyboard focus.
     *
     * @param event the originating {@link KeyEvent}
     * @param row   the cell's row index (0–5)
     * @param col   the cell's column index (0–5)
     */
    void onCellKeyPressed(KeyEvent event, int row, int col);

    /**
     * Called when the given cell receives a mouse-click event.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index (0–5)
     * @param col   the cell's column index (0–5)
     */
    void onCellClicked(MouseEvent event, int row, int col);

    /**
     * Called when the mouse pointer enters the bounds of the given cell.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index (0–5)
     * @param col   the cell's column index (0–5)
     */
    void onCellMouseEntered(MouseEvent event, int row, int col);

    /**
     * Called when the mouse pointer exits the bounds of the given cell.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index (0–5)
     * @param col   the cell's column index (0–5)
     */
    void onCellMouseExited(MouseEvent event, int row, int col);
}
