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
     * Called to inform the controller that its machina has been created and is
     * successfully added to the universe.
     * <p>
     * <b>Note:</b> Since it may also be called when a
     * machina gets loaded with the world, not just when a player creates a new
     * machina, this method is not a replacement for CreationEvent!
     * 
     * @param machina
     *            The machina to link to
     */
    public void initialize(Machina machina);
}
