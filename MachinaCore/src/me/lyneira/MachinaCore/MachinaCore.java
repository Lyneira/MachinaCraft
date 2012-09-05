package me.lyneira.MachinaCore;

import java.util.logging.Logger;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public final class MachinaCore extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private static MachinaCore plugin;

    // private static PluginManager pluginManager;

    public final void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = getDescription();
        logInfo(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        // Set listener
        // pluginManager = this.getServer().getPluginManager();
        // pluginManager.registerEvents(new MachinaCoreListener(this), this);
    }

    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        logInfo(pdf.getName() + " is now disabled.");
    }
    
    /**
     * Send a severe message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public static final void logSevere(String message) {
        log.severe(message);
    }
    
    /**
     * Send a warning message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public static final void logWarning(String message) {
        log.warning(message);
    }

    /**
     * Send an informational message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public static final void logInfo(String message) {
        log.info(message);
    }
}
