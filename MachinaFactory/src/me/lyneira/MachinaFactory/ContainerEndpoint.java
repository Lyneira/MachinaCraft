package me.lyneira.MachinaFactory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaCore.InventoryTransaction;

/**
 * Built-in endpoint for chests and dispensers.
 * 
 * @author Lyneira
 */
class ContainerEndpoint implements PipelineEndpoint {

    private final BlockLocation location;

    ContainerEndpoint(Player player, BlockLocation location) throws PipelineException {
        this.location = location;

        if (EventSimulator.inventoryProtected(player, location))
            throw new PipelineException(location);
    }

    @Override
    public boolean verify() {
        switch (location.getType()) {
        case CHEST:
        case DISPENSER:
            return true;
        }
        return false;
    }

    /**
     * Handles a single item stack, attempting to store it in this container.
     * 
     * @param item
     * @return True if there was room for the item stack.
     */
    boolean handle(ItemStack item) {
        if (item == null)
            return false;

        InventoryTransaction transaction = new InventoryTransaction(((InventoryHolder) location.getBlock().getState()).getInventory());
        transaction.add(item);
        return transaction.execute();
    }

    /**
     * Handles an inventory, transferring the first item from it to this
     * container.
     * 
     * @param inventory
     * @return True if an item was transferred.
     */
    boolean handle(Inventory inventory) {
        if (inventory == null)
            return false;

        Inventory myInventory = (((InventoryHolder) location.getBlock().getState()).getInventory());
        if (inventory.equals(myInventory))
            return false;

        InventoryManager input = new InventoryManager(inventory);
        InventoryTransaction transaction = new InventoryTransaction(myInventory);
        if (input.findFirst()) {
            ItemStack item = input.get();
            item.setAmount(1);
            transaction.add(item);
            if (transaction.execute()) {
                input.decrement();
                return true;
            }
        }
        return false;
    }

    /**
     * Listener for item stacks.
     */
    private static final PacketListener<ItemStack> itemStackListener = new PacketListener<ItemStack>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, ItemStack payload) {
            return ((ContainerEndpoint) endpoint).handle(payload);
        }

        @Override
        public Class<ItemStack> payloadType() {
            return ItemStack.class;
        }
    };

    /**
     * Listener for inventories.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            return ((ContainerEndpoint) endpoint).handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler handler = new PacketHandler(itemStackListener, inventoryListener);

    @Override
    public PacketHandler getHandler() {
        return handler;
    }
}
