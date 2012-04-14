package me.lyneira.MachinaDrill;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * MachinaBlueprint representing a Drill blueprint
 * 
 * @author Lyneira
 * @author 5phinX
 */
final class Blueprint extends MovableBlueprint {
    private static BlueprintFactory blueprint;
    static int activationDepthLimit = 0;

    final static int mainModule;
    final static int headNormal;
    final static int headFast;
    final static int verticalModule;
    final static int verticalHeadNormal;
    final static int verticalHeadFast;
    final static Map<BlockRotation, BlockVector[]> horizontalDrillPattern = new EnumMap<BlockRotation, BlockVector[]>(BlockRotation.class);
    final static BlockVector[] verticalDrillPattern;

    final static int drillPatternSize;
    private final static Material anchorMaterial = Material.GOLD_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    final static Material headMaterialNormal = Material.IRON_BLOCK;
    final static Material headMaterialFast = Material.DIAMOND_BLOCK;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    final static Material chestMaterial = Material.CHEST;
    final static Material rotateMaterial = Material.STICK;

    final static BlueprintBlock chest;
    final static BlueprintBlock centralBase;
    final static BlueprintBlock head;
    final static BlueprintBlock furnace;

    final static BlueprintBlock verticalChest;
    final static BlueprintBlock verticalHead;
    final static BlueprintBlock verticalFurnace;

    static {
        blueprint = new BlueprintFactory(6);

        mainModule = blueprint.newModule();

        // Add key blocks to the blueprint
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, mainModule);
        // Anchor
        blueprint.addKey(new BlockVector(0, 0, 0), anchorMaterial, mainModule);
        // Central base, used for ground detection
        centralBase = blueprint.addKey(new BlockVector(0, -1, 0), baseMaterial, mainModule);
        // Furnace determines direction
        furnace = blueprint.addKey(new BlockVector(-1, -1, 0), burningFurnaceMaterial, mainModule);

        // Add non-key blocks
        // Output chest
        chest = blueprint.add(new BlockVector(-1, 0, 0), chestMaterial, mainModule);

