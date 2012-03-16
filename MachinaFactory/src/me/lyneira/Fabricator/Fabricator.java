package me.lyneira.Fabricator;

import java.util.Iterator;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaFactory.Component;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.MachinaFactory;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.Pipeline;
import me.lyneira.MachinaFactory.PipelineEndpoint;

public class Fabricator extends Component implements PipelineEndpoint {

    private static final int delay = 20;
    private static final int maxTicks = 2;

    private final Blueprint blueprint;
    private final Pipeline pipeline;
    private final List<ItemStack> recipe;
    private int tick = 0;

    Fabricator(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint.blueprint, anchor, yaw);
        this.blueprint = blueprint;
        recipe = determineRecipe();
        if (recipe == null) {
            onDeActivate(anchor);
            throw new ComponentActivateException();
        }
        pipeline = new Pipeline(anchor, player, sender());
    }

    private List<ItemStack> determineRecipe() {
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
        if (tick < maxTicks) {
            tick++;
            return new HeartBeatEvent(delay);
        }
        return null;
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

        tick = 0;
        MachinaFactory.log("Fabricator received inventory.");
        // TODO
        return false;
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
