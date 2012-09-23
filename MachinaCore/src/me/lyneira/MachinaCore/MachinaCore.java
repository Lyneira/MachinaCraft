package me.lyneira.MachinaCore;

import java.util.Iterator;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
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
    private final Multiverse multiverse = new Multiverse();

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
        blueprints.clear();
        HandlerList.unregisterAll(this);

        // Call super.onDisable last.
        super.onDisable();
    }

    boolean onMachinaTool(Player player, Block block) {
        BlockVector location = new BlockVector(block);
        Universe universe = multiverse.get(block.getWorld());
        Machina machina = universe.get(location);
        if (machina != null) {
            // TODO let the machina respond to being clicked
        } else {
            for (Iterator<MachinaBlueprint> it = blueprints.blueprints(); it.hasNext();) {
                machina = it.next().detect(universe, player, block);
                if (machina != null) {
                    return universe.add(machina);
                }
            }
        }
        return false;
    }

    public void registerBlueprints(MachinaPlugin plugin, List<MachinaBlueprint> blueprintList) {
        blueprints.put(plugin, blueprintList);
    }

    public void unregisterBlueprints(MachinaPlugin plugin) {
        blueprints.remove(plugin);
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
