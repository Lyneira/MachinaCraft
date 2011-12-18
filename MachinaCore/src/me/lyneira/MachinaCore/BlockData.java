package me.lyneira.MachinaCore;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Class that contains useful information about block types.
 * 
 * @author Lyneira
 */
public final class BlockData {
    private static final int numBlocks = 123;
    private static final BlockData[] blockId = new BlockData[numBlocks];
    private static final Random generator = new Random();

    /**
     * Enum for time in server ticks that it takes to break blocks.
     */
    private static enum BreakTime {
        NETHERRACK(4), FAST(7), MEDIUM(15), SLOW(20), IRON_DIAMOND_BLOCK(25), OBSIDIAN(220);

        public final int time;

        BreakTime(int time) {
            this.time = time;
        }
    }

    private BlockData() {
        // Only this class can instantiate itself.
    }

    /**
     * Returns the ItemStack that would result from a player breaking the given
     * Block with an appropriate tool.
     * 
     * @param location
     *            The location where the block is being broken
     * @return The item stack resulting from the block break, or null if there
     *         is no drop
     */
    public static final ItemStack breakBlock(final BlockLocation location) {
        Block block = location.getBlock();
        BlockData data;
        int blockType = block.getTypeId();
        try {
            data = blockId[blockType];
        } catch (Exception e) {
            return null;
        }
        // Got a BlockMetaData, now determine what to drop
        int item;
        byte dataValue;
        if (data.drop == 0) {
            return null;
        } else if (data.drop < 0) {
            item = blockType;
        } else {
            item = data.drop;
        }
        // Determine if we should use the Block's data value or the predefined
        // one
        if (data.data < 0) {
            dataValue = block.getData();
        } else {
            dataValue = (byte) data.data;
        }
        // Determine drop amount
        int amount;
        if (data.dropRandom > 0) {
            amount = data.dropMin + generator.nextInt(data.dropRandom);
        } else {
            amount = data.dropMin;
        }
        if (amount > 0) {
            return new ItemStack(item, amount, (short) 0, new Byte(dataValue));
        } else {
            return null;
        }
    }

    /**
     * Whether this block type is solid, meaning whether a device can rest on
     * it.
     */
    private boolean solid = false;

    /**
     * Whether this blockId can be drilled.
     */
    private boolean drillable = false;

    /**
     * How long (in server ticks) it takes to drill this block.
     */
    private int drillTime = 1;

    /**
     * What item Id will be dropped when this block is drilled. A value of 0
     * means nothing will be dropped. A negative value means the broken block's
     * typeId will be used.
     */
    private int drop = -1;

    /**
     * Metadata of the item to be dropped. A negative value means the broken
     * block's data will be used.
     */
    private int data = -1;

    /**
     * The minimum amount that will be dropped. If 0 or negative, relies on
     * dropRandom increasing to above 0 for anything to drop.
     */
    private int dropMin = 1;

    /**
     * A random amount from 0 to (dropRandom - 1) that will be dropped in
     * addition to dropMin.
     */
    private int dropRandom = 0;

    /**
     * Whether this block's data must be copied during a move.
     */
    private boolean copyData = false;

    /**
     * Whether this block has an inventory.
     */
    private boolean hasInventory = false;

    /**
     * Whether this block can only exist while attached to another block.
     */
    private boolean attached = false;

