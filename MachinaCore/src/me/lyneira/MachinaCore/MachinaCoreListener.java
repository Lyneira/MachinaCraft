package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerInteract(PlayerInteractEvent event) {
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
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void chunkUnload(ChunkUnloadEvent event) {
        MachinaRunner.notifyChunkUnload(event.getChunk());
    }

    /**
     * Collects the cancelled result from a pretend blockplace event by
     * EventSimulator and cancels the event afterward.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent event) {
        if (event == EventSimulator.pretendEvent) {
            EventSimulator.pretendEventCancelled = event.isCancelled();
            EventSimulator.pretendEvent = null;
            event.setCancelled(true);
        }
    }

    /**
     * Collects the cancelled result from a pretend blockplace event by
     * EventSimulator and cancels the event afterward.
     * 
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent event) {
        if (event == EventSimulator.pretendEvent) {
            EventSimulator.pretendEventCancelled = event.isCancelled();
            EventSimulator.pretendEvent = null;
            event.setCancelled(true);
        }
    }
}
