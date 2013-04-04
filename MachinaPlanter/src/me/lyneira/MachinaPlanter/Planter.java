package me.lyneira.MachinaPlanter;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.Tool;
import me.lyneira.MachinaPlanter.crop.CropCarrot;
import me.lyneira.MachinaPlanter.crop.CropCocoa;
import me.lyneira.MachinaPlanter.crop.CropHandler;
import me.lyneira.MachinaPlanter.crop.CropMelon;
import me.lyneira.MachinaPlanter.crop.CropNetherWart;
import me.lyneira.MachinaPlanter.crop.CropPotato;
import me.lyneira.MachinaPlanter.crop.CropPumpkin;
import me.lyneira.MachinaPlanter.crop.CropWheat;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.InventoryTransaction;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import com.google.common.base.Predicate;

public class Planter implements Machina {
    private static int delay = 20;
    private final static int harvestCost = 20;
    private final static int plantingCost = 10;
    static int maxLength = 16;
    static int maxWidth = 10;
    private static boolean useEnergy = false;
    private static boolean useTool = true;
    private static Map<Material, CropHandler> harvestableMap = new EnumMap<Material, CropHandler>(Material.class);
    private static Map<Material, CropHandler> plantableMap = new EnumMap<Material, CropHandler>(Material.class);

    private final Rail rail;
    private final BlockLocation lever;
    private final BlockLocation base;
    private final BlockLocation chest;
    private final BlockLocation furnace;
    private final BlockRotation furnaceYaw;
    private final boolean harvest;
    private State state;
    private int currentEnergy = 0;

    Planter(Rail rail, BlockLocation lever, BlockLocation base, BlockLocation chest, BlockLocation furnace, BlockRotation furnaceYaw, boolean harvest) {
        this.rail = rail;
        this.lever = lever;
        this.base = base;
        this.chest = chest;
        this.furnace = furnace;
        this.furnaceYaw = furnaceYaw;
        this.harvest = harvest;
        Fuel.setFurnace(furnace.getBlock(), furnaceYaw, true);
        state = activate;
    }

    @Override
    public boolean verify(BlockLocation anchor) {
        if (!(anchor.checkType(Blueprint.anchorMaterial) //
                && lever.checkType(Material.LEVER) //
                && base.checkType(Blueprint.baseMaterial) //
                && chest.checkType(Material.CHEST) //
        && furnace.checkTypes(Material.BURNING_FURNACE)))
            return false;

        return rail.verify();
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        state = state.run();
        if (state == null)
            return null;
        return new HeartBeatEvent(delay);
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        if (player.hasPermission("machinaplanter.activate")) {
            if (state == activate || state == plant)
                state = deactivate;
        }
        return true;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
        Fuel.setFurnace(furnace.getBlock(), furnaceYaw, false);
    }

    /**
     * Runs all the necessary planter actions for the current tile.
     * 
     * @throws NoEnergyException
     */
    private void operateOnTile() throws NoEnergyException {
        if (rail.getRowType() == RailType.SKIP)
            return;
        BlockLocation tile = rail.currentTile();
        BlockLocation crop = tile.getRelative(BlockFace.UP);

        // Attempt to till to farmland
        till(tile, crop);

        if (harvest)
            harvest(crop);

        plant(tile, crop);
    }

    /**
     * Attempts to till the current tile if appropriate.
     * 
     * @param tile
     * @param crop
     * @throws NoEnergyException
     */
    private void till(BlockLocation tile, BlockLocation crop) throws NoEnergyException {
        switch (tile.getType()) {
        case DIRT:
        case GRASS:
            switch (crop.getType()) {
            case SNOW:
            case LONG_GRASS:
                crop.setEmpty();
            case AIR:
                useEnergy(plantingCost);
                try {
                    useTool();
                    tile.setType(Material.SOIL);
                } catch (NoToolException e) {
                }
            default:
                break;
            }
            break;
        default:
            break;
        }
    }

