package me.lyneira.MachinaCore;

import me.lyneira.MachinaCore.plugin.MPConfig;
import me.lyneira.MachinaCore.tool.ToolInteractResult;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Listener for Bukkit events.
 * 
 * @author Lyneira
 */
public class MachinaCoreListener implements Listener {

    private final MachinaCore plugin;
    private final int wrenchId;
    private final int tinkerToolId;

    MachinaCoreListener(MachinaCore plugin) {
        this.plugin = plugin;
        MPConfig config = plugin.mpGetConfig();
        wrenchId = config.getMaterialId("machina-wrench", Material.WOOD_AXE.getId());
        tinkerToolId = config.getMaterialId("machina-tinkertool", Material.WOOD_HOE.getId());
        plugin.logInfo("Using id " + wrenchId + " as wrench tool.");
        plugin.logInfo("Using id " + tinkerToolId + " as tinkering tool.");
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null)
            return;
        int typeId = item.getTypeId();
        ToolInteractResult result = ToolInteractResult.NODAMAGE;
        if (typeId == wrenchId) {
            // Call the wrench method
            result = plugin.wrenchClick(event.getPlayer(), event.getClickedBlock(), event.getAction() == Action.RIGHT_CLICK_BLOCK);
        } else if (typeId == tinkerToolId) {
            // TODO Call the tinker tool method
        } else {
            // TODO Allow machina to react to the item used on click
        }

        if (result == ToolInteractResult.DAMAGE) {
            // The tool was used for something, decrease durability.
            short maxDurability = item.getType().getMaxDurability();
            if (maxDurability == 0)
                return;
            short newDurability = (short) (item.getDurability() + 1);
            if (newDurability >= maxDurability) {
                event.getPlayer().setItemInHand(null);
            } else {
                item.setDurability(newDurability);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldLoad(WorldLoadEvent event) {
        plugin.multiverse.load(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldUnload(WorldUnloadEvent event) {
        plugin.multiverse.unload(event.getWorld());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onWorldSave(WorldSaveEvent event) {
        plugin.multiverse.save(event.getWorld());
    }
}
