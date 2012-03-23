package me.lyneira.ItemRelay;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.EventSimulator;
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
public class Blueprint implements MachinaBlueprint {
    static final Material anchorMaterial = Material.BRICK;
    final BlueprintBlock chest;
    final BlueprintBlock dispenser;
    final BlueprintBlock sender;
    final ComponentBlueprint blueprintChest;
    final ComponentBlueprint blueprintDispenser;

    /**
     * The blueprints for the base, inactive and active states are specified
     * here.
     */
    public Blueprint() {
        BlueprintBlock[] blueprintBaseChest = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                chest = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.CHEST, true), //
        };
        BlueprintBlock[] blueprintBaseDispenser = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                dispenser = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.DISPENSER, true), //
        };
        BlueprintBlock[] blueprintInactive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.WOOD, false), //
                new BlueprintBlock(new BlockVector(1, 1, 0), Material.IRON_FENCE, false), //
        };

        BlueprintBlock[] blueprintActive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.IRON_FENCE, false), //
                sender = new BlueprintBlock(new BlockVector(2, 0, 0), Material.WOOD, false), //
        };
        blueprintChest = new ComponentBlueprint(blueprintBaseChest, blueprintInactive, blueprintActive);
        blueprintDispenser = new ComponentBlueprint(blueprintBaseDispenser, blueprintInactive, blueprintActive);
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        BlockRotation yaw = null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockLocation container = null;
        Material containerMaterial = null;
        for (BlockRotation i : BlockRotation.values()) {
            container = anchor.getRelative(i.getYawFace());
            containerMaterial = container.getType();
            if (containerMaterial == Material.CHEST || containerMaterial == Material.DISPENSER) {
                yaw = i.getOpposite();
                break;
            }
        }

        if (yaw == null)
            return null;

        if (!player.hasPermission("machinafactory.itemrelay")) {
            player.sendMessage("You do not have permission to activate an item relay.");
            return null;
        }

        if (EventSimulator.inventoryProtected(player, container))
            return null;

        try {
            if (containerMaterial == Material.CHEST) {
                return new ChestRelay(this, yaw, player, anchor);
            } else if (containerMaterial == Material.DISPENSER) {
                return new DispenserRelay(this, yaw, player, anchor);
            }
        } catch (ComponentDetectException e) {
        } catch (ComponentActivateException e) {
        }
        return null;
    }
}
