package me.lyneira.MachinaCore;

import gnu.trove.map.hash.THashMap;

import java.util.ArrayList;
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
    private final Map<MachinaPlugin, MachinaBlueprint[]> blueprints = new THashMap<MachinaPlugin, MachinaBlueprint[]>(8);
    private MachinaBlueprint[] bakedBlueprints;
    private boolean modified = true;

    /**
     * Adds the given list of blueprints for the given plugin to the store. If
     * there already was a list stored for this plugin, it is replaced by the
     * given list.
     * 
     * @param plugin
     *            The plugin for which to store
     * @param blueprintArray
     *            The list of blueprints to store
     */
    void put(MachinaPlugin plugin, MachinaBlueprint[] blueprintArray) {
        blueprints.put(plugin, blueprintArray);
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
    MachinaBlueprint[] blueprints() {
        bake();
        return bakedBlueprints;
    }

    /**
     * If the blueprintstore has been modified, reconstructs the list of all
     * blueprints for quick iteration.
     */
    private void bake() {
        if (modified == false)
            return;

        if (blueprints.size() == 0) {
            bakedBlueprints = new MachinaBlueprint[0];
            return;
        }

        List<MachinaBlueprint> blueprintsFlat = new ArrayList<MachinaBlueprint>(8);
        for (MachinaBlueprint[] blueprintArray : blueprints.values()) {
            for (int i = 0; i < blueprintArray.length; i++) {
                blueprintsFlat.add(blueprintArray[i]);
            }
        }
        bakedBlueprints = blueprintsFlat.toArray(new MachinaBlueprint[blueprintsFlat.size()]);
        modified = false;
    }
}
