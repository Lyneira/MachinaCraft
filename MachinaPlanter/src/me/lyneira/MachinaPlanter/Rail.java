package me.lyneira.MachinaPlanter;

import java.util.ArrayDeque;
import java.util.Deque;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;

import me.lyneira.MachinaCore.BlockLocation;

/**
 * Represents the rail that the planter moves along.
 * 
 * @author Lyneira
 * 
 */
class Rail {
    private final BlockFace direction;
    private final Deque<RailBlock> rail;
    private final MovingRail movingRail;

    // Only this class can instantiate itself
    private Rail(BlockFace direction, Deque<RailBlock> rail, MovingRail movingRail) {
        this.direction = direction;
        this.rail = rail;
        this.movingRail = movingRail;
    }

    final boolean verify() {
        for (RailBlock i : rail) {
            if (!i.location.checkType(i.material))
                return false;
        }
        return movingRail.verify();
    }

    final boolean activate() {
        return movingRail.activate();
    }

    final boolean deactivate() {
        return movingRail.deactivate();
    }

    /**
     * Returns the current tile for planting below the head.
     * 
     * @return The block two spaces below the head. If the head was not active
     *         this will return null.
     */
    final BlockLocation currentTile() {
        return movingRail.currentTile();
    }
    
    /**
     * Attempts to retract the head back toward parking position.
     * @return True if the head moved, false if there was a collision or.
     */
    final HeadNextResult retractHead() {
        return movingRail.retractHead();
    }

    /**
     * Attempts to move the head to the next tile for planting. If the head has
     * reached the end of the moving rail, the moving rail will advance.
     * 
     * @return True if the head reached the next tile for planting, false if
     *         there was a collision or the rail could not be extended further.
     */
    boolean nextTile() {
        if (getRowType() == RailType.SKIP)
            return extend();
        switch (movingRail.nextTile()) {
        case OK:
            return true;
        case RAIL_END:
            return extend();
        default:
            return false;
        }
    }
    
    boolean isHeadMovingBackward() {
        return movingRail.isHeadMovingBackward();
    }

    /**
     * Attempts to extend the rail and advance the moving rail.
     * 
     * @return True if successful, false if the rail could not be extended or
     *         there was a collision while moving.
     */
    private boolean extend() {
        if (rail.size() >= Planter.maxLength)
            return false;

        if (!add(rail, rail.peek().location.getRelative(direction)))
            return false;

        if (movingRail.move(direction))
            return true;

        rail.pop();
        return false;
    }

    /**
     * Attempts to retract the moving rail.
     * 
     * @return True if successful, false if there was a collision or the rail
     *         has reached its base point.
     */
    boolean retract() {
        if (rail.size() == 1)
            return false;

        rail.pop();

        if (movingRail.move(direction.getOppositeFace()))
            return true;

        add(rail, rail.peek().location.getRelative(direction));
        return false;
    }

    private static class RailBlock {
        final BlockLocation location;
        final Material material;
        final RailType type;

        private RailBlock(BlockLocation location, Material material) {
            this.location = location;
            this.material = material;
            if (material == Blueprint.railPlantMaterial) {
                type = RailType.PLANT;
            } else if (material == Blueprint.railTillMaterial) {
                type = RailType.TILL;
            } else { //Blueprint.railSkipMaterial
                type = RailType.SKIP;
            }
        }
    }
    
    /**
     * @return True if the rail is currently at a planting row.
     */
    final RailType getRowType() {
        return rail.peek().type;
    }

    /**
     * Attempts to add an element to the rail length
     */
    private static final boolean add(Deque<RailBlock> rail, BlockLocation location) {
        Material material = location.getType();
        if (material == Blueprint.railTillMaterial || material == Blueprint.railPlantMaterial || material == Blueprint.railSkipMaterial) {
            rail.push(new RailBlock(location, material));
            return true;
        }
        return false;
    }

    /**
     * Attempts to detect a planter rail from the given anchor and yaw. If a
     * valid rail could not be detected, returns null.
     * 
     * @param anchor
     * @param direction
     * @return The newly detected rail, or null if there is no valid rail.
     */
    static Rail detect(BlockLocation anchor, BlockFace direction, BlockFace movingRailDirection) {
        final Deque<RailBlock> rail = new ArrayDeque<RailBlock>(Planter.maxLength);
        if (!add(rail, anchor))
            return null;

        MovingRail movingRail = null;
        BlockLocation lastRailBlock = anchor;
        // Try to extend the rail one block at a time to see if the moving head
        // might be further up
        for (int i = 0; i < Planter.maxLength; i++) {
            movingRail = MovingRail.detect(lastRailBlock.getRelative(BlockFace.UP), movingRailDirection);
            if (movingRail != null)
                break;
            lastRailBlock = lastRailBlock.getRelative(direction);
            if (!add(rail, lastRailBlock))
                break;
        }
        if (movingRail == null)
            return null;

        return new Rail(direction, rail, movingRail);
    }
}
