package me.lyneira.MachinaCore.event;

/**
 * Called when a machina is removed from the universe. This is irrevocable, to
 * keep this from happening in some circumstances, see TODO . The machina is not
 * checked for integrity when this event is fired, it may not be fully intact.
 * 
 * @author Lyneira
 */
public class RemovalEvent extends Event {
    private static final EventDispatcher dispatcher = new EventDispatcher();

    @Override
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
