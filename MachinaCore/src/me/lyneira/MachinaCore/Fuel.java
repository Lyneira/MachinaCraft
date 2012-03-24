package me.lyneira.MachinaCore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

/**
 * Represents fuel properties and implements fuel consumption from a furnace.
 * 
 * @author Lyneira
 */
public class Fuel {
    private Fuel() {
        // Cannot instantiate this class.
    }

    private static final Map<Integer, Integer> burnTimes = new HashMap<Integer, Integer>(24);
    /**
     * Lazy instantiation for plugins that may add furnace recipes.
     */
    private static Set<Material> burnable = null;

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
        final Integer burnTime = burnTimes.get(typeId);
        if (burnTime == null)
            return 0;
        return burnTime;
    }

    /**
     * Returns the base Minecraft burn time for the given material. This will
     * ignore any changes made to burn times in the config file.
     * 
     * @param material
     * @return The burn time. Returns 0 for any material that is not a fuel.
     */
    public static int burnTimeBase(Material material) {
        switch (material) {
        case BLAZE_ROD:
            return blazeRodTime;

        case COAL:
            return coalTime;

        case WOOD:
        case LOG:
        case FENCE:
        case WOOD_STAIRS:
        case TRAP_DOOR:
        case WORKBENCH:
        case BOOKSHELF:
        case CHEST:
        case JUKEBOX:
        case NOTE_BLOCK:
        case LOCKED_CHEST:
        case FENCE_GATE:
            return woodTime;

        case SAPLING:
        case STICK:
            return saplingTime;
        default:
            return 0;
        }
    }

    /**
     * Returns true if this material can be burned in a furnace.
     * 
     * @param material
     * @return
     */
    public static boolean isBurnable(Material material) {
        if (burnable == null)
            burnable = getBurnableMaterials();
        return (burnable.contains(material));
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
            FurnaceInventory furnaceInventory = furnace.getInventory();
            ItemStack fuelStack = furnaceInventory.getFuel();
            if (fuelStack == null)
                return 0;
            int burnTime = burnTime(fuelStack.getType().getId());
            if (burnTime > 0) {
                fuelStack.setAmount(fuelStack.getAmount() - 1);
                furnaceInventory.setFuel(fuelStack);
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
        if (configuration == null)
            return;

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

    private static Set<Material> getBurnableMaterials() {
        Set<Material> burnables = new HashSet<Material>(24);
        for (Iterator<Recipe> it = MachinaCore.plugin.getServer().recipeIterator(); it.hasNext();) {
            Recipe recipe = it.next();
            if (recipe instanceof FurnaceRecipe) {
                burnables.add(((FurnaceRecipe) recipe).getInput().getType());
            }
        }
        return burnables;
    }
}
