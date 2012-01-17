package me.lyneira.HoverPad;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * HoverPad operation class
 * 
 * @author Lyneira
 */
class HoverPad extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static final int moveDelay = 10;

    protected HoverPad(MovableBlueprint blueprint, List<Integer> moduleIndices, BlockRotation yaw, Player player) {
        super(blueprint, moduleIndices, yaw, player);
        HoverPadPlugin.log.info("Activating hoverpad.");
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (!player.isOnline())
            return null;
        
        Location location = player.getLocation();
        BlockLocation newAnchor = new BlockLocation(location.getWorld(), location.getBlockX(), location.getBlockY()-1, location.getBlockZ());
        if (!newAnchor.equals(anchor)) {
            if (doMove(anchor, newAnchor)) {
                return new HeartBeatEvent(moveDelay, newAnchor);
            }
        } else {
            return new HeartBeatEvent(moveDelay);
        }

        return null;
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        if (player.hasPermission("hoverpad"))
            return false;
        return true;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
        HoverPadPlugin.log.info("Deactivating hoverpad.");
    }

    /**
     * Moves the pad to the player's position
     * @param anchor
     * @param newAnchor
     * @return
     */
    private boolean doMove(BlockLocation anchor, BlockLocation newAnchor) {
        if (detectCollisionTeleport(anchor, newAnchor)) {
            return false;
        }
        
        teleport(anchor, newAnchor);
        return true;
    }
}
