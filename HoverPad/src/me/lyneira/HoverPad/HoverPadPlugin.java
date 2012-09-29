package me.lyneira.HoverPad;

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
public class HoverPadPlugin extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private MachinaCore machinaCore;
    private Blueprint blueprint;

    @Override
    public final void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        ConfigurationManager config = new ConfigurationManager(this);
        Blueprint.loadConfiguration(config.getAll());
        HoverPad.loadConfiguration(config.getAll());
        
        // Create the new blueprint after configuration has been loaded.
        blueprint = Blueprint.blueprint();

        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
        machinaCore.registerBlueprint(blueprint);
    }

    @Override
    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        machinaCore.unRegisterBlueprint(blueprint);
    }
}
