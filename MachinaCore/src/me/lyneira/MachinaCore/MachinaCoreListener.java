package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.event.Listener;

/**
 * Listener for Bukkit events.
 * 
 * @author Lyneira
 */
public class MachinaCoreListener implements Listener {

    private final MachinaCore plugin;
    private final int toolId;

    MachinaCoreListener(MachinaCore plugin) {
        this.plugin = plugin;
        toolId = plugin.mpGetConfig().getMaterialId("machina-tool", Material.WOOD_AXE.getId());
        plugin.logInfo("I found tool id " + toolId);
    }
}
