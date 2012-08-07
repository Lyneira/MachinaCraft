package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.util.InventoryTransaction;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * Item Relay with a dispenser as container. Will also suck in items near it on
 * each tick.
 * 
 * @author Lyneira
 */
public class DispenserRelay extends ItemRelay {

    private static final double suctionDistance = 3;

    DispenserRelay(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, blueprint.blueprintDispenser, anchor, yaw, player);
    }

    /**
     * Sucks items within a certain distance into the dispenser.
     */
    @Override
    protected void relayActions() {
        BlockLocation container = container();
        Location location = container.getLocation();
        for (Entity i : container.getWorld().getEntitiesByClass(Item.class)) {
            if (! i.isDead() && i.getLocation().distance(location) < suctionDistance) {
                ItemStack item = ((Item) i).getItemStack();
                Inventory myInventory = (((InventoryHolder) container().getBlock().getState()).getInventory());
                InventoryTransaction transaction = new InventoryTransaction(myInventory);
                transaction.add(item);
                if (transaction.execute()) {
                    age = 0;
                    i.remove();
                }
            }
        }
    }

    @Override
    protected BlockLocation container() {
        return anchor.getRelative(blueprint.dispenser.vector(yaw));
    }
}
