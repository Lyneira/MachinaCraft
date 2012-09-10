package me.lyneira.MachinaCore.machina;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina keeps track of the blocks that it is made up of in two ways: The
 * machina-centric model and the world-centric instance. The model can be set
 * and modified by the plugin controlling this machina, while the instance is
 * managed by this class.
 * 
 * @author Lyneira
 */
public class Machina implements MachinaSection {
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
     * Instance - A compiled list of the actual in-game blocks of the machina.
     * These are generated from the active model by translating their
     * coordinates into the game world.
     */
}
