package me.lyneira.MachinaPlanter.crop;

import java.util.ArrayList;
import java.util.Collection;
import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Handles potatoes for the planter.
 * 
 * @author Lyneira
 */
public class CropPotato implements CropHandler {

    private final boolean harvest;

    public CropPotato(boolean harvest) {
        this.harvest = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.POTATO;
    }

    @Override
    public Material getPlantableItem() {
        return Material.POTATO_ITEM;
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
     * Hardcoded drops, with a 2% chance for a poisonous potato.
     */
    @Override
    public Collection<ItemStack> getDrops() {
        Collection<ItemStack> drops = new ArrayList<ItemStack>(2);
        drops.add(new ItemStack(Material.POTATO_ITEM, 1 + GenericCrop.randomNextInt(4)));
        if (GenericCrop.randomNextInt(100) < 2) {
            drops.add(new ItemStack(Material.POISONOUS_POTATO));
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
