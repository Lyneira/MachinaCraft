package me.lyneira.MachinaCore;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.plugin.MachinaCraftPlugin;
import me.lyneira.MachinaCore.plugin.MachinaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public final class MachinaCore extends MachinaCraftPlugin {
    private static MachinaCore plugin;
    // private static PluginManager pluginManager;

    private final BlueprintStore blueprints = new BlueprintStore();

    @Override
    public void onEnable() {
        super.onEnable();
        // Call super.onEnable first.

        plugin = this;
        // Set listener
        // pluginManager = this.getServer().getPluginManager();
        getServer().getPluginManager().registerEvents(new MachinaCoreListener(this), this);
    }

    @Override
    public void onDisable() {
        blueprints.clearAll();
        HandlerList.unregisterAll(this);

        // Call super.onDisable last.
        super.onDisable();
    }

    public boolean onMachinaTool(Player player, Block block) {
        return false;
    }
    
    public void registerBlueprint(MachinaPlugin plugin, MachinaBlueprint blueprint) {
        blueprints.add(plugin, blueprint);
    }
    
    public void unregisterBlueprints(MachinaPlugin plugin) {
        blueprints.clear(plugin);
    }

    /* **************
     * Static methods
     */

    /**
     * Send a severe message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final static void severe(String message) {
        plugin.logSevere(message);
    }

    /**
     * Send a warning message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final static void warning(String message) {
        plugin.logWarning(message);
    }

    /**
     * Send an informational message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final static void info(String message) {
        plugin.logInfo(message);
    }
}
