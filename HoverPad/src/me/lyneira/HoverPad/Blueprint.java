package me.lyneira.HoverPad;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
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
 * MachinaBlueprint representing a HoverPad blueprint
 * 
 * @author Lyneira
 * @author Nividica
 */
class Blueprint extends MovableBlueprint {
    final static Material anchorOFF = Material.REDSTONE_LAMP_OFF;
    final static Material anchorON = Material.REDSTONE_LAMP_ON;
    private static Material baseMaterial = Material.WOOD;
    private static Material fluffMaterial = Material.WOOL;

    private final int mainModule;

    // Constructor
    private Blueprint(BlueprintFactory blueprint, int mainModule) {
        super(blueprint);
        this.mainModule = mainModule;
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        // Search for a lever above the anchor
        if (leverFace != BlockFace.UP) {
            return null;
        }

        // Check for the redstone lamp
        if (anchor.getType() != anchorOFF) {
            return null;
        }

        List<Integer> detectedModules = new ArrayList<Integer>(1);
        BlockRotation yaw = BlockRotation.ROTATE_0;

        // Detect all non-key blocks
        if (detectOther(anchor, yaw, mainModule)) {
            // Found all blocks, add the module to the detected list
            detectedModules.add(mainModule);
        } else {
            // Not all blocks in the blueprint were found
            return null;
        }

        // Check the permissions
        if (!player.hasPermission("hoverpad")) {
            // Player does not have permission
            player.sendMessage("You do not have permission to activate a hoverpad.");
            return null;
        }

        // Create the HoverPad machina!
        return new HoverPad(this, detectedModules, yaw, player);
    }

    /**
     * Static blueprint constructor so that we have a {@link BlueprintFactory} to give to super()
     * @return A new Blueprint
     */
    static Blueprint blueprint() {
        // Create a blueprint with space for 1 module
        BlueprintFactory blueprint = new BlueprintFactory(1);

        // Create the module and store it's ID
        int mainModule = blueprint.newModule();

        // **** Main module ****
        // The lever is always key.
        blueprint.addKey(new BlockVector(0, 1, 0), Material.LEVER, mainModule);
        // Anchor ( Add as key so the detection will skip this )
        blueprint.addKey(new BlockVector(0, 0, 0), anchorON, mainModule);
        // Radius 1
        blueprint.add(new BlockVector(1, 0, -1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, -1), baseMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, -1), baseMaterial, mainModule);
        // Radius 2
        // Front side
        blueprint.add(new BlockVector(2, 0, -1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(2, 0, 1), fluffMaterial, mainModule);
        // Right side
        blueprint.add(new BlockVector(1, 0, 2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, 2), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-1, 0, 2), fluffMaterial, mainModule);
        // Back side
        blueprint.add(new BlockVector(-2, 0, 1), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, 0), baseMaterial, mainModule);
        blueprint.add(new BlockVector(-2, 0, -1), fluffMaterial, mainModule);
        // Left side
        blueprint.add(new BlockVector(-1, 0, -2), fluffMaterial, mainModule);
        blueprint.add(new BlockVector(0, 0, -2), baseMaterial, mainModule);
        blueprint.add(new BlockVector(1, 0, -2), fluffMaterial, mainModule);

        return new Blueprint(blueprint, mainModule);
    }

    static void loadConfiguration(ConfigurationSection configuration) {
        int inner = configuration.getInt("inner-material", baseMaterial.getId());
        int outer = configuration.getInt("outer-material", fluffMaterial.getId());
        if (BlockData.isSolid(inner))
            baseMaterial = Material.getMaterial(inner);
        if (BlockData.isSolid(outer))
            fluffMaterial = Material.getMaterial(outer);
    }
}
