package me.lyneira.MachinaCore.map;

/**
 * Class for storing immutable block-based vectors. Usable as both a vector and
 * coordinates. Used as the key for CoordinateMap3D.
 * 
 * @author Lyneira
 */
public class BlockVector {
    public final int x;
    public final int y;
    public final int z;

    public BlockVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public boolean equalsOther(BlockVector other) {
        if (other == null)
            return false;
        return equalsOtherNotNull(other);
    }
    
    public boolean equalsOtherNotNull(BlockVector other) {
        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public int hashCode() {
        return y << 24 ^ x ^ z;
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
