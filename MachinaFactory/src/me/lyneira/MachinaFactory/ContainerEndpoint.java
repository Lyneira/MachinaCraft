package me.lyneira.MachinaFactory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.util.InventoryTransaction;

/**
 * Built-in endpoint for chests and dispensers.
 * 
 * @author Lyneira
 */
public class ContainerEndpoint implements PipelineEndpoint {

    private final List<BlockLocation> storage = new ArrayList<BlockLocation>(1);

    ContainerEndpoint(Player player, BlockLocation location) throws PipelineException {

        if (EventSimulator.inventoryProtectedStatic(player, location))
            throw new PipelineException(location);

        // Add the available containers in descending order, so the bottom
        // element will receive items first.
        // Go up
        for (BlockLocation i = location.getRelative(BlockFace.UP); verifyContainer(i) && (!EventSimulator.inventoryProtectedStatic(player, i)); i = i.getRelative(BlockFace.UP)) {
            storage.add(i);
        }
        // Add center location
        storage.add(location);
        // Go down
        for (BlockLocation i = location.getRelative(BlockFace.DOWN); verifyContainer(i) && (!EventSimulator.inventoryProtectedStatic(player, i)); i = i.getRelative(BlockFace.DOWN)) {
            storage.add(i);
        }
    }

    @Override
    public boolean verify() {
        for (BlockLocation i : storage) {
            if (!verifyContainer(i))
                return false;
        }
        return true;
    }

    private boolean verifyContainer(BlockLocation location) {
        switch (location.getType()) {
        case CHEST:
        case DISPENSER:
            return true;
        default:
            return false;
        }
    }

    private boolean handle(ItemStack item) {
        if (item == null)
            return false;

        int i = storage.size() - 1;
        while (true) {
            if (storeItem(storage.get(i), item))
                return true;
            i--;
            if (i == -1)
                return false;
        }
    }

    /**
     * Stores a single item stack in the container at the given location. Does
     * not check whether the location has a valid container.
     * 
     * @param location
     * @param item
     * @return True if there was room for the item stack.
     */
    public static boolean store(BlockLocation location, ItemStack item) {
        if (item == null)
            return false;

        return storeItem(location, item);
    }

    private final static boolean storeItem(BlockLocation location, ItemStack item) {
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
            return container.handle(payload);
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
