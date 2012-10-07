package me.lyneira.MachinaCore;

import java.util.Iterator;
import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.plugin.MachinaCraftPlugin;
import me.lyneira.MachinaCore.plugin.MachinaPlugin;
import me.lyneira.MachinaCore.tool.ToolInteractResult;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public final class MachinaCore extends MachinaCraftPlugin {
    private static MachinaCore plugin;
    // private static PluginManager pluginManager;

    private final BlueprintStore blueprints = new BlueprintStore();
    final Multiverse multiverse = new Multiverse();

    @Override
    public void onEnable() {
        super.onEnable();
        // Call super.onEnable first.

        plugin = this;
        // Set listener
        // pluginManager = this.getServer().getPluginManager();
        getServer().getPluginManager().registerEvents(new MachinaCoreListener(this), this);
        
        // Initialize the currently loaded worlds
        for (World world : getServer().getWorlds()) {
            multiverse.load(world);
        }
    }

    @Override
    public void onDisable() {
        blueprints.clear();
        HandlerList.unregisterAll(this);

        // Call super.onDisable last.
        super.onDisable();
    }

    /**
     * Initiates actions for when the wrench is used: machina detection, status
     * info, removal.
     * 
     * @param player
     *            The player who activated the wrench
     * @param block
     *            The block the wrench was used on
     * @return DAMAGE if the wrench should be damaged
     */
    public ToolInteractResult wrenchClick(Player player, Block block, boolean rightClick) {
        BlockVector location = new BlockVector(block);
        Universe universe = multiverse.get(block.getWorld());
        Machina machina = universe.get(location);
        if (rightClick) {
            if (machina != null) {
                // TODO Show status information
            } else {
                for (MachinaBlueprint blueprint : blueprints.blueprints()) {
                    machina = blueprint.detect(universe, player, block);
                    if (machina != null) {
                        if (universe.add(machina)) {
                            // TODO Send creation event
                            return ToolInteractResult.DAMAGE;
                        } else {
                            return ToolInteractResult.NODAMAGE;
                        }
                    }
                }
            }
        } else {
            if (machina != null) {
                // TODO check permissions, send removal event
                universe.remove(machina);
            }
        }
        return ToolInteractResult.NODAMAGE;
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
