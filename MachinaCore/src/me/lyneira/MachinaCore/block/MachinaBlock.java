package me.lyneira.MachinaCore.block;

import org.bukkit.World;
import org.bukkit.block.Block;


/**
 * Represents a single block with type and data in a Machina. A data value of -1
 * means anything goes.
 * 
 * @author Lyneira
 */
public class MachinaBlock extends BlockVector {

    public final int typeId;
    public final short data;

    public MachinaBlock(int x, int y, int z, int typeId, short data) {
        super(x, y, z);
        this.typeId = typeId;
        this.data = data;
    }

    public MachinaBlock(BlockVector vector, int typeId, short data) {
        super(vector);
        this.typeId = typeId;
        this.data = data;
    }

    public MachinaBlock(int x, int y, int z, int typeId) {
        super(x, y, z);
        this.typeId = typeId;
        this.data = -1;
    }

    public MachinaBlock(BlockVector vector, int typeId) {
        super(vector);
        this.typeId = typeId;
        this.data = -1;
    }
    
    /**
     * Returns a MachinaBlock rotated by the given BlockRotation around the Y
     * axis.
     * 
     * @param rotate
     *            The amount to rotate by
     * @return A new rotated MachinaBlock
     */
    @Override
    public MachinaBlock rotateYaw(final BlockRotation rotate) {
        switch (rotate) {
        case ROTATE_0:
            return this;
        case ROTATE_90:
            return new MachinaBlock(z, y, -x, typeId, data);
        case ROTATE_180:
            return new MachinaBlock(-x, y, -z, typeId, data);
        case ROTATE_270:
        default:
            return new MachinaBlock(-z, y, x, typeId, data);
        }
    }

    public boolean match(World world) {
        return matchInternal(world, x, y, z);
    }
    
    public boolean match(World world, int originX, int originY, int originZ) {
        return matchInternal(world, x + originX, y + originY, z + originZ);
    }
    
    public boolean match(World world, BlockVector origin) {
        return matchInternal(world, x + origin.x, y + origin.y, z + origin.z);
    }
    
    /**
     * Compares only the type of this MachinaBlock to the given block and returns true if they match.
     * @param block The block to compare to
     * @return True if the type matches.
     */
    public boolean matchTypeOnly(Block block) {
        return matchInternal(block.getWorld(), block.getX(), block.getY(), block.getZ());
    }

    private boolean matchInternal(World world, int x, int y, int z) {
        if (data == -1) {
            return world.getBlockTypeIdAt(x, y, z) == typeId;
        } else {
            final Block block = world.getBlockAt(x, y, z);
            return (block.getTypeId() == typeId && block.getData() == data);
        }
    }
    
    @Override
    public String toString() {
        return "[type: " + typeId + " data: " + data + " - " + super.toString() + "]";
    }
}
