package me.lyneira.HoverPad;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintFactory;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MovableBlueprint;

/**
 * MachinaBlueprint representing a HoverPad blueprint
 * 
 * @author Lyneira
 */
class Blueprint extends MovableBlueprint {
    private static BlueprintFactory blueprint = new BlueprintFactory(1);
    final static int mainModule = blueprint.newModule();
    
    final static Material anchorMaterial = Material.GOLD_BLOCK;
    private final static Material baseMaterial = Material.WOOD;
    private final static Material fluffMaterial = Material.WOOL;
    
    static {
        // **** Main module ****
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, mainModule);
        // Anchor
        blueprint.add(new BlockVector(0, 0, 0), anchorMaterial, mainModule);
        // Radius 1
        blueprint.add(new BlockVector(1, 0, -1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, 0), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, 1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, 1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 0), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, -1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, -1), fluffMaterial, mainModule);
        // Radius 2
        // Front side
        blueprint.add(new BlockVector(2, 0, -1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, 0), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, 1), fluffMaterial, mainModule);
        // Right side
        blueprint.add(new BlockVector(1, 0, 2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, 2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 2), fluffMaterial, mainModule);
        // Back side
        blueprint.add(new BlockVector(-2, 0, 1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, 0), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, -1), fluffMaterial, mainModule);
        // Left side
        blueprint.add(new BlockVector(-1, 0, -2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, -2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, -2), fluffMaterial, mainModule);
        // Radius 3
        // Front side
        blueprint.add(new BlockVector(3, 0, -1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(3, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(3, 0, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, 2), baseMaterial, mainModule);
        // Right side
        blueprint.add(new BlockVector(1, 0, 3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, 3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, 2), baseMaterial, mainModule);
        // Back side
        blueprint.add(new BlockVector(-3, 0, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-3, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-3, 0, -1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, -2), baseMaterial, mainModule);
        // Left side
        blueprint.add(new BlockVector(-1, 0, -3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, -3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, -3), baseMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, -2), baseMaterial, mainModule);
    }
    
    final static Blueprint instance = new Blueprint();

    protected Blueprint() {
        super(blueprint);
        blueprint = null;
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        if (leverFace != BlockFace.UP)
            return null;
        
        List<Integer> detectedModules = new ArrayList<Integer>(1);
        BlockRotation yaw = BlockRotation.ROTATE_0;
        if (detectOther(anchor, yaw, mainModule))
            detectedModules.add(mainModule);
        else
            return null;
        
        if (!player.hasPermission("hoverpad")) {
            player.sendMessage("You do not have permission to activate a hoverpad.");
            return null;
        }

        return new HoverPad(this, detectedModules, yaw, player);
    }
}
