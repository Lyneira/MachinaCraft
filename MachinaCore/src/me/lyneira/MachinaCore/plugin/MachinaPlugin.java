package me.lyneira.MachinaCore.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;

/**
 * Template class for quick development of a machina plugin. In addition to the
 * functionality provided by MachinaCraftPlugin, manages registration of machina
 * blueprints.
 * 
 * @author Lyneira
 */
public abstract class MachinaPlugin extends MachinaCraftPlugin {

    private List<MachinaBlueprint> blueprints = null;
    private MachinaCore machinaCore;

    @Override
    public final void onEnable() {
        super.onEnable();
        Plugin plugin = getServer().getPluginManager().getPlugin("MachinaCore");
        if (plugin == null) {
            logSevere("Could not retrieve MachinaCore plugin from server, something is very wrong! Not continuing with enable.");
            return;
        }
        machinaCore = (MachinaCore) plugin;
        mpEnable();
        registerBlueprints();
    }

    @Override
    public final void onDisable() {
        mpDisable();
        unregisterBlueprints();
        super.onDisable();
    }

    /**
     * Call this method during mpEnable() to add your blueprints for
     * registration.
     * 
     * @param blueprint
     *            A blueprint to add to this plugin.
     */
    protected void addBlueprint(MachinaBlueprint blueprint) {
        if (blueprint == null) {
            throw new NullPointerException("Cannot register a null blueprint!");
        }
        if (blueprints == null) {
            blueprints = new ArrayList<MachinaBlueprint>(1);
        }
        blueprints.add(blueprint);
    }

    /**
     * Provides a hook for onEnable(). Create and add your machina blueprints
     * with addBlueprint() here.
     */
    protected abstract void mpEnable();

    /**
     * Provides a hook for onDisable().
     */
    protected abstract void mpDisable();

    /* ***********************************************************************
     * Do not call these methods unless you know what you are doing with them!
     * MachinaPlugin handles your blueprints automatically as long as you add
     * them through addBlueprint() during mpEnable().
     * 
     * The methods below are only included for cases where you might need to do
     * some really strange stuff with modifying the available blueprints while a
     * server is running.
     */

    /**
     * Removes the given blueprint for this plugin. You should not normally need
     * to call this method.
     * 
     * @param blueprint
     *            A blueprint to remove from this plugin.
     */
    protected void removeBlueprint(MachinaBlueprint blueprint) {
        if (blueprints != null) {
            blueprints.remove(blueprint);
        }
    }

    /**
     * Removes all registered blueprints for this plugin. You should not
     * normally need to call this method.
     */
    protected void clearBlueprints() {
        if (blueprints != null) {
            blueprints.clear();
        }
    }

    /**
     * Automatically called after mpEnable(), you should not normally need to
     * call this method. Registers all blueprints added by addBlueprint() to
     * MachinaCore.
     */
    protected void registerBlueprints() {
        if (blueprints != null) {
            machinaCore.registerBlueprints(this, blueprints);
        }
    }

    /**
     * Automatically called after mpDisable(), you should not normally need to
     * call this method. Unregisters all blueprints for the plugin from
     * MachinaCore.
     */
    protected void unregisterBlueprints() {
        machinaCore.unregisterBlueprints(this);
        blueprints = null;
    }
}
