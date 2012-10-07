package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.Universe;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina consists of two models with a tree structure. The active model
 * represents the machina as it exists in the game world right now and cannot be
 * modified. The editable model can be changed by its controller. keeps track of the blocks that it is made up of in two ways: The
 * machina-centric model and the world-centric instance. The model can be set
 * and modified by the plugin controlling this machina, while the instance is
 * managed by this class.
 * 
 * @author Lyneira
 */
public final class Machina {
    public final Universe universe;

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
     *  **** Methods ****
     * 
     * Method that returns an iterator over all the blocks resulting from the
     * current editable model.
     */
}
