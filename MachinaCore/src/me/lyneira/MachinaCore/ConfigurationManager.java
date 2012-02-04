package me.lyneira.MachinaCore;

import java.io.File;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigurationManager {
    private Configuration configuration;

    public ConfigurationManager(JavaPlugin plugin) {
        File parent = plugin.getDataFolder();
        File file = new File(parent, "config.yml");

        if (!parent.exists())
            parent.mkdirs();

        configuration = plugin.getConfig();

        if (!file.exists()) {
            MachinaCore.log.info("MachinaCore: Saving default config for plugin " + plugin.getDescription().getName() + ".");
            plugin.saveDefaultConfig();
        }
    }

    public ConfigurationSection getSection(String path) {
        return configuration.getConfigurationSection(path);
    }
    
    public ConfigurationSection getAll() {
        return configuration;
    }
}
