package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;

import org.bukkit.inventory.ItemStack;

/**
 * Value object used to hold all necessary data for full or partial machina
 * updates. Internal use only.
 * 
 * <li>newBlocks and the top level inventories array are the same size.
 * 
 * <li>The block arrays have no null elements.
 * 
 * <li>The inventories array only contains non-null elements for the blocks that
 * need to have their inventory overwritten, and the arrays therein are the
 * correct size for the type of inventory being overwritten.
 * 
 * @author Lyneira
 */
public class MachinaUpdate {
    public final BlockVector[] oldBlocks;
    public final MachinaBlock[] newBlocks;
    public final ItemStack[][] inventories;

    public MachinaUpdate(BlockVector[] oldBlocks, MachinaBlock[] newBlocks, ItemStack[][] inventories) {
        this.newBlocks = newBlocks;
        this.inventories = inventories;
        this.oldBlocks = oldBlocks;
    }
}
