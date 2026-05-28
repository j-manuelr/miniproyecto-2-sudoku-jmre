package com.example.sudoku.model;


import java.util.ArrayList;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

/**
 * Concrete implementation of {@link ISudokuModel} for a 6×6 Sudoku puzzle.
 *
 * <h2>Data structures used (beyond plain arrays)</h2>
 * <ul>
 *   <li>{@link ArrayList}{@code <Integer>} — holds a shuffled candidate list
 *       inside {@link #generateSolution(int, int)} to ensure a different random
 *       board every run. Also used in {@link #getRandomEmptyCell()} to collect
 *       and shuffle empty cells.</li>
 *   <li>{@link Deque}{@code <int[]>} — stores the undo history. Every call to
 *       {@link #setValue(int, int, int)} pushes the previous {@code [row, col, value]}
 *       triple so the user can step back through their moves.</li>
 * </ul>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuModel implements ISudokuModel {

    /** Internal complete solution board. Never shown directly to the player. */
    private final int[][] solution;

    /** Working board: contains fixed clues + user entries (0 = empty). */
    private final int[][] board;

    /** Marks which cells are fixed clues (immutable during a puzzle). */
    private final boolean[][] fixed;

    /**
     * Undo history — each entry is {@code [row, col, previousValue]}.
     * Used as the second non-array data structure for this project.
     */
    private final Deque<int[]> moveHistory;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a new model and immediately generates the first puzzle.
     */
    public SudokuModel() {
        solution    = new int[BOARD_SIZE][BOARD_SIZE];
        board       = new int[BOARD_SIZE][BOARD_SIZE];
        fixed       = new boolean[BOARD_SIZE][BOARD_SIZE];
        moveHistory = new ArrayDeque<>();
        generateNewPuzzle();
    }

    // -------------------------------------------------------------------------
    // ISudokuModel — public API
    // -------------------------------------------------------------------------

    /** {@inheritDoc} */
    @Override
    public void generateNewPuzzle() {
        clearArrays();
        moveHistory.clear();
        if (!generateSolution(0, 0)) {
            throw new IllegalStateException("Failed to generate a valid Sudoku board.");
        }
        placePuzzleClues();
    }

    /** {@inheritDoc} */
    @Override
    public void resetPuzzle() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (!fixed[r][c]) board[r][c] = 0;
            }
        }
        moveHistory.clear();
    }

    /** {@inheritDoc} */
    @Override
    public int getValue(int row, int col) {
        validateCoordinates(row, col);
        return board[row][col];
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(int row, int col, int value) {
        validateCoordinates(row, col);
        validateValue(value);

        if (fixed[row][col] || board[row][col] == value) return;

        moveHistory.push(new int[]{row, col, board[row][col]});
        board[row][col] = value;
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
        int val = board[row][col];
        if (val == 0) return false;

        // Check row
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (c != col && board[row][c] == val) return true;
        }
        // Check column
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (r != row && board[r][col] == val) return true;
        }
        // Check block
        int blockStartRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int blockStartCol = (col / BLOCK_COLS) * BLOCK_COLS;
        for (int r = blockStartRow; r < blockStartRow + BLOCK_ROWS; r++) {
            for (int c = blockStartCol; c < blockStartCol + BLOCK_COLS; c++) {
                if ((r != row || c != col) && board[r][c] == val) return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int getHintForCell(int row, int col) {
        validateCoordinates(row, col);
        return solution[row][col];
    }

    /**
     * {@inheritDoc}
     *
     * <p>Uses an {@link ArrayList} to collect all empty cells and then
     * shuffles the list so the returned cell is always random.</p>
     */
    @Override
    public int[] getRandomEmptyCell() {
        // ArrayList used here as the second non-array data structure (empty-cell pool)
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (board[r][c] == 0) emptyCells.add(new int[]{r, c});
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
                if (board[r][c] == 0 || hasConflict(r, c)) return false;
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Pops the top entry from the move-history deque and
     * restores the previous value at that cell.</p>
     */
    @Override
    public void undoMove() {
        if (!moveHistory.isEmpty()) {
            int[] previous = moveHistory.pop();
            board[previous[0]][previous[1]] = previous[2];
        }
    }

    /**
     * Validates that row/col are inside board bounds.
     */
    private void validateCoordinates(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Coordinates out of range: (" + row + ", " + col + ")"
            );
        }
    }

    /**
     * Validates accepted cell values (0 empty, 1..BOARD_SIZE filled).
     */
    private void validateValue(int value) {
        if (value < 0 || value > BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Invalid value: " + value + ". Must be between 0 and " + BOARD_SIZE
            );
        }
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resets all internal arrays to their zero/false defaults.
     */
    private void clearArrays() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                solution[r][c] = 0;
                board[r][c]    = 0;
                fixed[r][c]    = false;
            }
        }
    }

    /**
     * Recursively fills {@link #solution} with a valid, randomly ordered
     * 6×6 Sudoku using backtracking.
     *
     * <p>An {@link ArrayList}{@code <Integer>} of candidates is shuffled at
     * each cell to guarantee a different board every invocation — this is the
     * primary non-array data structure used in board construction.</p>
     *
     * @param row current row being filled (0–5)
     * @param col current column being filled (0–5)
     * @return {@code true} if a valid board was completed from this position
     */
    private boolean generateSolution(int row, int col) {
        if (row == BOARD_SIZE) return true;

        int nextRow = (col == BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == BOARD_SIZE - 1) ? 0 : col + 1;

        // ArrayList<Integer> used as the primary non-array data structure
        // required to be in board construction logic
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int n = 1; n <= BOARD_SIZE; n++) candidates.add(n);
        Collections.shuffle(candidates);   // ensures board randomness

        for (int num : candidates) {
            if (isValidInSolution(row, col, num)) {
                solution[row][col] = num;
                if (generateSolution(nextRow, nextCol)) return true;
                solution[row][col] = 0;
            }
        }
        return false;
    }


    public boolean isCorrectValue(int row, int col, int value) {
        return (String.valueOf(solution[row][col]).equals(String.valueOf(value)));
    }

    /**
     * Checks whether {@code num} can be legally placed at {@code (row, col)}
     * in the {@link #solution} board, i.e. it does not appear in the same
     * row, column, or 2×3 block.
     *
     * @param row row index
     * @param col column index
     * @param num candidate value (1–6)
     * @return {@code true} if placement is valid
     */
    private boolean isValidInSolution(int row, int col, int num) {
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (solution[row][c] == num) return false;
        }
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (solution[r][col] == num) return false;
        }
        int blockStartRow = (row / BLOCK_ROWS) * BLOCK_ROWS;
        int blockStartCol = (col / BLOCK_COLS) * BLOCK_COLS;
        for (int r = blockStartRow; r < blockStartRow + BLOCK_ROWS; r++) {
            for (int c = blockStartCol; c < blockStartCol + BLOCK_COLS; c++) {
                if (solution[r][c] == num) return false;
            }
        }
        return true;
    }

    /**
     * Copies exactly {@value ISudokuModel#CLUES_PER_BLOCK} randomly chosen
     * cells from {@link #solution} into {@link #board} and marks them as fixed.
     * Each of the six 2×3 blocks receives its own independent random selection.
     */
    private void placePuzzleClues() {
        int blockRowCount = BOARD_SIZE / BLOCK_ROWS;   // 3
        int blockColCount = BOARD_SIZE / BLOCK_COLS;   // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {

                // Collect the cells in this block using an ArrayList
                ArrayList<int[]> blockCells = new ArrayList<>();
                for (int r = br * BLOCK_ROWS; r < (br + 1) * BLOCK_ROWS; r++) {
                    for (int c = bc * BLOCK_COLS; c < (bc + 1) * BLOCK_COLS; c++) {
                        blockCells.add(new int[]{r, c});
                    }
                }
                Collections.shuffle(blockCells);

                // Place exactly CLUES_PER_BLOCK fixed numbers
                for (int i = 0; i < CLUES_PER_BLOCK; i++) {
                    int r = blockCells.get(i)[0];
                    int c = blockCells.get(i)[1];
                    board[r][c]  = solution[r][c];
                    fixed[r][c]  = true;
                }
            }
        }
    }
}
