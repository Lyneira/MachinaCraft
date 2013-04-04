package me.lyneira.MachinaCore;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.ItemStack;

/**
 * Int-based implementation of a Location class.
 * 
 * @author Lyneira
 */
public final class BlockLocation {
    private final World world;
    public final int x;
    public final int y;
    public final int z;

    /**
     * Constructs a new {@link BlockLocation} with the given coordinates
     * 
     * @param world
     *            The world in which this location resides
     * @param x
     *            The x-coordinate of this new location
     * @param y
     *            The y-coordinate of this new location
     * @param z
     *            The z-coordinate of this new location
     */
    public BlockLocation(final World world, final int x, final int y, final int z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Constructs a new {@link BlockLocation} with the location of the given
     * {@link Block}
     * 
     * @param block
     *            The {@link Block} to use for the new location
     */
    public BlockLocation(final Block block) {
        world = block.getWorld();
        x = block.getX();
        y = block.getY();
        z = block.getZ();
    }

    /**
     * Returns the {@link Block} corresponding to this {@link BlockLocation}
     * 
     * @return Block
     */
    public final Block getBlock() {
        return world.getBlockAt(x, y, z);
    }

    /**
     * Returns the World corresponding to this {@link BlockLocation}
     * 
     * @return World
     */
    public final World getWorld() {
        return world;
    }

    /**
     * Returns true if the block type at this location matches the given
     * Material.
     * 
     * @return True if this location's type matches the given material
     */
    public final boolean checkType(final Material material) {
        return (world.getBlockTypeIdAt(x, y, z) == material.getId());
    }

    /**
     * Returns true if the block type at this location matches any of the given
     * Materials.
     * 
     * @return True if this location's type matches one the given materials
     */
    public final boolean checkTypes(final Material... materials) {
        int id = world.getBlockTypeIdAt(x, y, z);
        for (Material m : materials) {
            if (id == m.getId()) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return True if the location is safe to move into for a machina.
     */
    public final boolean isEmptyForCollision() {
        switch (world.getBlockAt(x, y, z).getType()) {
        case AIR:
        case SNOW:
        case LONG_GRASS:
            return true;
        default:
            return false;
        }
    }

    /**
     * Returns the {@link Material} at this {@link BlockLocation}
     * 
     * @return The {@link Material} at this location
     */
    public final Material getType() {
        return world.getBlockAt(x, y, z).getType();
    }

    /**
     * Returns the Type Id at this {@link BlockLocation}
     * 
     * @return The Type Id at this location
     */
    public final int getTypeId() {
        return world.getBlockTypeIdAt(x, y, z);
    }

    /**
     * Sets the Material at this {@link BlockLocation}.
     * 
     * @param type
     *            The Material to set to
     */
    public final void setType(final Material type) {
        world.getBlockAt(x, y, z).setType(type);
    }

    /**
     * Sets the Material at this {@link BlockLocation}.
     * 
     * @param typeId
     *            The type id to set
     */
    public final void setTypeId(final int typeId) {
        world.getBlockAt(x, y, z).setTypeId(typeId);
    }

    /**
     * Sets the data at this {@link BlockLocation}.
     * 
     * @param data
     *            The data to set
     */
    public final void setData(final byte data) {
        world.getBlockAt(x, y, z).setData(data);
    }

    /**
     * Sets the type id and data at this {@link BlockLocation}.
     * 
     * @param type
     *            The type id to set
     * @param data
     *            The data to set
     */
    public final void setTypeIdAndData(final int typeId, final byte data, boolean applyPhysics) {
        world.getBlockAt(x, y, z).setTypeIdAndData(typeId, data, applyPhysics);
    }

    /**
     * Returns true if this {@link BlockLocation} contains air.
     * 
     * @return True if the {@link BlockLocation} contains air.
     */
    public final boolean isEmpty() {
        return world.getBlockTypeIdAt(x, y, z) == 0;
    }

    /**
     * Sets this {@link BlockLocation} to air.
     */
    public final void setEmpty() {
        world.getBlockAt(x, y, z).setTypeIdAndData(0, (byte) 0, true);
    }

    /**
     * Gets the {@link BlockLocation} at the given face. This method is equal to
     * getRelative(face, 1)
     * 
     * @param face
     *            Face of this {@link BlockLocation} to return
     * @return New location at the given face
     */
    public final BlockLocation getRelative(final BlockFace face) {
        return new BlockLocation(world, x + face.getModX(), y + face.getModY(), z + face.getModZ());
    }

    /**
     * Gets the {@link BlockLocation} at the given face
     * 
     * @param face
     *            Face of this {@link BlockLocation} to return
     * @param distance
     *            Distance to get the block at
     * @return New location at the given face
     */
    public final BlockLocation getRelative(final BlockFace face, final int distance) {
        return new BlockLocation(world, x + face.getModX() * distance, y + face.getModY() * distance, z + face.getModZ() * distance);
    }

    /**
     * Gets the {@link BlockLocation} at the given {@link BlockVector}.
     * 
     * @param blockVector
     *            Face of this {@link BlockLocation} to return
     * @return New location at the given vector
     */
    public final BlockLocation getRelative(final BlockVector blockVector) {
        return blockVector.apply(world, x, y, z);
    }

    /**
     * Gets the {@link BlockLocation} at the given {@link BlockVector} at the
     * given distance.
     * 
     * @param blockVector
     *            Face of this {@link BlockLocation} to return
     * @param distance
     *            Number of times to apply this vector
     * @return New location at the given vector
     */
    public final BlockLocation getRelative(final BlockVector blockVector, final int distance) {
        return blockVector.apply(world, x, y, z, distance);
    }

    /**
     * Substract the given {@link BlockLocation} from this one. This returns a
     * vector pointing from 'other' to this BlockLocation.
     * 
     * @param other
     *            The {@link BlockLocation} to subtract
     * @return A new {@link BlockVector} pointing from 'other' to this location.
     */
    public final BlockVector subtract(final BlockLocation other) {
        return new BlockVector(this.x - other.x, this.y - other.y, this.z - other.z);
    }

    /**
     * Drops the given {@link ItemStack} at this location
     * 
     * @param item
     *            The item stack to drop
     */
    public final void dropItem(final ItemStack item) {
        world.dropItem(new Location(world, x + 0.5, y + 0.5, z + 0.5), item);
    }

    /**
     * @return A location centered in the middle of this {@link BlockLocation}.
     */
    public final Location getLocation() {
        return new Location(world, x + 0.5, y + 0.5, z + 0.5);
    }

    @Override
    public int hashCode() {
        return y << 24 ^ x ^ z ^ world.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (!(obj instanceof BlockLocation))
            return false;

        BlockLocation other = (BlockLocation) obj;
        if (this.world != other.world && (this.world == null || !this.world.equals(other.world))) {
            return false;
        }

        return this.x == other.x && this.y == other.y && this.z == other.z;
    }

    @Override
    public String toString() {
        return world.toString() + " - " + Integer.toString(x) + "," + Integer.toString(y) + "," + Integer.toString(z);
    }
}
