package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

/**
 * Factory class for constructing a module for a {@link Movable} machina. The
 * module is specified using the add or addKey functions, pointing towards the
 * east.
 * <p>
 * x+ is forward, y+ is up, z+ is right
 * 
 * @author Lyneira
 *
 */
final class ModuleFactory {

    private List<BlueprintBlock> moduleAttached = new ArrayList<BlueprintBlock>();
    private List<BlueprintBlock> module = new ArrayList<BlueprintBlock>();

    private List<BlueprintBlock> moduleFinal;

    private boolean finalized = false;

    /**
     * Adds the given Blueprint block to the blueprint storage.
     * 
     * @param block
     *            The BlueprintBlock to add.
     */
    private final void addBlock(BlueprintBlock block) {
        if (block.attached) {
            moduleAttached.add(block);
        } else {
            module.add(block);
        }
    }

    /**
     * Makes the blueprint final. Adding to it is not possible after this
     * function has been called.
     */
    private final void makeFinal() {
        moduleFinal = new ArrayList<BlueprintBlock>(moduleAttached.size() + module.size());

        moduleFinal.addAll(moduleAttached);

        moduleFinal.addAll(module);

        moduleAttached = null;
        module = null;

        finalized = true;
    }

    /**
     * Adds a non-key block to the blueprint.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     */
    public final BlueprintBlock add(BlockVector vector, Material type) {
        if (!finalized) {
            BlueprintBlock block = new BlueprintBlock(vector, type, false);
            addBlock(block);
            return block;
        } else {
            MachinaCore.log.warning("Attempt to add to a finalized blueprint failed.");
            return null;
        }
    }

    /**
     * Adds a key block to the blueprint. A key block will not be detected
     * automatically when a Movable machina is first activated.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     */
    public final BlueprintBlock addKey(BlockVector vector, Material type) {
        if (!finalized) {
            BlueprintBlock block = new BlueprintBlock(vector, type, true);
            addBlock(block);
            return block;
        } else {
            MachinaCore.log.warning("Attempt to add to a finalized blueprint failed.");
            return null;
        }
    }

    /**
     * Returns the final list of BlueprintBlocks from this blueprint. After this
     * function has been called, adding new blocks to the blueprint is not
     * possible.
     * 
     * @return A List of BlueprintBlocks.
     */
    final List<BlueprintBlock> getBlueprintFinal() {
        if (!finalized)
            makeFinal();
        return moduleFinal;
    }
}
