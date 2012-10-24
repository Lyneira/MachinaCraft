package me.lyneira.DummyPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class DummyCraftingInventory extends DummyInventory implements CraftingInventory {
    DummyCraftingInventory(HumanEntity holder) {
        super(holder);
    }

    @Override
    public ItemStack[] getMatrix() {
        return new ItemStack[4];
    }

    @Override
    public Recipe getRecipe() {
        return null;
    }

    @Override
    public ItemStack getResult() {
        return null;
    }

    @Override
    public void setMatrix(ItemStack[] arg0) {
    }

    @Override
    public void setResult(ItemStack arg0) {
    }

    @Override
    public int getSize() {
        return 5;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.CRAFTING;
    }
}
