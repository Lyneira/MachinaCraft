package me.lyneira.MachinaCore.plugin;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.plugin.Plugin;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.machina.MachinaDetector;

/**
 * Template class for quick development of a machina plugin. In addition to the
 * functionality provided by MachinaCraftPlugin, manages registration of machina
 * blueprints.
 * 
 * @author Lyneira
 */
public abstract class MachinaPlugin extends MachinaCraftPlugin {

    private List<MachinaDetector> detectors;
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
        registerDetectors();
    }

    @Override
    public final void onDisable() {
        mpDisable();
        unregisterDetectors();
        super.onDisable();
    }

    /**
     * Call this method during mpEnable() to add your detectors for
     * registration.
     * 
     * @param detector
     *            A detector to add for this plugin.
     */
    protected void addDetector(MachinaDetector detector) {
        if (detector == null) {
            throw new NullPointerException("Cannot register a null detector!");
        }
        if (detectors == null) {
            detectors = new ArrayList<MachinaDetector>(1);
        }
        detectors.add(detector);
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
     * MachinaPlugin handles your detectors automatically as long as you add
     * them through addDetector() during mpEnable().
     * 
     * The methods below are only included for cases where you might need to do
     * some really strange stuff with modifying the available detectors while a
     * server is running. If you do modify the list of detectors, you must
     * re-run registerDetectors() for the changes to take effect.
     */

    /**
     * Removes the given blueprint for this plugin. You should not normally need
     * to call this method.
     * 
     * @param blueprint
     *            A blueprint to remove from this plugin.
     */
    protected void removeDetector(MachinaBlueprint blueprint) {
        if (detectors != null) {
            detectors.remove(blueprint);
        }
    }

    /**
     * Removes all registered blueprints for this plugin. You should not
     * normally need to call this method.
     */
    protected void clearDetectors() {
        if (detectors != null) {
            detectors.clear();
        }
    }

    /**
     * Automatically called after mpEnable(), you should not normally need to
     * call this method. Registers all blueprints added by addBlueprint() to
     * MachinaCore.
     */
    protected void registerDetectors() {
        if (detectors != null) {
            machinaCore.registerDetectors(this, detectors);
        }
    }

    /**
     * Automatically called after mpDisable(), you should not normally need to
     * call this method. Unregisters all blueprints for the plugin from
     * MachinaCore.
     */
    protected void unregisterDetectors() {
        machinaCore.unregisterDetectors(this);
        detectors = null;
    }
}
