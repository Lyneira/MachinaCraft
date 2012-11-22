package me.lyneira.MachinaCore.event;

import me.lyneira.MachinaCore.block.MachinaBlock;
import gnu.trove.set.hash.THashSet;

/**
 * Called when a machina is being verified for integrity before receiving any
 * event. The controller can examine the result, do further checking, repair
 * damage and determine the outcome. To prevent unnecessary load, under normal
 * circumstances a {@link VerifyEvent} will occur at most once per tick per
 * machina, but this is not guaranteed.
 * 
 * @author Lyneira
 */
public class VerifyEvent extends Event {

    /**
     * Determines the outcome of the verify event.
     */
    public boolean verified = true;

    private THashSet<MachinaBlock> damage;

    /**
     * Returns true if the machina is damaged (there are blocks missing or the
     * wrong type)
     * 
     * @return True if the machina is damaged
     */
    public boolean isDamaged() {
        return damage != null;
    }

    /**
     * Returns the set of damaged blocks belonging to the machina in real-world
     * coordinates. Returns null if there is no damage.
     * 
     * @return A set of damaged blocks
     */
    public THashSet<MachinaBlock> getDamage() {
        return damage;
    }

    /**
     * Adds a block to the damaged blockset of this event.
     * 
     * @param block
     *            The block that is damaged
     */
    public void addDamage(MachinaBlock block) {
        if (damage == null) {
            damage = new THashSet<MachinaBlock>();
        }
        damage.add(block);
    }

    private static final EventDispatcher dispatcher = new EventDispatcher();

    @Override
    public EventDispatcher getDispatcher() {
        return dispatcher;
    }
}
