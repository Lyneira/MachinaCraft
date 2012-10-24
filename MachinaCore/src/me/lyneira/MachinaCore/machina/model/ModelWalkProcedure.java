package me.lyneira.MachinaCore.machina.model;

import me.lyneira.MachinaCore.block.MachinaBlock;

/**
 * Interface for procedures that walk a ModelTree .
 * 
 * @author Lyneira
 */
public interface ModelWalkProcedure {

    public boolean execute(MachinaBlock block);
}
