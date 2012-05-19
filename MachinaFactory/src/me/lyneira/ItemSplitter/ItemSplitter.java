package me.lyneira.ItemSplitter;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

import me.lyneira.InventoryProcessor.InventoryProcessor;
import me.lyneira.InventoryProcessor.ProcessInventoryException;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketTypeUnsupportedException;
import me.lyneira.MachinaFactory.Pipeline;
import me.lyneira.MachinaFactory.PipelineException;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.ItemUtils;

public class ItemSplitter extends InventoryProcessor {

    protected static final int delay = 20;
    /**
     * Number of ticks to stay active when the item relay cannot do anything.
     */
    protected static final int maxAge = 11;

    Blueprint blueprint;
    protected final Player player;
    protected int age = 0;
    private final Sender senderLeft;
    private final Sender senderRight;
    private Sender sender;
    private State state = new DualPipe();

    protected ItemSplitter(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        this.player = player;

        Pipeline pipelineLeft = new Pipeline(anchor, player, anchor.getRelative(blueprint.senderLeft.vector(yaw)));
        BlockLocation filterLeft = anchor.getRelative(blueprint.filterLeft.vector(yaw));

        Pipeline pipelineRight = new Pipeline(anchor, player, anchor.getRelative(blueprint.senderRight.vector(yaw)));
        BlockLocation filterRight = anchor.getRelative(blueprint.filterRight.vector(yaw));

        senderLeft = new Sender(pipelineLeft, filterLeft);
        senderRight = new Sender(pipelineRight, filterRight);
        sender = senderLeft;
    }

    @Override
    protected boolean process(Inventory inventory) throws ProcessInventoryException {
        InventoryManager manager = new InventoryManager(inventory);
        return state.run(manager);
    }

    @Override
    protected HeartBeatEvent postTransactions() {
        if (++age >= maxAge)
            return null;
        return new HeartBeatEvent(delay);
    }

    protected interface State {
        public boolean run(InventoryManager manager) throws ProcessInventoryException;
    }

    protected class DualPipe implements State {
        @Override
        public boolean run(InventoryManager manager) throws ProcessInventoryException {
            switch (sender.matchFilterWithPriority(manager, getOpposite())) {
            case FOUND:
                break;
            case FILTERED_NOMATCH:
                switchSender();
                switch (sender.matchFilterWithPriority(manager, getOpposite())) {
                case FOUND:
                    break;
                default:
                    return false;
                }
                break;
            default:
                return false;
            }
            // We've found a match in one of the senders. Try to send the item.
            ItemStack item = manager.get();
            item.setAmount(1);
            try {
                if (sender.pipeline.sendPacket(item)) {
                    manager.decrement();
                    age = 0;
                    switchSender();
                    return true;
                } else {
                    return false;
                }
            } catch (PipelineException e) {
            } catch (PacketTypeUnsupportedException e) {
            }
            switchSender();
            state = new SinglePipe();
            return state.run(manager);
        }

        protected final void switchSender() {
            if (sender == senderLeft)
                sender = senderRight;
            else
                sender = senderLeft;
        }

        protected Sender getOpposite() {
            if (sender == senderLeft)
                return senderRight;
            else
                return senderLeft;
        }
    }

    protected class SinglePipe implements State {
        @Override
        public boolean run(InventoryManager manager) throws ProcessInventoryException {
            if (!sender.matchFilter(manager)) {
                return false;
            }

            // We've found a match in one of the senders. Try to send the item.
            ItemStack item = manager.get();
            item.setAmount(1);
            try {
                if (sender.pipeline.sendPacket(item)) {
                    manager.decrement();
                    age = 0;
                    return true;
                } else {
                    return false;
                }
            } catch (PipelineException e) {
            } catch (PacketTypeUnsupportedException e) {
            }
            throw new ProcessInventoryException();
        }
    }

    /**
     * Adaptor class to easily switch between the left and right sender.
     */
    protected class Sender {
        final Pipeline pipeline;
        final Block filterChest;

        protected Sender(Pipeline pipeline, BlockLocation filter) {
            this.pipeline = pipeline;
            this.filterChest = filter.getRelative(BlockFace.UP).getBlock();
        }

        /**
         * Returns true if the InventoryManager contains an item that matches
         * the filter of this sender. The InventoryManager's cursor will be set
         * to this item.
         * 
         * @param manager
         *            The InventoryManager
         * @return True if an item was found, false otherwise.
         */
        protected boolean matchFilter(InventoryManager manager) {
            if (filterChest.getType() == Material.CHEST) {
                Inventory inventory = ((InventoryHolder) filterChest.getState()).getInventory();
                return match(inventory, manager);
            } else {
                // No chest here, so anything matches.
                return manager.findFirst();
            }
        }

        /**
         * Returns true if the InventoryManager contains an item that matches
         * the filter of this sender. The InventoryManager's cursor will be set
         * to this item. Prioritizes
         * 
         * @param manager
         *            The InventoryManager
         * @param sender
         *            The opposite sender to exclude items from.
         * @return True if an item was found, false otherwise.
         */
        protected SenderSearchResult matchFilterWithPriority(final InventoryManager manager, Sender opposite) {
            if (filterChest.getType() == Material.CHEST) {
                // Both sides have a chest, so find anything that matches this
                // chest
                Inventory inventory = ((InventoryHolder) filterChest.getState()).getInventory();
                if (match(inventory, manager))
                    return SenderSearchResult.FOUND;
                return SenderSearchResult.FILTERED_NOMATCH;
            } else {
                // No chest here.
                if (opposite.filterChest.getType() == Material.CHEST) {
                    // The other side has a chest, so we have to find an item
                    // that's not in the opposite's chest.
                    if (manager.find(new NotInContents(((InventoryHolder) opposite.filterChest.getState()).getInventory().getContents())))
                        return SenderSearchResult.FOUND;
                    return SenderSearchResult.FILTERED_NOMATCH;
                } else {
                    // No chest there either, send anything.
                    if (manager.findFirst())
                        return SenderSearchResult.FOUND;
                    return SenderSearchResult.EMPTY;
                }
            }
        }

        /**
         * Matches an item in the given sample inventory to an item in the given
         * manager's inventory. If an item was matched, the manager's cursor
         * will be set to it.
         * 
         * @param inventory
         *            The sample inventory
         * @param manager
         *            The manager to match against
         * @return True if an item was found, false otherwise.
         */
        private boolean match(Inventory inventory, InventoryManager manager) {
            ItemStack[] filterContents = inventory.getContents();
            for (ItemStack item : filterContents) {
                if (item == null)
                    continue;
                if (manager.findItemType(item)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Returns true for an {@link ItemStack} which is not in the given array of
     * inventory contents.
     */
    private class NotInContents implements Predicate<ItemStack> {
        private final ItemStack[] contents;

        private NotInContents(final ItemStack[] contents) {
            this.contents = contents;
        }

        @Override
        public final boolean apply(final ItemStack item) {
            if (item == null)
                return false;
            for (ItemStack i : contents) {
                if (ItemUtils.itemSafeEqualsTypeAndData(item, i))
                    return false;
            }
            return true;
        }
    };

    /**
     * Possible search results when searching the sender inventory with a
     * potential filter.
     */
    private enum SenderSearchResult {
        /**
         * The sending inventory was empty. Should not switch senders or to
         * single pipe state.
         */
        EMPTY,
        /**
         * This side contains a filter and no matching item was found.
         */
        FILTERED_NOMATCH,
        /**
         * An item to send was found.
         */
        FOUND
    }
}
