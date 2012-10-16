package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.Universe;
import me.lyneira.MachinaCore.machina.model.ModelTree;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina consists of a model with a tree structure. The model represents the
 * machina as it exists in the game world right now and cannot be modified
 * directly. When a request for modification is made, a shadow copy of
 * that node and all the ones below it is made, which the controller can edit.
 * One or more shadow copies existing for (parts of) the model can be manifested
 * by updating the machina.
 * <p/>
 * 
 * A machina keeps track of the blocks that it is made up of in
 * two ways: The machina-centric model and the world-centric instance. The model
 * can be modified by the plugin controlling this machina, while the
 * instance is managed by the machina's universe.
 * 
 * @author Lyneira
 */
public final class Machina {
    public final Universe universe;
    
    private ModelTree model;

    Machina(Universe universe) {
        this.universe = universe;
    }

    // The Universe this Machina belongs to.

    /*
     * Active model - A model of the machina as it exists in the world right
     * now. Coordinates are machina-centric.
     */

    /*
     * Editable model - Normally the same as the active model, but can be
     * modified by the controller and updated to move or change the machina in
     * the world.
     */

    /*
     * **** Methods ****
     * 
     * Method that returns an iterator over all the blocks resulting from the
     * current editable model.
     */

    /**
     * Returns a new array containing all the machina's blocks in absolute
     * positions. The array has no null elements, and each element is unique.
     * 
     * @return An array of the machina's blocks
     */
    public MachinaBlock[] instance() {
        // TODO
        return null;
    }

    /**
     * Returns a MachinaUpdate holding all necessary data to perform an update
     * to the machina. The update contains only data about modified parts of the
     * machina's model.
     * 
     * <li>Arrays are all the same size.
     * 
     * <li>The block arrays have no null elements.
     * 
     * <li>Each element is unique.
     * 
     * <li>The inventories array only contains non-null elements for the blocks
     * that need to have their inventory overwritten, and the arrays therein are
     * the correct size for the type of inventory being overwritten.
     * 
     * 
     * Plugin developers should not need to use this method.
     * 
     * @return An update for the machina
     */
    public MachinaUpdate createUpdate() {
        // TODO
        return new MachinaUpdate(null, null, null, null);
    }
    
    private int idCounter = 0;

    int nextNodeId() {
        return idCounter++;
    }
}
