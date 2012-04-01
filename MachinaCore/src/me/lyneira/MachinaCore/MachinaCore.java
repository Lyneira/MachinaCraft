package me.lyneira.MachinaCore;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public final class MachinaCore extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    static MachinaCore plugin;
    static PluginManager pluginManager;
    /**
     * This is a hashmap of the blueprint's class name to its blueprint. This
     * prevents accidental double insertions by buggy code.
     */
    private final Map<Class<?>, MachinaBlueprint> blueprints = new LinkedHashMap<Class<?>, MachinaBlueprint>();

    public final void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        // Set listener
        pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvents(new MachinaCoreListener(this), this);

        ConfigurationManager config = new ConfigurationManager(this);
        Fuel.loadConfiguration(config.getSection("fuels"));
        BlockData.loadBlockConfiguration(config.getSection("blocks"));
        BlockData.loadBreakTimeConfiguration(config.getSection("break-times"));
    }

    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");
        MachinaRunner.deActivateAll();
    }

    /**
     * Detects whether a machina is present using the given BlockLocation as the
     * anchor, and starts it if found. This function assumes the existence of a
     * lever has already been checked at leverFace.
     * 
     * @param player
     *            The player activating this machina
     * @param location
     *            The location to check at
     * @param leverFace
     *            The face to which the lever is attached
     * @param item
     *            The item in the player's hand
     */
    public final void onLever(Player player, final BlockLocation location, final BlockFace leverFace, ItemStack item) {
        if (MachinaRunner.exists(location)) {
            // Machina exists, run onLever.
            MachinaRunner.onLever(location, player, item);
        } else {
            for (MachinaBlueprint i : blueprints.values()) {
                Machina machina = i.detect(player, location, leverFace, item);
                if (machina != null) {
                    new MachinaRunner(this, machina, location, leverFace);
                    break;
                }
            }
        }
    }

    /**
     * Detects whether a machina is present in the given location. If not,
     * iterates over the given iterator and attempts to detect and activate one.
     * Null is returned if a machina could not be detected.
     * 
     * @param blueprint
     *            An iterator to blueprints to detect for
     * @param player
     *            The player to activate the machina for
     * @param location
     *            The location to check at
     * @return The machina detected, or null if none was found.
     */
    public Machina detectMachina(Iterator<MachinaBlueprint> blueprint, Player player, BlockLocation location) {
        if (MachinaRunner.exists(location)) {
            return MachinaRunner.getMachina(location);
        } else {
            while (blueprint.hasNext()) {
                Machina machina = blueprint.next().detect(player, location, null, null);
                if (machina != null) {
                    new MachinaRunner(this, machina, location, null);
                    return machina;
                }
            }
        }
        return null;
    }

    /**
     * Returns the machina present at this location, or null if none exists.
     * 
     * @param location
     * @return A machina, or null if none could be found
     */
    public Machina getMachina(BlockLocation location) {
        return MachinaRunner.getMachina(location);
    }

    /**
     * Returns true if a machina exists at this location
     * 
     * @param location
     * @return True if a machina exists here.
     */
    public boolean exists(BlockLocation location) {
        return MachinaRunner.exists(location);
    }

    /**
     * Registers a blueprint with MachinaCore. When a lever is rightclicked by a
     * player, this blueprint's detect function will be run.
     * 
     * @param blueprint
     *            The blueprint to register
     */
    public final void registerBlueprint(MachinaBlueprint blueprint) {
        blueprints.put(blueprint.getClass(), blueprint);
    }

    /**
     * Unregisters a blueprint with MachinaCore.
     * 
     * @param blueprint
     *            The blueprint to unregister
     */
    public final void unRegisterBlueprint(MachinaBlueprint blueprint) {
        blueprints.remove((blueprint.getClass()));
    }
}
