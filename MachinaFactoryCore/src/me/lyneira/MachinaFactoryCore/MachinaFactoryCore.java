package me.lyneira.MachinaFactoryCore;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core plugin for machinacraft factory components.
 * 
 * @author Lyneira
 */
public class MachinaFactoryCore extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private MachinaCore machinaCore;

    /**
     * This is a hashmap of the blueprint's class name to its blueprint. This
     * prevents accidental double insertions by buggy code.
     */
    private final Map<String, MachinaFactoryBlueprint> blueprints = new LinkedHashMap<String, MachinaFactoryBlueprint>();

    public void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");

        // TODO Split itemsender and factory core
        registerFactoryBlueprint(new ItemSenderBlueprint());
    }

    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        Iterator<MachinaFactoryBlueprint> it = blueprints.values().iterator();
        while (it.hasNext()) {
            MachinaFactoryBlueprint blueprint = it.next();
            if (blueprint.leverActivatable())
                machinaCore.unRegisterBlueprint(blueprint);
            it.remove();
        }
    }

    public void registerFactoryBlueprint(MachinaFactoryBlueprint blueprint) {
        blueprints.put(blueprint.getClass().getName(), blueprint);
        if (blueprint.leverActivatable())
            machinaCore.registerBlueprint(blueprint);
    }

    public void unregisterFactoryBlueprint(MachinaFactoryBlueprint blueprint) {
        blueprints.remove(blueprint.getClass().getName());
        if (blueprint.leverActivatable())
            machinaCore.unRegisterBlueprint(blueprint);
    }
}
