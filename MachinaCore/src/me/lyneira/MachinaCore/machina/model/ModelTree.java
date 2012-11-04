package me.lyneira.MachinaCore.machina.model;

import java.util.Iterator;

import me.lyneira.MachinaCore.block.MachinaBlock;

/**
 * Represents a tree-based model of a machina.
 * 
 * @author Lyneira
 */
public interface ModelTree {

    /* ************
     * Node methods
     */
    // public ModelNode get(int id);

    /**
     * Adds a new node to the model under the root and returns the id of the new
     * node. Equivalent to addNode(0).
     * 
     * @return Id of the node added
     */
    public int addNode();

    /**
     * Adds a new node to the model under the given parent node and returns the
     * id of the new node.
     * 
     * @param parentId
     *            The parent node to add this node under
     * @return Id of the node added
     */
    public int addNode(int parentId);

    /**
     * Deletes a node and all its subnodes from the model.
     * 
     * @param id
     *            The id of the node to remove
     */
    public void removeNode(int node);

    /**
     * Returns an array containing this node's children.
     * 
     * @return
     */
    public int[] getChildren(int node);

    /**
     * Returns the number of nodes held by this tree. Note that depending on
     * garbage collection of recently removed nodes, this may return a larger
     * number than the actual nodes reachable in the tree.
     * 
     * @return Node count
     */
    public int nodeCount();

    /* *************
     * Block methods
     */
    public MachinaBlock getBlock(int id);

    public MachinaBlock getBlock(int node, int id);

    public Iterator<Integer> getBlocks(int node);

    public int addBlock(MachinaBlock block);

    public int addBlock(MachinaBlock block, int node);

    public void deleteBlock(int id);

    public void deleteBlock(int node, int id);

    public void clearBlocks(int node);

    public void putBlock(MachinaBlock newBlock, int id);

    public void putBlock(MachinaBlock newBlock, int node, int id);
}
