package me.lyneira.MachinaCore.machina;

/**
 * Interface for a class that is responsible for controlling a Machina's
 * actions.
 * 
 * @author Lyneira
 * 
 */
public interface MachinaController {
    /**
     * Called when a machina is successfully added to the universe. 
     * 
     * @param machina The machina to link to
     */
    public void initialize(Machina machina);
}
