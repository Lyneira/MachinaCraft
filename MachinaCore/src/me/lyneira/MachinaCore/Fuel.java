package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents fuel properties and implements fuel consumption from a furnace.
 * 
 * @author Lyneira
 */
public class Fuel {
    private Fuel() {
        // Cannot instantiate this class.
    }

    public static final int smeltSlot = 0;
    public static final int fuelSlot = 1;

    //
    /**
     * Returns the burn time for the given Material, as if it were used to smelt
     * something in a furnace.
     * 
     * @param material
     *            The material for which to get the burn time.
     * @return The burn time. Returns 0 for any material that is not a fuel.
     */
    public static int burnTime(final Material material) {
        if (material == Material.COAL) {
            return 1600;
        } else if (material == Material.WOOD || material == Material.LOG || material == Material.FENCE || material == Material.WOOD_STAIRS || material == Material.TRAP_DOOR
                || material == Material.WORKBENCH || material == Material.BOOKSHELF || material == Material.CHEST || material == Material.JUKEBOX || material == Material.NOTE_BLOCK
                || material == Material.LOCKED_CHEST) {
            return 300;
        } else if (material == Material.SAPLING || material == Material.STICK || material == Material.SUGAR_CANE || material == Material.PAPER) {
            return 100;
        } else if (material == Material.BLAZE_ROD) {
            return 2400;
        }
        return 0;
    }

    /**
     * Consumes fuel from the given Furnace.
     * 
     * @param furnace
     *            The furnace to consume fuel from
     * @return The burn time of the fuel consumed. Returns 0 on failure.
     */
    public static int consume(final Furnace furnace) {
        try {
            Inventory furnaceInventory = furnace.getInventory();
            ItemStack fuelStack = furnaceInventory.getItem(fuelSlot);
            int burnTime = burnTime(fuelStack.getType());
            int amount = fuelStack.getAmount();
            if (burnTime > 0) {
                if (amount == 0) {
                    burnTime = 0;
                } else if (amount == 1) {
                    furnaceInventory.clear(fuelSlot);
                } else {
                    fuelStack.setAmount(amount - 1);
                    furnaceInventory.setItem(fuelSlot, fuelStack);
                }
            }
            return burnTime;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Sets the given Block to a Furnace with the given burn state, and sets the
     * furnace contents to the given Inventory.
     * 
     * @param block
     *            The block to set as a Furnace
     * @param direction
     *            The direction to set the furnace to.
     * @param burning
     *            Whether the furnace is burning
     * @param inventory
     *            Inventory to copy over
     * @return True if the operation was a success, false if the existing block
     *         could not be cast to a furnace. In that case, this function does
     *         nothing.
     */
    public static boolean setFurnace(final Block furnaceBlock, final BlockFace direction, final boolean burning) {
        try {
            Furnace furnace = (Furnace) furnaceBlock.getState();
            Inventory inventory = furnace.getInventory();
            ItemStack[] contents = inventory.getContents();
            inventory.clear();

            if (burning) {
                furnace.setType(Material.BURNING_FURNACE);
            } else {
                furnace.setType(Material.FURNACE);
            }
            // Set furnace direction
            furnace.setData(new org.bukkit.material.Furnace(direction));

            furnace.update(true);
            Inventory newInventory = ((Furnace) furnaceBlock.getState()).getInventory();
            newInventory.setContents(contents);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
