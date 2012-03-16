package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.Pipeline;
import me.lyneira.MachinaFactory.PipelineEndpoint;
import me.lyneira.MachinaFactory.PipelineException;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Component that will send items from its chest through a pipeline.
 * 
 * @author Lyneira
 */
public class ItemRelay extends Component implements PipelineEndpoint {

    private static final int delay = 20;

    private final Blueprint blueprint;
    private final Pipeline pipeline;
    private final Player player;

    ItemRelay(Blueprint blueprint, BlockRotation yaw, Player player, BlockLocation anchor) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        this.player = player;
        pipeline = new Pipeline(anchor, player, sender());
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (!player.isOnline())
            return null;

        state = state.run();
        if (state == null)
            return null;

        return new HeartBeatEvent(delay);
    }

    private final BlockLocation chest() {
        return anchor.getRelative(blueprint.chest.vector(yaw));
    }

    private final BlockLocation sender() {
        return anchor.getRelative(blueprint.sender.vector(yaw));
    }

    boolean handle(ItemStack item) {
        if (item == null)
            return false;

        grace = 0;
        InventoryManager manager = new InventoryManager(((InventoryHolder) chest().getBlock().getState()).getInventory());
        if (manager.hasRoom(item)) {
            manager.inventory.addItem(item);
            return true;
        }
        return false;
    }

    boolean handle(Inventory inventory) {
        if (inventory == null)
            return false;

        grace = 0;
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
     * Adaptor interface for different states
     */
    private interface State {
        /**
         * Runs this state and returns the next state.
         * 
         * @return The new state, or null if it should end.
         */
        State run();
    }

    /**
     * Attempts to send an item through the pipeline.
     */
    private final State sendItem = new State() {
        @Override
        public State run() {
            InventoryManager manager = new InventoryManager(((InventoryHolder) chest().getBlock().getState()).getInventory());
            if (manager.findFirst()) {
                ItemStack item = manager.get();
                item.setAmount(1);
                try {
                    if (pipeline.sendPacket(item)) {
                        manager.decrement();
                        return this;
                    } else {
                        recoveryState = sendInventory;
                        return sendInventory.run();
                    }
                } catch (PipelineException e) {
                    // Can't recover to a sending state if the pipeline is
                    // broken
                    recoveryState = gracePeriod;
                }

            }
            return gracePeriod.run();
        }
    };

    private final State sendInventory = new State() {
        @Override
        public State run() {
            Inventory inventory = ((InventoryHolder) chest().getBlock().getState()).getInventory();

            try {
                if (pipeline.sendPacket(inventory))
                    return this;
            } catch (PipelineException e) {
                // Can't recover to a sending state if the pipeline is broken
                recoveryState = gracePeriod;
            }

            return gracePeriod.run();
        }
    };

    private int grace = 0;
    private final State gracePeriod = new State() {
        /**
         * Number of grace ticks to keep when encountering no items to send or
         * when no pipeline could be created.
         */
        private static final int graceTicks = 2;

        @Override
        public State run() {
            if (grace < graceTicks) {
                grace++;
                return recoveryState;
            } else
                return null;
        }
    };

    /**
     * Starting state.
     */
    private State state = sendItem;
    private State recoveryState = sendItem;

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
