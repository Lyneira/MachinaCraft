package me.lyneira.MachinaCore;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.plugin.MachinaPlugin;

/**
 * Class that stores blueprints by plugin and provides iterators over blueprints
 * by plugin or all blueprints.
 * 
 * @author Lyneira
 */
class BlueprintStore {
    private final Map<MachinaPlugin, List<MachinaBlueprint>> blueprints = new THashMap<MachinaPlugin, List<MachinaBlueprint>>();
    private List<MachinaBlueprint> bakedBlueprints = new ArrayList<MachinaBlueprint>(0);
    private boolean modified = false;

    /**
     * Adds the given list of blueprints for the given plugin to the store. If
     * there already was a list stored for this plugin, it is replaced by the
     * given list.
     * 
     * @param plugin
     *            The plugin for which to store
     * @param blueprintList
     *            The list of blueprints to store
     */
    void put(MachinaPlugin plugin, List<MachinaBlueprint> blueprintList) {
        blueprints.put(plugin, blueprintList);
        modified = true;
    }

    /**
     * Removes the stored blueprints for a given plugin.
     * 
     * @param plugin
     *            The plugin for which to remove
     */
    void remove(MachinaPlugin plugin) {
        blueprints.remove(plugin);
        modified = true;
    }

    /**
     * Clears stored blueprints for all plugins.
     */
    void clear() {
        blueprints.clear();
        modified = true;
    }

    /**
     * Returns an iterator over all blueprints in the store.
     * 
     * @return An iterator over all blueprints
     */
    Iterator<MachinaBlueprint> blueprints() {
        bake();
        return bakedBlueprints.iterator();
    }

    /**
     * Returns an iterator over all blueprints for the given plugin.
     * 
     * @param plugin
     *            The plugin for which to return an iterator
     * @return An iterator over all blueprints for this plugin
     */
    Iterator<MachinaBlueprint> pluginBlueprints(MachinaPlugin plugin) {
        List<MachinaBlueprint> blueprintList = blueprints.get(plugin);
        if (blueprintList == null) {
            return null;
        }
        return blueprintList.iterator();
    }

    /**
     * If the blueprintstore has been modified, reconstructs the list of all
     * blueprints for quick iteration.
     */
    private void bake() {
        if (modified == false)
            return;

        if (blueprints.size() == 0) {
            bakedBlueprints = new ArrayList<MachinaBlueprint>(0);
            return;
        }

        bakedBlueprints = new ArrayList<MachinaBlueprint>(8);
        for (List<MachinaBlueprint> blueprintList : blueprints.values()) {
            bakedBlueprints.addAll(blueprintList);
        }
        modified = false;
    }
}
