package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * MachinaBlueprint representing a Builder blueprint
 * 
 * @author Lyneira
 */
public class Blueprint extends MovableBlueprint {
    private static List<BlueprintFactory> blueprints;
    final static int mainModuleIndex;
    final static int leftModuleIndex;
    final static int rightModuleIndex;

    final static Material headMaterial = Material.IRON_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    private final static Material supplyContainerMaterial = Material.CHEST;
    final static Material rotateMaterial = Material.STICK;

    final static int leverIndex;
    final static int centralBaseIndex;
    final static int furnaceIndex;
    final static int containerIndex;
    final static int primaryHeadIndex;

    final static int leftHeadIndex;
    final static int rightHeadIndex;

    static {
        blueprints = new ArrayList<BlueprintFactory>(3);

        mainModuleIndex = blueprints.size();
        BlueprintFactory mainModule = new BlueprintFactory();
        blueprints.add(mainModule);

        leftModuleIndex = blueprints.size();
        BlueprintFactory leftModule = new BlueprintFactory();
        blueprints.add(leftModule);

        rightModuleIndex = blueprints.size();
        BlueprintFactory rightModule = new BlueprintFactory();
        blueprints.add(rightModule);

        mainModule.addKey(new BlockVector(0, 1, 0), Material.LEVER).addKey(new BlockVector(0, 0, 0), baseMaterial).addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial)
                .addKey(new BlockVector(1, 1, 0), supplyContainerMaterial).addKey(new BlockVector(1, 0, 0), headMaterial);

        leftModule.addKey(new BlockVector(1, 0, -1), headMaterial).add(new BlockVector(0, 0, -1), baseMaterial);

        rightModule.addKey(new BlockVector(1, 0, 1), headMaterial).add(new BlockVector(0, 0, 1), baseMaterial);

        // Get handles to key blocks now. This finalizes the blueprints.
        ListIterator<Integer> handles = mainModule.getHandlesFinal().listIterator();
        leverIndex = handles.next();
        centralBaseIndex = handles.next();
        furnaceIndex = handles.next();
        containerIndex = handles.next();
        primaryHeadIndex = handles.next();
        handles = leftModule.getHandlesFinal().listIterator();
        leftHeadIndex = handles.next();
        handles = rightModule.getHandlesFinal().listIterator();
        rightHeadIndex = handles.next();
    }

    public final static Blueprint instance = new Blueprint();

    private Blueprint() {
        super(blueprints);
        blueprints = null;
    }

    /**
     * Detects whether a builder is present at the given BlockLocation. Key
     * blocks defined above must be detected manually.
     */
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;

        // Check if the Builder is on solid ground.
        if (!BlockData.isSolid(anchor.getRelative(BlockFace.DOWN).getTypeId()))
            return null;

        if (anchor.checkType(baseMaterial)) {
            // Search for a furnace around the anchor.
            for (BlockRotation i : BlockRotation.values()) {
                if (anchor.getRelative(i.getYawFace()).checkType(furnaceMaterial)) {
                    BlockRotation yaw = i.getOpposite();
                    BlockLocation primaryHead = anchor.getRelative(yaw.getYawFace());
                    if (primaryHead.checkType(headMaterial) && primaryHead.getRelative(BlockFace.UP).checkType(supplyContainerMaterial)) {
                        if (!player.hasPermission("machinabuilder.activate")) {
                            player.sendMessage("You do not have permission to activate a builder.");
                            return null;
                        }

                        List<Integer> detectedModules = new ArrayList<Integer>(3);
                        detectedModules.add(mainModuleIndex);
                        // Detect optional modules here.
                        BlockLocation head = primaryHead.getRelative(yaw.getLeft().getYawFace());
                        if (head.checkType(headMaterial) && detectOther(anchor, yaw, leftModuleIndex)) {
                            detectedModules.add(leftModuleIndex);
                        }
                        head = primaryHead.getRelative(yaw.getRight().getYawFace());
                        if (head.checkType(headMaterial) && detectOther(anchor, yaw, rightModuleIndex)) {
                            detectedModules.add(rightModuleIndex);
                        }

                        Builder builder = new Builder(instance, detectedModules, yaw, player, anchor);
                        if (itemInHand != null && itemInHand.getType() == rotateMaterial) {
                            builder.doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                            builder.onDeActivate(anchor);
                            builder = null;
                        }
                        return builder;
                    }
                }
            }
        }
        return null;
    }

}
