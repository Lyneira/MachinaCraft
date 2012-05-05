package me.lyneira.util;

import org.bukkit.Material;
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
    public final boolean find(final Predicate<ItemStack> predicate) {
        final ItemStack[] contents = inventory.getContents();
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
    public final boolean findFirst() {
        final ItemStack[] contents = inventory.getContents();
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
    public final boolean findItemType(final ItemStack item) {
        final ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            final ItemStack c = contents[index];
            if (ItemUtils.itemSafeEqualsTypeAndData(item, c))
                return true;
        }
        return false;
    }
    
    /**
     * Finds the first slot matching the given material.
     * @param material The material to look for.
     * @return True if an item was found.
     */
    public final boolean findMaterial(Material material) {
        final ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            final ItemStack c = contents[index];
            if (c == null)
                continue;
            if (c.getType() == material)
                return true;
        }
        return false;
    }
    
    /**
     * Finds the first slot matching the given type id and data. This won't find items of type 0 (air).
     * @param typeId
     * @param data
     * @return True if an item was found.
     */
    public final boolean findItemTypeAndData(final int typeId, final byte data) {
        final ItemStack[] contents = inventory.getContents();
        for (index = 0; index < contents.length; index++) {
            final ItemStack c = contents[index];
            if (c == null)
                continue;
            if (c.getTypeId() == typeId && c.getDurability() == data)
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

    // **** Static stuff ****

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

    /**
     * Detects a pattern of items placed on the left hand side of the inventory,
     * maximum size 3x3. Dispenser and chest inventories supported.
     * 
     * @param inventory
     * @return A matrix of itemstacks, null values mean an empty slot. Returns
     *         null if no pattern could be found.
     */
    public static ItemStack[][] detectPattern(Inventory inventory) {
        final int size = inventory.getSize();
        final ItemStack[][] contents;
        if (size == 9) {
            // Dispenser
            contents = new ItemStack[][] { new ItemStack[] { inventory.getItem(0), //
                    inventory.getItem(1), //
                    inventory.getItem(2), //
            }, new ItemStack[] { inventory.getItem(3), //
                    inventory.getItem(4), //
                    inventory.getItem(5), //
            }, new ItemStack[] { inventory.getItem(6), //
                    inventory.getItem(7), //
                    inventory.getItem(8), //
            } };
        } else if (size == 27 || size == 2 * 27) {
            // Chest inventory
            contents = new ItemStack[][] { new ItemStack[] { inventory.getItem(0), //
                    inventory.getItem(1), //
                    inventory.getItem(2), //
            }, new ItemStack[] { inventory.getItem(9), //
                    inventory.getItem(10), //
                    inventory.getItem(11), //
            }, new ItemStack[] { inventory.getItem(18), //
                    inventory.getItem(19), //
                    inventory.getItem(20), //
            } };
        } else {
            return null;
        }
        int iMin = 3;
        int iMax = -1;
        int jMin = 3;
        int jMax = -1;
        // Determine the size and position of the recipe here.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (contents[i][j] != null) {
                    if (i < iMin)
                        iMin = i;
                    if (i > iMax)
                        iMax = i;
                    if (j < jMin)
                        jMin = j;
                    if (j > jMax)
                        jMax = j;
                }
            }
        }
        final int rows = 1 + iMax - iMin;
        final int columns = 1 + jMax - jMin;
        if (rows < 1 || columns < 1)
            return null;

        // Create correctly sized matrix
        final ItemStack[][] matrix = new ItemStack[rows][columns];
        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                matrix[i - iMin][j - jMin] = contents[i][j];
            }
        }
        return matrix;
    }
}
