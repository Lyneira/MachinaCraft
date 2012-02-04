package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.material.Lever;

/**
 * Class that listens for interaction with a lever.
 * 
 * @author Lyneira
 */
final class MachinaCoreListener implements Listener {
    private final MachinaCore plugin;

    MachinaCoreListener(final MachinaCore plugin) {
        this.plugin = plugin;
    }

    /**
     * Detects whether the player right-clicked on a lever and starts machina
     * detection.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void playerInteract(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;

        Block block = event.getClickedBlock();
        if (block.getType() != Material.LEVER)
            return;

        Lever lever = (Lever) block.getState().getData();
        BlockFace attachedFace = lever.getAttachedFace();
        if (attachedFace == null) {
            MachinaCore.log.warning("MachinaCore: Lever activated by " + event.getPlayer().getName() + " seems to be attached to nothing?");
            return;
        }

        Block attachedTo = block.getRelative(attachedFace);
        plugin.onLever(event.getPlayer(), new BlockLocation(attachedTo), attachedFace.getOppositeFace(), event.getItem());
    }
    
    /**
     * Notifies MachinaRunners of a chunk unload.
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void chunkUnload(ChunkUnloadEvent event) {
        if (!event.isCancelled()) {
            MachinaRunner.notifyChunkUnload(event.getChunk());
        }
    }
}
