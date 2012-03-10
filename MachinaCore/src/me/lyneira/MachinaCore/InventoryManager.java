package me.lyneira.MachinaCore;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Class for processing of an Inventory. Maintains a cursor to the current
 * {@link ItemStack}, starting at the first slot. The cursor can be manipulated
 * by the find function.
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

        item.setAmount(item.getAmount() - 1);
        inventory.setItem(index, item);
    }

    /**
     * Tests whether the inventory has room for the given itemstack.
     * 
     * @param item
     * @return True if the inventory has room.
     */
    public boolean hasRoom(ItemStack item) {
        if (item == null || item.getTypeId() == 0)
            return true;
        ItemStack[] contents = inventory.getContents();

        int leftover = item.getAmount();
        int typeId = item.getTypeId();
        int durability = item.getDurability();
        for (int i = 0; i < contents.length; i++) {
            ItemStack currentItem = contents[i];
            if (currentItem == null || currentItem.getTypeId() == 0) {
                return true;
            } else if (typeId == currentItem.getTypeId() && durability == currentItem.getDurability()) {
                leftover = leftover - (currentItem.getMaxStackSize() - currentItem.getAmount());
                if (leftover <= 0) {
                    return true;
                }
            }
        }
        return false;
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
}
