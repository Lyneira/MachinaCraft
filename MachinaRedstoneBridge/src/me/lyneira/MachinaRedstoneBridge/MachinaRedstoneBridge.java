package me.lyneira.MachinaRedstoneBridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import me.lyneira.DummyPlayer.DummyPlayer;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.ConfigurationManager;
import me.lyneira.MachinaCore.MachinaCore;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.material.Lever;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.util.permissions.DefaultPermissions;

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
    private List<Permission> permissions;
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
        
        scheduler.scheduleSyncDelayedTask(this, loadConfiguration);

        getServer().getPluginManager().registerEvents(new RedstoneBridgeListener(this), this);
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
                player = new DummyPlayer(getServer(), world, log);
                // Add permissions to the player.
                PermissionAttachment attachment = player.addAttachment(this);
                for (Permission p : permissions) {
                    attachment.setPermission(p, true);
                }
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

    private Runnable loadConfiguration = new Runnable() {
        @Override
        public void run() {
            ConfigurationManager config = new ConfigurationManager(MachinaRedstoneBridge.this);
            ConfigurationSection configuration = config.getAll();

            List<String> permissionStrings = configuration.getStringList("permissions");
            if (permissionStrings == null) {
                permissions = new ArrayList<Permission>(0);
                return;
            }
            PluginManager pluginManager = getServer().getPluginManager();
            permissions = new ArrayList<Permission>(permissionStrings.size());
            for (String p : permissionStrings) {
                Permission permission = pluginManager.getPermission(p);
                if (permission == null) {
                    permission = DefaultPermissions.registerPermission(new Permission(p));
                }
                if (permission != null) {
                    permissions.add(permission);
                }
            }
        }
    };
}
