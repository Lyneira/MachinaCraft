package me.lyneira.MachinaCore.machina;

/**
 * Represents a part (or all) of the blocks of a machina. A machina can consist
 * of multiple sections that form a tree structure, with the machina itself
 * being the root. A section can move in relation to its parent, enabling a
 * machina to change shape. A section can be enabled (active in the world) or
 * disabled (not active). While it is disabled, any inventories belonging to
 * blocks in it are stored.
 * 
 * @author Lyneira
 */
public interface MachinaSection {

    // **** Tree methods ****

    // Method to get the root.

    // Method to get the parent.

    // Method to get the children. If an iterator, it should support removal of
    // children.

    // Method to get the child count.

    // Method to get the depth of this section compared to another section?

    // Method to create a new child.

    // Method to delete this section from its parent

    // Method to move this section to another location in the tree - Tree cycle
    // danger!

    // **** Action methods ****

    // Method to move this section (in the world, not within the tree)

    // Method to rotate this section

    // **** Block methods ****

    // Method to get the blocks in this section

    // Method to create a new block in this section

    // Method to remove a block from this section
}
