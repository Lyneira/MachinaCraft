package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.machina.model.MachinaModel;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina consists of a model with a tree structure. The model represents the
 * machina as it exists in the game world right now and cannot be modified
 * directly. Modifications will be held When a modification is made, ta shadow copy of
 * the relevant portion of the model is made  all the ones below it is made, which the controller can edit.
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
    public final MachinaController controller;
    
    private final MachinaModel model;

    Machina(Universe universe, MachinaModel model, MachinaController controller) {
        this.universe = universe;
        this.model = model;
        this.controller = controller;
    }

    /**
     * Returns the array containing all the machina's blocks in absolute
     * positions. The array has no null elements, and each element is unique.
     * 
     * @return Array of the machina's blocks
     */
    MachinaBlock[] instance() {
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
        return new MachinaUpdate(null, null, null);
    }
}
