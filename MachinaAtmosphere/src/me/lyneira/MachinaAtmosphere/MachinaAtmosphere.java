package me.lyneira.MachinaAtmosphere;

import java.util.logging.Logger;

import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MachinaAtmosphere extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    private MachinaCore machinaCore;
    private Blueprint blueprint;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");
        
        blueprint = new Blueprint();
        
        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
        machinaCore.registerBlueprint(blueprint);
    }

    @Override
    public void onDisable() {
        machinaCore.unRegisterBlueprint(blueprint);
    }

    /**
     * Sends an informational message to the server log.
     * 
     * @param message
     */
    public static void log(String message) {
        log.info("MachinaAtmosphere: " + message);
    }
}
