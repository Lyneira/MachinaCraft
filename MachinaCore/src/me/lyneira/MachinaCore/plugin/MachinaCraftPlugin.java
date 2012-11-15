package me.lyneira.MachinaCore.plugin;

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
    public final Logger log = getLogger();

    private boolean configFirstCall = true;

    /**
     * Similar to getConfig(), returns the plugin's configuration. On the first
     * call since enable, also saves the default config.yml if none exists yet.
     */
    public final MPConfig mpGetConfig() {
        this.getLogger();
        FileConfiguration config = getConfig();
        if (configFirstCall) {
            saveDefaultConfig();
            configFirstCall = false;
        }
        return new MPConfig(this, config);
    }

    @Override
    public void onDisable() {
        configFirstCall = true;
    }
}
