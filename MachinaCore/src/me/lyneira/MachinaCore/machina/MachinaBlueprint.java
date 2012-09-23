package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.Universe;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Represents the blueprint of a machina. The blueprint is used to detect and
 * create a new machina when the player rightclicks the appropriate tool on a
 * specified trigger block. It can be supplied with optional extensions and a
 * detector class that identifies these extensions and performs other
 * detect-time configuration.
 * 
 * @author Lyneira
 */
public class MachinaBlueprint {
    /*
     * Base model - The model that should be scanned for when detection is
     * triggered. The base model is the part of the machina that remains present
     * and static in all forms and variations of the machina. For best
     * performance, the trigger block should be in the base model but it is
     * possible to program the trigger block to be in a dynamic part of the
     * machina.
     * 
     * The base model can be predefined in code in a compact way, but it can
     * also be loaded from configuration files.
     */

    /*
     * Extensions - Extensions are the same as a base model but the location and
     * presence of these is variable. They are not directly detected by
     * MachinaCore, instead the Detector can trigger detection of an Extension
     * at a specific location. If successful, the extension will be added to the
     * model of the machina being constructed. The offset is automatically
     * calculated by MachinaCore. A single extension can be detected multiple
     * times at different locations.
     * 
     * They can be predefined same as the base model. Coordinates of the
     * extension's blocks are specific to this extension.
     */

    /*
     * Detector - Callback class that will be run when the base model has been
     * detected. It may detect extensions on the base model and add them to the
     * machina being constructed. The detector will receive the direction used
     * by MachinaCore to detect the base model, but in some cases (like a
     * symmetric machina) this may not be the correct direction. It may
     * therefore correct the machina's direction if required.
     */
    
    /**
     * 
     * @return
     */
    public Machina detect(Universe universe, Player player, Block block) {
        // TODO
        return null;
    }
}