        blueprint.add(new BlockVector(0, -1, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(0, -1, -1), baseMaterial, mainModule);

        // Iron and diamond heads
        headNormal = blueprint.newModule();
        head = blueprint.add(new BlockVector(1, 0, 0), headMaterialNormal, headNormal);
        headFast = blueprint.newModule();
        blueprint.add(new BlockVector(1, 0, 0), headMaterialFast, headFast);

        // Add drill pattern data 3x3
        drillPatternSize = 9;
        BlockVector[] basePattern = new BlockVector[drillPatternSize];
        basePattern[0] = new BlockVector(2, 0, 0);
        basePattern[1] = new BlockVector(2, 1, 0);
        basePattern[2] = new BlockVector(2, 0, 1);
        basePattern[3] = new BlockVector(2, -1, 0);
        basePattern[4] = new BlockVector(2, 0, -1);
        basePattern[5] = new BlockVector(2, 1, -1);
        basePattern[6] = new BlockVector(2, 1, 1);
        basePattern[7] = new BlockVector(2, -1, 1);
        basePattern[8] = new BlockVector(2, -1, -1);
        for (BlockRotation i : BlockRotation.values()) {
            BlockVector[] rotatedPattern = new BlockVector[drillPatternSize];
            for (int j = 0; j < drillPatternSize; j++) {
                rotatedPattern[j] = basePattern[j].rotated(i);
            }
            horizontalDrillPattern.put(i, rotatedPattern);
        }

        // Vertical module section
        // Add key blocks to the blueprint
        // The lever is always key.
        verticalModule = blueprint.newModule();

        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, verticalModule);
        // Anchor
        blueprint.addKey(new BlockVector(0, 0, 0), anchorMaterial, verticalModule);
        // Furnace determines direction
        verticalFurnace = blueprint.addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial, verticalModule);

        // Add non-key blocks
        // Output chest
        verticalChest = blueprint.add(new BlockVector(1, 0, 0), chestMaterial, verticalModule);
        blueprint.add(new BlockVector(0, -1, 1), baseMaterial, verticalModule);
        blueprint.add(new BlockVector(0, -1, -1), baseMaterial, verticalModule);

        // Iron and diamond heads
        verticalHeadNormal = blueprint.newModule();
        verticalHead = blueprint.addKey(new BlockVector(0, -1, 0), headMaterialNormal, verticalHeadNormal);
        verticalHeadFast = blueprint.newModule();
        blueprint.addKey(new BlockVector(0, -1, 0), headMaterialFast, verticalHeadFast);

        verticalDrillPattern = new BlockVector[drillPatternSize];
        verticalDrillPattern[0] = new BlockVector(0, -2, 0);
        verticalDrillPattern[1] = new BlockVector(1, -2, 0);
        verticalDrillPattern[2] = new BlockVector(0, -2, 1);
        verticalDrillPattern[3] = new BlockVector(-1, -2, 0);
        verticalDrillPattern[4] = new BlockVector(0, -2, -1);
        verticalDrillPattern[5] = new BlockVector(1, -2, -1);
        verticalDrillPattern[6] = new BlockVector(1, -2, 1);
        verticalDrillPattern[7] = new BlockVector(-1, -2, 1);
        verticalDrillPattern[8] = new BlockVector(-1, -2, -1);
    }

    final static Blueprint instance = new Blueprint();

    private Blueprint() {
        super(blueprint);
        blueprint = null;
    }

    /**
     * Detects whether a drill is present at the given BlockLocation. Key blocks
     * defined above must be detected manually.
     */
    public Machina detect(Player player, final BlockLocation anchor, final BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockLocation centralBase = anchor.getRelative(BlockFace.DOWN);
        List<Integer> detectedModules = new ArrayList<Integer>(2);
        Drill drill = null;
        if (centralBase.checkType(baseMaterial)) {
            // Check if the drill is on solid ground.
            if (!BlockData.isSolid(centralBase.getRelative(BlockFace.DOWN).getTypeId()))
                return null;

            // Search for a furnace around the central base.
            for (BlockRotation i : BlockRotation.values()) {
                if (!centralBase.getRelative(i.getYawFace()).checkType(furnaceMaterial))
                    continue;

                BlockRotation yaw = i.getOpposite();
                if (!detectOther(anchor, yaw, mainModule))
                    continue;

                detectedModules.add(mainModule);
                if (detectOther(anchor, yaw, headNormal)) {
                    detectedModules.add(headNormal);
                } else if (detectOther(anchor, yaw, headFast)) {
                    detectedModules.add(headFast);
                } else {
                    return null;
                }

                if (!player.hasPermission("machinadrill.activate")) {
                    player.sendMessage("You do not have permission to activate a drill.");
                    return null;
                }

                if (!Drill.canActivate(player)) {
                    player.sendMessage("You cannot activate any more drills.");
                    return null;
                }

                if (anchor.y < activationDepthLimit) {
                    player.sendMessage("You cannot activate a drill at this depth.");
                    return null;
                }

                if (EventSimulator.inventoryProtected(yaw, player, anchor, chest, furnace))
                    return null;

                // Detection was a success.
                drill = new Drill(this, detectedModules, yaw, player, anchor, chest, head, furnace);
                if (drill != null && itemInHand != null && itemInHand.getType() == rotateMaterial) {
                    // Support for rotation on a non-activated drill
                    drill.doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                    drill.onDeActivate(anchor);
                    drill = null;
                }
                break;
            }
        } else {
            final int headModule;
            if (centralBase.checkType(headMaterialNormal)) {
                headModule = verticalHeadNormal;
            } else if (centralBase.checkType(headMaterialFast)) {
                headModule = verticalHeadFast;
            } else {
                return null;
            }
            // Search for a furnace around the anchor.
            for (BlockRotation i : BlockRotation.values()) {
                if (!anchor.getRelative(i.getYawFace()).checkType(furnaceMaterial))
                    continue;

                BlockRotation yaw = i.getOpposite();
                if (!detectOther(anchor, yaw, verticalModule))
                    continue;

                detectedModules.add(verticalModule);
                detectedModules.add(headModule);

                if (!player.hasPermission("machinadrill.activate")) {
                    player.sendMessage("You do not have permission to activate a vertical drill.");
                    return null;
                }

                if (!Drill.canActivate(player)) {
                    player.sendMessage("You cannot activate any more drills.");
                    return null;
                }

                if (EventSimulator.inventoryProtected(yaw, player, anchor, verticalChest, verticalFurnace))
                    return null;

                // Detection was a success.
                drill = new Drill(this, detectedModules, yaw, player, anchor, verticalChest, verticalHead, verticalFurnace);
                break;
            }
        }
        if (drill != null)
            drill.increment();
        return drill;

    }
}
