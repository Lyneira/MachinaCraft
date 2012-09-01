package me.lyneira.MachinaFactory;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.util.InventoryManager;

import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Built-in endpoint for a furnace.
 * 
 * @author Lyneira
 */
public class FurnaceEndpoint implements PipelineEndpoint, Comparable<FurnaceEndpoint> {

    public final BlockLocation location;
    private int fuelAmount = 0;
    private int smeltingAmount = 0;

    public FurnaceEndpoint(Player player, BlockLocation location) {
        this.location = location;
    }

    @Override
    public int compareTo(FurnaceEndpoint other) {
        updateLevels();
        other.updateLevels();
        if (fuelAmount < 2) {
            if (fuelAmount == other.fuelAmount)
                return smeltingAmount - other.smeltingAmount;
            return fuelAmount - other.fuelAmount;
        } else {
            if (smeltingAmount == other.smeltingAmount)
                return fuelAmount - other.fuelAmount;
            return smeltingAmount - other.smeltingAmount;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof FurnaceEndpoint))
            return false;

        return compareTo((FurnaceEndpoint) other) == 0;
    }

    @Override
    public boolean verify() {
        switch (location.getType()) {
        case FURNACE:
        case BURNING_FURNACE:
            return true;
        default:
            return false;
        }
    }

    /**
     * Handles a sending inventory for the furnace at the given location. Does
     * not check if the location has a valid furnace.<br>
     * Fuels: Added to the fuel slot if there is room.<br>
     * Items that can be burnt: Added to the burn slot.
     * 
     * @param location
     * @param inventory
     * @return True if the inventory was changed.
     */
    public boolean handle(Inventory inventory) {
        FurnaceInventory furnaceInventory = ((Furnace) location.getBlock().getState()).getInventory();
        if (furnaceInventory == inventory) {
            return false;
        }
        updateLevels();

        if (fuelAmount < 2) {
            if (restockFuel(furnaceInventory, inventory))
                return true;
            return restockSmelting(furnaceInventory, inventory);
        } else {
            if (restockSmelting(furnaceInventory, inventory))
                return true;
            return restockFuel(furnaceInventory, inventory);
        }
    }

    /**
     * Updates the status of the furnace to the current amount of fuel and
     * smelting items.
     */
    private final void updateLevels() {
        FurnaceInventory inventory = (((Furnace) location.getBlock().getState()).getInventory());
        final ItemStack fuelItem = inventory.getFuel();
        if (fuelItem == null || fuelItem.getType() == Material.AIR) {
            fuelAmount = 0;
        } else {
            fuelAmount = fuelItem.getAmount();
        }

        final ItemStack smeltItem = inventory.getSmelting();
        if (smeltItem == null || smeltItem.getType() == Material.AIR) {
            smeltingAmount = 0;
        } else {
            smeltingAmount = smeltItem.getAmount();
        }
    }

    private static boolean restockFuel(FurnaceInventory furnaceInventory, Inventory inventory) {
        InventoryManager manager = new InventoryManager(inventory);
        ItemStack fuelItem = furnaceInventory.getFuel();
        if (fuelItem == null || fuelItem.getType() == Material.AIR) {
            if (!manager.find(isFuelItem))
                return false;
            fuelItem = new ItemStack(manager.get());
            fuelItem.setAmount(1);
            furnaceInventory.setFuel(fuelItem);
            manager.decrement();
            return true;
        } else if (manager.findItemType(fuelItem)) {
            int amount = fuelItem.getAmount();
            if (amount < fuelItem.getMaxStackSize()) {
                fuelItem.setAmount(amount + 1);
                furnaceInventory.setFuel(fuelItem);
                manager.decrement();
                return true;
            }
        }
        return false;
    }

    private static boolean restockSmelting(FurnaceInventory furnaceInventory, Inventory inventory) {
        InventoryManager manager = new InventoryManager(inventory);
        ItemStack smeltItem = furnaceInventory.getSmelting();
        if (smeltItem == null || smeltItem.getType() == Material.AIR) {
            if (!manager.find(burnableItem))
                return false;
            smeltItem = new ItemStack(manager.get());
            smeltItem.setAmount(1);
            furnaceInventory.setSmelting(smeltItem);
            manager.decrement();
            return true;
        } else if (manager.findItemType(smeltItem)) {
            int amount = smeltItem.getAmount();
            if (amount < smeltItem.getMaxStackSize()) {
                smeltItem.setAmount(amount + 1);
                furnaceInventory.setSmelting(smeltItem);
                manager.decrement();
                return true;
            }
        }
        return false;
    }

    public static final Predicate<ItemStack> isFuelItem = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            return (Fuel.burnTimeBase(item.getType()) != 0);
        }
    };

    public static final Predicate<ItemStack> burnableItem = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            return Fuel.isBurnable(item.getType());
        }
    };

    /**
     * Listener for item stacks.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            FurnaceEndpoint furnaceEndpoint = (FurnaceEndpoint) endpoint;
            return furnaceEndpoint.handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler handler = new PacketHandler(inventoryListener);

    @Override
    public PacketHandler getHandler() {
        return handler;
    }
}
