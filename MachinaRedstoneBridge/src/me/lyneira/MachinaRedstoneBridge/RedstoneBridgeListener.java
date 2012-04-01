package me.lyneira.MachinaRedstoneBridge;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.material.Diode;

class RedstoneBridgeListener implements Listener {
    private static final int repeaterOn = Material.DIODE_BLOCK_ON.getId();
    private static final int bridgeBlock = Material.BRICK.getId();

    private final MachinaRedstoneBridge plugin;

    RedstoneBridgeListener(MachinaRedstoneBridge plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onRedstone(BlockPhysicsEvent event) {
        if (event.getChangedTypeId() != repeaterOn)
            return;
        Block block = event.getBlock();
        if (block.getTypeId() == repeaterOn) {
            ((Diode) block.getState().getData()).getFacing();
            BlockFace direction = ((Diode) block.getState().getData()).getFacing();
            Block target = block.getRelative(direction);
            if (target.getTypeId() == bridgeBlock) {
                // Queue up the bridge's target.
                plugin.queueDetect(target.getRelative(direction));
            }
        }
    }
}
