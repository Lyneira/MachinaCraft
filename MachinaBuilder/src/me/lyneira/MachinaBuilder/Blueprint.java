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
import me.lyneira.MachinaCore.ModuleFactory;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * MachinaBlueprint representing a Builder blueprint
 * 
 * @author Lyneira
 */
public class Blueprint extends MovableBlueprint {
    private static BlueprintFactory blueprint;
    final static int mainModuleId;
    final static int leftModuleId;
    final static int rightModuleId;

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

        ModuleFactory mainModule = blueprint.newModule();
        mainModuleId = mainModule.id;
        
        ModuleFactory leftModule = blueprint.newModule();
        leftModuleId = leftModule.id;
        
        ModuleFactory rightModule = blueprint.newModule();
        rightModuleId = rightModule.id;

        // The lever is always key.
        mainModule.addKey(new BlockVector(0, 1, 0), Material.LEVER);
        // Central base, used for ground detection
        centralBase = mainModule.addKey(new BlockVector(0, 0, 0), baseMaterial);
        // Furnace determines direction
        furnace = mainModule.addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial);

        chest = mainModule.add(new BlockVector(1, 1, 0), supplyContainerMaterial);
        primaryHead = mainModule.add(new BlockVector(1, 0, 0), headMaterial);

        leftHead = leftModule.add(new BlockVector(1, 0, -1), headMaterial);
        leftModule.add(new BlockVector(0, 0, -1), baseMaterial);

        rightHead = rightModule.add(new BlockVector(1, 0, 1), headMaterial);
        rightModule.add(new BlockVector(0, 0, 1), baseMaterial);
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
            if (!detectOther(anchor, yaw, mainModuleId))
                continue;

            if (!player.hasPermission("machinabuilder.activate")) {
                player.sendMessage("You do not have permission to activate a builder.");
                return null;
            }

            List<Integer> detectedModules = new ArrayList<Integer>(3);
            detectedModules.add(mainModuleId);

            // Detect optional modules here.
            if (detectOther(anchor, yaw, leftModuleId)) {
                detectedModules.add(leftModuleId);
            }
            if (detectOther(anchor, yaw, rightModuleId)) {
                detectedModules.add(rightModuleId);
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
