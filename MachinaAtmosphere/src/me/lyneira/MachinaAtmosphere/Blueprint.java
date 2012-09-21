package me.lyneira.MachinaAtmosphere;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.Machina;
import me.lyneira.MachinaCore.machina.MachinaBlueprint;

public class Blueprint implements MachinaBlueprint {

    final static BlueprintBlock sign;

    final static BlueprintBlock[] blocks = { new BlueprintBlock(new BlockVector(-1, 0, 0), Material.LEVER, true), //
            new BlueprintBlock(new BlockVector(0, 0, 0), Material.SMOOTH_BRICK, false), //
            new BlueprintBlock(new BlockVector(0, 1, 0), Material.GOLD_BLOCK, false), //
            new BlueprintBlock(new BlockVector(0, 2, 0), Material.DIAMOND_BLOCK, false), //
            sign = new BlueprintBlock(new BlockVector(-1, 1, 0), Material.WALL_SIGN, false), //
    };

    @Override
    public Machina detect(Player player, BlockLocation anchor, BlockFace leverFace, ItemStack itemInHand) {
        final BlockRotation yaw;
        try {
            yaw = BlockRotation.yawFromBlockFace(leverFace).getOpposite();
        } catch (Exception e) {
            // Lever was not on the side of the block, so stop here.
            return null;
        }
        for (BlueprintBlock b : blocks) {
            if (b.key || anchor.getRelative(b.vector(yaw)).getTypeId() == b.typeId)
                continue;
            return null;
        }
        return new AtmosphereGenerator(yaw);
    }

}
