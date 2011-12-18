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
public final class BlueprintFactory {

    private List<BlueprintBlock> blueprintAttached = new ArrayList<BlueprintBlock>();
    private List<Integer> handlesAttached = new ArrayList<Integer>();

    private List<BlueprintBlock> blueprint = new ArrayList<BlueprintBlock>();
    private List<Integer> handles = new ArrayList<Integer>();

    private List<BlueprintBlock> blueprintFinal;
    private List<Integer> handlesFinal;

    private boolean finalized = false;

    /**
     * Adds the given Blueprint block to the blueprint storage.
     * 
     * @param block
     *            The BlueprintBlock to add.
     * @return The index of the block that was added, within its respective List
     */
    private final void addBlock(BlueprintBlock block, boolean key) {
        if (block.attached) {
            if (key)
                handlesAttached.add(blueprintAttached.size());
            blueprintAttached.add(block);
        } else {
            if (key)
                handles.add(blueprint.size());
            blueprint.add(block);
        }
    }

    /**
     * Makes the blueprint final. Adding to it is not possible after this
     * function has been called.
     */
    private final void makeFinal() {
        blueprintFinal = new ArrayList<BlueprintBlock>(blueprintAttached.size() + blueprint.size());
        int handlesAttachedSize = handlesAttached.size();
        handlesFinal = new ArrayList<Integer>(handlesAttachedSize + handles.size());

        blueprintFinal.addAll(blueprintAttached);
        handlesFinal.addAll(handlesAttached);

        blueprintFinal.addAll(blueprint);
        for (int i : handles) {
            handlesFinal.add(i + handlesAttachedSize);
        }

        blueprintAttached = null;
        handlesAttached = null;
        blueprint = null;
        handles = null;

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
    public final BlueprintFactory add(BlockVector vector, Material type) {
        if (!finalized)
            addBlock(new BlueprintBlock(vector, type, false), false);
        else
            MachinaCore.log.warning("Attempt to add to a finalized blueprint failed.");
        return this;
    }

    /**
     * Adds a key block to the blueprint.
     * 
     * @param vector
     *            {@link BlockVector} specifying where this block is located in
     *            relation to the anchor
     * @param type
     *            {@link Material}
     */
    public final BlueprintFactory addKey(BlockVector vector, Material type) {
        if (!finalized)
            addBlock(new BlueprintBlock(vector, type, true), true);
        else
            MachinaCore.log.warning("Attempt to add to a finalized blueprint failed.");
        return this;
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
        return blueprintFinal;
    }

    /**
     * Returns the final list of handles to the key blocks that have been added
     * to the blueprint. The list will contain the handles in the order the key
     * blocks were added. After this function has been called, adding new blocks
     * to the blueprint is not possible.
     * 
     * @return A List of indices to key blocks.
     */
    public final List<Integer> getHandlesFinal() {
        if (!finalized)
            makeFinal();
        return handlesFinal;
    }
}
