package com.example.sudoku.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Generates a complete, random Sudoku solution and distributes
 * the initial puzzle clues onto the working board.
 *
 * <p>Single responsibility: write new values into the three board trees
 * ({@code solutionTree}, {@code boardTree}, {@code fixedTree}) to produce
 * a valid, fresh puzzle. It never reads from these trees for game-state
 * purposes and never manages undo history or validation.</p>
 *
 * <h2>Algorithm</h2>
 * <ol>
 *   <li>Clear all three trees to 0.</li>
 *   <li>Fill {@code solutionTree} via recursive backtracking. At each cell
 *       an {@link ArrayList}{@code <Integer>} of candidates is shuffled before
 *       iteration, guaranteeing a unique layout every run.</li>
 *   <li>For each of the six 2×3 blocks, randomly select exactly
 *       {@value ISudokuModel#CLUES_PER_BLOCK} cells, copy their solution
 *       values into {@code boardTree}, and mark them as fixed in
 *       {@code fixedTree}.</li>
 * </ol>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */

public class SudokuGenerator {

    /** Destination tree for the complete solved board. */
    private final SudokuBoardTree solutionTree;

    /** Destination tree for the player-visible board. */
    private final SudokuBoardTree boardTree;

    /** Destination tree for the fixed-cell mask (1 = fixed, 0 = editable). */
    private final SudokuBoardTree fixedTree;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a generator that will write into the three provided trees.
     *
     * @param solutionTree destination for the complete solution
     * @param boardTree    destination for the playable board (clues only)
     * @param fixedTree    destination for the fixed-cell mask
     */
    public SudokuGenerator(SudokuBoardTree solutionTree,
                           SudokuBoardTree boardTree,
                           SudokuBoardTree fixedTree) {
        this.solutionTree = solutionTree;
        this.boardTree    = boardTree;
        this.fixedTree    = fixedTree;
    }

    // -------------------------------------------------------------------------
    // Public entry point
    // -------------------------------------------------------------------------

    /**
     * Fills the three board trees with a new, randomly generated puzzle.
     *
     * @throws IllegalStateException if the backtracking algorithm cannot
     *         find a valid solution (should never occur on a 6×6 board)
     */
    public void generate() {
        clearTrees();
        if (!backtrack(0, 0)) {
            throw new IllegalStateException("Failed to generate a valid Sudoku board.");
        }
        placePuzzleClues();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Resets every cell leaf in all three trees to 0.
     */
    private void clearTrees() {
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
                solutionTree.setValue(r, c, 0);
                boardTree.setValue(r, c, 0);
                fixedTree.setValue(r, c, 0);
            }
        }
    }

    /**
     * Recursively fills {@code solutionTree} with valid digits using
     * backtracking, shuffling the candidate list at each step for randomness.
     *
     * @param row current row (0–5)
     * @param col current column (0–5)
     * @return {@code true} if a valid completed board was found from this position
     */
    private boolean backtrack(int row, int col) {
        if (row == ISudokuModel.BOARD_SIZE) return true;

        int nextRow = (col == ISudokuModel.BOARD_SIZE - 1) ? row + 1 : row;
        int nextCol = (col == ISudokuModel.BOARD_SIZE - 1) ? 0       : col + 1;

        // ArrayList<Integer> — non-array structure used in board construction
        ArrayList<Integer> candidates = new ArrayList<>();
        for (int n = 1; n <= ISudokuModel.BOARD_SIZE; n++) candidates.add(n);
        Collections.shuffle(candidates);    // randomises every new game

        for (int num : candidates) {
            if (isValidPlacement(row, col, num)) {
                solutionTree.setValue(row, col, num);
                if (backtrack(nextRow, nextCol)) return true;
                solutionTree.setValue(row, col, 0);  // backtrack
            }
        }
        return false;
    }

    /**
     * Returns {@code true} if {@code num} can be legally placed at
     * {@code (row, col)} in the solution tree without violating any rule.
     *
     * @param row row index
     * @param col column index
     * @param num candidate digit (1–6)
     * @return {@code true} if no duplicate exists in the row, column, or block
     */
    private boolean isValidPlacement(int row, int col, int num) {
        for (int c = 0; c < ISudokuModel.BOARD_SIZE; c++) {
            if (solutionTree.getValue(row, c) == num) return false;
        }
        for (int r = 0; r < ISudokuModel.BOARD_SIZE; r++) {
            if (solutionTree.getValue(r, col) == num) return false;
        }
        int bRow = (row / ISudokuModel.BLOCK_ROWS) * ISudokuModel.BLOCK_ROWS;
        int bCol = (col / ISudokuModel.BLOCK_COLS) * ISudokuModel.BLOCK_COLS;
        for (int r = bRow; r < bRow + ISudokuModel.BLOCK_ROWS; r++) {
            for (int c = bCol; c < bCol + ISudokuModel.BLOCK_COLS; c++) {
                if (solutionTree.getValue(r, c) == num) return false;
            }
        }
        return true;
    }

    /**
     * Copies exactly {@value ISudokuModel#CLUES_PER_BLOCK} randomly chosen
     * cells per 2×3 block from the solution into {@code boardTree}, and marks
     * those cells as fixed (value = 1) in {@code fixedTree}.
     */
    private void placePuzzleClues() {
        int blockRowCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_ROWS;   // 3
        int blockColCount = ISudokuModel.BOARD_SIZE / ISudokuModel.BLOCK_COLS;   // 2

        for (int br = 0; br < blockRowCount; br++) {
            for (int bc = 0; bc < blockColCount; bc++) {

                // Collect and shuffle the cell positions in this block
                ArrayList<int[]> blockCells = new ArrayList<>();
                for (int r = br * ISudokuModel.BLOCK_ROWS; r < (br + 1) * ISudokuModel.BLOCK_ROWS; r++) {
                    for (int c = bc * ISudokuModel.BLOCK_COLS; c < (bc + 1) * ISudokuModel.BLOCK_COLS; c++) {
                        blockCells.add(new int[]{r, c});
                    }
                }
                Collections.shuffle(blockCells);

                // Stamp the first CLUES_PER_BLOCK cells as fixed clues
                for (int i = 0; i < ISudokuModel.CLUES_PER_BLOCK; i++) {
                    int r = blockCells.get(i)[0];
                    int c = blockCells.get(i)[1];
                    boardTree.setValue(r, c, solutionTree.getValue(r, c));
                    fixedTree.setValue(r, c, 1);
                }
            }
        }
    }
}
