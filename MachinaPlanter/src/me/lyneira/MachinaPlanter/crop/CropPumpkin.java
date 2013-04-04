package me.lyneira.MachinaPlanter.crop;

import java.util.Collection;

import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Handles pumpkins for the planter.
 * 
 * @author Lyneira
 */
public class CropPumpkin implements CropHandler {

    private final boolean harvest;

    public CropPumpkin(boolean harvest) {
        this.harvest = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.PUMPKIN;
    }

    @Override
    public Material getPlantableItem() {
        return Material.PUMPKIN_SEEDS;
    }
    
    @Override
    public boolean checkPlantableItemData(MaterialData data) {
        return true;
    }

    @Override
    public boolean canUseBonemealAtHarvest() {
        return false;
    }

    @Override
    public boolean canUseBonemealWhilePlanting() {
        return true;
    }

    @Override
    public boolean useBonemeal(BlockLocation crop) {
        return false;
    }

    @Override
    public boolean isRipe(BlockLocation crop) {
        return true;
    }

    @Override
    public boolean harvestAllowed() {
        return harvest;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return null;
    }

    @Override
    public boolean canPlant(BlockLocation tile) {
        return GenericCrop.isFarmland(tile);
    }

    @Override
    public void plant(BlockLocation crop, boolean usedBonemeal) {
        crop.setType(Material.PUMPKIN_STEM);
        if (usedBonemeal) {
            GenericCrop.useBonemeal(crop);
        }
    }
}
