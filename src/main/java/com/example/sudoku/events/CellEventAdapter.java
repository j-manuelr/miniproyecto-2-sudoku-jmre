package com.example.sudoku.events;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Abstract adapter for {@link ICellEventListener}.
 * Provides empty default implementations for all event callback methods,
 * allowing concrete subclasses to override only the events they care about.
 *
 * <p>Following the <em>Adapter design pattern</em>, inner-class handlers
 * in the controller extend this class instead of directly implementing
 * {@link ICellEventListener}, reducing boilerplate.</p>
 *
 * @author Juan Rosero
 * @version 1.0
 * @see ICellEventListener
 */
public abstract class CellEventAdapter implements ICellEventListener {

    /**
     * Default implementation — does nothing.
     * Override to handle key-press events on a cell.
     *
     * @param event the originating {@link KeyEvent}
     * @param row   the cell's row index
     * @param col   the cell's column index
     */
    @Override
    public void onCellKeyPressed(KeyEvent event, int row, int col) {
        // no-op by default
    }

    /**
     * Default implementation — does nothing.
     * Override to handle mouse-click events on a cell.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index
     * @param col   the cell's column index
     */
    @Override
    public void onCellClicked(MouseEvent event, int row, int col) {
        // no-op by default
    }

    /**
     * Default implementation — does nothing.
     * Override to handle mouse-enter events on a cell.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index
     * @param col   the cell's column index
     */
    @Override
    public void onCellMouseEntered(MouseEvent event, int row, int col) {
        // no-op by default
    }

    /**
     * Default implementation — does nothing.
     * Override to handle mouse-exit events on a cell.
     *
     * @param event the originating {@link MouseEvent}
     * @param row   the cell's row index
     * @param col   the cell's column index
     */
    @Override
    public void onCellMouseExited(MouseEvent event, int row, int col) {
        // no-op by default
    }
}
