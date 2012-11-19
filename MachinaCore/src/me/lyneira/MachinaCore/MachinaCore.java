package me.lyneira.MachinaCore;

import java.util.List;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.machina.MachinaDetector;
import me.lyneira.MachinaCore.machina.Universe;
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
    private static BukkitScheduler scheduler;
    // private static PluginManager pluginManager;

    private final BlueprintStore blueprints = new BlueprintStore();
    final Multiverse multiverse = new Multiverse();
    

    @Override
    public void onEnable() {
        super.onEnable();
        // Call super.onEnable first.

        plugin = this;
        scheduler = getServer().getScheduler();

        // Set listener
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
        final BlockVector location = new BlockVector(block);
        final Universe universe = multiverse.get(block.getWorld());
        Machina machina = universe.get(location);
        if (rightClick) {
            if (machina != null) {
                // TODO Show status information
                info("You rightclicked on a machina, yadayada hooray and stuff");
            } else {
                for (MachinaBlueprint blueprint : blueprints.blueprints()) {

                    switch (blueprint.detect(universe, block, player)) {
                    case SUCCESS:
                        return ToolInteractResult.DAMAGE;
                    case COLLISION:
                        return ToolInteractResult.NODAMAGE;
                    case FAILURE:
                    }
                }
            }
        } else {
            if (machina != null) {
                // TODO check permissions
                universe.remove(machina);
            }
        }
        return ToolInteractResult.NODAMAGE;
    }

    public void registerDetectors(MachinaPlugin plugin, List<MachinaDetector> detectorList) {
        MachinaBlueprint[] blueprintArray = new MachinaBlueprint[detectorList.size()];
        int i = 0;
        for (MachinaDetector detector : detectorList) {
            final MachinaBlueprint blueprint = detector.getBlueprint();
            if (blueprint == null) {
                logSevere("Detector registration for " + plugin.getName() + " encountered a null blueprint, not continuing!");
                return;
            }
            MachinaBlueprint.machinaCoreFriend.setDetector(blueprint, detector);
            blueprintArray[i++] = blueprint;
        }
        blueprints.put(plugin, blueprintArray);
    }

    public void unregisterDetectors(MachinaPlugin plugin) {
        blueprints.remove(plugin);
    }

    /* **************
     * Static methods
     */
    
    /**
     * Schedules a runnable task to occur after the specified number of server ticks
     * @param task 
     * @param delay
     * @return A BukkitTask with the task id
     */
    public final static BukkitTask runTask(Runnable task, long delay) {
        return scheduler.runTaskLater(plugin, task, delay);
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
     * Send a severe message to the server log.
     * 
     * @param message
     *            Message to send
     */
    public final static void severe(String message) {
        plugin.logSevere(message);
    }

    /**
     * Send a severe message and an exception to the server log.
     * 
     * @param message
     *            Message to send
     * @param ex
     *            Exception to send
     */
    public final static void exception(String message, Throwable ex) {
        plugin.logException(message, ex);
    }

    public static abstract class MachinaBlueprintFriend {
        protected abstract void setDetector(MachinaBlueprint blueprint, MachinaDetector detector);
    }
}
