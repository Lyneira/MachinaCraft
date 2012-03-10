package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaFactoryCore.Component;
import me.lyneira.MachinaFactoryCore.ComponentActivateException;
import me.lyneira.MachinaFactoryCore.ComponentDetectException;
import me.lyneira.MachinaFactoryCore.PacketHandler;
import me.lyneira.MachinaFactoryCore.PacketListener;
import me.lyneira.MachinaFactoryCore.Pipeline;
import me.lyneira.MachinaFactoryCore.PipelineEndpoint;
import me.lyneira.MachinaFactoryCore.PipelineException;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Component that will send items from its chest through a pipeline.
 * 
 * @author Lyneira
 */
class ItemRelay extends Component implements PipelineEndpoint {

    private static final int delay = 20;
    private Blueprint blueprint;
    private Pipeline pipeline;

    ItemRelay(Blueprint blueprint, BlockRotation yaw, Player player, BlockLocation anchor, BlockFace leverFace) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        BlockLocation sender = sender();
        try {
            pipeline = new Pipeline(player, sender);
        } catch (PipelineException e) {
            pipeline = null;
        }
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (pipeline != null && doSend()) {
            return new HeartBeatEvent(delay);
        }
        return null;
    }

    /**
     * Attempts to send an item through the pipeline.
     * 
     * @return True if the item was accepted by the other end.
     */
    boolean doSend() {
        try {
            InventoryManager manager = new InventoryManager(((InventoryHolder) chest().getBlock().getState()).getInventory());
            if (manager.findFirst()) {
                ItemStack item = manager.get();
                item.setAmount(1);
                if (pipeline.sendPacket(item)) {
                    manager.decrement();
                    return true;
                }
            }
        } catch (PipelineException e) {

        }
        return false;
    }

    BlockLocation chest() {
        return anchor.getRelative(blueprint.chest.vector(yaw));
    }

    BlockLocation sender() {
        return anchor.getRelative(blueprint.sender.vector(yaw));
    }

    boolean handle(ItemStack item) {
        InventoryManager manager = new InventoryManager(((InventoryHolder) chest().getBlock().getState()).getInventory());
        if (manager.hasRoom(item)) {
            manager.inventory.addItem(item);
            return true;
        }
        return false;
    }

    boolean handle(Inventory inventory) {
        Inventory myInventory = (((InventoryHolder) chest().getBlock().getState()).getInventory());
        if (inventory.equals(myInventory))
            return false;

        InventoryManager input = new InventoryManager(inventory);
        InventoryManager output = new InventoryManager(myInventory);
        if (input.findFirst()) {
            ItemStack item = input.get();
            item.setAmount(1);
            if (output.hasRoom(item)) {
                output.inventory.addItem(item);
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
            return ((ItemRelay) endpoint).handle(payload);
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
            return ((ItemRelay) endpoint).handle(payload);
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
