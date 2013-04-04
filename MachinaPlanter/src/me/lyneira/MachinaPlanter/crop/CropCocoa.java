package me.lyneira.MachinaPlanter.crop;

import java.util.Collection;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.TreeSpecies;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.CocoaPlant;
import org.bukkit.material.Dye;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Tree;

/**
 * Handles cocoa beans for the planter.
 * 
 * @author Lyneira
 */
public class CropCocoa implements CropHandler {

    private final boolean harvest;

    public CropCocoa(boolean harvest) {
        this.harvest = harvest;
    }

    @Override
    public Material getHarvestableMaterial() {
        return Material.COCOA;
    }

    @Override
    public Material getPlantableItem() {
        return Material.INK_SACK;
    }

    /**
     * Check if the dye is cocoa beans.
     */
    @Override
    public boolean checkPlantableItemData(MaterialData data) {
        return ((Dye) data).getColor() == DyeColor.BROWN;
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
        try {
            BlockState state = crop.getBlock().getState();
            CocoaPlant cocoaPlant = (CocoaPlant) state.getData();
            CocoaPlant.CocoaPlantSize size = cocoaPlant.getSize();
            if (cocoaPlant.getSize() == CocoaPlant.CocoaPlantSize.LARGE) {
                return false;
            }
            switch (size) {
            case SMALL:
                cocoaPlant.setSize(CocoaPlant.CocoaPlantSize.MEDIUM);
                break;
            case MEDIUM:
                cocoaPlant.setSize(CocoaPlant.CocoaPlantSize.LARGE);
                break;
            default:
            }
            state.setData(cocoaPlant);
            return state.update();
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean isRipe(BlockLocation crop) {
        try {
            CocoaPlant cocoaPlant = (CocoaPlant) crop.getBlock().getState().getData();
            return cocoaPlant.getSize() == CocoaPlant.CocoaPlantSize.LARGE;
        } catch (ClassCastException e) {
            return false;
        }
    }

    @Override
    public boolean harvestAllowed() {
        return harvest;
    }

    @Override
    public Collection<ItemStack> getDrops() {
        
        Dye dye = new Dye();
        dye.setColor(DyeColor.BROWN);
        ItemStack item = new ItemStack(Material.INK_SACK, 3, dye.getData());
        return GenericCrop.getDrops(item);
    }

    @Override
    public boolean canPlant(BlockLocation tile) {
        BlockLocation crop = tile.getRelative(BlockFace.UP);
        return findJungleLog(crop) != null;
    }

    @Override
    public void plant(BlockLocation crop, boolean usedBonemeal) {
        BlockRotation rotation = findJungleLog(crop);
        if (rotation == null)
            return;
        Block block = crop.getBlock();
        block.setType(getHarvestableMaterial());
        BlockState state = block.getState();
        state.setData(new CocoaPlant(CocoaPlant.CocoaPlantSize.SMALL, rotation.getYawFace()));
        state.update();
    }
    
    /**
     * Finds a jungle log around this crop block.
     * @param crop The block to check around.
     * @return The rotation for which a jungle log was found, null otherwise.
     */
    private BlockRotation findJungleLog(BlockLocation crop) {
        for (BlockRotation r : BlockRotation.values()) {
            Block block = crop.getRelative(r.getYawFace()).getBlock();
            if (block.getType() == Material.LOG) {
                BlockState state = block.getState();
                try {
                    Tree tree = (Tree) state.getData();
                    if (tree.getSpecies() == TreeSpecies.JUNGLE) {
                        return r;
                    }
                } catch (ClassCastException e) {
                }
            }
        }
        return null;
    }
}
