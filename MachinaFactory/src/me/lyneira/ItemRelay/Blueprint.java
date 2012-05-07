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
    private final Material anchorMaterial;
    final BlueprintBlock sender;
    final BlueprintBlock chest;
    final BlueprintBlock dispenser;
    final BlueprintBlock furnace = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.FURNACE, true);
    final BlueprintBlock brewingStand;
    final ComponentBlueprint blueprintChest;
    final ComponentBlueprint blueprintDispenser;
    final ComponentBlueprint blueprintFurnace;
    final ComponentBlueprint blueprintBrewing;

    /**
     * The blueprints for the base, inactive and active states are specified
     * here.
     */
    public Blueprint() {
        anchorMaterial = ComponentBlueprint.coreMaterial();
        BlueprintBlock[] blueprintBaseChest = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                chest = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.CHEST, true), //
        };
        BlueprintBlock[] blueprintBaseDispenser = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                dispenser = new BlueprintBlock(new BlockVector(-1, 0, 0), Material.DISPENSER, true), //
        };
        BlueprintBlock[] blueprintBaseFurnace = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
        // We leave out the furnace from the blueprint and verify it manually.
        };
        BlueprintBlock[] blueprintBaseBrewing = { new BlueprintBlock(new BlockVector(0, 0, 0), anchorMaterial, true), //
                new BlueprintBlock(new BlockVector(-1, 0, 0), ComponentBlueprint.pipelineMaterial(), true), //
                brewingStand = new BlueprintBlock(new BlockVector(-1, 1, 0), Material.BREWING_STAND, true), //
        };
        BlueprintBlock[] blueprintInactive = { new BlueprintBlock(new BlockVector(1, 1, 0), Material.IRON_FENCE, false), //
                new BlueprintBlock(new BlockVector(1, 0, 0), ComponentBlueprint.pipelineMaterial(), false), //
        };

        BlueprintBlock[] blueprintActive = { new BlueprintBlock(new BlockVector(1, 0, 0), Material.IRON_FENCE, false), //
                sender = new BlueprintBlock(new BlockVector(2, 0, 0), ComponentBlueprint.pipelineMaterial(), false), //
        };
        blueprintChest = new ComponentBlueprint(blueprintBaseChest, blueprintInactive, blueprintActive);
        blueprintDispenser = new ComponentBlueprint(blueprintBaseDispenser, blueprintInactive, blueprintActive);
        blueprintFurnace = new ComponentBlueprint(blueprintBaseFurnace, blueprintInactive, blueprintActive);
        blueprintBrewing = new ComponentBlueprint(blueprintBaseBrewing, blueprintInactive, blueprintActive);
    }

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        BlockRotation yaw = null;

        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockLocation container = null;
        Material containerMaterial = null;
        ROTATIONS: for (BlockRotation i : BlockRotation.values()) {
            container = anchor.getRelative(i.getYawFace());

            containerMaterial = container.getType();
            switch (containerMaterial) {
            case CHEST:
            case DISPENSER:
            case FURNACE:
            case BURNING_FURNACE:
                yaw = i.getOpposite();
                break ROTATIONS;
            default:
                break;
            }
            // Brewing stand breaks the mold so detect it here and set the
            // container material properly.
            if (containerMaterial == ComponentBlueprint.pipelineMaterial() && container.getRelative(BlockFace.UP).checkType(Material.BREWING_STAND)) {
                yaw = i.getOpposite();
                container = container.getRelative(BlockFace.UP);
                containerMaterial = Material.BREWING_STAND;
                break;
            }
        }

        if (yaw == null)
            return null;

        if (!player.hasPermission("machinafactory.itemrelay")) {
            player.sendMessage("You do not have permission to activate an item relay.");
            return null;
        }

        if (EventSimulator.inventoryProtectedStatic(player, container))
            return null;

        try {
            switch (containerMaterial) {
            case CHEST:
                return new ChestRelay(this, anchor, yaw, player);
            case DISPENSER:
                return new DispenserRelay(this, anchor, yaw, player);
            case FURNACE:
            case BURNING_FURNACE:
                return new FurnaceRelay(this, anchor, yaw, player);
            case BREWING_STAND:
                return new BrewingRelay(this, anchor, yaw, player);
            default:
                break;
            }
        } catch (ComponentDetectException e) {
        } catch (ComponentActivateException e) {
        }
        return null;
    }
}
