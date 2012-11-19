package me.lyneira.MachinaCore.event;

import org.bukkit.event.EventException;

import me.lyneira.MachinaCore.machina.MachinaController;

/**
 * Defines the class for event callbacks to MachinaControllers
 * 
 * @author Lyneira
 */
public interface EventExecutor {
    public void execute(MachinaController controller, Event event) throws EventException;
}
