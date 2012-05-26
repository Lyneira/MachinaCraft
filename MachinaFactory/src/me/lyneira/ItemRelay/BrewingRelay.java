package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.PacketTypeUnsupportedException;
import me.lyneira.MachinaFactory.PipelineEndpoint;
import me.lyneira.MachinaFactory.PipelineException;
import me.lyneira.util.InventoryManager;

import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Item Relay with a brewing stand for automatic brewing.
 * 
 * @author Lyneira
 */
public class BrewingRelay extends ItemRelay {

    protected BrewingRelay(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, blueprint.blueprintBrewing, anchor, yaw, player);
        state = brewingFlush;
    }

    @Override
    protected BlockLocation container() {
        return anchor.getRelative(blueprint.brewingStand.vector(yaw));
    }

    protected boolean handle(Inventory inventory) {
        if (state != brewingCollect) {
            // Even though we don't handle the inventory return true here, so
            // other components don't deactivate too soon.
            return true;
        }
        BrewerInventory brewingInventory = ((BrewingStand) container().getBlock().getState()).getInventory();

        // Determine the lowest tier of potion present in the brewing inventory.
        PotionTier tier = PotionTier.EMPTY;
        int emptySlots = 0;
        for (int i = 0; i < 3; i++) {
            ItemStack item = brewingInventory.getItem(i);
            if (item == null) {
                emptySlots++;
                continue;
            }

            switch (item.getType()) {
            case AIR:
                emptySlots++;
                break;
            case POTION:
                PotionTier current = getTier(item);
                if (current.ordinal() < tier.ordinal())
                    tier = current;
                break;
            default:
                // An incompatible item was found, flush the brewing stand.
                state = brewingFlush;
                return false;
            }
        }

        // Now determine what to do.
        InventoryManager manager = new InventoryManager(inventory);
        ItemStack item = brewingInventory.getIngredient();
        if (emptySlots == 0) {
            // We need an ingredient for our potions now.

            if (item != null && item.getType() != Material.AIR) {
                // Already an ingredient in the brewer.
                return false;
            }

            if (!manager.find(ingredient))
                return false;
            item = manager.get();
            item.setAmount(1);
            brewingInventory.setIngredient(item);
            manager.decrement();
            age = 0;
            return true;
        }
        // We need potions!
        boolean found = false;

        switch (tier) {
        case WATER:
            found = manager.find(waterBottle);
            if (found)
                break;
            return fillWaterBottle(brewingInventory, manager);
        case AWKWARD:
            found = manager.find(awkwardPotion);
            break;
        case POTION:
            found = manager.find(potion);
            break;
        case EMPTY:
            // Completely empty brewer: Search for water bottles first, then
            // empty glass bottles, then awkward, then others.
            found = manager.find(waterBottle);
            if (found)
                break;
            if (fillWaterBottle(brewingInventory, manager))
                return true;
            found = manager.find(awkwardPotion);
            if (found)
                break;
            found = manager.find(potion);
            break;
        default:
            return false;
        }
        if (found) {
            for (int i = 0; i < 3; i++) {
                item = brewingInventory.getItem(i);
                if (item == null || item.getType() == Material.AIR) {
                    // Add potion to the first empty potion slot in the brewing
                    // stand.
                    item = manager.get();
                    brewingInventory.setItem(i, item);
                    manager.clear();
                    age = 0;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean fillWaterBottle(BrewerInventory brewingInventory, InventoryManager manager) {
        if (!(manager.findMaterial(Material.WATER_BUCKET) && manager.findMaterial(Material.GLASS_BOTTLE)))
            return false;
        
        for (int i = 0; i < 3; i++) {
            ItemStack item = brewingInventory.getItem(i);
            if (item == null || item.getType() == Material.AIR) {
                // Add potion to the first empty potion slot in the brewing
                // stand.
                item = new ItemStack(Material.POTION);
                brewingInventory.setItem(i, item);
                manager.decrement();
                age = 0;
                return true;
            }
        }

        return false;
    }

    /**
     * Represents different tiers of potions.
     */
    private enum PotionTier {
        WATER, AWKWARD, POTION, EMPTY
    }

    /**
     * Determines the tier of a given potion.
     * <p/>
     * <b>Pre:</b> The item stack must be of type Material.POTION.
     * 
     * @param item
     * @return
     */
    private static PotionTier getTier(ItemStack item) {
        short durability = item.getDurability();
        if (durability == 0)
            return PotionTier.WATER;
        if ((durability & 0x10) == 0x10)
            return PotionTier.AWKWARD;
        return PotionTier.POTION;
    }

    private static final Predicate<ItemStack> waterBottle = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null || item.getType() != Material.POTION)
                return false;
            return getTier(item) == PotionTier.WATER;
        }
    };

    private static final Predicate<ItemStack> awkwardPotion = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null || item.getType() != Material.POTION)
                return false;
            return getTier(item) == PotionTier.AWKWARD;
        }
    };

    private static final Predicate<ItemStack> potion = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null || item.getType() != Material.POTION)
                return false;
            return getTier(item) == PotionTier.POTION;
        }
    };

    private static final Predicate<ItemStack> ingredient = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            switch (item.getType()) {
            // Primary ingredients:
            case NETHER_STALK:
                // Secondary ingredients:
            case MAGMA_CREAM:
            case SUGAR:
            case SPECKLED_MELON:
            case SPIDER_EYE:
            case GHAST_TEAR:
            case BLAZE_POWDER:
                // Tertiary ingredients:
            case REDSTONE:
            case GLOWSTONE_DUST:
            case FERMENTED_SPIDER_EYE:
            case SULPHUR:
                return true;
            default:
                return false;
            }
        }
    };

    /**
     * In this state the brewing relay waits until the brewing stand becomes
     * active. Inventories sent to it in this state will be used to fill the
     * brewer.
     */
    private static final State brewingCollect = new State() {
        @Override
        public State run(ItemRelay relay) {
            BrewingStand brewer = (BrewingStand) ((BrewingRelay) relay).container().getBlock().getState();
            if (brewer.getBrewingTime() > 0) {
                return brewingActive;
            }
            return this;
        }
    };

    /**
     * In this state the brewing relay waits until brewing is complete.
     */
    private static final State brewingActive = new State() {
        @Override
        public State run(ItemRelay relay) {
            BrewingRelay brewingRelay = (BrewingRelay) relay;
            BrewingStand brewer = (BrewingStand) brewingRelay.container().getBlock().getState();
            if (brewer.getBrewingTime() > 0) {
                brewingRelay.age = 0;
                return this;
            }
            return brewingFlush;
        }
    };

    private static final State brewingFlush = new State() {
        @Override
        public State run(ItemRelay relay) {
            InventoryManager manager = new InventoryManager(((InventoryHolder) relay.container().getBlock().getState()).getInventory());

            // If no items remain, the brewer is ready for another cycle.
            if (!manager.findFirst()) {
                return brewingCollect;
            }

            ItemStack item = manager.get();
            item.setAmount(1);
            try {
                if (relay.pipeline.sendPacket(item)) {
                    manager.decrement();
                    relay.age = 0;
                }
            } catch (PacketTypeUnsupportedException e) {
                // Can't recover.
                return null;
            } catch (PipelineException e) {
                // Can't recover.
                return null;
            }
            return this;
        }
    };

    /**
     * Listener for inventories.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            return ((BrewingRelay) endpoint).handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler brewingHandler = new PacketHandler(inventoryListener);

    @Override
    public PacketHandler getHandler() {
        return brewingHandler;
    }
}
