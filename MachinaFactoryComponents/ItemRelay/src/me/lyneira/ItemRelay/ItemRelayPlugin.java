package me.lyneira.ItemRelay;

import java.util.logging.Logger;

import me.lyneira.MachinaFactory.MachinaFactory;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class ItemRelayPlugin extends JavaPlugin {
    final static Logger log = Logger.getLogger("Minecraft");
    private MachinaFactory machinaFactory;
    private Blueprint blueprint = new Blueprint();

    @Override
    public final void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        machinaFactory = (MachinaFactory) getServer().getPluginManager().getPlugin("MachinaFactory");
        machinaFactory.registerFactoryBlueprint(blueprint, ItemRelay.class, true);
    }

    @Override
    public final void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");

        machinaFactory.unregisterFactoryBlueprint(blueprint);
    }
}
