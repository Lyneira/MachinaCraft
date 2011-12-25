package me.lyneira.MachinaCore;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a blueprint for a Machina.
 * 
 * @author Lyneira
 */
public interface MachinaBlueprint {
    /**
     * Function that is called to detect whether a machina exists at the given
     * BlockLocation, with a lever at the given BlockFace. This function may
     * assume that the existence of the lever has been verified.
     * 
     * @param leverAnchor
     *            The location from which to start searching
     * @param leverFace
     *            The face on the anchor to which the lever is attached
     * @return A new Machina if successful, null otherwise.
     */
    public Machina detect(final Player player, final BlockLocation anchor, final BlockFace leverFace, final ItemStack itemInHand);
}
