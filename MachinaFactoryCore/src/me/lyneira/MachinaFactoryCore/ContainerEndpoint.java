package me.lyneira.MachinaFactoryCore;

import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;


import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.InventoryManager;

public class ContainerEndpoint implements PipelineEndpoint {

    private static final PacketHandler handler;

    static {
        handler = new PacketHandler(new ContainerItemListener());
    }

    private final BlockLocation location;

    @Override
    public PacketHandler getHandler() {
        return handler;
    }

    public ContainerEndpoint(BlockLocation location) {
        this.location = location;
    }

    boolean handleItem(ItemStack item) {
        InventoryManager manager = new InventoryManager(((InventoryHolder) location.getBlock().getState()).getInventory());
        if (manager.hasRoom(item)) {
            manager.inventory.addItem(item);
            return true;
        }
        return false;
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
}
