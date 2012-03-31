package me.lyneira.ItemSplitter;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentBlueprint;
import me.lyneira.MachinaFactory.ComponentDetectException;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Blueprint implements MachinaBlueprint {
    private final Material anchorMaterial;
    private static final Material splitterMaterial = Material.BOOKSHELF;
    final BlueprintBlock senderLeft;
    final BlueprintBlock filterLeft;
    final BlueprintBlock senderRight;
    final BlueprintBlock filterRight;
    final ComponentBlueprint blueprint;

    public Blueprint() {
        anchorMaterial = ComponentBlueprint.coreMaterial();
        BlueprintBlock[] blueprintBase = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                filterRight = new BlueprintBlock(new BlockVector(1, 0, 1), splitterMaterial, false), //
                new BlueprintBlock(new BlockVector(1, 0, 0), splitterMaterial, true), //
                filterLeft = new BlueprintBlock(new BlockVector(1, 0, -1), splitterMaterial, false), //
        };
        BlueprintBlock[] blueprintInactive = { new BlueprintBlock(new BlockVector(2, 1, 1), Material.IRON_FENCE, false), //
                new BlueprintBlock(new BlockVector(2, 0, 1), ComponentBlueprint.pipelineMaterial(), false), //
                new BlueprintBlock(new BlockVector(2, 1, -1), Material.IRON_FENCE, false), //
                new BlueprintBlock(new BlockVector(2, 0, -1), ComponentBlueprint.pipelineMaterial(), false), //
        };

        BlueprintBlock[] blueprintActive = { new BlueprintBlock(new BlockVector(2, 0, 1), Material.IRON_FENCE, false), //
                senderRight = new BlueprintBlock(new BlockVector(3, 0, 1), ComponentBlueprint.pipelineMaterial(), false), //
                new BlueprintBlock(new BlockVector(2, 0, -1), Material.IRON_FENCE, false), //
                senderLeft = new BlueprintBlock(new BlockVector(3, 0, -1), ComponentBlueprint.pipelineMaterial(), false), //
        };
        blueprint = new ComponentBlueprint(blueprintBase, blueprintInactive, blueprintActive);
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        BlockRotation yaw = null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        for (BlockRotation i : BlockRotation.values()) {
            if (anchor.getRelative(i.getYawFace()).checkType(Material.BOOKSHELF)) {
                yaw = i;
                break;
            }
        }

        if (yaw == null)
            return null;

        if (!player.hasPermission("machinafactory.itemrelay")) {
            player.sendMessage("You do not have permission to activate an item splitter.");
            return null;
        }

        // Try to return new splitter
        try {
            return new ItemSplitter(this, anchor, yaw, player);
        } catch (ComponentActivateException e) {
        } catch (ComponentDetectException e) {
        }
        return null;
    }
}
