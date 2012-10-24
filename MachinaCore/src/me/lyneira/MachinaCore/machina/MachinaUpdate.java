package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;

import org.bukkit.inventory.ItemStack;

/**
 * Value object used to hold all necessary data for full or partial machina
 * updates. Internal use only.
 * 
 * @author Lyneira
 */
public class MachinaUpdate {
    public final BlockVector[] oldBlocks;
    public final MachinaBlock[] newBlocks;
    public final ItemStack[][] inventories;

    MachinaUpdate(BlockVector[] oldBlocks, MachinaBlock[] newBlocks, ItemStack[][] inventories) {
        this.newBlocks = newBlocks;
        this.inventories = inventories;
        this.oldBlocks = oldBlocks;
    }
}
