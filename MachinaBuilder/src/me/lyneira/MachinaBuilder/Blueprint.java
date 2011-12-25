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
    private static BlueprintFactory blueprint = new BlueprintFactory(5);
    final static int mainModule = blueprint.newModule();
    final static int leftModule = blueprint.newModule();
    final static int rightModule = blueprint.newModule();
    final static int backendBasicModule = blueprint.newModule();
    final static int backendRoadModule = blueprint.newModule();

    final static Material headMaterial = Material.IRON_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    private final static Material furnaceMaterial = Material.FURNACE;
    private final static Material burningFurnaceMaterial = Material.BURNING_FURNACE;
    private final static Material supplyContainerMaterial = Material.CHEST;
    final static Material rotateMaterial = Material.STICK;

    final static BlueprintBlock centralBase;
    final static BlueprintBlock chest;
    final static BlueprintBlock furnaceBasic;
    final static BlueprintBlock furnaceRoad;
    final static BlueprintBlock chestRoad;
    final static BlueprintBlock primaryHead;
    final static BlueprintBlock leftHead;
    final static BlueprintBlock rightHead;

    static {

        // **** Main module ****
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, mainModule);
        // Central base, used for ground detection
        centralBase = blueprint.addKey(new BlockVector(0, 0, 0), baseMaterial, mainModule);
        chest = blueprint.add(new BlockVector(1, 1, 0), supplyContainerMaterial, mainModule);
        primaryHead = blueprint.add(new BlockVector(1, 0, 0), headMaterial, mainModule);
        
        // **** Basic backend module ****
        // Furnace has to be key because it isn't burning at detection
        furnaceBasic = blueprint.addKey(new BlockVector(-1, 0, 0), burningFurnaceMaterial, backendBasicModule);
        
        // **** Road builder backend module ****
        // Furnace has to be key because it isn't burning at detection
        furnaceRoad = blueprint.addKey(new BlockVector(-2, 0, 0), burningFurnaceMaterial, backendRoadModule);
        blueprint.add(new BlockVector(-1, 0, 0), baseMaterial, backendRoadModule);
        chestRoad = blueprint.add(new BlockVector(-1, 1, 0), supplyContainerMaterial, backendRoadModule);

        // **** Left module ****
        leftHead = blueprint.add(new BlockVector(1, 0, -1), headMaterial, leftModule);
        blueprint.add(new BlockVector(0, 0, -1), baseMaterial, leftModule);

        // **** Right module ****
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
            List<Integer> detectedModules = new ArrayList<Integer>(3);
            BlockRotation yaw = i.getOpposite();
            BlockFace furnaceFace = i.getYawFace();
            BlueprintBlock furnace;
            if (anchor.getRelative(furnaceFace).checkType(furnaceMaterial)) {
                furnace = furnaceBasic;
                detectedModules.add(backendBasicModule);
            } else if (anchor.getRelative(furnaceFace, 2).checkType(furnaceMaterial) && detectOther(anchor, yaw, backendRoadModule)) {
                furnace = furnaceRoad;
                detectedModules.add(backendRoadModule);
            } else {
                continue;
            }

            if (!detectOther(anchor, yaw, mainModule))
                continue;
            if (!player.hasPermission("machinabuilder.activate")) {
                player.sendMessage("You do not have permission to activate a builder.");
                return null;
            }

            detectedModules.add(mainModule);

            // Detect optional modules here.
            if (detectOther(anchor, yaw, leftModule)) {
                detectedModules.add(leftModule);
            }
            if (detectOther(anchor, yaw, rightModule)) {
                detectedModules.add(rightModule);
            }

            Builder builder = new Builder(this, detectedModules, yaw, player, anchor, furnace);
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
