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
    static Material coreMaterial = Material.GOLD_BLOCK;
    static Material baseMaterial = Material.WOOD;
    static Material headMaterialNormal = Material.IRON_BLOCK;
    static Material headMaterialFast = Material.DIAMOND_BLOCK;
    
    private final Detector detector = new Detector();

    @Override
    protected void mpEnable() {
        // TODO
        addBlueprint(createBlueprint());
        
    }

    @Override
    protected void mpDisable() {

    }
    
    private MachinaBlueprint createBlueprint() {
        MachinaBlock trigger = new MachinaBlock(0,0,0,coreMaterial.getId());
        ConstructionModelTree model = new ConstructionModelTree();
// TODO
        return new MachinaBlueprint(detector, trigger, model);
        
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
