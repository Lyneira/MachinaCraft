package me.lyneira.MachinaFactory;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.ConfigurationManager;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;
import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Core plugin for machinacraft factory components.
 * 
 * @author Lyneira
 */
public class MachinaFactory extends JavaPlugin {
    private final static Logger log = Logger.getLogger("Minecraft");
    static MachinaFactory plugin;
    static MachinaCore machinaCore;

    /**
     * Hashmap of the blueprint's class to its blueprint. This prevents
     * accidental double insertions.
     */
    private final Map<Class<? extends MachinaBlueprint>, MachinaFactoryBlueprint> blueprints = new LinkedHashMap<Class<? extends MachinaBlueprint>, MachinaFactoryBlueprint>();
    /**
     * Hashmap of the blueprint's class to its blueprint. This map contains only
     * those blueprints whose machina are a valid {@link PipelineEndpoint}.
     */
    private final Map<Class<? extends MachinaBlueprint>, MachinaBlueprint> endpointBlueprints = new LinkedHashMap<Class<? extends MachinaBlueprint>, MachinaBlueprint>();

    @Override
    public void onEnable() {
        plugin = this;
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
        
        ConfigurationManager config = new ConfigurationManager(this);
        ComponentBlueprint.loadConfiguration(config.getAll());

        // Enable built-in components.
        registerFactoryBlueprint(new me.lyneira.ItemRelay.Blueprint(), me.lyneira.ItemRelay.ItemRelay.class, true);
        registerFactoryBlueprint(new me.lyneira.Fabricator.Blueprint(this), me.lyneira.Fabricator.Fabricator.class, false);
        registerFactoryBlueprint(new me.lyneira.ItemSplitter.Blueprint(), me.lyneira.ItemSplitter.ItemSplitter.class, false);
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        Iterator<MachinaFactoryBlueprint> it = blueprints.values().iterator();
        while (it.hasNext()) {
            MachinaFactoryBlueprint blueprint = it.next();
            if (blueprint.leverActivatable)
                machinaCore.unRegisterBlueprint(blueprint.blueprint);
            it.remove();
        }
        endpointBlueprints.clear();
    }

    /**
     * Registers a {@link MachinaBlueprint} with MachinaFactoryCore. If it is
     * lever-activatable, it will also be registered with MachinaCore.
     * 
     * @param blueprint
     */
    public void registerFactoryBlueprint(MachinaBlueprint blueprint, Class<? extends Machina> machinaType, boolean leverActivatable) {
        MachinaFactoryBlueprint b = new MachinaFactoryBlueprint(blueprint, machinaType, leverActivatable);
        blueprints.put(blueprint.getClass(), b);
        if (leverActivatable)
            machinaCore.registerBlueprint(blueprint);
        if (b.validEndpoint)
            endpointBlueprints.put(blueprint.getClass(), blueprint);
    }

    /**
     * Unregisters the {@link MachinaFactoryBlueprint} with MachinaFactoryCore.
     * If it was lever-activatable, it will also be unregistered with
     * MachinaCore.
     * 
     * @param blueprint
     */
    public void unregisterFactoryBlueprint(MachinaBlueprint blueprint) {
        MachinaFactoryBlueprint b = blueprints.get(blueprint.getClass());
        if (b != null) {
            blueprints.remove(blueprint.getClass());
            if (b.leverActivatable)
                machinaCore.unRegisterBlueprint(blueprint);
            if (b.validEndpoint)
                endpointBlueprints.remove(blueprint.getClass());
        }
    }

    /**
     * Attempts to detect an endpoint at the given location and returns it.
     * 
     * @param player
     *            The player activating this machina
     * @param location
     *            The location to detect at
     * @return A PipelineEndpoint if successful, null otherwise.
     */
    PipelineEndpoint detectEndpoint(Player player, BlockLocation location) {
        Machina detectedMachina = machinaCore.detectMachina(endpointBlueprints.values().iterator(), player, location);
        if (detectedMachina == null)
            return null;
        if (detectedMachina instanceof PipelineEndpoint)
            return (PipelineEndpoint) detectedMachina;
        return null;
    }
    
    /**
     * Sends an informational message to the server log.
     * @param message
     */
    public static void log(String message) {
        log.info("MachinaFactory: " + message);
    }
}
