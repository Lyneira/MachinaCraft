package me.lyneira.MachinaCore.machina.model;

import java.util.List;

import me.lyneira.util.collection.IdHolder;

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
class ModelNode implements IdHolder {

    // *** State ***
    int id;
    boolean active = true;
    int[] blocks;
    ModelNode parent;
    List<ModelNode> children;
    
    @Override
    public void setId(int id) {
        this.id = id;
    }
    
    public int getId() {
        return id;
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
