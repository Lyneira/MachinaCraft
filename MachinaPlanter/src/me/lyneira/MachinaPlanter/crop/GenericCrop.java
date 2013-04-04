package me.lyneira.MachinaPlanter.crop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

import me.lyneira.MachinaCore.BlockLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

/**
 * Provides generic functionality for handling crops.
 * 
 * @author Lyneira
 * 
 */
public class GenericCrop {

    private final static Random random = new Random();
    private final static byte fullyGrown = 7;

    /**
     * Attempts to use bonemeal on the given block. Works with wheat,
     * pumpkin/melon stems, carrots and potatoes.
     * 
     * @param crop
     *            The block being fertilized
     * @return True if bonemeal was used
     */
    public static boolean useBonemeal(BlockLocation crop) {
        Block block = crop.getBlock();
        int data = block.getData();
        if (data == fullyGrown)
            return false;
        /*
         * Randomly advance 2-5 growth stages, taken from CraftBukkit's
         * BlockStem.java and BlockCrops.java
         */
        data = data + 2 + randomNextInt(4);
        if (data > fullyGrown) {
            data = fullyGrown;
        }
        block.setData((byte) data);
        return true;
    }

    /**
     * Returns true if this crop's state is ripe for harvest. Works with wheat,
     * pumpkin/melon stems, carrots and potatoes.
     * 
     * @param crop
     *            The block to check
     * @return True if the crop is ripe
     */
    public static boolean isCropRipe(BlockLocation crop) {
        byte data = crop.getBlock().getData();
        return data == fullyGrown;
    }

    /**
     * Returns true if the the tile is farmland.
     * 
     * @param tile
     *            The tile to check
     * @return True if the tile is farmland
     */
    public static boolean isFarmland(BlockLocation tile) {
        return tile.checkType(Material.SOIL);
    }

    /**
     * Returns an integer between 0 and n-1 inclusive.
     */
    public static int randomNextInt(int n) {
        return random.nextInt(n);
    }
    
    public static Collection<ItemStack> getDrops(ItemStack item) {
        Collection<ItemStack> drops = new ArrayList<ItemStack>(1);
        drops.add(item);
        return drops;
    }
}
