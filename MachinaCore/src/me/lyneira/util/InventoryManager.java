package me.lyneira.util;


import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Class for processing of an Inventory one item at a time. Maintains a cursor
 * to the current {@link ItemStack}, starting at the first slot. The cursor can
 * be manipulated by the find function.
 * 
 * @author Lyneira
 */
public class InventoryManager {
    public final Inventory inventory;
    private int index = 0;

    public InventoryManager(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Finds the first ItemStack that satisfies the given {@link Predicate}.
     * 
     * @param predicate
     * @return True if an ItemStack was found.
     */
    public boolean find(Predicate<ItemStack> predicate) {
        ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            if (predicate.apply(contents[index])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first non-empty slot in the inventory.
     * 
     * @return True if an item was found.
     */
    public boolean findFirst() {
        ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            if (contents[index] != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds the first slot matching the given item. Amount is not checked.
     * 
     * @param material
     * @return True if an item was found.
     */
    public boolean findItemType(ItemStack item) {
        ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            ItemStack c = contents[index];
            if (ItemUtils.itemSafeEqualsTypeAndData(item, c))
                return true;
        }
        return false;
    }

    /**
     * Increments the ItemStack at the current position.
     * 
     * @param amount
     */
    public void increment() {
        ItemStack item = inventory.getItem(index);

        if (item == null)
            return;

        int newAmount = item.getAmount() + 1;
        if (newAmount > item.getMaxStackSize()) {
            return;
        }
        item.setAmount(newAmount);
        inventory.setItem(index, item);
    }

    /**
     * Subtracts the given amount from the ItemStack at the current position.
     * 
     * @param amount
     */
    public void decrement() {
        ItemStack item = inventory.getItem(index);
        if (item == null)
            return;

        int newAmount = item.getAmount() - 1;
        if (newAmount < 1) {
            inventory.clear(index);
        } else {
            item.setAmount(newAmount);
            inventory.setItem(index, item);
        }
    }

    /**
     * Gets a copy of the ItemStack at the current position.
     * 
     * @return
     */
    public ItemStack get() {
        return inventory.getItem(index).clone();
    }

    /**
     * Sets the current position to a copy of the given ItemStack.
     * 
     * @param item
     */
    public void set(ItemStack item) {
        inventory.setItem(index, item);
    }

    /**
     * Clears the slot at the current position.
     */
    public void clear() {
        inventory.clear(index);
    }

    /**
     * Returns the inventory belonging only to the given block, filtering out
     * double inventories. Does not check whether the given block has an
     * inventory.
     * 
     * @return The inventory for the given block.
     */
    public static Inventory getSafeInventory(Block block) {
        Inventory inventory = ((InventoryHolder) block.getState()).getInventory();
        if (inventory instanceof DoubleChestInventory) {
            DoubleChestInventory doubleChest = (DoubleChestInventory) inventory;
            Inventory left = doubleChest.getLeftSide();
            if (((BlockState) doubleChest.getLeftSide().getHolder()).getBlock().equals(block)) {
                return left;
            } else {
                return doubleChest.getRightSide();
            }
        } else {
            return inventory;
        }
    }
}
