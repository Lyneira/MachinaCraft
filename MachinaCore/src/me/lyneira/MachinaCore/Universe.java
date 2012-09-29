package me.lyneira.MachinaCore;

import org.bukkit.World;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
import me.lyneira.MachinaCore.map.CoordinateMap3D;

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
    private final CoordinateMap3D<Machina> machinaMap = new CoordinateMap3D<Machina>();

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
        return machinaMap.get(location);
    }

    /**
     * Adds a machina to the universe.
     * 
     * @param machina
     *            The machina to add
     * @return True if the machina was successfully added, false if there was a
     *         collision with another machina.
     */
    boolean add(Machina machina) {
        // TODO
        return false;
    }

    /**
     * Removes a machina from the universe.
     * 
     * @param machina
     *            The machina to remove
     */
    void remove(Machina machina) {
        // TODO
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

    // A list of all machinae in the world.

    // A fast map from block to its corresponding machina (or null if none)
}
