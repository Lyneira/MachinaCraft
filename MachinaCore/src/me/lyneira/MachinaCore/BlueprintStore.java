package me.lyneira.MachinaCore;

import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.plugin.MachinaPlugin;

/**
 * Class that registers blueprints from other plugins. It stores the registered
 * blueprints and manages them, providing detection by trigger block and can
 * unregister all, by plugin, or a specific blueprint.
 * 
 * @author Lyneira
 */
class BlueprintStore {
    /**
     * Adds the blueprint to the store for the given plugin. 
     * @param plugin The plugin for which to add a blueprint
     * @param blueprint The blueprint to add
     */
    void add(MachinaPlugin plugin, MachinaBlueprint blueprint) {
        // TODO
    }
    
    /**
     * Clears all stored blueprints for the given plugin.
     * @param plugin The plugin for which to clear
     */
    void clear(MachinaPlugin plugin) {
        // TODO
    }
    
    /**
     * Clears the blueprint store of all blueprints.
     */
    void clearAll() {
        // TODO
    }
}
