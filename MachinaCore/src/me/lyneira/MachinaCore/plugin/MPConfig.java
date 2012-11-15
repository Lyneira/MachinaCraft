package me.lyneira.MachinaCore.plugin;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Wrapper class with convenience methods for loading material ids and bounded
 * values for MachinaCraft plugins.
 * 
 * @author Lyneira
 */

public class MPConfig {

    private final MachinaCraftPlugin plugin;
    public final FileConfiguration config;

    MPConfig(MachinaCraftPlugin plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public int getMaterialId(String path, int defaultValue) {
        if (config.isInt(path)) {
            return config.getInt(path);
        } else if (config.isString(path)) {
            Material material = Material.matchMaterial(config.getString(path));
            if (material != null) {
                return material.getId();
            }
        }
        plugin.log.warning("Config option '" + path + "' was not a valid material id, loading default value " + defaultValue);
        return defaultValue;
    }
}
