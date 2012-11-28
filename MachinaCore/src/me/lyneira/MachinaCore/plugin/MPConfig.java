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

    /**
     * Returns the typeId specified by the given config path. Will match either an integer or a material enum name.
     * @param path The config path to get
     * @param defaultValue The default value to return if a typeId could not be matched.
     * @return A type id
     */
    public int getPathTypeId(String path, int defaultValue) {
        if (config.isInt(path)) {
            return config.getInt(path);
        } else if (config.isString(path)) {
            Material material = Material.matchMaterial(config.getString(path));
            if (material != null) {
                return material.getId();
            }
        }
        plugin.logWarning("Config option '" + path + "' was not a valid material id, loading default value " + defaultValue);
        return defaultValue;
    }

    /**
     * Returns the typeId specified by the given string. Will match either an integer or a material enum name.
     * @param configItem The string to parse
     * @param The default value to return if a typeId could not be matched.
     * @return A type id
     */
    public static int parseTypeId(String stringType, int defaultValue) {
        try {
            return Integer.parseInt(stringType);
        } catch (NumberFormatException e) {
        }
        Material material = Material.matchMaterial(stringType);
        if (material != null) {
            return material.getId();
        }
        return defaultValue;
    }
}
