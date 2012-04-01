package me.lyneira.MachinaRedstoneBridge;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import me.lyneira.DummyPlayer.DummyPlayer;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.material.Lever;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

/**
 * Plugin that attempts to activate a machina if a repeater powers an activator
 * block, and the block in front of the activator is a lever.
 * 
 * @author Lyneira
 */
public class MachinaRedstoneBridge extends JavaPlugin implements Runnable {
    private final static Logger log = Logger.getLogger("Minecraft");
    private final static int leverType = Material.LEVER.getId();

    private final Set<Block> queuedBlocks = new LinkedHashSet<Block>();

    private MachinaCore machinaCore;
    private Map<World, Player> dummyPlayers;

    private boolean queueScheduled = false;
    private BukkitScheduler scheduler;

    @Override
    public void onEnable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " version " + pdf.getVersion() + " is now enabled.");

        machinaCore = (MachinaCore) getServer().getPluginManager().getPlugin("MachinaCore");
        scheduler = getServer().getScheduler();
        dummyPlayers = new HashMap<World, Player>(8);

        this.getServer().getPluginManager().registerEvents(new RedstoneBridgeListener(this), this);
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile pdf = getDescription();
        log.info(pdf.getName() + " is now disabled.");
    }

    /**
     * Queues a lever block for machina detection.
     * 
     * @param block
     */
    void queueDetect(Block block) {
        if (block.getTypeId() != leverType)
            return;
        queuedBlocks.add(block);
        if (!queueScheduled) {
            queueScheduled = true;
            scheduler.scheduleSyncDelayedTask(this, this);
        }
    }

    /**
     * Processes the queue of scheduled Machina detects.
     */
    @Override
    public void run() {
        queueScheduled = false;
        for (Iterator<Block> it = queuedBlocks.iterator(); it.hasNext();) {
            Block block = it.next();
            it.remove();

            if (block.getTypeId() != leverType)
                continue;

            Player player = dummyPlayers.get(block.getWorld());
            if (player == null) {
                World world = block.getWorld();
                player = new DummyPlayer(getServer(), world);
                dummyPlayers.put(world, player);
            }

            Lever lever = (Lever) block.getState().getData();
            BlockFace attachedFace = lever.getAttachedFace();
            if (attachedFace == null) {
                log.warning("MachinaRedstoneBridge: Lever at " + block.toString() + "seems to be attached to nothing?");
                return;
            }
            Block attachedTo = block.getRelative(attachedFace);
            machinaCore.onLever(player, new BlockLocation(attachedTo), attachedFace.getOppositeFace(), null);
        }
    }

    void log(String message) {
        log.info("MachinaRedstoneBridge: " + message);
    }
}
