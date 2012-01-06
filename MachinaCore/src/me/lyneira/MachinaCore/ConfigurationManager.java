package me.lyneira.MachinaCore;

import java.io.File;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationManager {
    private YamlConfiguration configuration;
    private final String fuelSection = "fuels";
    private final String blockSection = "blocks";

    void load(MachinaCore plugin) {
        File parent = plugin.getDataFolder();
        File file = new File(parent, "config.yml");

        if (!parent.exists())
            parent.mkdirs();

        if (file.exists()) {
            configuration = YamlConfiguration.loadConfiguration(file);
        } else {
            configuration = new YamlConfiguration();
            setHeader();
            try {
                configuration.save(file);
            } catch (Exception e) {
                MachinaCore.log.warning("MachinaCore: Could not create empty configuration file.");
            }
        }
        Configuration defaults = new MemoryConfiguration();
        defaults.createSection(fuelSection);
        defaults.createSection(blockSection);
        configuration.setDefaults(defaults);

        ConfigurationSection fuel = configuration.getConfigurationSection(fuelSection);
        ConfigurationSection blocks = configuration.getConfigurationSection(blockSection);

        Fuel.loadConfiguration(fuel);
        BlockData.loadConfiguration(blocks);
    }

    private final void setHeader() {
        String header = "MachinaCore configuration file for customizing fuel burn times and block properties.\n\n";
        header += "The values specified here can override built-in defaults, specify new fuels or additional block properties.\n";
        header += "For an overview of all ids in minecraft, see:\nhttp://www.minecraftwiki.net/wiki/Data_values\n\n";
        header += "fuels section: Specifies the burn time for an id in ticks (20 ticks = 1 second)\n";
        header += "blocks section: Specifies the following block properties per id.";
        header += " Built-in defaults will sometimes differ from the global defaults given below.";
        header += " It is recommended to only define properties that you know you want to change.\n";
        header += "    solid: <boolean> - default: false - Whether the block is solid, meaning a machina could rest on it.\n";
        header += "    drillable: <boolean> - default: false - Whether the block can be drilled.\n";
        header += "    drillTime: <integer> - default: 1 - The number of ticks it takes to drill this block.\n";
        header += "    drop: <integer> - default: -1 - The item id to drop when this block is broken. Default is to use the block's own id, a value of 0 means drop nothing.\n";
        header += "    data: <integer> - default: -1 - The data value to set for the item dropped. Default is to use the block's data value.\n";
        header += "    dropMin: <integer> - default: 1 - The minimum amount to drop. If less than 1, nothing will be dropped unless dropRandom causes the result to be above 0.\n";
        header += "    dropRandom: <integer> - default: 0 - A random amount (0 to dropRandom-1) to add to dropMin.\n";
        header += "    copyData: <boolean> - default: false - (ADVANCED) Whether to preserve the block's data value when moving as part of a machina.\n";
        header += "    hasInventory: <boolean> - default: false - (ADVANCED) Whether the block has an inventory that should be preserved when moving as part of a machina.\n";
        header += "    attached: <boolean> - default: false - (ADVANCED) Whether the block can only exist attached to another block. (levers, ladders, rails, doors, etc)\n";
        header += "\nExample configuration that changes some properties for wooden planks:\n";
        header += "fuels:\n";
        header += "    5: 800\n";
        header += "blocks:\n";
        header += "    5:\n";
        header += "        solid: false\n";
        header += "        drillable: false\n";
        configuration.options().header(header);
    }
}
