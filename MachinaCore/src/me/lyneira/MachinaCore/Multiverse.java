package me.lyneira.MachinaCore;

import gnu.trove.map.hash.THashMap;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
import me.lyneira.MachinaCore.machina.Universe;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Represents and manages all {@link Universe}s (worlds) that MachinaCraft knows
 * about. Provides fast lookups from world to universe. When given a world that
 * does not have a Universe yet, it will be created.
 * 
 * @author Lyneira
 * 
 */
public class Multiverse {
    private final THashMap<World, Universe> universes = new THashMap<World, Universe>();

    /**
     * Returns the universe for the given world. If it doesn't exist yet, a new
     * one will be created.
     * 
     * @param world
     *            The world to get the universe for
     * @return The universe for the world.
     */
    Universe get(World world) {
        Universe universe = universes.get(world);
        if (universe == null) {
            universe = Universe.friend.create(world);
            universes.put(world, universe);
        }
        return universe;
    }

    /**
     * Returns the machina that owns this block. If no machina owns this block,
     * returns null.
     * 
     * @param location
     *            The location to get the machina for.
     * @return The machina owning this location, or null.
     */
    Machina getMachina(Block location) {
        return get(location.getWorld()).get(new BlockVector(location));
    }

    /**
     * Loads the universe for a world.
     * 
     * @param world
     *            The world to be loaded
     */
    void load(World world) {
        Universe.friend.load(get(world));
    }

    /**
     * Unloads the universe for a world.
     * 
     * @param world
     *            The world being unloaded
     */
    void unload(World world) {
        Universe universe = universes.remove(world);
        if (universe != null) {
            Universe.friend.unload(universe);
        }
    }

    /**
     * Saves the universe for a world.
     * 
     * @param world
     *            The world being saved
     */
    void save(World world) {
        Universe.friend.save(get(world));
    }
    
    public static abstract class UniverseFriend {
        protected abstract Universe create(World world);
        protected abstract void load(Universe universe);
        protected abstract void unload(Universe universe);
        protected abstract void save(Universe universe);
    }
}
