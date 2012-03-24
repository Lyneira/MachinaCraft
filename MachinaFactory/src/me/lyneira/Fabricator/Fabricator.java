package me.lyneira.Fabricator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.Recipe;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaCore.InventoryTransaction;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.PacketTypeUnsupportedException;
import me.lyneira.MachinaFactory.Pipeline;
import me.lyneira.MachinaFactory.PipelineEndpoint;
import me.lyneira.MachinaFactory.PipelineException;

public class Fabricator extends Component implements PipelineEndpoint {

    private static final int delay = 20;
    private static final int maxAge = 10;

    private final Blueprint blueprint;
    private final Pipeline pipeline;
    private final Transaction recipeTransaction;
    private final Set<InventoryHolder> transactions;
    private final Map<InventoryHolder, Boolean> responses;
    private int age = 0;

    Fabricator(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        recipeTransaction = determineRecipe();
        if (recipeTransaction == null) {
            onDeActivate(anchor);
            throw new ComponentActivateException();
        }
        pipeline = new Pipeline(anchor, player, sender());
        transactions = new LinkedHashSet<InventoryHolder>(8);
        responses = new HashMap<InventoryHolder, Boolean>(8);
    }

    private Transaction determineRecipe() {
        Inventory chestInventory = InventoryManager.getSafeInventory(chest().getBlock());
        RecipeVerifier verifier;
        try {
            verifier = new RecipeVerifier(chestInventory);
        } catch (ComponentActivateException e) {
            return null;
        }
        Iterator<Recipe> it = blueprint.plugin.getServer().recipeIterator();
        return verifier.find(it);
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (age++ >= maxAge)
            return null;

        // Handle all transactions here.
        for (Iterator<InventoryHolder> it = transactions.iterator(); it.hasNext();) {
            InventoryHolder holder = it.next();
            it.remove();
            Inventory inventory = holder.getInventory();
            if (inventory == null) {
                // Null inventory = return false
                responses.put(holder, false);
                continue;
            }

            InventoryTransaction transaction = new InventoryTransaction(inventory);
            transaction.remove(recipeTransaction.ingredients);
            if (!transaction.verify()) {
                // Transaction can't be completed, so the sending inventory
                // has not enough items.
                responses.put(holder, false);
                continue;
            }

            boolean sendResult = false;
            try {
                sendResult = pipeline.sendPacket(recipeTransaction.result.clone());
            } catch (PacketTypeUnsupportedException e) {
                // Other end can't handle items
                return null;
            } catch (PipelineException e) {
                // Pipeline is broken
                return null;
            }

            if (sendResult) {
                // Make a new transaction here in case the fabricator fed the
                // result back to the source inventory.
                transaction = new InventoryTransaction(inventory);
                transaction.remove(recipeTransaction.ingredients);
                transaction.execute();
            } else {
                // Other end of the pipeline can't handle this item.
                responses.put(holder, false);
                continue;
            }
            age = 0;
            responses.put(holder, true);
        }

        return new HeartBeatEvent(delay);
    }

    private final BlockLocation chest() {
        return anchor.getRelative(blueprint.chest.vector(yaw));
    }

    private final BlockLocation sender() {
        return anchor.getRelative(blueprint.sender.vector(yaw));
    }

    private boolean handle(Inventory inventory) {
        if (inventory == null)
            return false;

        InventoryHolder holder = inventory.getHolder();
        transactions.add(holder);

        if (responses.containsKey(holder)) {
            return responses.get(holder);
        } else {
            return true;
        }
    }

    /**
     * Listener for inventories.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            return ((Fabricator) endpoint).handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler handler = new PacketHandler(inventoryListener);

    @Override
    public PacketHandler getHandler() {
        return handler;
    }
}
