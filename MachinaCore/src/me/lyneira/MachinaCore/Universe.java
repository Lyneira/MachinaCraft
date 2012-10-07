package me.lyneira.MachinaCore;

import java.util.Iterator;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;

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
    private final World world;
    private final THashMap<BlockVector, Machina> globalMap = new THashMap<BlockVector, Machina>();
    private final THashMap<Machina, BlockVector[]> instances = new THashMap<Machina, BlockVector[]>();

    // Static fields used during a machina update
    private static int[] updateTypes;
    private static byte[] updateData;
    private static ItemStack[] updateInventoryContents;
    
    private static THashSet<BlockVector> updateClearSet = new THashSet<BlockVector>();

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
        BlockVector[] instance = instances.get(machina);
        if (instance != null)
            return false;

        instance = createInstance(machina);

        for (BlockVector v : instance) {
            if (globalMap.get(v) != null)
                return false;
        }

        // Machina has no collisions, OK to add.
        for (BlockVector v : instance) {
            globalMap.put(v, machina);
        }

        instances.put(machina, instance);

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
    public boolean update(Machina machina) {
        BlockVector[] instance = instances.get(machina);
        if (instance == null)
            return false;

        BlockVector[] newInstance = createUpdateInstance(machina);

        for (BlockVector v : newInstance) {
            Machina m = globalMap.get(v);

            if (m == null) {
                // TODO Do collision detection
            } else if (m != machina) {
                // Block belongs to another machina, definitely a collision.
                return false;
            }
            // Block belongs to this machina's existing instance, that's never a
            // collision
        }

        // Machina has no collisions, OK to update.
        for (BlockVector v : instance) {
            globalMap.remove(v);
            updateClearSet.add(v);
        }
        for (BlockVector v : newInstance) {
            globalMap.put(v, machina);
            updateClearSet.remove(v);
            // TODO Write out new machina instance
        }
        instances.put(machina, newInstance);
        for (Iterator<BlockVector> it = updateClearSet.iterator(); it.hasNext();) {
            BlockVector v = it.next();
            it.remove();
            // TODO Clear vectors left empty by the machina.
        }
        
        // Release arrays used for writing out the new instance
        updateTypes = null;
        updateData = null;
        updateInventoryContents = null;
        return true;
    }

    /**
     * Removes a machina from the universe.
     * 
     * @param machina
     *            The machina to remove
     */
    void remove(Machina machina) {
        BlockVector[] instance = instances.get(machina);
        if (instance == null)
            return;

        for (BlockVector v : instance) {
            globalMap.remove(v);
        }
        instances.remove(machina);
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

    /**
     * Creates an array containing all the machina's blocks. The array has no
     * null elements, and each element is unique.
     * 
     * @return An array of the machina's blocks
     */
    private final BlockVector[] createInstance(Machina machina) {
        // TODO
        return null;
    }

    /**
     * Creates an array containing all the machina's blocks. The array has no
     * null elements, and each element is unique. It also initializes the static
     * update* fields to enable the updater to write out the new instance to the
     * world.
     * 
     * @return An array of the machina's blocks
     */
    private final BlockVector[] createUpdateInstance(Machina machina) {
        // TODO
        return null;
    }
}
