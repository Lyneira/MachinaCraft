package me.lyneira.MachinaCore.machina;

import org.bukkit.World;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.model.ConstructionModel;

/**
 * Interface for a class that can detect dynamic portions of a machina being
 * constructed and perform other needed configuration. The detector is also
 * expected to perform any necessary permission checks on the player that
 * initiated the detection.
 * 
 * @author Lyneira
 * 
 */
public interface MachinaDetector {

    /**
     * Called when the detector is registered with MachinaCore. The returned
     * blueprint will be used to detect a machina's base model before being
     * handed to the detector.
     * 
     * @return The base blueprint for this detector.
     */
    public MachinaBlueprint getBlueprint();

    /**
     * Performs any dynamic detection for the detected base model, adding those
     * blocks to it. Performs needed permission checks for the player.
     * Configures a MachinaController and returns it if detection was
     * successful, null otherwise. If successful, the model that was passed in
     * will be used to initialize the corresponding machina and it will be
     * linked to the returned MachinaController.
     * 
     * @param model
     *            The base model that was detected
     * @param player
     *            The player that initiated the detection. Can be null if
     *            detection was triggered by redstone or other plugin means.
     * @param world
     *            The world the machina is being detected in
     * @param yaw
     *            The direction detected for the base model. May not be the only
     *            valid direction if the trigger block is also the origin and
     *            the machina's base model has symmetry.
     * @param origin
     *            The origin point for the detected base model
     * @return A MachinaController if successful, null otherwise.
     */
    public MachinaController detect(ConstructionModel model, Player player, World world, BlockRotation yaw, BlockVector origin);
}
