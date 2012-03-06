package me.lyneira.MachinaFactoryCore;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.Machina;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Blueprint for the {@link ItemSender}
 * 
 * @author Lyneira
 */
class ItemSenderBlueprint implements MachinaFactoryBlueprint {
    static final Material anchorMaterial = Material.SMOOTH_BRICK;
    final BlueprintBlock chest;
    final BlueprintBlock senderActive;
    final BlueprintBlock senderInactive;
    final ComponentBlueprint blueprint;

    ItemSenderBlueprint() {
        BlueprintBlock[] blueprintBase = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                chest = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.CHEST, true), //
        };
        BlueprintBlock[] blueprintInactive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.WOOD, false), //
                new BlueprintBlock(new BlockVector(1, 1, 0), Material.IRON_FENCE, false), //
                senderInactive = new BlueprintBlock(new BlockVector(2, 0, 0), Material.REDSTONE_TORCH_ON, false), //
        };

        BlueprintBlock[] blueprintActive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.IRON_FENCE, false), //
                new BlueprintBlock(new BlockVector(2, 0, 0), Material.WOOD, false), //
                senderActive = new BlueprintBlock(new BlockVector(3, 0, 0), Material.REDSTONE_TORCH_ON, false), //
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

        boolean active;
        try {
            active = blueprint.detectOther(anchor, yaw);
            return new ItemSender(this, yaw, player, anchor, leverFace, active);
        } catch (ComponentDetectException e) {
            return null;
        } catch (ComponentActivateException e) {
            return null;
        }
    }

    @Override
    public boolean leverActivatable() {
        return true;
    }
}
