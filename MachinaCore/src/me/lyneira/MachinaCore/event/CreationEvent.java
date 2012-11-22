package me.lyneira.MachinaCore.event;

import org.bukkit.entity.Player;

/**
 * Called when a machina is first created by a player.
 * 
 * @author Lyneira
 */
public class CreationEvent extends Event {

    private final Player player;

    public CreationEvent(Player player) {
        this.player = player;
    }

    /**
     * Returns the player that created the machina. May be null if it was
     * created by another plugin.
     * 
     * @return The player that created the machina, or null if none
     */
    public Player getPlayer() {
        return player;
    }

    private static final EventDispatcher dispatcher = new EventDispatcher();

    @Override
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
