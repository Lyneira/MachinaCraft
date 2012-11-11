package me.lyneira.MachinaCore.plugin;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Template class for quick development of any plugin that interacts with
 * MachinaCraft. Provides logging functionality, enable/disable messages and a
 * config retrieval method that takes care of saving the default config if not
 * already there.
 * 
 * If you extend this class instead of MachinaPlugin, you are expected to call
 * super.onEnable() and super.onDisable() if you override them.
 * 
 * @author Lyneira
 */
public abstract class MachinaCraftPlugin extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");

    private String name = "MachinaPlugin";
    private boolean configFirstCall = true;

    /**
     * Similar to getConfig(), returns the plugin's configuration. On the first
     * call since enable, also saves the default config.yml if none exists yet.
     */
    public final MPConfig mpGetConfig() {
        FileConfiguration config = getConfig();
        if (configFirstCall) {
            saveDefaultConfig();
            configFirstCall = false;
        }
        return new MPConfig(this, config);
    }

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        name = pdf.getName();
    }

    @Override
    public void onDisable() {
        configFirstCall = true;
    }

    /**
     * Send a severe message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final void logSevere(String message) {
        log.severe("[" + name + "] " + message);
    }

    /**
     * Send a warning message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final void logWarning(String message) {
        log.warning("[" + name + "] " + message);
    }

    /**
     * Send an informational message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final void logInfo(String message) {
        log.info("[" + name + "] " + message);
    }

}
