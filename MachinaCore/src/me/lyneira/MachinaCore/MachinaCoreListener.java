package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

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
        plugin.logInfo("Using id " + toolId + " as activation tool.");
    }
    
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // Take action if player right clicked a block with the tool.
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getItem().getTypeId() == toolId) {
            
        }
    }
}
