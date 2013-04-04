package me.lyneira.MachinaPlanter.crop;

import java.util.Collection;

import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class CropNetherWart implements CropHandler {

    private final boolean harvest;
    private final static byte fullyGrown = 3;

    public CropNetherWart(boolean harvest) {
        this.harvest = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.NETHER_WARTS;
    }

    @Override
    public Material getPlantableItem() {
        return Material.NETHER_STALK;
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
        return false;
    }

    @Override
    public boolean useBonemeal(BlockLocation crop) {
        return false;
    }

    @Override
    public boolean isRipe(BlockLocation crop) {
        byte data = crop.getBlock().getData();
        return data == fullyGrown;
    }

    @Override
    public boolean harvestAllowed() {
        return harvest;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        return GenericCrop.getDrops(new ItemStack(Material.NETHER_STALK, 2 + GenericCrop.randomNextInt(4)));
    }

    @Override
    public boolean canPlant(BlockLocation tile) {
        return tile.checkType(Material.SOUL_SAND);
    }

    @Override
    public void plant(BlockLocation crop, boolean usedBonemeal) {
        crop.setType(Material.NETHER_WARTS);
    }

}
