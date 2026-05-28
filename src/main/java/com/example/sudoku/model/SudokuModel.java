package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;
<<<<<<< HEAD
import java.util.LinkedList;

=======
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
/**
 * Coordinator for the Sudoku game model.
 *
 * <p>Single responsibility: manage the current board state (read/write cell
 * values, track which cells are fixed, expose hints) and coordinate the three
 * collaborating classes that each own one distinct concern:</p>
 * <ul>
<<<<<<< HEAD
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
=======
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
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
 * </ul>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuModel implements ISudokuModel {

<<<<<<< HEAD
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
=======
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

>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
        generateNewPuzzle();
    }

    // ── ISudokuModel ───────────────────────────────────────────────────────────

    /** {@inheritDoc} */
    @Override
    public void generateNewPuzzle() {
<<<<<<< HEAD
        clearFixed();
        moveHistory.clear();
        solution = generator.generate();           // Deque-backed board
        board    = new LinkedList<>(solution);     // copy as working board
        clearNonFixedCells();
        placePuzzleClues();
=======
        moveHistory.clear();
        generator.generate();
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    }

    /** {@inheritDoc} */
    @Override
    public void resetPuzzle() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
<<<<<<< HEAD
                if (!fixed[r][c]) boardSet(r, c, 0);
=======
                if (fixedTree.getValue(r, c) == 0) {
                    boardTree.setValue(r, c, 0);
                }
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
            }
        }
        moveHistory.clear();
    }

    /** {@inheritDoc} */
    @Override
    public int getValue(int row, int col) {
        validateCoordinates(row, col);
<<<<<<< HEAD
        return boardGet(board, row, col);
=======
        return boardTree.getValue(row, col);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    }

    /** {@inheritDoc} */
    @Override
    public void setValue(int row, int col, int value) {
        validateCoordinates(row, col);
        validateValue(value);
<<<<<<< HEAD
        if (fixed[row][col] || boardGet(board, row, col) == value) return;
        moveHistory.push(row, col, boardGet(board, row, col));
        boardSet(row, col, value);
=======
        if (fixedTree.getValue(row, col) == 1) return;
        int previous = boardTree.getValue(row, col);
        if (previous == value) return;
        moveHistory.push(row, col, previous);
        boardTree.setValue(row, col, value);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
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
<<<<<<< HEAD
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
=======
        return validator.hasConflict(row, col);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    }

    /** {@inheritDoc} */
    @Override
    public int getHintForCell(int row, int col) {
        validateCoordinates(row, col);
<<<<<<< HEAD
        return boardGet(solution, row, col);
=======
        return solutionTree.getValue(row, col);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
    }

    /**
     * {@inheritDoc}
     *
<<<<<<< HEAD
     * <p>Uses an {@link ArrayList} to collect and shuffle empty cells so the
     * returned cell is always random.</p>
=======
     * <p>Uses an {@link ArrayList} to collect and shuffle all empty cells,
     * returning a random one.</p>
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    @Override
    public int[] getRandomEmptyCell() {
        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
<<<<<<< HEAD
                if (boardGet(board, r, c) == 0) emptyCells.add(new int[]{r, c});
=======
                if (boardTree.getValue(r, c) == 0) emptyCells.add(new int[]{r, c});
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
            }
        }
        if (emptyCells.isEmpty()) return null;
        Collections.shuffle(emptyCells);
        return emptyCells.get(0);
    }

    /** {@inheritDoc} — delegates to {@link SudokuValidator}. */
    @Override
    public boolean isSolved() {
<<<<<<< HEAD
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
=======
        return validator.isSolved();
    }

    /** {@inheritDoc} — delegates to {@link MoveHistory}. */
    @Override
    public void undoMove() {
        int[] previous = moveHistory.pop();
        if (previous != null) {
            boardTree.setValue(previous[0], previous[1], previous[2]);
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
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
<<<<<<< HEAD
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
=======
     * Throws {@link IllegalArgumentException} if the coordinates fall outside
     * the board range [0, BOARD_SIZE).
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    private void validateCoordinates(int row, int col) {
        if (row < 0 || row >= BOARD_SIZE || col < 0 || col >= BOARD_SIZE) {
            throw new IllegalArgumentException(
                    "Coordinates out of range: (" + row + ", " + col + ")"
            );
        }
    }

    /**
<<<<<<< HEAD
     * Validates that {@code value} is a legal cell value (0 = empty,
     * 1..{@value ISudokuModel#BOARD_SIZE} = filled).
     *
     * @throws IllegalArgumentException if out of range
=======
     * Throws {@link IllegalArgumentException} if {@code value} is outside
     * [0, BOARD_SIZE] (0 = clear, 1–BOARD_SIZE = valid digit).
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
     */
    private void validateValue(int value) {
        if (value < 0 || value > BOARD_SIZE) {
            throw new IllegalArgumentException(
<<<<<<< HEAD
                    "Invalid value: " + value + ". Must be 0–" + BOARD_SIZE
=======
                    "Invalid value: " + value + ". Must be 0 to " + BOARD_SIZE
>>>>>>> 90907dc1bcae63501b91cb1c565795d81e6d4986
            );
        }
    }
}
