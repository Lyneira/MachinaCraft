package me.lyneira.MachinaDrill;

import org.bukkit.Material;

import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;
import me.lyneira.MachinaCore.machina.model.ConstructionModelTree;
import me.lyneira.MachinaCore.plugin.MachinaPlugin;

/**
 * Main Plugin.
 * 
 * @author Lyneira
 */
public class MachinaDrill extends MachinaPlugin {
    static int materialCore = Material.GOLD_BLOCK.getId();
    static int materialBase = Material.WOOD.getId();
    static int materialHeadNormal = Material.IRON_BLOCK.getId();
    static int materialHeadFast = Material.DIAMOND_BLOCK.getId();
    
    private final Detector detector = new Detector();

    @Override
    protected void mpEnable() {
        addDetector(createBlueprint());
    }

    @Override
    protected void mpDisable() {

    }
    
    private MachinaBlueprint createBlueprint() {
        MachinaBlock trigger = new MachinaBlock(0,0,0,materialCore);
        ConstructionModelTree model = new ConstructionModelTree();
        model.addBlock(trigger);
// TODO
        return new MachinaBlueprint(trigger, model);
        
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
