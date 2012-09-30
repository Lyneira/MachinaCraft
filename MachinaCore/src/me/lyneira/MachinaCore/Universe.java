package me.lyneira.MachinaCore;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

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
    private final Set<Machina> machinae = new LinkedHashSet<Machina>();

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
        if (machinae.contains(machina))
            return false;

        int size = 0;
        for (Iterator<BlockVector> it = machina.instance(); it.hasNext();) {
            size++;
            if (machinaMap.get(it.next()) != null)
                return false;
        }
        if (size == 0)
            return false;

        // Machina has at least 1 block and no collisions, OK to add.
        for (Iterator<BlockVector> it = machina.instance(); it.hasNext();) {
            machinaMap.put(it.next(), machina);
        }

        return true;
    }

    /**
     * Removes a machina from the universe.
     * 
     * @param machina
     *            The machina to remove
     */
    void remove(Machina machina) {
        for (Iterator<BlockVector> it = machina.instance(); it.hasNext();) {
            machinaMap.remove(it.next());
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
}
