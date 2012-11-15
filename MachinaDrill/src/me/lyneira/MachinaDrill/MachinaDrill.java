package me.lyneira.MachinaDrill;

import me.lyneira.MachinaCore.plugin.MachinaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public class MachinaDrill extends MachinaPlugin {
    
    static MachinaDrill plugin;
    
    @Override
    protected void mpEnable() {
        plugin = this;
        addDetector(new Detector());
    }

    @Override
    protected void mpDisable() {

    }
    
//    final static Logger log = Logger.getLogger("Minecraft");
//    private MachinaCore machinaCore;
//
//    @Override
//    public final void onEnable() {
//        PluginDescriptionFile pdf = getDescription();
//        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");
//
//        ConfigurationManager config = new ConfigurationManager(this);
//        Drill.loadConfiguration(config.getAll());
//
//        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
//        machinaCore.registerBlueprint(Blueprint.instance);
//    }
//
//    @Override
//    public final void onDisable() {
//        PluginDescriptionFile pdf = getDescription();
//        log.info(pdf.getName() + " is now disabled.");
//
//        machinaCore.unRegisterBlueprint(Blueprint.instance);
//    }

}
