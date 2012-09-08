package me.lyneira.MachinaCore.plugin;

/**
 * Template class for quick development of a machina plugin. In addition to the
 * functionality provided by MachinaCraftPlugin, provides registration of
 * machina blueprints.
 * 
 * @author Lyneira
 */
public abstract class MachinaPlugin extends MachinaCraftPlugin {

    @Override
    public final void onEnable() {
        super.onEnable();
        mpEnable();
    }

    @Override
    public final void onDisable() {
        mpDisable();
        super.onDisable();
    }
    
    // **********************************
    // *****   Override hooks for   *****
    // *****   implementing class   *****
    // **********************************
    //

    /**
     * Provides a hook for onEnable(). Override as necessary.
     */
    public void mpEnable() {
    }

    /**
     * Provides a hook for onDisable(). Override as necessary.
     */
    public void mpDisable() {
    }
}
