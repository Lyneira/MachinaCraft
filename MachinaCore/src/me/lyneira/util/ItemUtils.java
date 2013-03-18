package me.lyneira.util;

import org.bukkit.inventory.ItemStack;

/**
 * Utility functions for dealing with items.
 * 
 * @author Lyneira
 */
public class ItemUtils {
    /**
     * Magic number that signifies any data value is allowed for a recipe
     * ingredient
     */
    private final static short dataWildcard = 32767;

    private ItemUtils() {
        // Can't be instantiated
    }

    /**
     * Returns true if the type and data of two item stacks match.
     * <p>
     * Not safe to call with null items.
     * </p>
     * 
     * @param item
     * @param other
     * @return True if the item stacks match, false otherwise.
     */
    public static final boolean itemEqualsTypeAndData(final ItemStack item, final ItemStack other) {
        return item.getTypeId() == other.getTypeId() && item.getDurability() == other.getDurability();
    }

    /**
     * Returns true if the type and data of two item stacks are not null and
     * match.
     * <p>
     * This function is safe to call with null items.
     * </p>
     * 
     * @param item
     * @param other
     * @return True if the item stacks are not null and match, false otherwise.
     */
    public static final boolean itemSafeEqualsTypeAndData(final ItemStack item, final ItemStack other) {
        if (item == null) {
            if (other == null) {
                return true;
            }
            return false;
        } else if (other == null) {
            return false;
        }

        return item.getTypeId() == other.getTypeId() && item.getDurability() == other.getDurability();
    }

    /**
     * Returns true if the type and data of a recipe ingredient matches the
     * given item. A data value of -1 means any data value is allowed.
     * <p>
     * Not safe to call with null items.
     * </p>
     * 
     * @param ingredient
     * @param item
     * @return True if the item stacks match, false otherwise.
     */
    public static final boolean recipeIngredientEqualsTypeAndData(final ItemStack ingredient, final ItemStack item) {
        if (ingredient.getTypeId() != item.getTypeId())
            return false;
        final int recipeDurability = ingredient.getDurability();
        return recipeDurability == dataWildcard || recipeDurability == item.getDurability();
    }

    /**
     * Returns true if the type and data of a recipe ingredient matches the
     * given item. A data value of -1 means any data value is allowed.
     * <p>
     * This function is safe to call with null items.
     * </p>
     * 
     * @param ingredient
     * @param item
     * @return True if the item stacks match, false otherwise.
     */
    public static final boolean recipeIngredientSafeEqualsTypeAndData(final ItemStack ingredient, final ItemStack item) {
        if (ingredient == null) {
            if (item == null) {
                return true;
            }
            return false;
        } else if (item == null) {
            return false;
        }

        if (ingredient.getTypeId() != item.getTypeId())
            return false;
        final int recipeDurability = ingredient.getDurability();
        return recipeDurability == dataWildcard || recipeDurability == item.getDurability();
    }
}
