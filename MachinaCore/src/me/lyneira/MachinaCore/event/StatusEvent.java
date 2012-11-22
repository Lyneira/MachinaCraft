package me.lyneira.MachinaCore.event;

import org.bukkit.entity.Player;

/**
 * Called when a player requests status information from a machina.
 * 
 * @author Lyneira
 * 
 */
public class StatusEvent extends Event {
    /**
     * The player requesting the machina's status
     */
    public final Player player;

    public StatusEvent(Player player) {
        this.player = player;
    }

    private static final EventDispatcher dispatcher = new EventDispatcher();

    @Override
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
