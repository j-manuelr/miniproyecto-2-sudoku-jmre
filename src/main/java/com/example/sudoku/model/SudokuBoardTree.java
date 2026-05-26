package com.example.sudoku.model;

/**
 * N-ary tree that stores and manages the 6×6 Sudoku board.
 *
 * <h2>Structure</h2>
 * <pre>
 *                  [ROOT  -1]                 depth 0
 *          /    /    |    \    \    \
 *        [R0] [R1] [R2] [R3] [R4] [R5]       depth 1  (row nodes)
 *        /|\              ...          /|\
 *      [0][3][0][1][0][0]   [0][0][4][0][2][0]  depth 2  (cell leaf nodes)
 * </pre>
 *
 * <p>To access cell {@code (row, col)}:</p>
 * <pre>
 *     root → child(row) → child(col) → getValue()
 * </pre>
 *
 * <h2>Why a tree instead of int[][]?</h2>
 * <p>The N-ary tree makes the hierarchical nature of the board explicit:
 * the board owns rows, and each row owns cells.  Every level of the
 * hierarchy is a first-class object, making it easy to add per-row or
 * per-cell metadata in the future (e.g. annotations, highlights).</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */

public class SudokuBoardTree {

    // -------------------------------------------------------------------------
    // Constants (mirrors ISudokuModel for independence)
    // -------------------------------------------------------------------------

    /** Total rows and columns of the 6×6 board. */
    private static final int SIZE = ISudokuModel.BOARD_SIZE;   // 6

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * Root of the N-ary tree.
     * Its {@code SIZE} children are the row nodes;
     * each row node has {@code SIZE} cell-leaf children.
     */
    private final SudokuTreeNode root;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Builds an empty board tree.
     * All cell leaves are initialised to 0 (empty).
     *
     * <h3>Tree construction — step by step</h3>
     * <ol>
     *   <li>Create root with value -1 (sentinel).</li>
     *   <li>For each row index r (0–5): create a row node (value = r)
     *       and add it as a child of root.</li>
     *   <li>For each column index c (0–5): create a cell leaf node
     *       (value = 0) and add it as a child of the row node.</li>
     * </ol>
     */
    public SudokuBoardTree() {
        root = new SudokuTreeNode(-1);          // depth 0: board root

        for (int r = 0; r < SIZE; r++) {
            SudokuTreeNode rowNode = new SudokuTreeNode(r);   // depth 1: row
            root.addChild(rowNode);

            for (int c = 0; c < SIZE; c++) {
                SudokuTreeNode cellNode = new SudokuTreeNode(0); // depth 2: cell (0 = empty)
                rowNode.addChild(cellNode);
            }
        }
    }

    // -------------------------------------------------------------------------
    // Public API  (drop-in replacement for int[][] board)
    // -------------------------------------------------------------------------

    /**
     * Returns the value stored at {@code (row, col)}.
     *
     * <p>Navigation: {@code root → child(row) → child(col) → getValue()}</p>
     *
     * @param row row index (0–5)
     * @param col column index (0–5)
     * @return cell value (0 = empty, 1–6 = filled)
     */
    public int getValue(int row, int col) {
        return getCellNode(row, col).getValue();
    }

    /**
     * Sets the value at {@code (row, col)}.
     *
     * <p>Navigation: {@code root → child(row) → child(col) → setValue(value)}</p>
     *
     * @param row   row index (0–5)
     * @param col   column index (0–5)
     * @param value new cell value (0–6)
     */
    public void setValue(int row, int col, int value) {
        getCellNode(row, col).setValue(value);
    }

    /**
     * Resets every cell in the board to 0 (empty).
     *
     * <p>Traverses all row nodes and then all cell nodes within each row.</p>
     */
    public void clearAll() {
        for (int r = 0; r < SIZE; r++) {
            SudokuTreeNode rowNode = root.getChild(r);    // depth 1
            for (int c = 0; c < SIZE; c++) {
                rowNode.getChild(c).setValue(0);           // depth 2
            }
        }
    }

    /**
     * Copies the entire content of a plain {@code int[][]} array into
     * the tree.  Useful for initialising the tree from a generated board.
     *
     * @param source 6×6 array to copy from
     */
    public void loadFromArray(int[][] source) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                setValue(r, c, source[r][c]);
            }
        }
    }

    /**
     * Dumps the tree content into a plain {@code int[][]} array.
     * Useful for comparing or passing to legacy methods.
     *
     * @return a new 6×6 array with the current cell values
     */
    public int[][] toArray() {
        int[][] result = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                result[r][c] = getValue(r, c);
            }
        }
        return result;
    }

    /**
     * Returns a human-readable representation of the board for debugging.
     *
     * <p>Example output for a 6×6 board:</p>
     * <pre>
     * [ 1  0  3 | 4  0  6 ]
     * [ 0  5  0 | 0  2  0 ]
     *  ---------------------
     * ...
     * </pre>
     *
     * @return formatted board string
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < SIZE; r++) {
            sb.append("[ ");
            for (int c = 0; c < SIZE; c++) {
                sb.append(getValue(r, c));
                if (c == 2) sb.append(" | ");   // block separator
                else if (c < SIZE - 1) sb.append("  ");
            }
            sb.append(" ]\n");
            if (r == 1 || r == 3) sb.append(" ---------------------\n");
        }
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------------------------

    /**
     * Navigates the tree and returns the leaf node at {@code (row, col)}.
     *
     * @param row row index
     * @param col column index
     * @return the cell leaf node
     */
    private SudokuTreeNode getCellNode(int row, int col) {
        return root.getChild(row).getChild(col);   // root → rowNode → cellNode
    }
}
