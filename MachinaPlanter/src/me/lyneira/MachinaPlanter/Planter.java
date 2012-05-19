package me.lyneira.MachinaPlanter;

import java.util.Collection;
import java.util.Random;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.InventoryTransaction;

import org.bukkit.CropState;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import com.google.common.base.Predicate;

class Planter implements Machina {
    private final static Random random = new Random();
    private static int delay = 20;
    private final static int harvestCost = 20;
    private final static int plantingCost = 10;
    static int maxLength = 16;
    static int maxWidth = 10;
    private static boolean harvestWheat = true;
    private static boolean harvestPumpkin = true;
    private static boolean harvestMelon = true;
    private static boolean harvestNetherWart = true;
    private static boolean useEnergy = false;
    private static boolean useTool = true;

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

    private void plant() throws PlantingFailedException {
        if (rail.getRowType() == RailType.SKIP)
            return;
        BlockLocation tile = rail.currentTile();
        BlockLocation crop = tile.getRelative(BlockFace.UP);

        switch (tile.getType()) {
        case DIRT:
            // There may be a pumpkin or melon on it.
            switch (crop.getType()) {
            case PUMPKIN:
                if (harvest && harvestPumpkin)
                    harvestBlock(crop);
                break;
            case MELON_BLOCK:
                if (harvest && harvestMelon)
                    harvestMelon(crop);
                break;
            }
            // Fall through to tilling.
        case GRASS:
            // Till to farmland.
            switch (crop.getType()) {
            case SNOW:
            case LONG_GRASS:
                crop.setEmpty();
            case AIR:
                useEnergy(plantingCost);
                useTool();
                tile.setType(Material.SOIL);
                plantFarmland(crop);
                break;
            }
            break;
        case SOIL:
            // Already farmland.
            switch (crop.getType()) {
            case LONG_GRASS:
            case AIR:
                plantFarmland(crop);
                break;
            case CROPS:
                if (harvest && harvestWheat)
                    if (harvestCrops(crop))
                        plantFarmland(crop);
                break;
            }
            break;
        case SOUL_SAND:
            // Nether wart farming
            switch (crop.getType()) {
            case SNOW:
            case AIR:
                plantNetherWart(crop);
                break;
            case NETHER_WARTS:
                if (harvest && harvestNetherWart)
                    if (harvestNetherWart(crop))
                        plantNetherWart(crop);
                break;
            }
            break;
        }
    }

    private void plantFarmland(BlockLocation crop) throws PlantingFailedException {
        if (rail.getRowType() != RailType.PLANT)
            return;
        InventoryManager manager = new InventoryManager(chestInventory());
        if (!manager.find(seeds))
            return;
        useEnergy(plantingCost);
        useTool();
        ItemStack item = manager.get();
        switch (item.getType()) {
        case SEEDS:
            crop.setType(Material.CROPS);
            break;
        case PUMPKIN_SEEDS:
            crop.setType(Material.PUMPKIN_STEM);
            break;
        case MELON_SEEDS:
            crop.setType(Material.MELON_STEM);
            break;
        }
        manager.decrement();
    }

    private void plantNetherWart(BlockLocation crop) throws PlantingFailedException {
        if (rail.getRowType() == RailType.PLANT)
            return;
        InventoryManager manager = new InventoryManager(chestInventory());
        if (!manager.findMaterial(Material.NETHER_STALK))
            return;
        useEnergy(plantingCost);
        useTool();
        crop.setType(Material.NETHER_WARTS);
        manager.decrement();
    }

    private boolean harvestCrops(BlockLocation crop) throws PlantingFailedException {
        if (((Crops) crop.getBlock().getState().getData()).getState() != CropState.RIPE)
            return false;

        InventoryTransaction transaction = new InventoryTransaction(chestInventory());
        // Hardcoded drops for now, as Block.getDrops() does not return the
        // seeds properly.

        // Added bonus: Seed supply from automatic harvesting will stay stable,
        // players have to harvest manually if they want more seeds.
        transaction.add(new ItemStack(Material.WHEAT));
        transaction.add(new ItemStack(Material.SEEDS));
        return doHarvest(crop, transaction);
    }

