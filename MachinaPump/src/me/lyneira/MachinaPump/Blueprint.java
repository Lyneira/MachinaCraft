package me.lyneira.MachinaPump;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.Machina;
import me.lyneira.MachinaCore.MachinaBlueprint;

/**
 * MachinaBlueprint representing a Pump blueprint
 * 
 * @author Lyneira
 */
final class Blueprint implements MachinaBlueprint {
    final static Blueprint instance = new Blueprint();

    final static Material anchorMaterial = Material.GOLD_BLOCK;

    private Blueprint() {
        // Singleton
    }

    public Machina detect(Player player, final BlockLocation anchor, final BlockFace leverFace, ItemStack itemInHand) {
        if (!anchor.checkType(anchorMaterial))
            return null;

        BlockRotation yaw = null;
        BlockFace cauldron = null;
        boolean lavaMode = false;
        for (BlockRotation i : BlockRotation.values()) {
            BlockFace face = i.getYawFace();
            BlockLocation location = anchor.getRelative(face);
            if (location.checkType(Material.FURNACE)) {
                yaw = i.getOpposite();
                ItemStack item = ((Furnace) location.getBlock().getState()).getInventory().getSmelting();
                if (item != null && item.getType() == Material.IRON_BLOCK) {
                    lavaMode = true;
                } else {
                    lavaMode = false;
                }
            } else if (location.checkType(Material.CAULDRON)) {
                cauldron = face;
            }
        }
        if (anchor.getRelative(BlockFace.UP).checkType(Material.CAULDRON)) {
            cauldron = BlockFace.UP;
        }

        if (yaw != null && cauldron != null) {
            if (!player.hasPermission("machinapump.activate")) {
                player.sendMessage("You do not have permission to activate a pump.");
                return null;
            }
            if (!Pump.canActivate(player)) {
                player.sendMessage("You cannot activate any more pumps.");
                return null;
            }
            Pump pump = new Pump(yaw, player, anchor, leverFace, cauldron, lavaMode);
            pump.increment();
            return pump;
        }
        return null;
    }
}
