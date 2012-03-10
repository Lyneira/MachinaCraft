package me.lyneira.MachinaDrill;

import java.util.logging.Logger;

import me.lyneira.MachinaCore.ConfigurationManager;
import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public class MachinaDrill extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private MachinaCore machinaCore;

    @Override
    public final void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        ConfigurationManager config = new ConfigurationManager(this);
        Drill.loadConfiguration(config.getAll());

        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
        machinaCore.registerBlueprint(Blueprint.instance);
    }

    @Override
    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        machinaCore.unRegisterBlueprint(Blueprint.instance);
    }

}
