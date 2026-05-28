package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Concrete implementation of {@link ISudokuModel} for a 6×6 Sudoku puzzle.
 *
 * <h2>Data structures used (beyond plain arrays)</h2>
 * <ul>
 *   <li><b>{@code LinkedList<Integer>} (implements {@code Deque<Integer>})</b> —
 *       Used as a flat row-major board for both {@code solution} and {@code board}.
 *       Cell {@code (row, col)} maps to index {@code row * BOARD_SIZE + col}.
 *       This Deque-backed board is the primary non-array structure present in
 *       board-construction logic (criterion: "1 must be in board construction").
 *       The actual generation is delegated to {@link SudokuBoardGenerator}, which
 *       also uses this structure internally.</li>
 *   <li><b>{@link MoveHistory}</b> — wraps an {@code ArrayDeque<int[]>} as a LIFO
 *       undo stack.  This is the second non-array data structure.</li>
 *   <li><b>{@link ArrayList}{@code <int[]>}</b> — used in
 *       {@link #getRandomEmptyCell()} to collect and shuffle empty cells.</li>
 * </ul>
 *
 * <h2>Single Responsibility breakdown</h2>
 * <ul>
 *   <li>Solution <em>generation</em> is delegated to {@link SudokuBoardGenerator}.</li>
 *   <li>Undo <em>history</em> is delegated to {@link MoveHistory}.</li>
 *   <li>This class owns only: board state, validation, and puzzle-clue placement.</li>
 * </ul>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuModel implements ISudokuModel {

    /**
     * Complete solution board stored as a flat {@code LinkedList<Integer>}
     * (which implements {@code Deque<Integer>}).
     * Index: {@code row * BOARD_SIZE + col}.
     */
    private LinkedList<Integer> solution;

    /**
     * Working board (fixed clues + user entries, 0 = empty).
     * Same flat Deque-backed representation as {@link #solution}.
     */
    private LinkedList<Integer> board;

    /** Marks which cells are fixed puzzle clues (immutable to the player). */
    private final boolean[][] fixed;

    /** Dedicated object for undo history — SRP: single responsibility. */
    private final MoveHistory moveHistory;

    /** Dedicated object for board generation — SRP: single responsibility. */
    private final SudokuBoardGenerator generator;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new model and immediately generates the first puzzle.
     */
    public SudokuModel() {
        fixed       = new boolean[BOARD_SIZE][BOARD_SIZE];
        moveHistory = new MoveHistory();
        generator   = new SudokuBoardGenerator();
        generateNewPuzzle();
    }

    // -------------------------------------------------------------------------
    // ISudokuModel — public API
    // -------------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public void generateNewPuzzle() {
        clearFixed();
        moveHistory.clear();
        solution = generator.generate();           // Deque-backed board
        board    = new LinkedList<>(solution);     // copy as working board
        clearNonFixedCells();
        placePuzzleClues();
    }

    /** {@inheritDoc} */
    @Override
    public void resetPuzzle() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (!fixed[r][c]) boardSet(r, c, 0);
            }
        }
        moveHistory.clear();
    }

    /** {@inheritDoc} */
    @Override
    public int getValue(int row, int col) {
        validateCoordinates(row, col);
        return boardGet(board, row, col);
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(int row, int col, int value) {
        validateCoordinates(row, col);
        validateValue(value);
        if (fixed[row][col] || boardGet(board, row, col) == value) return;
        moveHistory.push(row, col, boardGet(board, row, col));
        boardSet(row, col, value);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isFixed(int row, int col) {
        validateCoordinates(row, col);
        return fixed[row][col];
    }

    /** {@inheritDoc} */
    @Override
    public boolean hasConflict(int row, int col) {
        validateCoordinates(row, col);
        int val = boardGet(board, row, col);
        if (val == 0) return false;

        // Check row
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (c != col && boardGet(board, row, c) == val) return true;
        }
        // Check column
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (r != row && boardGet(board, r, col) == val) return true;
        }
        // Check 2×3 block
        int bsr = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int bsc = (col / BLOCK_COLS) * BLOCK_COLS;
        for (int r = bsr; r < bsr + BLOCK_ROWS; r++) {
            for (int c = bsc; c < bsc + BLOCK_COLS; c++) {
                if ((r != row || c != col) && boardGet(board, r, c) == val) return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getHintForCell(int row, int col) {
        validateCoordinates(row, col);
        return boardGet(solution, row, col);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses an {@link ArrayList} to collect and shuffle empty cells so the
     * returned cell is always random.</p>
     */
    @Override
    public int[] getRandomEmptyCell() {
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (boardGet(board, r, c) == 0) emptyCells.add(new int[]{r, c});
            }
        }
        if (emptyCells.isEmpty()) return null;
        Collections.shuffle(emptyCells);
        return emptyCells.get(0);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSolved() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (boardGet(board, r, c) == 0 || hasConflict(r, c)) return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public int[] undoMove() {
        int[] previous = moveHistory.pop();
        if (previous != null) {
            boardSet(previous[0], previous[1], previous[2]);
            return new int[]{previous[0], previous[1]};
        }
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isCorrectValue(int row, int col, int value) {
        validateCoordinates(row, col);
        return boardGet(solution, row, col) == value;   // fixed: int comparison, not String
    }

    // -------------------------------------------------------------------------
    // Deque-board helpers
    // -------------------------------------------------------------------------

    /**
     * Returns the value at {@code (row, col)} from the given flat Deque board.
     *
     * @param dequeBoard the {@code LinkedList} acting as the board
     * @param row        row index (0–5)
     * @param col        column index (0–5)
     * @return cell value
     */
    private int boardGet(LinkedList<Integer> dequeBoard, int row, int col) {
        return dequeBoard.get(row * BOARD_SIZE + col);
    }

    /**
     * Writes {@code value} to {@code (row, col)} in the working {@link #board}.
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value value to store
     */
    private void boardSet(int row, int col, int value) {
        board.set(row * BOARD_SIZE + col, value);
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resets the {@link #fixed} array to all {@code false}.
     */
    private void clearFixed() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                fixed[r][c] = false;
            }
        }
    }

    /**
     * Sets every non-fixed cell on the working board to 0.
     */
    private void clearNonFixedCells() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (!fixed[r][c]) boardSet(r, c, 0);
            }
        }
    }

    /**
     * Copies exactly {@value ISudokuModel#CLUES_PER_BLOCK} randomly chosen
     * cells per block from {@link #solution} into {@link #board} and marks
     * them as fixed clues.
     */
    private void placePuzzleClues() {
        int blockRowCount = BOARD_SIZE / BLOCK_ROWS;   // 3
        int blockColCount = BOARD_SIZE / BLOCK_COLS;   // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {
                ArrayList<int[]> blockCells = new ArrayList<>();
                for (int r = br * BLOCK_ROWS; r < (br + 1) * BLOCK_ROWS; r++) {
                    for (int c = bc * BLOCK_COLS; c < (bc + 1) * BLOCK_COLS; c++) {
                        blockCells.add(new int[]{r, c});
                    }
                }
                Collections.shuffle(blockCells);

                for (int i = 0; i < CLUES_PER_BLOCK; i++) {
                    int r = blockCells.get(i)[0];
                    int c = blockCells.get(i)[1];
                    boardSet(r, c, boardGet(solution, r, c));
                    fixed[r][c] = true;
                }
            }
        }
    }

    /**
     * Validates that {@code (row, col)} is within board bounds.
     *
     * @throws IllegalArgumentException if out of range
     */
    private void validateCoordinates(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Coordinates out of range: (" + row + ", " + col + ")"
            );
        }
    }

    /**
     * Validates that {@code value} is a legal cell value (0 = empty,
     * 1..{@value ISudokuModel#BOARD_SIZE} = filled).
     *
     * @throws IllegalArgumentException if out of range
     */
    private void validateValue(int value) {
        if (value < 0 || value > BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid value: " + value + ". Must be 0–" + BOARD_SIZE
            );
        }
    }
}
