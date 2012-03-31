package me.lyneira.MachinaFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlueprintBlock;

/**
 * Class representing the blueprint of any factory component.
 * 
 * @author Lyneira
 */
public class ComponentBlueprint {
    private static Material coreMaterial = Material.BRICK;
    static Material pipelineMaterial = Material.WOOD;
    final List<BlueprintBlock> blueprintBase;
    final List<BlueprintBlock> blueprintInactive;
    final List<BlueprintBlock> blueprintActive;
    final int[] dataIndices;

    /**
     * Blocks that need collision detection when activating.
     */
    final List<BlueprintBlock> activateDiffPlus;
    /**
     * Blocks that need to be emptied when activating.
     */
    final List<BlueprintBlock> activateDiffMinus;
    /**
     * Blocks that need collision detection when deactivating.
     */
    final List<BlueprintBlock> deactivateDiffPlus;
    /**
     * Blocks that need to be emptied when deactivating.
     */
    final List<BlueprintBlock> deactivateDiffMinus;

    /**
     * Constructs a ComponentBlueprint from the given three blueprints. While
     * inactive the component will consist of the base and inactive blueprints.
     * While active, the active blueprint replaces the inactive blueprint.
     * <p>
     * Note: Attachables such as a torch should be put last in their array.
     * </p>
     * 
     * @param blueprintBase
     *            An array of {@link BlueprintBlock}s that is present in either
     *            state.
     * @param blueprintInactive
     *            An array of {@link BlueprintBlock}s that need to be present
     *            when the component first powers on.
     * @param blueprintActive
     *            An array of {@link BlueprintBlock}s that will replace the
     *            inactive blueprint while the component is powered on.
     */
    public ComponentBlueprint(BlueprintBlock[] blueprintBase, BlueprintBlock[] blueprintInactive, BlueprintBlock[] blueprintActive) {
        // Verify whether active and inactive have the same size and contain the
        // same block type IDs in the same order.
        if (blueprintInactive.length != blueprintActive.length)
            throw new Error("Error creating new ComponentBlueprint: blueprintInactive and blueprintActive must be the same size.");

        // Process the copyData and copyInventory values
        List<Integer> dataIndices = new ArrayList<Integer>();
        for (int i = 0; i < blueprintInactive.length; i++) {
            int typeId = blueprintInactive[i].typeId;
            if (typeId != blueprintActive[i].typeId) {
                throw new Error("Error creating new ComponentBlueprint: blueprintInactive and blueprintActive must contain the same blueprint block types in the same order.");
            }
            if (BlockData.copyData(typeId))
                dataIndices.add(i);
        }

        this.dataIndices = new int[dataIndices.size()];
        for (int i = 0; i < dataIndices.size(); i++)
            this.dataIndices[i] = dataIndices.get(i);

        this.blueprintBase = (blueprintBase == null) ? Arrays.asList(new BlueprintBlock[] {}) : Arrays.asList(blueprintBase.clone());
        this.blueprintInactive = (blueprintInactive == null) ? Arrays.asList(new BlueprintBlock[] {}) : Arrays.asList(blueprintInactive.clone());
        this.blueprintActive = (blueprintActive == null) ? Arrays.asList(new BlueprintBlock[] {}) : Arrays.asList(blueprintActive.clone());
        activateDiffPlus = positiveDifference(this.blueprintActive, this.blueprintInactive);
        activateDiffMinus = negativeDifference(this.blueprintInactive, this.blueprintActive);
        deactivateDiffPlus = positiveDifference(this.blueprintInactive, this.blueprintActive);
        deactivateDiffMinus = negativeDifference(this.blueprintActive, this.blueprintInactive);

    }

    /**
     * Detects the non-key blocks in the blueprint and returns true if the
     * component is active.
     * 
     * @param anchor
     * @param yaw
     * @return True if the detected machina is active.
     * @throws Exception
     *             if detection failed.
     */
    boolean detectOther(BlockLocation anchor, BlockRotation yaw) throws ComponentDetectException {
        if (!detectOther(anchor, yaw, blueprintBase))
            throw new ComponentDetectException();

        if (detectOther(anchor, yaw, blueprintInactive))
            return false;
        if (detectOther(anchor, yaw, blueprintActive))
            return true;

        throw new ComponentDetectException();
    }

    /**
     * Detects the non-key blocks in the given {@link BlueprintBlock} list and
     * return true if successful.
     * 
     * @param anchor
     * @param yaw
     * @param blueprint
     * @return True if the {@link BlueprintBlock} list was detected.
     */
    private boolean detectOther(BlockLocation anchor, BlockRotation yaw, List<BlueprintBlock> blueprint) {
        for (BlueprintBlock i : blueprint) {
            if (i.key)
                continue;

            int typeId = anchor.getRelative(i.vector(yaw)).getTypeId();

            if (typeId != i.typeId) {
                return false;
            }
        }
        return true;
    }

    /**
     * Calculates the difference set between blueprint and filterBy.
     * 
     * @param blueprint
     *            The blueprint to start with
     * @param filterBy
     *            The blueprint to filter by
     * @return A new list
     */
    private List<BlueprintBlock> positiveDifference(List<BlueprintBlock> blueprint, List<BlueprintBlock> filterBy) {
        List<BlueprintBlock> result = new ArrayList<BlueprintBlock>(blueprint.size());
        for (BlueprintBlock i : blueprint) {
            if (!filterBy.contains(i))
                result.add(i);
        }
        return result;
    }

    /**
     * Calculates the difference set between blueprint and filterBy. The
     * negative difference will always include attached blocks.
     * 
     * @param blueprint
     *            The blueprint to start with
     * @param filterBy
     *            The blueprint to filter by
     * @return A new list
     */
    private List<BlueprintBlock> negativeDifference(List<BlueprintBlock> blueprint, List<BlueprintBlock> filterBy) {
        List<BlueprintBlock> result = new ArrayList<BlueprintBlock>(blueprint.size());
        for (BlueprintBlock i : blueprint) {
            if (i.attached || !filterBy.contains(i))
                result.add(i);
        }
        return result;
    }
    
    /**
     * Returns the material for the core block of MachinaFactory components.
     * @return
     */
    public static final Material coreMaterial() {
        return coreMaterial;
    }
    
    /**
     * Returns the material used for pipelines.
     * @return
     */
    public static final Material pipelineMaterial() {
        return pipelineMaterial;
    }
    
    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        Material material = Material.getMaterial(configuration.getInt("core-material", coreMaterial.getId()));
        if (material != null)
            coreMaterial = material;
        material = Material.getMaterial(configuration.getInt("pipeline-material", pipelineMaterial.getId()));
        if (material != null)
            pipelineMaterial = material;
        
    }
}
