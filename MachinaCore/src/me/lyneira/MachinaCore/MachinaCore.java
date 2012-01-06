package me.lyneira.MachinaCore;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
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

    public static MachinaCore plugin;
    final static Logger log = Logger.getLogger("Minecraft");
    static PluginManager pluginManager;
    private final MachinaCorePlayerListener playerListener = new MachinaCorePlayerListener(this);
    private final MachinaCoreWorldListener worldListener = new MachinaCoreWorldListener();
    private final Map<String, MachinaBlueprint> blueprints = new LinkedHashMap<String, MachinaBlueprint>();
    private ConfigurationManager config;

    public final void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        // Set listener
        pluginManager = this.getServer().getPluginManager();
        pluginManager.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Monitor, this);
        pluginManager.registerEvent(Event.Type.CHUNK_UNLOAD, worldListener, Event.Priority.Monitor, this);
        
        config = new ConfigurationManager();
        config.load(this);
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
     * @param location
     *            The location to check at
     * @param leverFace
     *            The face to which the lever is attached
     */
    final void onLever(Player player, final BlockLocation location, final BlockFace leverFace, ItemStack item) {
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
     * Registers a blueprint with MachinaCore. When a lever is rightclicked by
     * a player, this blueprint's detect function will be run.
     * 
     * @param blueprint
     *            The blueprint to register
     */
    public final void registerBlueprint(MachinaBlueprint blueprint) {
        blueprints.put(blueprint.getClass().getName(), blueprint);
    }

    /**
     * Unregisters a blueprint with MachinaCore.
     * 
     * @param blueprint
     *            The blueprint to unregister
     */
    public final void unRegisterBlueprint(MachinaBlueprint blueprint) {
        blueprints.remove((blueprint.getClass().getName()));
    }
}
