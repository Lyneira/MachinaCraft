package me.lyneira.MachinaCore.machina;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitTask;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.event.Event;
import me.lyneira.MachinaCore.event.EventHandler;
import me.lyneira.MachinaCore.event.HeartBeatEvent;
import me.lyneira.MachinaCore.event.RemovalEvent;
import me.lyneira.MachinaCore.event.VerifyEvent;
import me.lyneira.MachinaCore.machina.model.MachinaModel;

/**
 * 
 * Class representing any machina. A Machina is a collection of blocks that
 * constitutes a machine in the game world. This machine can move, change shape
 * or take other actions controlled by its plugin.
 * <p/>
 * 
 * A Machina consists of a model with a tree structure. The model represents the
 * machina as it exists in the game world right now and cannot be modified
 * directly. Modifications will be held in a shadow copy of the relevant portion
 * of the model which the controller can edit. The modified model can be
 * manifested by updating the machina.
 * <p/>
 * 
 * A machina keeps track of the blocks that it is made up of in two ways: The
 * machina-centric model and the world-centric instance. The model can be
 * modified by the plugin controlling this machina, while the instance is
 * managed by the machina's universe.
 * 
 * @author Lyneira
 */
public final class Machina {

    /* **************************
     * Plugin API
     */

    public final Universe universe;
    public final MachinaController controller;

    /**
     * Causes a {@link HeartBeatEvent} to occur after the given delay in server
     * ticks. If a delay < 1 is specified, no event will occur and if an event
     * was scheduled earlier, it will be cancelled.
     * 
     * @param delay
     *            The delay for the next heartbeat
     */
    public void setHeartBeat(int delay) {
        if (heartBeat != null) {
            heartBeat.cancel();
        }
        if (delay < 1) {
            heartBeat = null;
            return;
        }
        heartBeat = MachinaCore.runTask(new Runnable() {
            @Override
            public void run() {
                heartBeat = null;
                HeartBeatEvent event = new HeartBeatEvent();
                Machina.this.callEvent(event);
                setHeartBeat(event.getNext());
            }
        }, delay);
    }

    /**
     * Calls the given event on this machina's controller. If the controller has
     * an {@link EventHandler} set up for the event, it will be able to react to
     * it.
     * 
     * @param event
     *            The event to call
     */
    public void callEvent(Event event) {
        if (verify()) {
            callEventInternal(event);
        }
    }

    /**
     * Sets this machina as unverified. This guarantees that another
     * verification will be done if this machina receives any further events on
     * this server tick.
     */
    public void setUnverified() {
        lastVerify = -1;
    }

    /* **************************
     * MachinaCore API
     */

    /**
     * Machina centric model that allows advanced manipulation of the machina's
     * blocks
     */
    private final MachinaModel model;
    /**
     * The blocks belonging to this machina in real world coordinates
     */
    private MachinaBlock[] instance;
    /**
     * The current heartbeat task scheduled.
     */
    private BukkitTask heartBeat;
    /**
     * Used to avoid calling verify() more than once in a tick.
     */
    private long lastVerify = -1;

    Machina(Universe universe, MachinaModel model, MachinaController controller) {
        this.universe = universe;
        this.model = model;
        this.controller = controller;
    }

    /**
     * Returns a MachinaUpdate holding all necessary data to perform an update
     * to the machina. The update contains only data about modified parts of the
     * machina's model.
     * 
     * <li>Arrays are all the same size.
     * 
     * <li>The block arrays have no null elements.
     * 
     * <li>Each element is unique.
     * 
     * <li>The inventories array only contains non-null elements for the blocks
     * that need to have their inventory overwritten, and the arrays therein are
     * the correct size for the type of inventory being overwritten.
     * 
     * @return An update for the machina
     */
    MachinaUpdate createUpdate() {
        // TODO
        return new MachinaUpdate(null, null, null);
    }

    void initialize() {
        controller.initialize(this);
    }

    /**
     * Returns the array containing all the machina's blocks in absolute
     * positions. The array has no null elements.
     * 
     * @return Array of the machina's blocks
     */
    MachinaBlock[] instance() {
        if (instance == null) {
            instance = model.instance();
        }
        return instance;
    }

    /**
     * Sends the irrevocable removal event and prepares the machina for removal
     * from the universe.
     */
    void onRemove() {
        callEventInternal(new RemovalEvent());

        // Cancel any potential heartbeats that may be pending for this machina
        setHeartBeat(0);
    }

    private void callEventInternal(Event event) {
        try {
            event.getDispatcher().dispatch(controller, event);
        } catch (Throwable ex) {
            MachinaCore.exception("Could not pass event " + event.getEventName() + " to " + getControllerName(), ex);
        }
    }

    private boolean verify() {
        final World world = universe.world;
        final long time = world.getFullTime();
        if (time == lastVerify) {
            return true;
        }
        final VerifyEvent event = new VerifyEvent();
        for (MachinaBlock block : instance()) {
            if (!block.match(world)) {
                event.addDamage(block);
                event.verified = false;
            }
        }
        // Allow controller to act now that the basic blocks have been verified.
        callEventInternal(event);
        if (event.verified) {
            lastVerify = time;
            return true;
        }

        // Verify failed, remove this machina
        universe.remove(this);
        return false;
    }

    private String getControllerName() {
        return controller.getClass().getSimpleName();
    }
}
