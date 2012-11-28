package me.lyneira.MachinaCore.machina;

import gnu.trove.map.hash.THashMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.set.hash.THashSet;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.Multiverse;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;

/**
 * Represents all machinae in a world. This universal block store keeps track of
 * all blocks in the world that belong to a machina, and all machinae
 * themselves. It also prevents machinae from intersecting each other and
 * provides fast testing whether any block in the world is part of a
 * {@link Machina}.
 * 
 * @author Lyneira
 */
public class Universe {
    public final World world;
    private final THashMap<BlockVector, Machina> globalMap = new THashMap<BlockVector, Machina>();
    private final THashSet<Machina> machinae = new THashSet<Machina>();

    Universe(World world) {
        this.world = world;
    }

    /**
     * Returns the machina that owns the block at this location. If no machina
     * owns this block, returns null.
     * 
     * @param location
     *            The location to get the machina for.
     * @return The machina owning this location, or null.
     */
    public Machina get(BlockVector location) {
        return globalMap.get(location);
    }

    /**
     * Adds a machina to the universe.
     * 
     * @param machina
     *            The machina to add
     * @return True if the machina was successfully added, false if if there was
     *         a collision or it is already in the universe.
     */
    boolean add(Machina machina) {
        if (machinae.contains(machina))
            return false;

        MachinaBlock[] instance = machina.instance();

        for (MachinaBlock b : instance) {
            if (globalMap.get(b) != null)
                return false;
        }

        // Machina has no collisions, OK to add.
        for (MachinaBlock b : instance) {
            globalMap.put(b, machina);
        }

        machinae.add(machina);

        machina.initialize();
        return true;
    }

    /**
     * Updates a machina in the universe.
     * 
     * @param machina
     *            The machina to update
     * @return True if the machina was successfully updated, false if there was
     *         a collision or it was not in this universe.
     */
    boolean update(Machina machina) {
        if (!machinae.contains(machina))
            return false;

        MachinaUpdate update = machina.model.createUpdate();
        BlockVector[] oldBlocks = update.oldBlocks;
        MachinaBlock[] newBlocks = update.newBlocks;
        ItemStack[][] inventories = update.inventories;

        for (MachinaBlock b : newBlocks) {
            MachinaCore.info("Going to check block " + b.toString() + "Actual block is: " + b.getBlock(world).toString());
            Machina m = globalMap.get(b);

            if (m == null) {
                // Do collision detection here.
                // TODO Expand collision detection to allow grass/snow as empty
                if (!b.getBlock(world).isEmpty()) {
                    MachinaCore.info("The block was not empty!");
                    return false;
                }
                MachinaCore.info("Empty block");
                continue;
            } else if (m != machina) {
                // Block belongs to another machina, definitely a collision.
                MachinaCore.info("It belongs to another machina");
                return false;
            }
            MachinaCore.info("Block belongs to this machina");
            /*
             * If neither of the above is true, the block belongs to this
             * machina's existing instance, that's never a collision
             */
        }

        // Machina has no collisions, OK to update.
        THashSet<BlockVector> updateClearSet = new THashSet<BlockVector>(oldBlocks.length);
        for (BlockVector v : oldBlocks) {
            globalMap.remove(v);
            updateClearSet.add(v);
        }
        // Write out updated machina instance
        for (int i = 0; i < newBlocks.length; i++) {
            final MachinaBlock b = newBlocks[i];
            globalMap.put(b, machina);
            updateClearSet.remove(b);

            final Block block = b.getBlock(world);
            final int typeId = block.getTypeId();
            /*
             * Even if the block type is not modified, this is still necessary
             * in the case of a machina moving in the direction of a double
             * chest or a very lucky teleport.
             */
            if (BlockData.hasInventory(typeId)) {
                try {
                    if (typeId == BlockData.chestId) {
                        ((Chest) block.getState()).getBlockInventory().clear();
                    } else {
                        ((InventoryHolder) block.getState()).getInventory().clear();
                    }
                } catch (Throwable e) {
                    MachinaCore.severe("Unsuccessful attempt to clear inventory from block before write: " + block.toString());
                    e.printStackTrace();
                }
            }
            if (b.typeId != typeId || b.data != block.getData()) {
                block.setTypeIdAndData(b.typeId, (byte) b.data, false);
            }

            final ItemStack[] contents = inventories[i];
            if (contents == null)
                continue;
            final Inventory inventory;
            try {
                if (b.typeId == BlockData.chestId) {
                    inventory = ((Chest) block.getState()).getBlockInventory();
                } else {
                    inventory = ((InventoryHolder) block.getState()).getInventory();
                }
                inventory.setContents(contents);
            } catch (Throwable e) {
                MachinaCore.warning("While updating a machina, attempted to write inventory to a block that does not seem to support it! - block: " + block.toString() + ", inventory size: "
                        + contents.length);
                e.printStackTrace();
            }
        }
        // Clear blocks that the machina left empty
        updateClearSet.forEach(clearRemovedVectors);

        return true;
    }

    /**
     * Removes a machina from the universe.
     * 
     * @param machina
     *            The machina to remove
     */
    public void remove(Machina machina) {
        if (!machinae.contains(machina))
            return;

        machina.onRemove();

        for (BlockVector v : machina.instance()) {
            globalMap.remove(v);
        }
        machinae.remove(machina);
    }

    void load() {
        // TODO
    }

    void unload() {
        // TODO
    }

    void save() {
        // TODO
    }

    private TObjectProcedure<BlockVector> clearRemovedVectors = new TObjectProcedure<BlockVector>() {
        @Override
        public boolean execute(BlockVector v) {
            final Block block = v.getBlock(world);
            int typeId = block.getTypeId();
            if (BlockData.hasInventory(typeId)) {
                try {
                    if (typeId == BlockData.chestId) {
                        ((Chest) block.getState()).getBlockInventory().clear();
                    } else {
                        ((InventoryHolder) block.getState()).getInventory().clear();
                    }
                } catch (Throwable e) {
                    MachinaCore.severe("Unsuccessful attempt to clear inventory from block after write: " + block.toString());
                    e.printStackTrace();
                }
            }
            block.setTypeIdAndData(0, (byte) 0, false);
            return true;
        }
    };

    public final static Multiverse.UniverseFriend multiverseFriend = new Multiverse.UniverseFriend() {
        @Override
        protected Universe create(World world) {
            return new Universe(world);
        }

        @Override
        protected void load(Universe universe) {
            universe.load();
        }

        @Override
        protected void unload(Universe universe) {
            universe.unload();
        }

        @Override
        protected void save(Universe universe) {
            universe.save();
        }
    };
}
