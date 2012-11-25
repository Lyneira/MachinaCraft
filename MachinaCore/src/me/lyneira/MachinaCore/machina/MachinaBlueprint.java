package me.lyneira.MachinaCore.machina;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.event.CreationEvent;
import me.lyneira.MachinaCore.machina.model.BlueprintModel;
import me.lyneira.MachinaCore.machina.model.ConstructionModel;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Represents the blueprint of a machina. It is used to detect and create a new
 * machina when the player rightclicks the appropriate tool on the specified
 * trigger block. Its results are handed to the BlueprintDetector that made it
 * It can be supplied with a detector class that identifies additional portions
 * of the machina and performs other detect-time configuration.
 * 
 * The trigger block
 * 
 * @author Lyneira
 */
public class MachinaBlueprint {

    /**
     * The model that should be scanned for when detection is triggered. The
     * base model is a part of the machina that remains present and static in
     * all forms and variations of the machina. For best detection performance,
     * it should be a set of blocks that fully defines the direction of the
     * machina. (for example blocks of differing types that don't occur
     * symmetrically)
     */
    private final BlueprintModel baseModel;

    /**
     * Callback class that will be run when the base model has been detected. It
     * may detect extensions on the base model and add them to the machina being
     * constructed. The detector will receive the direction used by MachinaCore
     * to detect the base model, but in some cases (like a symmetric machina)
     * this may not be the correct direction.
     */
    private MachinaDetector detector;
    private final MachinaBlock triggerBlock;

    public MachinaBlueprint(MachinaBlock triggerBlock, BlueprintModel baseModel) {
        this.baseModel = new BlueprintModel(baseModel);
        if (triggerBlock == null)
            throw new NullPointerException("Cannot construct a MachinaBlueprint without a trigger block!");
        this.triggerBlock = triggerBlock;
    }

    /*
     * Extensions - Extensions are the same as a base model but the location and
     * presence of these is variable. They are not directly detected by
     * MachinaCore, instead the Detector can trigger detection of an Extension
     * at a specific location. If successful, the extension will be added to the
     * model of the machina being constructed. The offset is automatically
     * calculated by MachinaCore. A single extension can be detected multiple
     * times at different locations.
     * 
     * They can be predefined same as the base model. Coordinates of the
     * extension's blocks are specific to this extension.
     */

    /*
     * Detector - Callback class that will be run when the base model has been
     * detected. It may detect extensions on the base model and add them to the
     * machina being constructed. The detector will receive the direction used
     * by MachinaCore to detect the base model, but in some cases (like a
     * symmetric machina) this may not be the correct direction.
     */

    /**
     * Detects whether a machina conforming to this blueprint is present at the
     * given block and adds it to the given universe.
     * 
     * @param universe
     *            The universe (and its corresponding world) to detect in
     * @param block
     *            The block to detect at
     * @param player
     *            The player that initiated this detection or null if not a
     *            player
     * @return
     */
    public DetectResult detect(Universe universe, Block block, Player player) {
        if (!triggerBlock.matchTypeOnly(block))
            return DetectResult.FAILURE;

        ConstructionModel constructionModel = null;
        for (BlockRotation r : BlockRotation.values()) {
            final MachinaBlock rotatedTrigger = triggerBlock.rotateYaw(r);
            final BlockVector origin = new BlockVector(block.getX() - rotatedTrigger.x, block.getY() - rotatedTrigger.y, block.getZ() - rotatedTrigger.z);
            final World world = block.getWorld();
            constructionModel = baseModel.construct(block.getWorld(), r, origin);
            if (constructionModel != null) {
                /*
                 * Machina base model was constructed, now hand it to the
                 * detector for extension
                 */
                final MachinaController controller = detector.detect(constructionModel, player, world, r, origin);
                final Machina machina;
                if (controller == null) {
                    return DetectResult.FAILURE;
                } else if (universe.add(machina = new Machina(universe, constructionModel.machinaModel(), controller))) {
                    machina.scheduleEvent(new CreationEvent(player), 1);
                    return DetectResult.SUCCESS;
                } else {
                    MachinaCore.info("Successfully detected a machina but had a collision!");
                    return DetectResult.COLLISION;
                }
            }
        }
        return DetectResult.FAILURE;
    }

    public final static MachinaCore.MachinaBlueprintFriend machinaCoreFriend = new MachinaCore.MachinaBlueprintFriend() {
        @Override
        protected void setDetector(MachinaBlueprint blueprint, MachinaDetector detector) {
            blueprint.detector = detector;
        }
    };
}
