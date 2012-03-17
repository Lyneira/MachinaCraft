package me.lyneira.MachinaCore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a transaction on an {@link Inventory}. The transaction can be
 * verified before it is attempted, and will either execute entirely or not at
 * all.
 * 
 * @author Lyneira
 */
public class InventoryTransaction {
    private final Inventory inventory;
    private boolean verified = false;
    private boolean verifyResult;
    private final ItemStack[] contents;
    private List<ItemStack> addItems = new ArrayList<ItemStack>(4);
    private List<ItemStack> removeItems = new ArrayList<ItemStack>(4);

    public InventoryTransaction(Inventory inventory) {
        this.inventory = inventory;
        contents = inventory.getContents();
    }

    /**
     * Adds an {@link ItemStack} to be added to the transaction
     * 
     * @param item
     */
    public void add(ItemStack item) {
        if (verified)
            return;
        if (item != null)
            addItems.add(item);
    }

    /**
     * Adds multiple {@link ItemStack}s to be added to the transaction.
     * 
     * @param items
     */
    public void add(Collection<ItemStack> items) {
        if (verified)
            return;
        if (items == null)
            return;
        for (ItemStack i : items) {
            if (i != null)
                addItems.add(i);
        }
    }

    /**
     * Adds an {@link ItemStack} to be removed to the transaction.
     * 
     * @param item
     */
    public void remove(ItemStack item) {
        if (verified)
            return;
        if (item != null)
            removeItems.add(item);
    }

    /**
     * Adds multiple {@link ItemStack}s to be removed to the transaction.
     * 
     * @param items
     */
    public void remove(Collection<ItemStack> items) {
        if (verified)
            return;
        if (items == null)
            return;
        for (ItemStack i : items) {
            if (i != null)
                removeItems.add(i);
        }
    }

    /**
     * Verifies whether the transaction can be completed and returns true if
     * successful. Verifying will make it impossible to further modify the
     * transaction.
     * 
     * @return True if the transaction can be completed.
     */
    public boolean verify() {
        if (verified)
            return verifyResult;

        // Now simulate the transaction
        verified = true;
        verifyResult = true;
        // Remove items first
        for (ItemStack item : removeItems) {
            for (int toDelete = item.getAmount(); toDelete > 0;) {
                int first = first(item);
                if (first == -1) {
                    // We don't have this item in inventory
                    return verifyResult = false;
                } else {
                    ItemStack c = contents[first];
                    int amount = c.getAmount();
                    if (amount <= toDelete) {
                        // This stack is all used up
                        toDelete -= amount;
                        contents[first] = null;
                    } else {
                        // Stack has enough for toDelete
                        c.setAmount(amount - toDelete);
                        toDelete = 0;
                    }
                }
            }
        }
        // Now add items
        for (ItemStack item : addItems) {
            int remainingAmount = item.getAmount();
            while (true) {
                int firstPartial = firstPartial(item);
                if (firstPartial == -1) {
                    // No first partial stack, find an empty slot
                    int firstEmpty = firstEmpty();

                    if (firstEmpty == -1) {
                        // No free space to add items.
                        return verifyResult = false;
                    } else {
                        // Free slot, store the itemstack.
                        ItemStack newItem = item.clone();
                        // Make sure the new item doesn't get negative
                        // durability.
                        if (newItem.getDurability() == -1)
                            newItem.setDurability((short) 0);
                        contents[firstEmpty] = newItem;
                        break;
                    }
                } else {
                    // Partial stack found
                    ItemStack partialItem = contents[firstPartial];

                    int partialAmount = partialItem.getAmount();
                    int maxAmount = partialItem.getMaxStackSize();

                    if (remainingAmount + partialAmount <= maxAmount) {
                        // Itemstack fits
                        partialItem.setAmount(remainingAmount + partialAmount);
                        break;
                    }
                    // Partial fit
                    partialItem.setAmount(maxAmount);
                    remainingAmount = partialAmount - maxAmount;
                }
            }
        }
        return verifyResult;
    }

    /**
     * Attempts to execute the transaction on the inventory, returning true if
     * successful.
     * 
     * @return True if successful, false otherwise.
     */
    public boolean execute() {
        if (!verified)
            verify();
        if (!verifyResult)
            return false;
        inventory.setContents(contents);
        return true;
    }

    /**
     * Returns the index of the first stack for this itemstack. -1 durability on
     * item means any data value is allowed.
     * 
     * @param contents
     * @param item
     * @return The first index of a partial item stack, -1 if none was found.
     */
    private final int first(ItemStack item) {
        for (int i = 0; i < contents.length; i++) {
            ItemStack c = contents[i];
            if (c != null && matchItemTypes(item, c)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first partial stack for this itemstack. -1
     * durability on item means any data value is allowed.
     * 
     * @param contents
     * @param item
     * @return The first index of a partial item stack, -1 if none was found.
     */
    private final int firstPartial(ItemStack item) {
        for (int i = 0; i < contents.length; i++) {
            ItemStack c = contents[i];
            if (c != null && matchItemTypes(item, c) && c.getAmount() < c.getMaxStackSize()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the index of the first empty slot.
     * 
     * @param contents
     * @return The first index of an empty slot, or -1 if none was found.
     */
    private final int firstEmpty() {
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] == null)
                return i;
        }
        return -1;
    }

    /**
     * Returns true if item and content match. -1 durability on item means any
     * data value is allowed.
     * 
     * @param item
     * @param content
     * @return True if the item types match.
     */
    private static final boolean matchItemTypes(ItemStack item, ItemStack content) {
        if (item.getTypeId() != content.getTypeId())
            return false;

        if (item.getDurability() == -1 || item.getDurability() == content.getDurability()) {
            return true;
        }
        return true;
    }
}
