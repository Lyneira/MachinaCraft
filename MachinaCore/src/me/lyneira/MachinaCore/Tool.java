package me.lyneira.MachinaCore;

import me.lyneira.util.InventoryManager;

import org.bukkit.Material;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Class implementing tool usage by machina
 * 
 * @author Lyneira
 */
public class Tool {
    /**
     * Expends a use of the given toolType from the furnaceInventory. If the
     * furnaceInventory's smelting slot is empty, attempts to retrieve a new
     * tool from the given supplyInventory. Returns true if successful.
     * 
     * @param furnaceInventory The furnace from which the tool should be used.
     * @param toolType A predicate specifying what tool should be used.
     * @param supplyInventory Supply inventory to search in case the furnaceInventory is empty. May be null to not search.
     * @return True if a single use was expended for the given tool type.
     */
    public static boolean useInFurnace(FurnaceInventory furnaceInventory, Predicate<ItemStack> toolType, Inventory supplyInventory) {
        ItemStack tool = furnaceInventory.getSmelting();
        if (tool == null || tool.getType() == Material.AIR) {
            // Try and find a tool in the chest.
            InventoryManager manager = new InventoryManager(supplyInventory);
            if (supplyInventory == null || !manager.find(toolType))
                return false;
            tool = manager.get();
            furnaceInventory.setSmelting(tool);
            manager.decrement();
            tool = furnaceInventory.getSmelting();
        } else if (!toolType.apply(tool))
            return false;

        // Use up durability.
        short newDurability = (short) (tool.getDurability() + 1);
        if (newDurability >= tool.getType().getMaxDurability()) {
            furnaceInventory.setSmelting(null);
        } else {
            tool.setDurability(newDurability);
        }
        return true;
    }
}
