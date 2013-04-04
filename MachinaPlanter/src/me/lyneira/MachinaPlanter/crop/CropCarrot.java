package me.lyneira.MachinaPlanter.crop;

import java.util.Collection;
import me.lyneira.MachinaCore.BlockLocation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

/**
 * Handles carrots for the planter.
 * 
 * @author Lyneira
 */
public class CropCarrot implements CropHandler {

    private final boolean harvest;

    public CropCarrot(boolean harvest) {
        this.harvest = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.CARROT;
    }

    @Override
    public Material getPlantableItem() {
        return Material.CARROT_ITEM;
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

    @Override
    public Collection<ItemStack> getDrops() {
        return GenericCrop.getDrops(new ItemStack(Material.CARROT_ITEM, 1 + GenericCrop.randomNextInt(4)));
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
