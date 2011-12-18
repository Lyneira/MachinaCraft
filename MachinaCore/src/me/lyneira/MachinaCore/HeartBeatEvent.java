package me.lyneira.MachinaCore;

/**
 * Represents the result of a Machina's heartbeat.
 * 
 * @author Lyneira
 */
public final class HeartBeatEvent {
    /**
     * The delay until the next heartbeat. A value of 0 or negative means
     * deactivation.
     */
    public final int delay;

    /**
     * The new anchor location for the machina. A null value means the machina
     * does not need to be moved.
     */
    public final BlockLocation newAnchor;

    /**
     * Constructor for a HeartBeatEvent that only specifies a delay. This
     * function is equal to HeartBeatEvent(delay, null)
     * 
     * @param delay
     *            The delay until the next heartbeat
     */
    public HeartBeatEvent(final int delay) {
        this.delay = delay;
        this.newAnchor = null;
    }

    /**
     * Constructor for a HeartBeatEvent that specifies both a delay and a new
     * anchor.
     * 
     * @param delay
     *            The delay until the next heartbeat
     * @param newAnchor
     *            The new anchor location of the machina.
     */
    public HeartBeatEvent(final int delay, final BlockLocation newAnchor) {
        this.delay = delay;
        this.newAnchor = newAnchor;
    }
}
