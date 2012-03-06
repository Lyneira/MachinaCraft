package me.lyneira.MachinaCore;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * 
 * Represents a machine that can be activated by a lever.
 * 
 * @author Lyneira
 */
public interface Machina {
    /**
     * Function that is called to verify the integrity of the machina at the
     * given BlockLocation while it is running. Unlike the detect function in
     * MachinaBlueprint, this function can not assume the lever for this machina
     * has been checked.
     * 
     * @param anchor
     *            The anchor location to which the machina's lever is attached
     * @return True if the machina is intact
     */
    public boolean verify(final BlockLocation anchor);

    /**
     * Function that is called when the machina at the given BlockLocation can
     * take action. This function is called only after verify() succeeds. It may
     * return null or a HeartBeatEvent with a delay of 0 or less to indicate it
     * should deactivate.
     * 
     * @param anchor
     *            The anchor location to which the machina's lever is attached
     * @return A HeartBeatEvent specifying the result of the heartbeat.
     */
    public HeartBeatEvent heartBeat(final BlockLocation anchor);

    /**
     * This function is called when a player flips a lever on a running machina.
     * It should usually deactivate, but using a certain item to flip the
     * machina could cause it to take a different action. This also gives the
     * machina a chance to say whether or not it should really deactivate. (for
     * example, deactivate permission) MachinaCore will deactivate the machina
     * if this function returns false.
     * 
     * @param anchor
     *            The anchor location to which the machina's lever is attached
     * @param player
     *            The player attempting to deactivate the machina
     * @return True if the machina should continue to move.
     */
    public boolean onLever(final BlockLocation anchor, final Player player, final ItemStack itemInHand);

    /**
     * Function that is called at the moment of deactivation.<br>
     * <br>
     * <b>Important:</b> The machina may not be intact at this point!
     * 
     * @param anchor
     *            The anchor location to which the machina's lever is attached
     */
    public void onDeActivate(final BlockLocation anchor);
}