    /**
     * Attempts to harvest the current crop block.
     * 
     * @param crop
     *            The block being harvested
     * @throws NoEnergyException
     */
    private void harvest(BlockLocation crop) throws NoEnergyException {
        CropHandler handler = harvestableMap.get(crop.getType());
        if (handler == null)
            return;

        boolean canHarvest = handler.isRipe(crop);
        if (!canHarvest) {
            if (handler.canUseBonemealAtHarvest()) {
                /*
                 * Use bonemeal, but it may still fail to produce a harvestable
                 * crop.
                 */
                InventoryManager manager = new InventoryManager(chestInventory());
                if (manager.findItemTypeAndData(Material.INK_SACK.getId(), (byte) 15)
                // Attempt to use bonemeal if found
                        && handler.useBonemeal(crop)) {
                    manager.decrement();
                    canHarvest = handler.isRipe(crop);
                }

            }
        }
        if (canHarvest) {
            InventoryTransaction transaction = new InventoryTransaction(chestInventory());
            Collection<ItemStack> drops = handler.getDrops();
            if (drops == null) {
                // Ask Bukkit
                drops = crop.getBlock().getDrops();
            }
            transaction.add(drops);
            useEnergy(harvestCost);
            try {
                useTool();
            } catch (NoToolException e) {
                return;
            }

            if (!transaction.execute()) {
                return;
            }
            crop.setEmpty();
        }
    }

    /**
     * Attempts to plant a crop on the current block.
     * 
     * @param tile
     *            The ground tile being planted in
     * @param crop
     *            The block above the tile
     * @throws NoEnergyException
     */
    private void plant(BlockLocation tile, BlockLocation crop) throws NoEnergyException {
        if (rail.getRowType() != RailType.PLANT)
            return;
        // Cannot plant in a non-empty space.
        if (!crop.isEmpty())
            return;

        InventoryManager manager = new InventoryManager(chestInventory());
        if (!manager.find(plantable))
            return;
        ItemStack item = manager.get();
        Material seedType = item.getType();
        CropHandler handler = plantableMap.get(seedType);
        if (handler == null) {
            MachinaPlanter.log("SEVERE: Got a null CropHandler after finding a suitable item to plant!");
            return;
        }
        if (!handler.canPlant(tile))
            return;

        useEnergy(plantingCost);
        manager.decrement();
        boolean usedBonemeal = false;
        if (handler.canUseBonemealWhilePlanting()) {
            usedBonemeal = manager.findItemTypeAndData(Material.INK_SACK.getId(), (byte) 15);
            if (usedBonemeal) {
                manager.decrement();
            }
        }
        handler.plant(crop, usedBonemeal);
    }

    private void useTool() throws NoToolException {
        if (!useTool)
            return;
        FurnaceInventory furnaceInventory = ((Furnace) furnace.getBlock().getState()).getInventory();
        if (!Tool.useInFurnace(furnaceInventory, planterTool, chestInventory())) {
            throw new NoToolException();
        }
    }

    /**
     * Uses the given amount of energy and returns true if successful.
     * 
     * @param energy
     *            The amount of energy needed for the next action
     * @return True if enough energy could be used up
     */
    protected void useEnergy(final int energy) throws NoEnergyException {
        if (!useEnergy)
            return;

        while (currentEnergy < energy) {
            int newFuel = Fuel.consume((Furnace) furnace.getBlock().getState());
            if (newFuel > 0) {
                currentEnergy += newFuel;
            } else {
                throw new NoEnergyException();
            }
        }
        currentEnergy -= energy;
    }

    private final Inventory chestInventory() {
        return ((InventoryHolder) chest.getBlock().getState()).getInventory();
    }

    private interface State {
        State run();
    }

    private final State activate = new State() {
        @Override
        public State run() {
            if (rail.activate()) {
                try {
                    operateOnTile();
                    return plant;
                } catch (NoEnergyException e) {
                    return deactivate;
                }
            } else
                return parkHead.run();
        }
    };

    private final State deactivate = new State() {
        @Override
        public State run() {
            if (rail.deactivate()) {
                return parkInit;
            } else {
                return null;
            }
        }
    };

