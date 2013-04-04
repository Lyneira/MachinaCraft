package me.lyneira.MachinaPlanter.crop;

import java.util.Collection;

import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Represents a handler for the planting and harvesting of crops.
 * 
 * @author Lyneira
 * 
 */
public interface CropHandler {
    /**
     * @return The Material (block type) which this CropHandler can harvest.
     */
    Material getHarvestableMaterial();

    /**
     * @return The Material (item type) that this CropHandler should look for in
     *         an inventory when planting.
     */
    Material getPlantableItem();

    /**
     * Returns true if the given data value matches the one required for this
     * crop. Intended for cocoa beans which are technically INK_SACs and need a
     * data value check.
     * 
     * @param data The MaterialData to compare to.
     * @return True if the data value is suitable for planting the item.
     */
    boolean checkPlantableItemData(MaterialData data);

    /**
     * Returns true if the crop's growth should be accelerated with bonemeal
     * just before harvesting.
     * 
     * @return True if the crop can grow faster with bonemeal
     */
    boolean canUseBonemealAtHarvest();

    /**
     * Returns true if the crop's growth should be accelerated with bonemeal
     * after planting.
     * 
     * @return True if the crop can grow faster with bonemeal
     */
    boolean canUseBonemealWhilePlanting();

    /**
     * Attempts to use bonemeal on the crop. Only called when either
     * canUseBonemealAtHarvest or canUseBonemealWhilePlanting returns true.
     * 
     * @param crop
     *            The block to use bonemeal on
     * @return True if a bonemeal was used up
     */
    boolean useBonemeal(BlockLocation crop);

    /**
     * Returns true if the crop is ready for harvesting. When this method is
     * called, the crop block will be of the Material returned by
     * getHarvestableMaterial().
     * 
     * @param crop
     *            The block to be harvested
     * @return True if the crop can be harvested
     */
    boolean isRipe(BlockLocation crop);

    /**
     * @return True if this crop is allowed to be harvested by the planter.
     */
    boolean harvestAllowed();

    /**
     * Returns one or more ItemStacks dropped from harvesting the crop. If
     * MachinaCore should be used to determine the drops, returns null.
     * 
     * @return A collection of item drops from harvesting this crop
     */
    Collection<ItemStack> getDrops();

    /**
     * Returns true if the crop can be planted on top of this tile.
     * 
     * @param tile
     *            The ground below the crop to be planted
     * @return True if this crop can be planted
     */
    boolean canPlant(BlockLocation tile);

    /**
     * Plants a new crop. The crop block is guaranteed to be empty.
     * 
     * @param crop
     *            The block to be planted in
     * @param usedBonemeal
     *            Whether bonemeal was used
     */
    void plant(BlockLocation crop, boolean usedBonemeal);
}
