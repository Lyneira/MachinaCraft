package me.lyneira.MachinaCore;

import org.bukkit.World;

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
        // TODO
        return null;
    }
    
    boolean add(Machina machina) {
        // TODO
        return false;
    }

    // A list of all machinae in the world.

    // A fast map from block to its corresponding machina (or null if none)
}
