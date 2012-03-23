package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaCore.InventoryTransaction;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentBlueprint;
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
 * Component that will send items from its container through a pipeline.
 * 
 * @author Lyneira
 */
public abstract class ItemRelay extends Component implements PipelineEndpoint {

    private static final int delay = 20;
    /**
     * Number of ticks to stay active when the item relay cannot do anything.
     */
    private static final int maxAge = 10;
    
    final Blueprint blueprint;
    private final Pipeline pipeline;
    private final Player player;
    int age = 0;

    ItemRelay(Blueprint blueprint, ComponentBlueprint componentBlueprint, BlockRotation yaw, Player player, BlockLocation anchor) throws ComponentActivateException, ComponentDetectException {
        super(componentBlueprint, anchor, yaw);
        this.blueprint = blueprint;
        this.player = player;
        pipeline = new Pipeline(anchor, player, sender());
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (!player.isOnline())
            return null;
        
        if (age++ >= maxAge)
            return null;

        relayActions();
        state = state.run();
        if (state == null)
            return null;

        return new HeartBeatEvent(delay);
    }
    
    /**
     * Called before item sending takes place. Overridde if necessary.
     */
    void relayActions() {
        
    }

    abstract BlockLocation container();

    private final BlockLocation sender() {
        return anchor.getRelative(blueprint.sender.vector(yaw));
    }

    boolean handle(ItemStack item) {
        if (item == null)
            return false;

        InventoryTransaction transaction = new InventoryTransaction(((InventoryHolder) container().getBlock().getState()).getInventory());
        transaction.add(item);
        if (transaction.execute()) {
            age = 0;
            return true;
        }
        return false;
    }

    boolean handle(Inventory inventory) {
        if (inventory == null)
            return false;

        Inventory myInventory = (((InventoryHolder) container().getBlock().getState()).getInventory());
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
                age = 0;
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
            InventoryManager manager = new InventoryManager(((InventoryHolder) container().getBlock().getState()).getInventory());
            if (manager.findFirst()) {
                ItemStack item = manager.get();
                item.setAmount(1);
                try {
                    if (pipeline.sendPacket(item)) {
                        manager.decrement();
                        age = 0;
                        return this;
                    } else {
                        return sendInventory.run();
                    }
                } catch (PipelineException e) {
                    // Can't recover if the pipeline is broken
                    age = maxAge - 2;
                    return receiveOnly;
                }

            }
            return this;
        }
    };

    private final State sendInventory = new State() {
        @Override
        public State run() {
            Inventory inventory = ((InventoryHolder) container().getBlock().getState()).getInventory();

            boolean sendResult = false;
            try {
                sendResult = pipeline.sendPacket(inventory);
            } catch (PipelineException e) {
                // Can't recover if the pipeline is broken
                age = maxAge - 2;
                return receiveOnly;
            }
            if (sendResult) {
                age = 0;
            }

            return this;
        }
    };
    
    private final State receiveOnly = new State() {
        @Override
        public State run() {
            return this;
        }
    };

    /**
     * Starting state.
     */
    private State state = sendItem;

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
