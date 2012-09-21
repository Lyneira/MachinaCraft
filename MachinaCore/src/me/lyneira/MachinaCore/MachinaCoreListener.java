package me.lyneira.MachinaCore;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

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
        ItemStack item = event.getItem();
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && item.getTypeId() == toolId) {
            Player player = event.getPlayer();
            if (plugin.onMachinaTool(player, event.getClickedBlock())) {
                // The tool was used for something, decrease durability.
                short maxDurability = item.getType().getMaxDurability();
                if (maxDurability == 0)
                    return;
                short newDurability = (short) (item.getDurability() + 1);
                if (newDurability >= maxDurability) {
                    player.setItemInHand(null);
                } else {
                    item.setDurability(newDurability);
                }
            }
        }
    }
}
