package me.lyneira.MachinaCore.machina;

import org.bukkit.World;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.model.ModelTree;

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
     * Performs any dynamic detection for the detected base model, adding those
     * blocks to it. Performs needed permission checks for the player.
     * Configures a MachinaController and returns it if detection was
     * successful, null otherwise. If successful, the model that was passed in
     * will be used to initialize the corresponding machina and it will be
     * linked to the returned MachinaController.
     * 
     * @param baseModel
     *            The base model that was detected
     * @param player
     *            The player that initiated the detection
     * @param world
     *            The world the machina is being detected in
     * @param rotation
     *            The direction detected for the base model. May not be the only
     *            valid direction if the trigger block is also the origin and
     *            the machina's base model has symmetry.
     * @param origin
     *            The origin point for the detected base model
     * @return A MachinaController if successful, null otherwise.
     */
    public MachinaController detect(ModelTree model, Player player, World world, BlockRotation rotation, BlockVector origin);
}
