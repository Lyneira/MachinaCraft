package me.lyneira.MachinaCore;

import org.bukkit.World;
import org.bukkit.block.BlockFace;

/**
 * Int-based Vector class for use with blocks.
 * 
 * @author Lyneira
 */
public final class BlockVector {
    private final int x;
    private final int y;
    private final int z;

    /**
     * Constructs a BlockVector from the given x, y and z values.
     * 
     * @param x
     *            The length in the x direction
     * @param y
     *            The length in the y direction
     * @param z
     *            The length in the z direction
     */
    public BlockVector(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a BlockVector from the given BlockFace
     * 
     * @param face
     *            The BlockFace to copy
     */
    public BlockVector(final BlockFace face) {
        x = face.getModX();
        y = face.getModY();
        z = face.getModZ();
    }

    /**
     * Constructs a BlockVector from the given BlockVector
     * 
     * @param vector
     *            The BlockVector to copy
     */
    public BlockVector(final BlockVector vector) {
        x = vector.x;
        y = vector.y;
        z = vector.z;
    }

    /**
     * Adds the given BlockFace to this BlockVector
     * 
     * @param face
     *            The BlockFace to add
     * @return A new BlockVector with the BlockFace added
     */
    public final BlockVector add(final BlockFace face) {
        return new BlockVector(x + face.getModX(), y + face.getModY(), z + face.getModZ());
    }

    /**
     * Adds the given {@link BlockVector} to this {@link BlockVector}
     * 
     * @param vector
     *            The vector to add
     * @return A new {@link BlockVector} with the given vector added
     */
    public final BlockVector add(final BlockVector vector) {
        return new BlockVector(x + vector.x, y + vector.y, z + vector.z);
    }

    /**
     * Adds the given {@link BlockVector} n times to this {@link BlockVector}
     * 
     * @param vector
     *            The vector to add
     * @param n
     *            The number of times to apply this vector.
     * @return A new {@link BlockVector} with the given vector added
     */
    public final BlockVector add(final BlockVector vector, int n) {
        return new BlockVector(x + vector.x * n, y + vector.y * n, z + vector.z * n);
    }

    /**
     * Applies the BlockVector to the given World-coordinates and returns a new
     * BlockLocation
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @return The BlockLocation with this vector applied
     */
    public final BlockLocation apply(final World world, final int x, final int y, final int z) {
        return new BlockLocation(world, x + this.x, y + this.y, z + this.z);
    }

    /**
     * Applies the BlockVector times n to the given World-coordinates and
     * returns a new BlockLocation
     * 
     * @param world
     * @param x
     * @param y
     * @param z
     * @param n
     * @return The BlockLocation with this vector applied
     */
    public final BlockLocation apply(final World world, final int x, final int y, final int z, final int n) {
        return new BlockLocation(world, x + this.x * n, y + this.y * n, z + this.z * n);
    }

    /**
     * Returns a BlockVector rotated by the given BlockRotation around the Y
     * axis.
     * 
     * @param rotate
     *            The amount to rotate by
     * @return A new rotated BlockVector
     */
    public final BlockVector rotated(final BlockRotation rotate) {
        switch (rotate) {
        case ROTATE_0:
            return new BlockVector(x, y, z);
        case ROTATE_90:
            return new BlockVector(z, y, -x);
        case ROTATE_180:
            return new BlockVector(-x, y, -z);
        case ROTATE_270:
        default:
            return new BlockVector(-z, y, x);
        }
    }

    @Override
    public int hashCode() {
        return y << 24 ^ x ^ z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BlockVector other = (BlockVector) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z);
    }
}
