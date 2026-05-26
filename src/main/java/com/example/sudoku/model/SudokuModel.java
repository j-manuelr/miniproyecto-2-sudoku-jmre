package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
/**
 * Coordinator for the Sudoku game model.
 *
 * <p>Single responsibility: manage the current board state (read/write cell
 * values, track which cells are fixed, expose hints) and coordinate the three
 * collaborating classes that each own one distinct concern:</p>
 * <ul>
 *   <li>{@link SudokuGenerator} — board and solution generation.</li>
 *   <li>{@link SudokuValidator} — rule validation and completion checks.</li>
 *   <li>{@link MoveHistory}     — undo-history stack.</li>
 * </ul>
 *
 * <h2>Board trees</h2>
 * Three {@link SudokuBoardTree} instances replace the plain
 * {@code int[][]} / {@code boolean[][]} arrays that would otherwise be used:
 * <ul>
 *   <li>{@code boardTree}    — current player-visible state (0 = empty, 1–6 filled).</li>
 *   <li>{@code solutionTree} — immutable complete solution for hint/validation.</li>
 *   <li>{@code fixedTree}    — mask: 1 = immutable clue cell, 0 = editable.</li>
 * </ul>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuModel implements ISudokuModel {

    // ── Board trees ────────────────────────────────────────────────────────────
    private final SudokuBoardTree boardTree;
    private final SudokuBoardTree solutionTree;
    private final SudokuBoardTree fixedTree;

    // ── Collaborators ──────────────────────────────────────────────────────────
    private final SudokuGenerator generator;
    private final SudokuValidator  validator;
    private final MoveHistory      moveHistory;

    // ── Constructor ────────────────────────────────────────────────────────────

    /**
     * Constructs the model, creates the three board trees, wires the
     * collaborators, and generates the first puzzle.
     */
    public SudokuModel() {
        solutionTree = new SudokuBoardTree();
        boardTree    = new SudokuBoardTree();
        fixedTree    = new SudokuBoardTree();

        generator   = new SudokuGenerator(solutionTree, boardTree, fixedTree);
        validator   = new SudokuValidator(boardTree, solutionTree);
        moveHistory = new MoveHistory();

        generateNewPuzzle();
    }

    // ── ISudokuModel ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public void generateNewPuzzle() {
        moveHistory.clear();
        generator.generate();
    }

    /** {@inheritDoc} */
    @Override
    public void resetPuzzle() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (fixedTree.getValue(r, c) == 0) {
                    boardTree.setValue(r, c, 0);
                }
            }
        }
        moveHistory.clear();
    }

    /** {@inheritDoc} */
    @Override
    public int getValue(int row, int col) {
        validateCoordinates(row, col);
        return boardTree.getValue(row, col);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(int row, int col, int value) {
        validateCoordinates(row, col);
        validateValue(value);
        if (fixedTree.getValue(row, col) == 1) return;
        int previous = boardTree.getValue(row, col);
        if (previous == value) return;
        moveHistory.push(row, col, previous);
        boardTree.setValue(row, col, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFixed(int row, int col) {
        validateCoordinates(row, col);
        return fixedTree.getValue(row, col) == 1;
    }

    /** {@inheritDoc} — delegates to {@link SudokuValidator}. */
    @Override
    public boolean hasConflict(int row, int col) {
        validateCoordinates(row, col);
        return validator.hasConflict(row, col);
    }

    /** {@inheritDoc} */
    @Override
    public int getHintForCell(int row, int col) {
        validateCoordinates(row, col);
        return solutionTree.getValue(row, col);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses an {@link ArrayList} to collect and shuffle all empty cells,
     * returning a random one.</p>
     */
    @Override
    public int[] getRandomEmptyCell() {
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (boardTree.getValue(r, c) == 0) emptyCells.add(new int[]{r, c});
            }
        }
        if (emptyCells.isEmpty()) return null;
        Collections.shuffle(emptyCells);
        return emptyCells.get(0);
    }

    /** {@inheritDoc} — delegates to {@link SudokuValidator}. */
    @Override
    public boolean isSolved() {
        return validator.isSolved();
    }

    /** {@inheritDoc} — delegates to {@link MoveHistory}. */
    @Override
    public void undoMove() {
        int[] previous = moveHistory.pop();
        if (previous != null) {
            boardTree.setValue(previous[0], previous[1], previous[2]);
        }
    }

    /** {@inheritDoc} — delegates to {@link SudokuValidator}. */
    @Override
    public boolean isCorrectValue(int row, int col, int value) {
        validateCoordinates(row, col);
        return validator.isCorrectValue(row, col, value);
    }

    // ── Guards ─────────────────────────────────────────────────────────────────

    /**
     * Throws {@link IllegalArgumentException} if the coordinates fall outside
     * the board range [0, BOARD_SIZE).
     */
    private void validateCoordinates(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Coordinates out of range: (" + row + ", " + col + ")"
            );
        }
    }

    /**
     * Throws {@link IllegalArgumentException} if {@code value} is outside
     * [0, BOARD_SIZE] (0 = clear, 1–BOARD_SIZE = valid digit).
     */
    private void validateValue(int value) {
        if (value < 0 || value > BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid value: " + value + ". Must be 0 to " + BOARD_SIZE
            );
        }
    }
}
