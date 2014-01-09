package me.lyneira.MachinaRedstoneBridge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Diode;
import org.bukkit.event.block.BlockRedstoneEvent;
class RedstoneBridgeListener implements Listener {
    private static final int repeateroff = Material.DIODE_BLOCK_OFF.getId();
    static int bridgeBlock = Material.BRICK.getId();

    private final MachinaRedstoneBridge plugin;

    RedstoneBridgeListener(MachinaRedstoneBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRedstone(BlockRedstoneEvent event) {
       // if (event. != repeaterOn)
       //     return;
        Block block = event.getBlock();
        if (block.getTypeId() == repeateroff) {
            if (block.getRelative(BlockFace.DOWN).getTypeId() == bridgeBlock) {
                BlockFace direction = ((Diode) block.getState().getData()).getFacing();
                Block target = block.getRelative(direction);
                // Queue up the bridge's target.
                plugin.queueDetect(target);
            }
        }
    }
}
