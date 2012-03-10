package me.lyneira.ItemRelay;

import java.util.logging.Logger;

import me.lyneira.MachinaFactoryCore.MachinaFactoryCore;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRelayPlugin extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private MachinaFactoryCore machinaFactoryCore;
    private Blueprint blueprint = new Blueprint();

    @Override
    public final void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        machinaFactoryCore = (MachinaFactoryCore) getServer().getPluginManager().getPlugin("MachinaFactoryCore");
        machinaFactoryCore.registerFactoryBlueprint(blueprint, ItemRelay.class, true);
    }

    @Override
    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        machinaFactoryCore.unregisterFactoryBlueprint(blueprint);
    }
}