    private final State plant = new State() {
        @Override
        public State run() {
            if (rail.nextTile()) {
                try {
                    operateOnTile();
                    return this;
                } catch (NoEnergyException e) {
                    return deactivate;
                }
            } else {
                return deactivate.run();
            }
        }
    };

    /**
     * In this state the planter will retract the moving rail if the head's
     * current direction is backward.
     */
    private final State parkInit = new State() {
        @Override
        public State run() {
            if (rail.isHeadMovingBackward()) {
                rail.retract();
                return parkHead;
            } else {
                return parkHead.run();
            }
        }
    };

    /**
     * In this state the planter will pull the head back to its parking
     * position.
     */
    private final State parkHead = new State() {
        @Override
        public State run() {
            switch (rail.retractHead()) {
            case OK:
                return this;
            case RAIL_END:
                return parkMovingRail.run();
            default:
                return null;
            }
        }
    };

    /**
     * In this state the planter will retract the moving rail back to its
     * parking position.
     */
    private final State parkMovingRail = new State() {
        @Override
        public State run() {
            if (rail.retract()) {
                return this;
            } else {
                return null;
            }
        }
    };

    // Statics

    /**
     * Allows for adding crop types for the planter to handle.
     * 
     * @param handler
     */
    public static void addCrop(CropHandler handler) {
        if (harvestableMap.containsKey(handler.getHarvestableMaterial())) {
            MachinaPlanter.log("Warning: Crophandler " + handler.getClass().getName() + " is overriding existing harvestable mapping for " + handler.getHarvestableMaterial().toString());
        }
        harvestableMap.put(handler.getHarvestableMaterial(), handler);
        if (plantableMap.containsKey(handler.getPlantableItem())) {
            MachinaPlanter.log("Warning: Crophandler " + handler.getClass().getName() + " is overriding existing plantable mapping for " + handler.getPlantableItem().toString());
        }
        plantableMap.put(handler.getPlantableItem(), handler);
    }

    private static final Predicate<ItemStack> plantable = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            CropHandler handler = plantableMap.get(item.getType());
            if (handler == null)
                return false;
            return handler.checkPlantableItemData(item.getData());
        }
    };

    private static final Predicate<ItemStack> planterTool = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            switch (item.getType()) {
            case DIAMOND_HOE:
            case GOLD_HOE:
            case IRON_HOE:
            case STONE_HOE:
            case WOOD_HOE:
                return true;
            default:
                return false;
            }
        }
    };

    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        delay = Math.max(configuration.getInt("action-delay", delay), 1);
        maxLength = Math.min(Math.max(configuration.getInt("max-length", maxLength), 1), 64);
        maxWidth = Math.min(Math.max(configuration.getInt("max-width", maxWidth), 1), 64);
        boolean harvestWheat = configuration.getBoolean("harvest-wheat", true);
        boolean harvestWheatSeeds = configuration.getBoolean("harvest-wheat-seeds", true);
        boolean harvestPumpkin = configuration.getBoolean("harvest-pumpkin", true);
        boolean harvestMelon = configuration.getBoolean("harvest-melon", true);
        boolean harvestNetherWart = configuration.getBoolean("harvest-netherwart", true);
        boolean harvestCarrot = configuration.getBoolean("harvest-carrot", true);
        boolean harvestPotato = configuration.getBoolean("harvest-potato", true);
        boolean harvestCocoa = configuration.getBoolean("harvest-cocoa", true);
        useEnergy = configuration.getBoolean("use-energy", useEnergy);
        useTool = configuration.getBoolean("use-tool", useTool);

        // Add all the default crops.
        addCrop(new CropWheat(harvestWheat, harvestWheatSeeds));
        addCrop(new CropPumpkin(harvestPumpkin));
        addCrop(new CropMelon(harvestMelon));
        addCrop(new CropNetherWart(harvestNetherWart));
        addCrop(new CropCarrot(harvestCarrot));
        addCrop(new CropPotato(harvestPotato));
        addCrop(new CropCocoa(harvestCocoa));
    }
}
