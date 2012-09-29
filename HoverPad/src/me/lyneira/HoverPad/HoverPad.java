package me.lyneira.HoverPad;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Lever;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * HoverPad operation class
 * 
 * @author Lyneira
 * @author Nividica
 */
class HoverPad extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static int baseMoveDelay = 8;
    private static int halfMoveDelay = baseMoveDelay / 2;
    private int moveDelay = baseMoveDelay;

    // Constructor
    HoverPad(MovableBlueprint blueprint, List<Integer> moduleIndices, BlockRotation yaw, Player player) {
        super(blueprint, moduleIndices, yaw, player);
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        // If the player has gone offline, stop the machina
        if (!player.isOnline())
            return null;

        // Get the location of the player
        Location location = player.getLocation();

        // When true decreases the moveDelay
        boolean speedUp = false;

        // The anchor's Y position relative to the players
        int offsetY = 1;

        // Is the player sneaking?
        if (player.isSneaking()) {

            // Increase the Y offset (Inversely decreasing the anchor position)
            offsetY = 2;

            // Also decrease the delay
            speedUp = true;

        } else if (player.isSprinting()) {

            // If we are sprinting decrease the delay
            speedUp = true;
        }

        // Decrease the moveDelay if needed.
        if (speedUp) {
            moveDelay = halfMoveDelay;
        } else {
            moveDelay = baseMoveDelay;
        }

        // Create a new potential anchor position
        BlockLocation newAnchor = new BlockLocation(location.getWorld(), location.getBlockX(), location.getBlockY() - offsetY, location.getBlockZ());

        // Did the player move?
        if (!newAnchor.equals(anchor)) {

            // The player has moved, can the hoverpad follow?
            if (doMove(anchor, newAnchor)) {

                // We can follow
                return new HeartBeatEvent(moveDelay, newAnchor);

            } else {

                // We can not follow, are we trying to land?
                if (player.isSneaking()) {

                    // We are landing. Stop the pad. (Don't try gap closer)
                    return stopPad(anchor);
                }

                // We are not landing, so we should try to move 1 in the
                // direction the player was going ( gap closer )
                Block anchorBlock = anchor.getBlock();
                int X = stepValueToward(anchorBlock.getX(), location.getBlockX());
                int Y = anchorBlock.getY(); // Not sure if Y is really needed
                int Z = stepValueToward(anchorBlock.getZ(), location.getBlockZ());

                // Assign the newAnchor
                newAnchor = new BlockLocation(location.getWorld(), X, Y, Z);

                // Attempt to move in the direction of the player
                if (doMove(anchor, newAnchor)) {

                    // Gap closed. Stop the pad.
                    return stopPad(newAnchor);

                } else {

                    // All attempts to move failed. Stop the pad.
                    return stopPad(anchor);
                }

            }

        } else {

            // Check again next heartbeat
            return new HeartBeatEvent(moveDelay);
        }
    }

    /**
     * Moves a value toward another by 1
     * 
     * @param fromValue
     *            The base value
     * @param toValue
     *            The new value
     * @return fromValue+1: Ascending/Forward, fromValue: Still, fromValue-1:
     *         Descending/Backward
     */
    private int stepValueToward(int fromValue, int toValue) {
        int returnValue = fromValue;

        if (fromValue > toValue) {
            // Descending
            returnValue = fromValue - 1;
        } else if (fromValue < toValue) {
            // Ascending
            returnValue = fromValue + 1;
        }

        return returnValue;

    }

    /**
     * Turns the lever attached to the anchor off and returns null
     * 
     * @param anchor
     *            The current location of the anchor
     * @return null
     */
    private HeartBeatEvent stopPad(BlockLocation anchor) {
        BlockState leverBlockState;
        Block leverBlock;

        // Get the lever block
        leverBlock = anchor.getRelative(BlockFace.UP).getBlock();

        // Make sure the lever is still there
        if (leverBlock.getType() == Material.LEVER) {

            // Get the block state
            leverBlockState = leverBlock.getState();

            // Set the power state to false
            ((Lever) leverBlockState.getData()).setPowered(false);

            // Update the state
            leverBlockState.update();

        }

        // Return null, stopping the pad if used with 'return stopPad( anchor )'
        // in the heartbeat event
        return null;
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        // Check permissions
        if (player.hasPermission("hoverpad")) {

            // Player does not have permission
            return false;

        }

        return true;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
    }

    /**
     * Moves the pad to the player's position
     * 
     * @param anchor
     * @param newAnchor
     * @return True: The HoverPad moved, False: The HoverPad could not move
     */
    private boolean doMove(BlockLocation anchor, BlockLocation newAnchor) {
        // Will we collide with anything if we move?
        if (detectCollisionTeleport(anchor, newAnchor)) {
            // Collision detected, do not move
            return false;
        }

        // No collision detected, move the pad
        teleport(anchor, newAnchor);

        return true;
    }
    
    static void loadConfiguration(ConfigurationSection configuration) {
        baseMoveDelay = Math.max(configuration.getInt("move-delay", baseMoveDelay), 1);
        halfMoveDelay = Math.max(baseMoveDelay/2, 1);
    }

}
