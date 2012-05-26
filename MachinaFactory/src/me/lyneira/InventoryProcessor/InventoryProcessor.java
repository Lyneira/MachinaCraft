package me.lyneira.InventoryProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentBlueprint;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.PipelineEndpoint;

/**
 * A Component that processes a received inventory. It has no buffer inventory
 * of its own, so it registers the holders of any inventory sent to it. The
 * registered holders will have their inventories processed during a heartbeat.
 * <p>
 * When handling an inventory, it will reply 'true' to inventories with new
 * holders, otherwise the result of the last process transaction.
 * </p>
 * 
 * @author Lyneira
 */
public abstract class InventoryProcessor extends Component implements PipelineEndpoint {

    private final List<InventoryHolder> transactions;
    private boolean response = false;

    protected InventoryProcessor(ComponentBlueprint blueprint, BlockLocation anchor, BlockRotation yaw) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, anchor, yaw);
        transactions = new ArrayList<InventoryHolder>(8);
    }

    @Override
    public final HeartBeatEvent heartBeat(BlockLocation anchor) {
        response = false;
        for (Iterator<InventoryHolder> it = transactions.iterator(); it.hasNext();) {
            InventoryHolder holder = it.next();
            it.remove();
            Inventory inventory = holder.getInventory();
            if (inventory == null) {
                // Null inventory, respond with false
                continue;
            }

            try {
                if (process(inventory))
                    response = true;
            } catch (ProcessInventoryException e) {
                return null;
            }
        }
        return postTransactions();
    }

    /**
     * Called for each registered inventory during a heartbeat.
     * 
     * @param inventory
     *            A non-null inventory.
     * @return True if the inventory was successfully processed, false
     *         otherwise.
     * @throws ProcessInventoryException
     *             The {@link InventoryProcessor} cannot continue.
     */
    protected abstract boolean process(Inventory inventory) throws ProcessInventoryException;

    /**
     * Called after transactions have been processed successfully.
     * 
     * @return A {@link HeartBeatEvent} specifying the delay for the next
     *         heartbeat, or null to stop.
     */
    protected abstract HeartBeatEvent postTransactions();

    /**
     * Add the holder of this inventory to the transaction list. Null holders
     * are not allowed.
     * 
     * @param inventory
     * @return True if the holder's inventory has never been processed,
     *         otherwise the result of the last transaction.
     */
    private boolean handle(Inventory inventory) {
        if (inventory == null)
            return false;

        InventoryHolder holder = inventory.getHolder();
        if (holder == null)
            return false;
        transactions.add(holder);

        return response;
    }

    /**
     * Listener for inventories.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            return ((InventoryProcessor) endpoint).handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler handler = new PacketHandler(inventoryListener);

    @Override
    public final PacketHandler getHandler() {
        return handler;
    }
}
