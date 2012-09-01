package me.lyneira.MachinaPlanter;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;

/**
 * MachinaBlueprint representing a Planter blueprint
 * 
 * @author Lyneira
 * 
 */
class Blueprint implements MachinaBlueprint {

    // Base materials
    final static Material anchorMaterial = Material.BRICK;
    final static Material baseMaterial = Material.GOLD_BLOCK;
    // Planter rail materials
    final static Material railTillMaterial = Material.FENCE;
    final static Material railPlantMaterial = Material.WOOD;
    final static Material railSkipMaterial = Material.GLASS;
    // Moving head materials
    final static Material planterMovingRailMaterial = Material.FENCE;
    final static Material planterHeadBlockMaterial = Material.WOOD;
    final static Material planterHeadMaterial = Material.IRON_FENCE;

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockLocation base = anchor.getRelative(BlockFace.DOWN);
        if (!base.checkType(baseMaterial))
            return null;

        BlockRotation railYaw = null;
        BlockLocation chest = null;
        BlockRotation movingRailYaw = null;
        BlockLocation furnace = null;

        // Find the direction of the rail and moving rail.
        for (BlockRotation i : BlockRotation.values()) {
            switch (base.getRelative(i.getYawFace()).getType()) {
            case FURNACE:
            case BURNING_FURNACE:
                furnace = base.getRelative(i.getYawFace());
                movingRailYaw = i.getOpposite();
                break;
            case CHEST:
                chest = base.getRelative(i.getYawFace());
                railYaw = i.getOpposite();
                break;
            default:
            }
        }

        // The directions of the two rails must be orthogonal.
        if (railYaw == null || (railYaw.getLeft() != movingRailYaw && railYaw.getRight() != movingRailYaw)) {
            return null;
        }

        Rail rail = Rail.detect(anchor.getRelative(BlockFace.UP), railYaw.getYawFace(), movingRailYaw.getYawFace());

        if (rail == null)
            return null;

        if (!player.hasPermission("machinaplanter.activate")) {
            player.sendMessage("You do not have permission to activate a planter.");
            return null;
        }
        
        if (EventSimulator.inventoryProtectedStatic(player, chest))
            return null;

        return new Planter(rail, anchor.getRelative(leverFace), base, chest, furnace, movingRailYaw.getOpposite(), player.hasPermission("machinaplanter.harvest"));
    }

}
