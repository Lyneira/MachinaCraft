package me.lyneira.MachinaCore;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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

    /**
     * Simulates a rightclick event on the target block. Returns true if the rightclick interaction is allowed.
     * @param target The target location to click on.
     * @param player The player to simulate for
     * @param clickedFace The blockface to click on
     * @return True if the player may interact with this block
     */
    public static boolean blockRightClick(BlockLocation target, Player player, BlockFace clickedFace) {
        PlayerInteractEvent event = new PlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, null, target.getBlock(), clickedFace);
        MachinaCore.pluginManager.callEvent(event);

        if (event.isCancelled())
            return false;
        return true;
    }

    /**
     * Function for testing whether one or more inventories inside a machina are protected from the player.
     * @param yaw The direction of the machina
     * @param player The player to simulate for
     * @param anchor The anchor of the machina
     * @param blocks One or more BlueprintBlocks belonging to the machina
     * @return False if and only if the player may interact with and break all the given blocks. True otherwise. 
     */
    public static boolean inventoryProtected(BlockRotation yaw, Player player, BlockLocation anchor, BlueprintBlock... blocks) {
        BlockFace clickedFace = yaw.getOpposite().getYawFace();
        for (BlueprintBlock b : blocks) {
            BlockLocation target = anchor.getRelative(b.vector(yaw));
            if (!(blockRightClick(target, player, clickedFace) && blockBreak(target, player)))
                return true;
        }
        return false;
    }
    
    /**
     * Tests whether the inventory in this block is protected from the player.
     * @param player The player to simulate for
     * @param target The target block
     * @return False if and only if the player may interact with and break the given block. True otherwise.
     */
    public static boolean inventoryProtected(Player player, BlockLocation target) {
        if (!(blockRightClick(target, player, BlockFace.NORTH) && blockBreak(target, player)))
            return true;
        return false;
    }
}
