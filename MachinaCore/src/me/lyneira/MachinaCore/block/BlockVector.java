package me.lyneira.MachinaCore.block;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Class for storing immutable block-based vectors. Usable as both a vector and
 * coordinates. Used as the key for CoordinateMap3D.
 * 
 * @author Lyneira
 */
public class BlockVector {
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
    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Constructs a new BlockVector identical to the given vector.
     * 
     * @param vector The vector to copy
     */
    public BlockVector(BlockVector vector) {
        this.x = vector.x;
        this.y = vector.y;
        this.z = vector.z;
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
     * Constructs a BlockVector from the given Block
     * 
     * @param face
     *            The BlockFace to copy
     */
    public BlockVector(final Block block) {
        x = block.getX();
        y = block.getY();
        z = block.getZ();
    }

    /**
     * Returns the {@link Block} that this BlockVector represents in the given
     * World.
     * 
     * @param world
     *            The world to get the block in
     * @return The block equivalent to this BlockVector.
     */
    public final Block getBlock(World world) {
        return world.getBlockAt(x, y, z);
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
     * Adds the given coordinates x, y, z to this {@link BlockVector}
     * 
     * @param x
     *            The x coordinate
     * @param y
     *            The y coordinate
     * @param z
     *            The z coordinate
     * @return A new {@link BlockVector} with the given coordinates added
     */
    public final BlockVector add(final int x, final int y, final int z) {
        return new BlockVector(x + this.x, y + this.y, z + this.z);
    }

    /**
     * Substract the given {@link BlockVector} from this one. If this vector and
     * 'other' are locations, substract() returns a vector pointing from 'other'
     * to this BlockLocation.
     * 
     * @param other
     *            The {@link BlockVector} to subtract
     * @return A new {@link BlockVector} with the given vector subtracted
     */
    public final BlockVector subtract(final BlockVector other) {
        return new BlockVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Faster equality test for BlockVectors than equals()
     * 
     * @param other
     * @return True if the vectors are equal.
     */
    public boolean equalsOther(final BlockVector other) {
        if (other == null)
            return false;
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    /**
     * Fastest equality test for BlockVectors, use only if the parameter is
     * known to be non-null.
     * 
     * @param other
     * @return True if the vectors are equal.
     */
    public boolean equalsOtherNotNull(final BlockVector other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    /**
     * Returns a BlockVector rotated by the given BlockRotation around the Y
     * axis.
     * 
     * @param rotate
     *            The amount to rotate by
     * @return A new rotated BlockVector
     */
    public final BlockVector rotateYaw(final BlockRotation rotate) {
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
        /*
         * Squeeze the coordinates into a long, keeping only 28 bits of x and z.
         * Masking y is unnecessary because it's completely shifted to the
         * right)
         * 
         * 28 bits still gives a range of ~158 million in both positive and
         * negative directions, and the CraftBukkit world is limited to 30
         * million in either direction.
         */
        long hashCode = x & 0xFFFFFFFL | ((z & 0xFFFFFFFL) << 28) | (y << 56);
        /*
         * Apply a hash method from MurmurHash3
         * http://code.google.com/p/smhasher/wiki/MurmurHash3
         */
        hashCode ^= hashCode >>> 33;
        hashCode *= 0xff51afd7ed558ccdL;
        hashCode ^= hashCode >>> 33;
        hashCode *= 0xc4ceb9fe1a85ec53L;
        hashCode ^= hashCode >>> 33;
        return (int) hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof BlockVector))
            return false;

        BlockVector other = (BlockVector) obj;

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public String toString() {
        return Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z);
    }
}
