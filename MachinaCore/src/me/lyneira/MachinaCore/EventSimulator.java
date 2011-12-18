package me.lyneira.MachinaCore;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 * Class that simulates events.
 * 
 * @author Lyneira
 */
public class EventSimulator {
    private EventSimulator() {
        // Cannot instantiate
    }

    /**
     * Simulates a block place event on behalf of a player. Returns true if the
     * player could build the new block.
     * 
     * @param target
     *            The target location to place at
     * @param typeId
     *            The typeId of the block to place
     * @param placedAgainst
     *            The block that it will be placed against
     * @param player
     *            The player to simulate for
     * @return True if the player may place a block at the location
     */
    public static boolean blockPlace(BlockLocation target, int typeId, BlockLocation placedAgainst, Player player) {
        Block placedBlock = target.getBlock();
        BlockState replacedBlockState = placedBlock.getState();
        replacedBlockState.setTypeId(typeId);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(placedBlock, replacedBlockState, placedAgainst.getBlock(), null, player, true);
        MachinaCore.pluginManager.callEvent(placeEvent);

        if (placeEvent.isCancelled())
            return false;
        return true;
    }

    /**
     * Simulates a block break event
     * 
     * @param target
     *            The target location to break at
     * @param player
     *            The player to simulate for
     * @return True if the player may break a block at the location
     */
    public static boolean blockBreak(BlockLocation target, Player player) {
        Block block = target.getBlock();
        BlockBreakEvent breakEvent = new BlockBreakEvent(block, player);
        MachinaCore.pluginManager.callEvent(breakEvent);
        if (breakEvent.isCancelled())
            return false;
        return true;
    }

    /**
     * Function for a moving machina to test whether it's allowed to move to a
     * new location by protection plugins. Returns true if the player could
     * build (and break) the new block.
     * 
     * This function is zero-sum for the benefit of block logging plugins. In
     * other words, it both places and removes the block so that rollback events
     * don't leave a block trail for moved machina.
     * 
     * @param target
     *            The target location to place at
     * @param typeId
     *            The typeId of the block to place
     * @param placedAgainst
     *            The block that it will be placed against
     * @param player
     *            The player to simulate for
     * @return True if the player may place a block at the location
     */
    public static boolean blockPlaceNoTrace(BlockLocation target, int typeId, BlockLocation placedAgainst, Player player) {
        Block placedBlock = target.getBlock();
        BlockState replacedBlockState = placedBlock.getState();
        replacedBlockState.setTypeId(typeId);

        BlockPlaceEvent placeEvent = new BlockPlaceEvent(placedBlock, replacedBlockState, placedAgainst.getBlock(), null, player, true);
        MachinaCore.pluginManager.callEvent(placeEvent);

        if (placeEvent.isCancelled())
            return false;

        BlockBreakEvent breakEvent = new BlockBreakEvent(placedBlock, player);
        MachinaCore.pluginManager.callEvent(breakEvent);
        return true;
    }
}
