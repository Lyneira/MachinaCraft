package me.lyneira.MachinaCore;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

/**
 * An enum of counter-clockwise rotations around 90-degree intervals.
 * 
 * When used for specifying yaw, they represent rotations to the following
 * BlockFace compass directions:<br>
 * ROTATE_0: SOUTH<br>
 * ROTATE_90: EAST<br>
 * ROTATE_180: NORTH<br>
 * ROTATE_270: WEST<br>
 * 
 * @author Lyneira
 */
public enum BlockRotation {
    ROTATE_0, ROTATE_90, ROTATE_180, ROTATE_270;

    private final static BlockRotation[] byOrdinal = BlockRotation.values();

    /**
     * Returns the opposite rotation.
     */
    public final BlockRotation getOpposite() {
        return byOrdinal[(this.ordinal() + 2) % 4];
    }

    /**
     * Returns the rotation left of this one.
     */
    public final BlockRotation getLeft() {
        return byOrdinal[(this.ordinal() + 1) % 4];
    }

    /**
     * Returns the rotation right of this one.
     */
    public final BlockRotation getRight() {
        return byOrdinal[(this.ordinal() + 3) % 4];
    }

    /**
     * Adds the given {@link BlockRotation} to this one and returns the result.
     * 
     * @param other
     *            The rotation to add.
     * @return The new rotation
     */
    public final BlockRotation add(final BlockRotation other) {
        return byOrdinal[(this.ordinal() + other.ordinal()) % 4];
    }

    /**
     * Subtracts the given {@link BlockRotation} from this one and returns the
     * result.
     * 
     * @param other
     *            The rotation to add.
     * @return The new rotation
     */
    public final BlockRotation subtract(final BlockRotation other) {
        return byOrdinal[(this.ordinal() - other.ordinal() + 4) % 4];
    }

    /**
     * Converts a BlockRotation into a BlockFace in the XZ plane.
     * 
     * @return The BlockFace corresponding to this rotation.
     */
    public final BlockFace getYawFace() {
        if (this == ROTATE_0) {
            return BlockFace.SOUTH;
        } else if (this == ROTATE_90) {
            return BlockFace.EAST;
        } else if (this == ROTATE_180) {
            return BlockFace.NORTH;
        } else { // ROTATE_270
            return BlockFace.WEST;
        }
    }

    /**
     * Converts the yaw from a float-based {@link Location} to a BlockRotation
     * 
     * @param yaw
     * @return
     */
    public static final BlockRotation yawFromLocation(Location location) {
        // Normalize yaw (which can be negative) to an integer between 0 and 360 (exclusive).
        int yaw = ((int)location.getYaw() % 360 + 360) % 360;
        if (yaw < 45) {
            // WEST
            return ROTATE_270;
        } else if (yaw < 135) {
            // NORTH
            return ROTATE_180;
        } else if (yaw < 225) {
            // EAST
            return ROTATE_90;
        } else if (yaw < 315) {
            // SOUTH
            return ROTATE_0;
        } else {
            // WEST
            return ROTATE_270;
        }
    }

    public static final BlockRotation yawFromBlockFace(BlockFace face) throws Exception {
        if (face == BlockFace.SOUTH)
            return ROTATE_0;
        else if (face == BlockFace.EAST)
            return ROTATE_90;
        else if (face == BlockFace.NORTH)
            return ROTATE_180;
        else if (face == BlockFace.WEST)
            return ROTATE_270;
        else
            throw new Exception("Invalid BlockFace given to yawFromBlockFace, must be one of: SOUTH, EAST, NORTH, WEST");
    }
}
