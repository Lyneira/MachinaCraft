package me.lyneira.MachinaFactory;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.InventoryManager;

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
public class FurnaceEndpoint implements PipelineEndpoint {

    private final BlockLocation location;

    FurnaceEndpoint(Player player, BlockLocation location) throws PipelineException {
        this.location = location;
    }

    @Override
    public boolean verify() {
        switch (location.getType()) {
        case FURNACE:
        case BURNING_FURNACE:
            return true;
        }
        return false;
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
    public static boolean handle(BlockLocation location, Inventory inventory) {
        // InventoryManager manager = new InventoryManager(inventory);
        FurnaceInventory furnaceInventory = ((Furnace) location.getBlock().getState()).getInventory();
        if (furnaceInventory == inventory)
            return false;

        // If the furnace has no fuel, add one first.
        InventoryManager manager = new InventoryManager(inventory);
        ItemStack fuelItem = furnaceInventory.getFuel();
        if (fuelItem == null) {
            if (!manager.find(isFuelItem))
                return false;
            fuelItem = new ItemStack(manager.get());
            fuelItem.setAmount(1);
            furnaceInventory.setFuel(fuelItem);
            manager.decrement();
            return true;
        }

        // The furnace has fuel, so add a burnable item.
        ItemStack smeltItem = furnaceInventory.getSmelting();
        if (smeltItem == null) {
            if (!manager.find(burnableItem))
                return false;
            smeltItem = new ItemStack(manager.get());
            smeltItem.setAmount(1);
            furnaceInventory.setSmelting(smeltItem);
            manager.decrement();
            return true;
        }
        // Fuel and smelting are filled, so try to keep them stocked.
        // Restock fuel slot
        int amount = fuelItem.getAmount();
        if (manager.findItemType(fuelItem) && amount < fuelItem.getMaxStackSize()) {
            fuelItem.setAmount(amount + 1);
            furnaceInventory.setFuel(fuelItem);
            manager.decrement();
            return true;
        }
        // Restock smelting slot
        amount = smeltItem.getAmount();
        if (manager.findItemType(smeltItem) && amount < smeltItem.getMaxStackSize()) {
            smeltItem.setAmount(amount + 1);
            furnaceInventory.setSmelting(smeltItem);
            manager.decrement();
            return true;
        }
        return false;
    }

    private static final Predicate<ItemStack> isFuelItem = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            return (Fuel.burnTimeBase(item.getType()) != 0);
        }
    };

    private static final Predicate<ItemStack> burnableItem = new Predicate<ItemStack>() {
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
            FurnaceEndpoint furnace = (FurnaceEndpoint) endpoint;
            return FurnaceEndpoint.handle(furnace.location, payload);
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
