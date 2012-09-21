package me.lyneira.MachinaCore.machina;

/**
 * Represents a part (or all) of the blocks of a machina. A machina can consist
 * of multiple sections that form a tree structure, with the machina itself
 * being the root. A section can move in relation to its parent, enabling a
 * machina to change shape. A section can be enabled (active in the world) or
 * disabled (not active, its blocks are not present but they would appear when activated).
 * 
 * @author Lyneira
 */
public interface MachinaSection {

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
