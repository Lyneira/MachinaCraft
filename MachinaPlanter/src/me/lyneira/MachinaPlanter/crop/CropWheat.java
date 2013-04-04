package me.lyneira.MachinaPlanter.crop;

import java.util.ArrayList;
import java.util.Collection;
import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Handles wheat for the planter.
 * 
 * @author Lyneira
 */
public class CropWheat implements CropHandler {

    private final boolean harvest;
    private final boolean harvestSeeds;

    public CropWheat(boolean harvest, boolean harvestSeeds) {
        this.harvest = harvest;
        this.harvestSeeds = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.CROPS;
    }

    @Override
    public Material getPlantableItem() {
        return Material.SEEDS;
    }
    
    @Override
    public boolean checkPlantableItemData(MaterialData data) {
        return true;
    }

    @Override
    public boolean canUseBonemealAtHarvest() {
        return true;
    }

    @Override
    public boolean canUseBonemealWhilePlanting() {
        return false;
    }

    @Override
    public boolean useBonemeal(BlockLocation crop) {
        return GenericCrop.useBonemeal(crop);
    }

    @Override
    public boolean isRipe(BlockLocation crop) {
        if (crop.getType() != getHarvestableMaterial())
            return false;
        return GenericCrop.isCropRipe(crop);
    }

    @Override
    public boolean harvestAllowed() {
        return harvest;
    }

    /**
     * Hardcoded drops in order to allow seed drops to be configurable.
     */
    @Override
    public Collection<ItemStack> getDrops() {
        Collection<ItemStack> drops = new ArrayList<ItemStack>(2);
        drops.add(new ItemStack(Material.WHEAT));
        if (harvestSeeds) {
            int seedAmount = GenericCrop.randomNextInt(4);
            if (seedAmount > 0) {
                drops.add(new ItemStack(Material.SEEDS, seedAmount));
            }
        }
        return drops;
    }

    @Override
    public boolean canPlant(BlockLocation tile) {
        return GenericCrop.isFarmland(tile);
    }

    @Override
    public void plant(BlockLocation crop, boolean usedBonemeal) {
        crop.setType(getHarvestableMaterial());
    }
}
