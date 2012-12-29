package me.lyneira.MachinaCore;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;

/**
 * An enum of counter-clockwise rotations around 90-degree intervals.
 * 
 * When used for specifying yaw, they represent rotations to the following
 * BlockFace compass directions:<br>
 * ROTATE_0: EAST<br>
 * ROTATE_90: NORTH<br>
 * ROTATE_180: WEST<br>
 * ROTATE_270: SOUTH<br>
 * 
 * @author Lyneira
 */
public enum BlockRotation {
    ROTATE_0, ROTATE_90, ROTATE_180, ROTATE_270;

    private final static BlockFace[] yawFace = { BlockFace.EAST, BlockFace.NORTH, BlockFace.WEST, BlockFace.SOUTH };
    private final static BlockVector[] yawVector = { new BlockVector(yawFace[0]), new BlockVector(yawFace[1]), new BlockVector(yawFace[2]), new BlockVector(yawFace[3]) };
    private final static byte[] yawData = { 0x5, 0x2, 0x4, 0x3 };
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
        return yawFace[this.ordinal()];
    }

    /**
     * Converts a BlockRotation into a BlockVector in the XZ plane.
     * 
     * @return The BlockVector corresponding to this rotation.
     */
    public final BlockVector getYawVector() {
        return yawVector[this.ordinal()];
    }

    /**
     * Returns the proper data value to set the direction of a Wall Sign,
     * Furnace, Dispenser or Chest.
     * 
     * @return
     */
    public final byte getYawData() {
        return yawData[this.ordinal()];
    }

    /**
     * Converts the yaw from a float-based {@link Location} to a BlockRotation
     * 
     * 
     * @param yaw
     * @return
     */
    public static final BlockRotation yawFromLocation(Location location) {
        /*
         * Normalize yaw (which can be negative) to an integer between 0 and 360
         * (exclusive).
         * 
         * TODO If yaw is limited to the -180 to +180 degree range, optimize for
         * that?
         */
        int yaw = ((int) location.getYaw() % 360 + 360) % 360;
        if (yaw < 45) {
            // SOUTH
            return ROTATE_270;
        } else if (yaw < 135) {
            // WEST
            return ROTATE_180;
        } else if (yaw < 225) {
            // NORTH
            return ROTATE_90;
        } else if (yaw < 315) {
            // EAST
            return ROTATE_0;
        } else {
            // SOUTH
            return ROTATE_270;
        }
    }

    public static final BlockRotation yawFromBlockFace(BlockFace face) throws Exception {
        switch (face) {
        case EAST:
            return ROTATE_0;
        case NORTH:
            return ROTATE_90;
        case WEST:
            return ROTATE_180;
        case SOUTH:
            return ROTATE_270;
        default:
            throw new Exception("Invalid BlockFace given to yawFromBlockFace, must be one of: EAST, NORTH, WEST, SOUTH");
        }
    }
}
