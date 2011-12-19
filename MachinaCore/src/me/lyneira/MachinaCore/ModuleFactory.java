package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

/**
 * Class for constructing a blueprint for a {@link Machina}. If it has a
 * direction, this blueprint represents it pointing towards the south.
 * <p>
 * x+ is forward, y+ is up, z+ is right
 * <p>
 * An instance of this class should be set to null once a
 * {@link MovableBlueprint} has been created with it.
 * 
 * @author Lyneira
 * 
 */
public final class ModuleFactory {

    private List<BlueprintBlock> moduleAttached = new ArrayList<BlueprintBlock>();
    private List<BlueprintBlock> module = new ArrayList<BlueprintBlock>();

    private List<BlueprintBlock> moduleFinal;

    private boolean finalized = false;
    
    public final int id;

    ModuleFactory(int id) {
        this.id = id;
    }
    
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
    public final List<BlueprintBlock> getBlueprintFinal() {
        if (!finalized)
            makeFinal();
        return moduleFinal;
    }
}
