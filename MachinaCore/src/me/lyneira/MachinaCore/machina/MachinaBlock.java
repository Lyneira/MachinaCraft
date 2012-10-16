package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.block.BlockVector;

/**
 * Represents a single block with type and data in a Machina.
 * 
 * @author Lyneira
 */
public class MachinaBlock extends BlockVector {

    public final int typeId;
    public final byte data;

    public MachinaBlock(int x, int y, int z, int typeId, byte data) {
        super(x, y, z);
        this.typeId = typeId;
        this.data = data;
    }
    
    public MachinaBlock(BlockVector vector, int typeId, byte data) {
        super(vector);
        this.typeId = typeId;
        this.data = data;
    }

}
