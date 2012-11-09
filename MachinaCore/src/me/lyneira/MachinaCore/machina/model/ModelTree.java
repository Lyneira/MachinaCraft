package me.lyneira.MachinaCore.machina.model;

import gnu.trove.procedure.TIntProcedure;

import java.util.Iterator;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;

/**
 * Represents a tree-based model of a machina. Nodes added to the tree return a
 * unique id that can be used to retrieve them. A model tree always has a root
 * node, so if you just need to add blocks to a static base model of the
 * machina, no addNode() call is required.
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
    public int addNode(BlockVector origin);

    /**
     * Adds a new node to the model under the given parent node and returns the
     * id of the new node.
     * 
     * @param parentId
     *            The parent node to add this node under
     * @return Id of the node added
     */
    public int addNode(int parentId, BlockVector origin);

    /**
     * Deletes a node and all its subnodes from the model.
     * 
     * @param id
     *            The id of the node to remove
     */
    public void removeNode(int nodeId);
    
    /**
     * Returns true if the given node identifier exists in this model.
     * @param nodeId True if the node exists, false otherwise
     */
    public boolean hasNode(int nodeId);

    /**
     * Returns an array containing this node's children in no particular order.
     * 
     * @return
     */
    public void forEachChild(int nodeId, TIntProcedure procedure);

    /**
     * Returns the number of nodes held by this tree. Note that depending on
     * pending modifications, this may return a larger
     * number than the actual nodes reachable in the live tree.
     * 
     * @return Node count
     */
    public int nodeCount();

    /* *************
     * Block methods
     */
    public MachinaBlock getBlock(int id);

    public MachinaBlock getBlock(int nodeId, int id);

    public Iterator<Integer> getBlocks(int nodeId);

    public int addBlock(MachinaBlock block);

    public int addBlock(MachinaBlock block, int nodeId);

    public void deleteBlock(int id);

    public void deleteBlock(int nodeId, int id);

    public void clearBlocks(int nodeId);

    public void putBlock(MachinaBlock newBlock, int id);

    public void putBlock(MachinaBlock newBlock, int nodeId, int id);
}