    private boolean harvestNetherWart(BlockLocation crop) throws PlantingFailedException {
        byte data = crop.getBlock().getData();
        // Fully grown stage
        if (data != 3)
            return false;

        // Hardcoded drop as Block.getDrops() does not return nether stalk
        // stacks.
        InventoryTransaction transaction = new InventoryTransaction(chestInventory());
        transaction.add(new ItemStack(Material.NETHER_STALK, 2 + random.nextInt() % 4));
        return doHarvest(crop, transaction);
    }

    private boolean harvestMelon(BlockLocation crop) throws PlantingFailedException {
        // Hardcoded drop as Block.getDrops() returns 3-7 melons rather than
        // melon slices.
        InventoryTransaction transaction = new InventoryTransaction(chestInventory());
        transaction.add(new ItemStack(Material.MELON, 3 + random.nextInt() % 5));
        return doHarvest(crop, transaction);
    }

    /**
     * Attempts to break the given crop block and collect the results.
     * 
     * @param crop
     *            The block to harvest.
     * @return True if the results were collected.
     * @throws PlantingFailedException
     */
    private boolean harvestBlock(BlockLocation crop) throws PlantingFailedException {
        Collection<ItemStack> drops = crop.getBlock().getDrops();
        InventoryTransaction transaction = new InventoryTransaction(chestInventory());
        transaction.add(drops);
        return doHarvest(crop, transaction);
    }

    private boolean doHarvest(BlockLocation crop, InventoryTransaction transaction) throws PlantingFailedException {
        if (!transaction.verify())
            return false;
        useEnergy(harvestCost);
        useTool();
        transaction.execute();
        crop.setEmpty();

        return true;
    }

    private void useTool() throws PlantingFailedException {
        if (!useTool)
            return;
        FurnaceInventory furnaceInventory = ((Furnace) furnace.getBlock().getState()).getInventory();
        ItemStack tool = furnaceInventory.getSmelting();
        if (tool == null || tool.getType() == Material.AIR) {
            // Try and find a tool in the chest.
            InventoryManager manager = new InventoryManager(chestInventory());
            if (!manager.find(planterTool))
                throw new PlantingFailedException();
            tool = manager.get();
            furnaceInventory.setSmelting(tool);
            manager.decrement();
            tool = furnaceInventory.getSmelting();
        } else if (!planterTool.apply(tool))
            throw new PlantingFailedException();

        // Use up durability.
        short newDurability = (short) (tool.getDurability() + 1);
        if (newDurability >= tool.getType().getMaxDurability()) {
            furnaceInventory.setSmelting(null);
        } else {
            tool.setDurability(newDurability);
        }
    }
    
    /**
     * Uses the given amount of energy and returns true if successful.
     *
     * @param energy
     *            The amount of energy needed for the next action
     * @return True if enough energy could be used up
     */
    protected void useEnergy(final int energy) throws PlantingFailedException {
        if (!useEnergy)
            return;

        while (currentEnergy < energy) {
            int newFuel = Fuel.consume((Furnace) furnace.getBlock().getState());
            if (newFuel > 0) {
                currentEnergy += newFuel;
            } else {
                throw new PlantingFailedException();
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
                    plant();
                    return plant;
                } catch (PlantingFailedException e) {
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
                    plant();
                    return this;
                } catch (PlantingFailedException e) {
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

    private static final Predicate<ItemStack> seeds = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            switch (item.getType()) {
            case SEEDS:
            case PUMPKIN_SEEDS:
            case MELON_SEEDS:
                return true;
            }
            return false;
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
            }
            return false;
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
        harvestWheat = configuration.getBoolean("harvest-wheat", harvestWheat);
        harvestPumpkin = configuration.getBoolean("harvest-pumpkin", harvestPumpkin);
        harvestMelon = configuration.getBoolean("harvest-melon", harvestMelon);
        harvestNetherWart = configuration.getBoolean("harvest-netherwart", harvestNetherWart);
        useEnergy = configuration.getBoolean("use-energy", useEnergy);
        useTool = configuration.getBoolean("use-tool", useTool);
    }
}
