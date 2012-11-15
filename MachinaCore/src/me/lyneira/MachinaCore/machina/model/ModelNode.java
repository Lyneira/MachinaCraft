package me.lyneira.MachinaCore.machina.model;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.TIntHashSet;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.util.collection.UniqueIdObjectIterator;
import me.lyneira.util.collection.UniqueIdObjectMap;

/**
 * Represents a part (or all) of the blocks of a machina. A machina can consist
 * of multiple nodes that form a tree structure. A node can be active in the
 * world or inactive (its blocks and all those under it are not present but they
 * would appear when activated).
 * <p>
 * 
 * Nodes have an offset (defaulting to 0,0,0) from their parent node, so that
 * different parts of a machina can move in relation to each other. If the
 * offset of a node changes, it will change the effective location of all the
 * blocks that fall under it, including those belonging to child nodes.
 * 
 * @author Lyneira
 */
class ModelNode {

    /**
     * This id must be set by the managing collection!
     */
    // int id;
    final int parent;
    BlockVector origin;
    boolean active = true;
    final UniqueIdObjectMap<MachinaBlock> blocks;
    private TIntHashSet children = null;

    /**
     * Creates a new root node with the given origin.
     * 
     * @param origin
     *            The location around which this node's blocks are centered
     */
    ModelNode(BlockVector origin) {
        parent = -1;
        this.origin = origin;
        blocks = new UniqueIdObjectMap<MachinaBlock>(initialBlockCapacity);
    }

    /**
     * Creates a new node with the given parent and origin
     * 
     * @param parent
     *            The parent node of this node
     * @param origin
     *            The location around which this node's blocks are centered
     */
    ModelNode(int parent, BlockVector origin) {
        this.parent = parent;
        this.origin = origin;
        blocks = new UniqueIdObjectMap<MachinaBlock>(initialBlockCapacity);
    }
    
    ModelNode(int parent, BlockVector origin, int initialBlockCapacity) {
        this.parent = parent;
        this.origin = origin;
        blocks = new UniqueIdObjectMap<MachinaBlock>(initialBlockCapacity);
    }

    ModelNode(ModelNode other) {
        parent = other.parent;
        origin = other.origin;
        blocks = new UniqueIdObjectMap<MachinaBlock>(other.blocks);
        active = other.active;
        copyChildren(other);
    }
    
    void copyChildren(ModelNode other) {
        if (other.children != null) {
            children = new TIntHashSet(other.children.capacity());
            children.addAll(other.children);
        } else {
            children = null;
        }
    }

    void addChild(int id) {
        if (children == null) {
            children = new TIntHashSet(initialChildCapacity);
        }
        children.add(id);
    }

    void removeChild(int id) {
        if (children == null) {
            return;
        }
        children.remove(id);
    }

    TIntIterator children() {
        return children.iterator();
    }

    void forEachChild(TIntProcedure procedure) {
        if (children == null) {
            return;
        }
        children.forEach(procedure);
    }

    void clearBlocks() {
        blocks.clear(initialBlockCapacity);
    }
    
    UniqueIdObjectIterator<MachinaBlock> blockIterator() {
        return blocks.iterator();
    }

    /*
     * **** Tree methods **** Method to get the root.
     * 
     * Method to get the parent.
     * 
     * Method to get the children. If an iterator, it should support removal of
     * children.
     * 
     * Method to get the child count.
     * 
     * Method to get the depth of this section compared to another section?
     * 
     * Method to create a new child.
     * 
     * Method to delete this section from its parent
     * 
     * Method to move this section to another location in the tree - Tree cycle
     * danger!
     * 
     * Method to merge one section into another. The second will absorb all the
     * blocks of the first, after coordinate translation has taken place.
     */

    /*
     * **** Action methods ****
     * 
     * Method to move this section (in the world, not within the tree)
     * 
     * Method to rotate this section
     */

    /*
     * **** Block methods ****
     * 
     * Method to get the blocks in this section
     * 
     * Method to create a new block in this section
     * 
     * Method to remove a block from this section
     */
    private final static int initialBlockCapacity = 1;
    private final static int initialChildCapacity = 4;
}