    static {
        // The Big Scary Static Init of Everything Block Related...
        for (int i = 0; i < numBlocks; i++) {
            blockId[i] = new BlockData();
        }

        blockId[Material.STONE.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time).drop(Material.COBBLESTONE.getId());

        blockId[Material.GRASS.getId()].solid().drillable().drillTime(BreakTime.FAST.time).drop(Material.DIRT.getId());

        blockId[Material.DIRT.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.COBBLESTONE.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.WOOD.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.SAPLING.getId()].drillable().copyData().attached();

        blockId[Material.BEDROCK.getId()].solid();

        blockId[Material.WATER.getId()].copyData();

        blockId[Material.STATIONARY_WATER.getId()].copyData();

        blockId[Material.LAVA.getId()].copyData();

        blockId[Material.STATIONARY_LAVA.getId()].copyData();

        blockId[Material.SAND.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.GRAVEL.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.GOLD_ORE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);

        blockId[Material.IRON_ORE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);

        blockId[Material.COAL_ORE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time).drop(Material.COAL.getId());

        blockId[Material.LOG.getId()].solid().drillable().copyData().drillTime(BreakTime.MEDIUM.time);

        // 1 in 20 chance to drop 1 sapling
        blockId[Material.LEAVES.getId()].solid().drillable().copyData().drillTime(BreakTime.FAST.time).drop(Material.SAPLING.getId()).dropMin(-18).dropRandom(20);

        blockId[Material.SPONGE.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        // Easter: Glass blocks can be recovered with a drill
        blockId[Material.GLASS.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.LAPIS_ORE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time).drop(Material.INK_SACK.getId()).data(4).dropMin(4).dropRandom(4);

        blockId[Material.LAPIS_BLOCK.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);

        blockId[Material.DISPENSER.getId()].solid().copyData().inventory();

        blockId[Material.SANDSTONE.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.NOTE_BLOCK.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.BED_BLOCK.getId()].copyData();

        blockId[Material.POWERED_RAIL.getId()].drillable().data(0).copyData().attached();

        blockId[Material.DETECTOR_RAIL.getId()].drillable().data(0).copyData().attached();

        blockId[Material.PISTON_STICKY_BASE.getId()].solid().copyData();

        blockId[Material.WEB.getId()].drillable().drillTime(BreakTime.MEDIUM.time);

        // Drop seeds 1 in 8 chance
        blockId[Material.LONG_GRASS.getId()].drillable().copyData().attached().drop(Material.SEEDS.getId()).dropMin(-6).drop(8).data(0);

        // Easter: Drop dead bush
        blockId[Material.DEAD_BUSH.getId()].drillable().attached();

        blockId[Material.PISTON_BASE.getId()].solid().copyData();

        blockId[Material.PISTON_EXTENSION.getId()].solid().copyData();

        blockId[Material.WOOL.getId()].solid().drillable().copyData().drillTime(BreakTime.FAST.time);

        blockId[Material.YELLOW_FLOWER.getId()].drillable().attached();

        blockId[Material.RED_ROSE.getId()].drillable().attached();

        blockId[Material.BROWN_MUSHROOM.getId()].drillable().attached();

        blockId[Material.RED_MUSHROOM.getId()].drillable().attached();

        blockId[Material.GOLD_BLOCK.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);

        blockId[Material.IRON_BLOCK.getId()].solid().drillable().drillTime(BreakTime.IRON_DIAMOND_BLOCK.time);

        blockId[Material.DOUBLE_STEP.getId()].solid().drillable().copyData().drillTime(BreakTime.MEDIUM.time).drop(Material.STEP.getId()).dropMin(2);

        blockId[Material.STEP.getId()].solid().drillable().copyData().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.BRICK.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);

        blockId[Material.TNT.getId()].solid().drillable().drillTime(BreakTime.FAST.time);

        blockId[Material.BOOKSHELF.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time).drop(Material.BOOK.getId()).dropMin(3);

        blockId[Material.MOSSY_COBBLESTONE.getId()].solid().drillable().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.OBSIDIAN.getId()].solid().drillable().drillTime(BreakTime.OBSIDIAN.time);

        blockId[Material.TORCH.getId()].drillable().data(0).copyData().attached();

        blockId[Material.FIRE.getId()].drillable().drop(0).copyData();

        blockId[Material.MOB_SPAWNER.getId()].solid();

