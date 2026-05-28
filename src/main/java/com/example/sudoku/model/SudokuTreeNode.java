package com.example.sudoku.model;
import java.util.ArrayList;
import java.util.List;

/**
 * A single node in the N-ary tree that represents the Sudoku board.
 *
 * <h2>Tree shape</h2>
 * <pre>
 *              [ROOT]              depth 0  — board root (value = -1)
 *         /    |    \
 *       [R0]  [R1] ... [R5]        depth 1  — one node per row (value = row index)
 *       / \              / \
 *     [C0][C1]...    [C0][C5]      depth 2  — leaf nodes (value = cell content 0-6)
 * </pre>
 *
 * <p>Each node can have an arbitrary number of children, stored in an
 * {@link ArrayList}. Leaf nodes (depth 2) always have an empty children list.</p>
 *
 * @author Juan Rosero, Natalia Parra
 * @version 1.0
 */
public class SudokuTreeNode {

    // -------------------------------------------------------------------------
    // Fields
    // -------------------------------------------------------------------------

    /**
     * The data stored in this node.
     * <ul>
     *   <li>Root  → {@code -1} (sentinel, unused)</li>
     *   <li>Row   → row index (0–5)</li>
     *   <li>Cell  → cell value (0 = empty, 1–6 = filled)</li>
     * </ul>
     */
    private int value;

    /**
     * Ordered list of child nodes.
     * Size is always {@value com.example.sudoku.model.ISudokuModel#BOARD_SIZE}
     * for root and row nodes; empty for leaf (cell) nodes.
     */
    private final List<SudokuTreeNode> children;

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Creates a node with the given value and no children.
     *
     * @param value data to store in this node
     */
    public SudokuTreeNode(int value) {
        this.value    = value;
        this.children = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Public API
    // -------------------------------------------------------------------------

    /**
     * Returns the value stored in this node.
     *
     * @return node value
     */
    public int getValue() {
        return value;
    }

    /**
     * Replaces the value stored in this node.
     *
     * @param value new value
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Appends a child node at the end of the children list.
     *
     * @param child node to add
     */
    public void addChild(SudokuTreeNode child) {
        children.add(child);
    }

    /**
     * Returns the child at the given index (0-based).
     *
     * @param index position in the children list
     * @return the child node
     * @throws IndexOutOfBoundsException if index is out of range
     */
    public SudokuTreeNode getChild(int index) {
        return children.get(index);
    }

    /**
     * Returns the number of direct children of this node.
     *
     * @return children count
     */
    public int getChildCount() {
        return children.size();
    }

    /**
     * Returns {@code true} if this node has no children (i.e. it is a leaf).
     *
     * @return {@code true} for leaf nodes
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }
}
