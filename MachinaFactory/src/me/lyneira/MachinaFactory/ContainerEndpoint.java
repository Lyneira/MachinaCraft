package me.lyneira.MachinaFactory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.InventoryTransaction;

/**
 * Built-in endpoint for chests and dispensers.
 * 
 * @author Lyneira
 */
public class ContainerEndpoint implements PipelineEndpoint {

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
     * Handles a single item stack, attempting to store it in the container at
     * the given location. Does not check whether the location has a valid
     * container.
     * 
     * @param location
     * @param item
     * @return True if there was room for the item stack.
     */
    public static boolean handle(BlockLocation location, ItemStack item) {
        if (item == null)
            return false;

        InventoryTransaction transaction = new InventoryTransaction(((InventoryHolder) location.getBlock().getState()).getInventory());
        transaction.add(item);
        return transaction.execute();
    }

    /**
     * Listener for item stacks.
     */
    private static final PacketListener<ItemStack> itemStackListener = new PacketListener<ItemStack>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, ItemStack payload) {
            ContainerEndpoint container = (ContainerEndpoint) endpoint;
            return ContainerEndpoint.handle(container.location, payload);
        }

        @Override
        public Class<ItemStack> payloadType() {
            return ItemStack.class;
        }
    };

    private static final PacketHandler handler = new PacketHandler(itemStackListener);

    @Override
    public PacketHandler getHandler() {
        return handler;
    }
}
