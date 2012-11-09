package me.lyneira.MachinaCore.machina.model;

import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.hash.TIntHashSet;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
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
    BlockVector origin;
    boolean active = true;
    UniqueIdObjectMap<MachinaBlock> blocks = new UniqueIdObjectMap<MachinaBlock>(1);
    final int parent;
    private TIntHashSet children = null;

    /**
     * Creates a new root modelnode.
     * @param origin
     */
    ModelNode(BlockVector origin) {
        parent = -1;
        this.origin = origin;
    }

    ModelNode(int parent, BlockVector origin) {
        this.parent = parent;
        this.origin = origin;
    }

    void addChild(int id) {
        if (children == null) {
            children = new TIntHashSet(4);
        }
        children.add(id);
    }

    void removeChild(int id) {
        if (children == null) {
            return;
        }
        children.remove(id);
    }

    void forEachChild(TIntProcedure procedure) {
        if (children == null) {
            return;
        }
        children.forEach(procedure);
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

}