        blockId[Material.WOOD_STAIRS.getId()].drillable().solid().copyData().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.CHEST.getId()].solid().copyData().inventory();

        blockId[Material.REDSTONE_WIRE.getId()].drillable().attached().drop(Material.REDSTONE.getId()).data(0);

        blockId[Material.DIAMOND_ORE.getId()].drillable().solid().drillTime(BreakTime.SLOW.time).drop(Material.DIAMOND.getId());

        blockId[Material.DIAMOND_BLOCK.getId()].drillable().solid().drillTime(BreakTime.IRON_DIAMOND_BLOCK.time);

        blockId[Material.WORKBENCH.getId()].drillable().solid().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.CROPS.getId()].drillable().copyData().attached().drop(Material.SEEDS.getId()).data(0);

        blockId[Material.SOIL.getId()].drillable().solid().copyData().drillTime(BreakTime.FAST.time).drop(Material.DIRT.getId()).data(0);

        blockId[Material.FURNACE.getId()].solid().copyData().inventory();

        blockId[Material.BURNING_FURNACE.getId()].solid().copyData().inventory();

        blockId[Material.SIGN_POST.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).drop(Material.SIGN.getId()).data(0);

        blockId[Material.WOODEN_DOOR.getId()].copyData().attached();

        blockId[Material.LADDER.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.RAILS.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.COBBLESTONE_STAIRS.getId()].drillable().solid().copyData().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.WALL_SIGN.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).drop(Material.SIGN.getId()).data(0);

        blockId[Material.LEVER.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.STONE_PLATE.getId()].drillable().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.IRON_DOOR_BLOCK.getId()].copyData().attached();

        blockId[Material.WOOD_PLATE.getId()].drillable().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.REDSTONE_ORE.getId()].drillable().solid().drillTime(BreakTime.SLOW.time).drop(Material.REDSTONE.getId()).data(0).dropMin(4).dropRandom(2);

        blockId[Material.GLOWING_REDSTONE_ORE.getId()].drillable().solid().drillTime(BreakTime.SLOW.time).drop(Material.REDSTONE.getId()).data(0).dropMin(4).dropRandom(2);

        blockId[Material.REDSTONE_TORCH_OFF.getId()].drillable().copyData().attached().drop(Material.REDSTONE_TORCH_ON.getId()).data(0);

        blockId[Material.REDSTONE_TORCH_ON.getId()].drillable().copyData().attached().data(0);

        blockId[Material.STONE_BUTTON.getId()].drillable().copyData().attached().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.SNOW.getId()].drillable().copyData().attached().drop(Material.SNOW_BALL.getId()).data(0);

        blockId[Material.ICE.getId()].drillable().solid().drillTime(BreakTime.MEDIUM.time).drop(0);

        blockId[Material.SNOW_BLOCK.getId()].drillable().solid().drillTime(BreakTime.FAST.time);

        // Cactus non-solid for the benefit of builder machina
        blockId[Material.CACTUS.getId()].drillable().attached().drillTime(BreakTime.FAST.time).data(0);

        blockId[Material.CLAY.getId()].drillable().solid().drillTime(BreakTime.FAST.time).drop(Material.CLAY_BALL.getId()).dropMin(4);

        blockId[Material.SUGAR_CANE_BLOCK.getId()].drillable().attached().drillTime(BreakTime.FAST.time).drop(Material.SUGAR_CANE.getId()).data(0);

        blockId[Material.JUKEBOX.getId()].solid().copyData();

        blockId[Material.FENCE.getId()].drillable().solid().drillTime(BreakTime.MEDIUM.time);

        blockId[Material.PUMPKIN.getId()].drillable().solid().drillTime(BreakTime.FAST.time).data(0);

        blockId[Material.NETHERRACK.getId()].drillable().solid().drillTime(BreakTime.NETHERRACK.time);

        blockId[Material.SOUL_SAND.getId()].drillable().solid().drillTime(BreakTime.FAST.time);

        // Easter: Drop full Glowstone amount
        blockId[Material.GLOWSTONE.getId()].drillable().solid().drillTime(BreakTime.FAST.time).drop(Material.GLOWSTONE_DUST.getId()).dropMin(4);

        blockId[Material.JACK_O_LANTERN.getId()].drillable().solid().copyData().drillTime(BreakTime.FAST.time).data(0);

        // The cake is a lie!
        blockId[Material.CAKE_BLOCK.getId()].drillable().drop(0).copyData().attached().drillTime(BreakTime.FAST.time);

        blockId[Material.DIODE_BLOCK_OFF.getId()].drillable().copyData().attached().drop(Material.DIODE.getId()).data(0);

        blockId[Material.DIODE_BLOCK_ON.getId()].drillable().copyData().attached().drop(Material.DIODE.getId()).data(0);

        blockId[Material.LOCKED_CHEST.getId()].solid();

        blockId[Material.TRAP_DOOR.getId()].drillable().copyData().attached().drillTime(BreakTime.FAST.time).data(0);

        blockId[Material.MONSTER_EGGS.getId()].drillable().solid().copyData().drillTime(BreakTime.MEDIUM.time).drop(Material.COBBLESTONE.getId()).data(0);

        blockId[Material.SMOOTH_BRICK.getId()].drillable().solid().copyData().drillTime(BreakTime.SLOW.time);

        blockId[Material.HUGE_MUSHROOM_1.getId()].drillable().solid().copyData().drillTime(BreakTime.FAST.time).drop(Material.BROWN_MUSHROOM.getId()).dropMin(-7).dropRandom(10);

        blockId[Material.HUGE_MUSHROOM_2.getId()].drillable().solid().copyData().drillTime(BreakTime.FAST.time).drop(Material.RED_MUSHROOM.getId()).dropMin(-7).dropRandom(10);

        blockId[Material.IRON_FENCE.getId()].drillable().solid().drillTime(BreakTime.FAST.time);

        blockId[Material.THIN_GLASS.getId()].drillable().solid().drillTime(BreakTime.FAST.time);

        blockId[Material.MELON_BLOCK.getId()].drillable().solid().drillTime(BreakTime.FAST.time);

        blockId[Material.PUMPKIN_STEM.getId()].drillable().drop(0).copyData().attached();

        blockId[Material.MELON_STEM.getId()].drillable().drop(0).copyData().attached();

        blockId[Material.VINE.getId()].drillable().copyData().attached().drillTime(BreakTime.FAST.time).data(0);

        blockId[Material.FENCE_GATE.getId()].drillable().solid().copyData().drillTime(BreakTime.MEDIUM.time).data(0);

        blockId[Material.BRICK_STAIRS.getId()].drillable().solid().copyData().drillTime(BreakTime.SLOW.time);

        blockId[Material.SMOOTH_STAIRS.getId()].drillable().solid().copyData().drillTime(BreakTime.SLOW.time);
        
        blockId[Material.MYCEL.getId()].solid().drillable().drillTime(BreakTime.FAST.time).drop(Material.DIRT.getId());
        
        blockId[Material.WATER_LILY.getId()].drillable().drillTime(BreakTime.FAST.time);
        
        blockId[Material.NETHER_BRICK.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);
        
        blockId[Material.NETHER_FENCE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);
        
        blockId[Material.NETHER_BRICK_STAIRS.getId()].solid().drillable().copyData().drillTime(BreakTime.SLOW.time);
        
        blockId[Material.NETHER_WARTS.getId()].drillable().copyData().drillTime(BreakTime.FAST.time).data(0);
        
        blockId[Material.ENCHANTMENT_TABLE.getId()].solid().drillable().drillTime(BreakTime.OBSIDIAN.time);
        
        blockId[Material.BREWING_STAND.getId()].copyData().inventory();
        
        blockId[Material.CAULDRON.getId()].copyData();
        
        blockId[Material.ENDER_PORTAL.getId()].copyData();
        
        blockId[Material.ENDER_STONE.getId()].solid().drillable().drillTime(BreakTime.SLOW.time);
    }

    // Private setters to make the initialization look better.
    private final BlockData solid() {
        this.solid = true;
        return this;
    }

    private final BlockData drillable() {
        this.drillable = true;
        return this;
    }

    private final BlockData drillTime(final int drillTime) {
        this.drillTime = drillTime;
        return this;
    }

    private final BlockData drop(final int drop) {
        this.drop = drop;
        return this;
    }

    private final BlockData data(final int data) {
        this.data = data;
        return this;
    }

    private final BlockData dropMin(final int dropMin) {
        this.dropMin = dropMin;
        return this;
    }

    private final BlockData dropRandom(final int dropRandom) {
        this.dropRandom = dropRandom;
        return this;
    }

    private final BlockData copyData() {
        this.copyData = true;
        return this;
    }

    private final BlockData inventory() {
        this.hasInventory = true;
        return this;
    }

    private final BlockData attached() {
        this.attached = true;
        return this;
    }

    /**
     * Returns true if the given block type is solid.
     * 
     * @param typeId
     *            The block id to check
     * @return True if the block type is solid
     */
    public static final boolean isSolid(final int typeId) {
        try {
            return blockId[typeId].solid;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the given block type can be drilled.
     * 
     * @param typeId
     *            The block id to check
     * @return True if the block type can be drilled
     */
    public static final boolean isDrillable(final int typeId) {
        try {
            return blockId[typeId].drillable;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns the drill time (in server ticks) for the given block type
     * 
     * @param typeId
     *            The block id to check
     * @return The drill time in server ticks
     */
    public static final int getDrillTime(final int typeId) {
        try {
            return blockId[typeId].drillTime;
        } catch (Exception e) {
            return BreakTime.MEDIUM.time;
        }
    }

    /**
     * Returns true if the given block type has data to be copied during a move.
     * 
     * @param typeId
     *            The block id to check
     * @return True if the block type has data to be copied.
     */
    public static final boolean copyData(final int typeId) {
        try {
            return blockId[typeId].copyData;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the given block type has an inventory.
     * 
     * @param typeId
     *            The block id to check
     * @return True if the block type has an inventory.
     */
    public static final boolean hasInventory(final int typeId) {
        try {
            return blockId[typeId].hasInventory;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Returns true if the given block type can only exist attached to another
     * block.
     * 
     * @param typeId
     *            The block id to check
     * @return True if the block type is attached.
     */
    public static final boolean isAttached(final int typeId) {
        try {
            return blockId[typeId].attached;
        } catch (Exception e) {
            return false;
        }
    }
}
