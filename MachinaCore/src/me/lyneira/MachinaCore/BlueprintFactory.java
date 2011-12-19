package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

/**
 * Factory class for constructing a modular blueprint for a {@link Movable} machina.
 * 
 * An instance of this class should be set to null and discarded once it has
 * been passed to the constructor of {@link MovableBlueprint}.
 * 
 * @author Lyneira
 */
public class BlueprintFactory {
    final List<ModuleFactory> modules;

    public BlueprintFactory(int initialCapacity) {
        modules = new ArrayList<ModuleFactory>(initialCapacity);
    }

    /**
     * Adds a new module to this blueprint and returns the id.
     * 
     * @return The id of the new module.
     */
    public int newModule() {
        int newId = modules.size();
        ModuleFactory newModule = new ModuleFactory();
        modules.add(newModule);
        return newId;
    }
    
    /**
     * Adds a non-key block to the blueprint.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     * @param id
     */
    public final BlueprintBlock add(BlockVector vector, Material type, int id) {
        return modules.get(id).add(vector, type);
    }
    
    /**
     * Adds a key block to the blueprint. A key block will not be detected
     * automatically when a Movable machina is first activated.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     * @param id
     */
    public final BlueprintBlock addKey(BlockVector vector, Material type, int id) {
        return modules.get(id).addKey(vector, type);
    }
}
