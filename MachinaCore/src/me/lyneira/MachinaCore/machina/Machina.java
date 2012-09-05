package me.lyneira.MachinaCore.machina;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina keeps track of the blocks that it is made up of in two ways: The
 * machina-centric blueprint and the world-centric instance. The blueprint can
 * be set and modified by the plugin controlling this machina, while the
 * instance is managed by this class.
 * 
 * @author Lyneira
 */
public class Machina implements MachinaSection {
    // The Universe this Machina belongs to.
    
    // Active blueprint - A blueprint of the machina as it exists in the world right now.
    
    // Modifiable blueprint - Normally the same as the active blueprint, but can be modified by the controller and updated to move or change the machina in the world. 

    // Instance - A compiled list of the actual in-game blocks of the machina.
}
