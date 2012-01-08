package me.lyneira.MachinaCore;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
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
    private static final Map<Integer, Integer> burnTimes = new HashMap<Integer, Integer>(24);

    private static final int blazeRodTime = 2400;
    private static final int coalTime = 1600;
    private static final int woodTime = 300;
    private static final int saplingTime = 100;

    static {
        burnTimes.put(Material.BLAZE_ROD.getId(), blazeRodTime);
        
        burnTimes.put(Material.COAL.getId(), coalTime);
        
        burnTimes.put(Material.WOOD.getId(), woodTime);
        burnTimes.put(Material.LOG.getId(), woodTime);
        burnTimes.put(Material.FENCE.getId(), woodTime);
        burnTimes.put(Material.WOOD_STAIRS.getId(), woodTime);
        burnTimes.put(Material.TRAP_DOOR.getId(), woodTime);
        burnTimes.put(Material.WORKBENCH.getId(), woodTime);
        burnTimes.put(Material.BOOKSHELF.getId(), woodTime);
        burnTimes.put(Material.CHEST.getId(), woodTime);
        burnTimes.put(Material.JUKEBOX.getId(), woodTime);
        burnTimes.put(Material.NOTE_BLOCK.getId(), woodTime);
        burnTimes.put(Material.LOCKED_CHEST.getId(), woodTime);
        burnTimes.put(Material.FENCE_GATE.getId(), woodTime);
        
        burnTimes.put(Material.SAPLING.getId(), saplingTime);
        burnTimes.put(Material.STICK.getId(), saplingTime);
        burnTimes.put(Material.SUGAR_CANE.getId(), saplingTime);
        burnTimes.put(Material.PAPER.getId(), saplingTime);
    }

    //
    /**
     * Returns the burn time for the given Material, as if it were used to smelt
     * something in a furnace.
     * 
     * @param material
     *            The material for which to get the burn time.
     * @return The burn time. Returns 0 for any material that is not a fuel.
     */
    public static int burnTime(int typeId) {
        int burnTime = 0;
        if (burnTimes.containsKey(typeId)) {
            burnTime = burnTimes.get(typeId);
        }
        return burnTime;
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
            int burnTime = burnTime(fuelStack.getType().getId());
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
    public static boolean setFurnace(final Block furnaceBlock, final BlockRotation direction, final boolean burning) {
        try {
            Inventory inventory = ((Furnace) furnaceBlock.getState()).getInventory();
            ItemStack[] contents = inventory.getContents();
            inventory.clear();

            if (burning) {
                furnaceBlock.setTypeIdAndData(Material.BURNING_FURNACE.getId(), direction.getYawData(), false);
            } else {
                furnaceBlock.setTypeIdAndData(Material.FURNACE.getId(), direction.getYawData(), false);
            }

            Inventory newInventory = ((Furnace) furnaceBlock.getState()).getInventory();
            newInventory.setContents(contents);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static final void loadConfiguration(ConfigurationSection configuration) {
        Map<String, Object> fuels = configuration.getValues(false);
        for (String id : fuels.keySet()) {
            int typeId;
            try {
                typeId = Integer.valueOf(id);
            } catch (Exception e) {
                MachinaCore.log.warning("MachinaCore: Could not parse fuel data for id: " + id);
                continue;
            }
            burnTimes.put(typeId, configuration.getInt(id, 0));
        }
    }
}
