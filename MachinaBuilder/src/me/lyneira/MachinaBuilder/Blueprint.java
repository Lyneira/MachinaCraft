package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * MachinaBlueprint representing a Builder blueprint
 * 
 * @author Lyneira
 */
public class Blueprint extends MovableBlueprint {
    private static BlueprintFactory blueprint;
    final static int mainModule;
    final static int leftModule;
    final static int rightModule;

    final static Material headMaterial = Material.IRON_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    private final static Material supplyContainerMaterial = Material.CHEST;
    final static Material rotateMaterial = Material.STICK;

    final static BlueprintBlock centralBase;
    final static BlueprintBlock chest;
    final static BlueprintBlock furnace;
    final static BlueprintBlock primaryHead;
    final static BlueprintBlock leftHead;
    final static BlueprintBlock rightHead;

    static {
        blueprint = new BlueprintFactory(3);

        mainModule = blueprint.newModule();
        leftModule = blueprint.newModule();
        rightModule = blueprint.newModule();

        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, mainModule);
        // Central base, used for ground detection
        centralBase = blueprint.addKey(new BlockVector(0, 0, 0), baseMaterial, mainModule);
        // Furnace determines direction
        furnace = blueprint.addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial, mainModule);

        chest = blueprint.add(new BlockVector(1, 1, 0), supplyContainerMaterial, mainModule);
        primaryHead = blueprint.add(new BlockVector(1, 0, 0), headMaterial, mainModule);

        leftHead = blueprint.add(new BlockVector(1, 0, -1), headMaterial, leftModule);
        blueprint.add(new BlockVector(0, 0, -1), baseMaterial, leftModule);

        rightHead = blueprint.add(new BlockVector(1, 0, 1), headMaterial, rightModule);
        blueprint.add(new BlockVector(0, 0, 1), baseMaterial, rightModule);
    }

    public final static Blueprint instance = new Blueprint();

    private Blueprint() {
        super(blueprint);
        blueprint = null;
    }

    /**
     * Detects whether a builder is present at the given BlockLocation. Key
     * blocks defined above must be detected manually.
     */
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;

        if (!anchor.checkType(baseMaterial))
            return null;

        // Check if the Builder is on solid ground.
        if (!BlockData.isSolid(anchor.getRelative(BlockFace.DOWN).getTypeId()))
            return null;

        // Search for a furnace around the anchor.
        for (BlockRotation i : BlockRotation.values()) {
            if (!anchor.getRelative(i.getYawFace()).checkType(furnaceMaterial))
                continue;

            BlockRotation yaw = i.getOpposite();
            if (!detectOther(anchor, yaw, mainModule))
                continue;

            if (!player.hasPermission("machinabuilder.activate")) {
                player.sendMessage("You do not have permission to activate a builder.");
                return null;
            }

            List<Integer> detectedModules = new ArrayList<Integer>(3);
            detectedModules.add(mainModule);

            // Detect optional modules here.
            if (detectOther(anchor, yaw, leftModule)) {
                detectedModules.add(leftModule);
            }
            if (detectOther(anchor, yaw, rightModule)) {
                detectedModules.add(rightModule);
            }

            Builder builder = new Builder(this, detectedModules, yaw, player, anchor);
            if (itemInHand != null && itemInHand.getType() == rotateMaterial) {
                builder.doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                builder.onDeActivate(anchor);
                builder = null;
            }
            return builder;
        }

        return null;
    }

}
