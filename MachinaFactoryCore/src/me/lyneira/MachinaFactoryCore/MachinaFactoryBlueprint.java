package me.lyneira.MachinaFactoryCore;

import me.lyneira.MachinaCore.MachinaBlueprint;

/**
 * Interface required for registering with MachinaFactoryCore.
 * 
 * @author Lyneira
 */
public interface MachinaFactoryBlueprint extends MachinaBlueprint {
    /**
     * Returns true if this blueprint may be activated by a lever.<br>
     * MachinaFactoryCore will register it with MachinaCore if true.
     * 
     * @return True if this blueprint may be activated by a lever.
     */
    public boolean leverActivatable();
}
