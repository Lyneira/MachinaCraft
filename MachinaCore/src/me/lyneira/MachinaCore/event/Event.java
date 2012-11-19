package me.lyneira.MachinaCore.event;

/**
 * Represents an event that a machina can receive.
 * 
 * @author Lyneira
 */
public abstract class Event {
    /**
     * Returns the dispatcher for this event type. For any implementing classes,
     * the dispatcher returned from this method should be declared
     * "private static final" and not shared with any other event classes.
     * 
     * @return The dispatcher for this event type
     */
    public abstract EventDispatcher getDispatcher();
    
    public String getEventName() {
        return getClass().getSimpleName();
    }
}
