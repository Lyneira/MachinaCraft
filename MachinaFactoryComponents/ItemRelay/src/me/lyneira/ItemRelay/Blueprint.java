package me.lyneira.ItemRelay;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentBlueprint;
import me.lyneira.MachinaFactory.ComponentDetectException;

/**
 * Blueprint for the {@link ItemRelay}.
 * 
 * @author Lyneira
 */
class Blueprint implements MachinaBlueprint {
    static final Material anchorMaterial = Material.BRICK;
    final BlueprintBlock chest;
    final BlueprintBlock sender;
    final ComponentBlueprint blueprint;

    /**
     * The blueprints for the base, inactive and active states are specified
     * here.
     */
    Blueprint() {
        BlueprintBlock[] blueprintBase = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                chest = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.CHEST, true), //
        };
        BlueprintBlock[] blueprintInactive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.WOOD, false), //
                new BlueprintBlock(new BlockVector(1, 1, 0), Material.IRON_FENCE, false), //
        };

        BlueprintBlock[] blueprintActive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.IRON_FENCE, false), //
                sender = new BlueprintBlock(new BlockVector(2, 0, 0), Material.WOOD, false), //
        };
        blueprint = new ComponentBlueprint(blueprintBase, blueprintInactive, blueprintActive);
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        BlockRotation yaw = null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        for (BlockRotation i : BlockRotation.values()) {
            if (anchor.getRelative(i.getYawFace()).checkType(Material.CHEST)) {
                yaw = i.getOpposite();
                break;
            }
        }

        if (yaw == null)
            return null;

        try {
            return new ItemRelay(this, yaw, player, anchor, leverFace);
        } catch (ComponentDetectException e) {
        } catch (ComponentActivateException e) {
        }
        return null;
    }
}
