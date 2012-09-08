package me.lyneira.MachinaCore;

import org.bukkit.event.HandlerList;

import me.lyneira.MachinaCore.plugin.MachinaCraftPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public final class MachinaCore extends MachinaCraftPlugin {
    // private static MachinaCore plugin;

    // private static PluginManager pluginManager;

    @Override
    public void onEnable() {
        super.onEnable();
        // Set listener
        // pluginManager = this.getServer().getPluginManager();
        getServer().getPluginManager().registerEvents(new MachinaCoreListener(this), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        super.onDisable();
    }

}
