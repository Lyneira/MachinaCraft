package me.lyneira.MachinaCore.plugin;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Template class for quick development of any plugin that interacts with
 * MachinaCraft. Provides logging functionality and a config retrieval method
 * that takes care of saving the default config if not already there.
 * 
 * If you extend this class instead of MachinaPlugin, you are expected to call
 * super.onEnable() and super.onDisable() if you override them.
 * 
 * @author Lyneira
 */
public abstract class MachinaCraftPlugin extends JavaPlugin {
    private boolean configFirstCall = true;
    private Logger log;

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
        log = getLogger();
    }

    @Override
    public void onDisable() {
        configFirstCall = true;
    }

    public void logInfo(String message) {
        log.info(message);
    }

    public void logWarning(String message) {
        log.warning(message);
    }

    public void logSevere(String message) {
        log.severe(message);
    }
    
    public void logException(String message, Throwable ex) {
        log.log(Level.SEVERE, message, ex);
    }
}
