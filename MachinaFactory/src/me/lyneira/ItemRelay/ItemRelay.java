package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentBlueprint;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.ContainerEndpoint;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.PacketTypeUnsupportedException;
import me.lyneira.MachinaFactory.Pipeline;
import me.lyneira.MachinaFactory.PipelineEndpoint;
import me.lyneira.MachinaFactory.PipelineException;
import me.lyneira.util.InventoryManager;

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
    protected static final int maxAge = 10;

    protected final Blueprint blueprint;
    protected final Pipeline pipeline;
    protected final Player player;
    protected int age = 0;

    protected ItemRelay(Blueprint blueprint, ComponentBlueprint componentBlueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
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
        state = state.run(this);
        if (state == null)
            return null;

        return new HeartBeatEvent(delay);
    }

    /**
     * Called before item sending takes place. Overridde if necessary.
     */
    protected void relayActions() {

    }

    protected abstract BlockLocation container();

    private final BlockLocation sender() {
        return anchor.getRelative(blueprint.sender.vector(yaw));
    }

    protected boolean handle(ItemStack item) {
        if (ContainerEndpoint.handle(container(), item)) {
            age = 0;
            return true;
        }
        return false;
    }

    /**
     * Adaptor interface for different states
     */
    protected interface State {
        /**
         * Runs this state and returns the next state.
         * 
         * @return The new state, or null if it should end.
         */
        State run(ItemRelay relay);
    }

    /**
     * Attempts to send an item through the pipeline.
     */
    protected static final State sendItem = new State() {
        @Override
        public State run(ItemRelay relay) {
            InventoryManager manager = new InventoryManager(((InventoryHolder) relay.container().getBlock().getState()).getInventory());
            if (manager.findFirst()) {
                ItemStack item = manager.get();
                item.setAmount(1);
                try {
                    if (relay.pipeline.sendPacket(item)) {
                        manager.decrement();
                        relay.age = 0;
                    }
                } catch (PacketTypeUnsupportedException e) {
                    return sendInventory.run(relay);
                } catch (PipelineException e) {
                    // Can't recover, go to receive only mode.
                    relay.age = maxAge - 2;
                    return receiveOnly;
                }
            }
            return this;
        }
    };

    protected static final State sendInventory = new State() {
        @Override
        public State run(ItemRelay relay) {
            Inventory inventory = ((InventoryHolder) relay.container().getBlock().getState()).getInventory();

            try {
                if (relay.pipeline.sendPacket(inventory)) {
                    relay.age = 0;
                }
            } catch (PacketTypeUnsupportedException e) {
                // Can't recover, go to receive only mode.
                relay.age = maxAge - 2;
                return receiveOnly;
            } catch (PipelineException e) {
                // Can't recover, go to receive only mode.
                relay.age = maxAge - 2;
                return receiveOnly;
            }

            return this;
        }
    };

    protected static final State receiveOnly = new State() {
        @Override
        public State run(ItemRelay relay) {
            return this;
        }
    };

    /**
     * Starting state.
     */
    protected State state = sendItem;

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

    private static final PacketHandler handler = new PacketHandler(itemStackListener);

    @Override
    public PacketHandler getHandler() {
        return handler;
    }
}
