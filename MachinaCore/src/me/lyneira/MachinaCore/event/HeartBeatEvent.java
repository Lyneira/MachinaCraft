package me.lyneira.MachinaCore.event;

/**
 * Called when a machina receives a "heart beat", meaning it should examine its
 * state and potentially take action.
 * 
 * @author Lyneira
 * 
 */
public class HeartBeatEvent extends Event {

    private int nextDelay = 0;

    /**
     * Returns the delay in server ticks for the next heartbeat. If 0 or
     * negative, there will be no next heartbeat.
     * 
     * @return Delay for the next heartbeat
     */
    public int getNext() {
        return nextDelay;
    }

    /**
     * Sets the delay in server ticks for the next heartbeat to occur. If set to
     * 0 or negative, no next heartbeat will occur. Defaults to 0.
     * 
     * @param delay
     *            Delay for the next heartbeat
     */
    public void next(int delay) {
        nextDelay = delay;
    }

    private static final EventDispatcher dispatcher = new EventDispatcher();

    @Override
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
