package me.lyneira.MachinaAtmosphere;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Machina;

public class AtmosphereGenerator implements Machina {

    private final BlockRotation yaw;

    AtmosphereGenerator(BlockRotation yaw) {
        this.yaw = yaw;
    }

    @Override
    public boolean verify(BlockLocation anchor) {
        for (BlueprintBlock b : Blueprint.blocks)
            if (anchor.getRelative(b.vector(yaw)).getTypeId() != b.typeId)
                return false;

        return true;
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        final Sign sign = (Sign) anchor.getRelative(Blueprint.sign.vector(yaw)).getBlock().getState();
        final World world = anchor.getWorld();
        final Biome oldBiome = world.getBiome(anchor.x, anchor.z);
        final Biome newBiome;
        if (oldBiome == Biome.JUNGLE) {
            newBiome = Biome.EXTREME_HILLS;
        } else {
            newBiome = Biome.JUNGLE;
        }
        final int radius = 10;
        sign.setLine(0, "[Old Biome]");
        sign.setLine(1, oldBiome.toString());
        sign.setLine(2, "[New Biome]");
        sign.setLine(3, newBiome.toString());
        sign.update();
        final int X1 = anchor.x - radius;
        final int X2 = anchor.x + radius;
        final int Z1 = anchor.z - radius;
        final int Z2 = anchor.z + radius;
        for (int x = X1; x <= X2; x++) {
            for (int z = Z1; z <= Z2; z++) {
                world.setBiome(x, z, newBiome);
            }
        }
        // Notify of a chunk biome update?
        final int chunkX1 = X1 >> 4;
        final int chunkX2 = X2 >> 4;
        final int chunkZ1 = Z1 >> 4;
        final int chunkZ2 = Z2 >> 4;
        for (int x = chunkX1; x <= chunkX2; x++) {
            for (int z = chunkZ1; z <= chunkZ2; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                MachinaAtmosphere.log(chunk.toString());
                chunk.load();
            }
        }
        return null;
    }

    @Override
    public boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
        return false;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
    }

}
