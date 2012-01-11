package me.lyneira.MachinaCore;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Chunk;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Wrapper class for a Machina. Keeps track of all active Machinae, initiates
 * verification checks and heartbeats, handles deactivation and scheduling.
 * 
 * @author Lyneira
 */
class MachinaRunner implements Runnable {
    /**
     * A static Map of all existing MachinaRunner instances
     */
    private static final Map<BlockLocation, MachinaRunner> machinae = new HashMap<BlockLocation, MachinaRunner>();
    private static final int chunkUnloadDistance = 1;

    private final MachinaCore plugin;
    private final Machina machina;
    private BlockLocation anchor;

    private boolean active = true;

    /**
     * Constructs a MachinaRunner which will immediately schedule the given
     * Machina for activation.
     * 
     * @param plugin
     *            The MachinaCore plugin
     * @param machina
     * @param anchor
     * @param leverFace
     */
    MachinaRunner(final MachinaCore plugin, final Machina machina, final BlockLocation anchor, final BlockFace leverFace) {
        this.plugin = plugin;
        this.machina = machina;
        this.anchor = anchor;
        machinae.put(anchor, this);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this);
    }

    /**
     * Runs the machina.
     */
    public void run() {
        if (!active)
            return;
        if (machina.verify(anchor)) {
            HeartBeatEvent event = machina.heartBeat(anchor);
            if (event == null || event.delay <= 0) {
                deActivate();
            } else {
                if (event.newAnchor != null) {
                    machinae.remove(anchor);
                    anchor = event.newAnchor;
                    machinae.put(anchor, this);
                }
                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, event.delay);
            }
        } else {
            deActivate();
        }
    }

    /**
     * Returns whether a machina anchor exists for the given BlockLocation
     * 
     * @param location
     *            The location to check for
     * @return True if a machina anchor exists at this location
     */
    static final boolean exists(final BlockLocation location) {
        return machinae.containsKey(location);
    }

    /**
     * Runs the machina's onLever function, and deactivates it if it returns
     * false.
     * 
     * @param location
     *            The location of the machina anchor
     * @param player
     *            The player attempting to deactivate the machina
     */
    static void onLever(final BlockLocation location, Player player, ItemStack item) {

        MachinaRunner machinaRunner = machinae.get(location);
        if (machinaRunner == null || (!machinaRunner.active))
            return;

        if (!machinaRunner.machina.verify(location) || !machinaRunner.machina.onLever(location, player, item)) {
            machinaRunner.deActivate();
        }
    }

    /**
     * Deactivates any MachinaRunner that may be present at the given
     * BlockLocation
     * 
     * @param location
     *            The location to deactivate
     */
    static final void deActivate(final BlockLocation location) {
        MachinaRunner machinaRunner = machinae.get(location);
        if (machinaRunner != null) {
            machinaRunner.deActivate();
        }
    }

    /**
     * Deactivates all MachinaRunners
     */
    static final void deActivateAll() {
        for (MachinaRunner machinaRunner : machinae.values()) {
            machinaRunner.deActivate();
        }
    }

    /**
     * Deactivates this MachinaRunner.
     */
    final void deActivate() {
        if (!active)
            return;

        active = false;
        machinae.remove(anchor);
        machina.onDeActivate(anchor);
    }

    /**
     * Deactivates this MachinaRunner without modifying the machinae hashmap.
     * Intended for use with an iteration over the hashmap.
     */
    private final void deActivateSafely() {
        if (!active)
            return;

        active = false;
        machina.onDeActivate(anchor);
    }

    /**
     * Deactivates all machina in or near an unloaded chunk.
     * 
     * @param chunk
     *            The chunk that is being unloaded
     */
    static final void notifyChunkUnload(Chunk chunk) {
        int x = chunk.getX();
        int z = chunk.getZ();

        for (Iterator<MachinaRunner> it = machinae.values().iterator(); it.hasNext();) {
            MachinaRunner machinaRunner = it.next();
            Chunk machinaChunk = machinaRunner.anchor.getBlock().getChunk();

            int xDistance = Math.abs(x - machinaChunk.getX());
            int zDistance = Math.abs(z - machinaChunk.getZ());

            if (xDistance <= chunkUnloadDistance && zDistance <= chunkUnloadDistance) {
                machinaRunner.deActivateSafely();
                it.remove();
            }
        }
    }
}
