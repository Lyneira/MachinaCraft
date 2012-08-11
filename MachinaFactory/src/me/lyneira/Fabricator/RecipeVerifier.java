package me.lyneira.Fabricator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.ItemUtils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

/**
 * Class that verifies the left 3x3 slots of a chest's inventory with
 * {@link ShapelessRecipe}s or {@link ShapedRecipe}s.
 * 
 * @author Lyneira
 */
class RecipeVerifier {
    private final int rows;
    private final int columns;
    private final ItemStack[][] matrix;
    private ItemStack result;

    /**
     * Constructs a new RecipeVerifier from the given inventory. A chest
     * inventory is assumed.
     * 
     * @param inventory
     */
    RecipeVerifier(Inventory inventory) throws ComponentActivateException {
        matrix = InventoryManager.detectPattern(inventory);
        if (matrix == null)
            throw new ComponentActivateException();
        rows = matrix.length;
        columns = matrix[0].length;
    }

    /**
     * Iterates through the given Recipe iterator and finds the recipe laid out
     * on this verifier's inventory.
     * 
     * @param it
     * @return A list of ingredients needed for each combine.
     */
    Transaction find(Iterator<Recipe> it) {
        while (it.hasNext()) {
            Recipe r = it.next();

            if (r instanceof ShapelessRecipe) {
                ShapelessRecipe recipe = (ShapelessRecipe) r;
                if (verify(recipe)) {
                    result = recipe.getResult();
                    return collect(recipe);
                }
            } else if (r instanceof ShapedRecipe) {
                ShapedRecipe recipe = (ShapedRecipe) r;
                if (verify(recipe)) {
                    result = recipe.getResult();
                    return collect(recipe);
                }
            }
        }
        return null;
    }

    Transaction collect(ShapelessRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredientList();
        List<ItemStack> collected = new ArrayList<ItemStack>(ingredients.size());
        INGREDIENTS: for (ItemStack ingredient : ingredients) {
            for (ItemStack item : collected) {
                if (ItemUtils.itemEqualsTypeAndData(item, ingredient)) {
                    item.setAmount(item.getAmount() + ingredient.getAmount());
                    continue INGREDIENTS;
                }
            }
            collected.add(ingredient);
        }
        return new Transaction(collected, recipe.getResult());
    }

    Transaction collect(ShapedRecipe recipe) {
        List<ItemStack> collected = new ArrayList<ItemStack>(rows * columns);
        String[] shape = recipe.getShape();
        Map<Character, ItemStack> map = recipe.getIngredientMap();
        for (String s : shape)
            INGREDIENTS: for (int i = 0; i < s.length(); i++) {
                ItemStack ingredient = map.get(s.charAt(i));
                if (ingredient == null)
                    continue;
                for (ItemStack item : collected) {
                    if (ItemUtils.itemEqualsTypeAndData(item, ingredient)) {
                        item.setAmount(item.getAmount() + ingredient.getAmount());
                        continue INGREDIENTS;
                    }
                }
                collected.add(ingredient);
            }
        return new Transaction(collected, recipe.getResult());
    }

    /**
     * Returns true if the recipe contained in this verifier matches the given
     * {@link ShapelessRecipe}.
     * 
     * @param recipe
     * @return True if the recipe was verified.
     */
    boolean verify(ShapelessRecipe recipe) {
        List<ItemStack> ingredients = recipe.getIngredientList();
        for (int i = 0; i < rows; i++) {
            RECIPEITEM: for (int j = 0; j < columns; j++) {
                ItemStack recipeItem = matrix[i][j];
                if (recipeItem == null)
                    continue;
                for (Iterator<ItemStack> it = ingredients.iterator(); it.hasNext();) {
                    ItemStack ingredient = it.next();

                    if (ItemUtils.recipeIngredientEqualsTypeAndData(ingredient, recipeItem)) {
                        int amount = ingredient.getAmount();
                        if (amount == 1)
                            it.remove();
                        else
                            ingredient.setAmount(amount - 1);
                        continue RECIPEITEM;
                    }
                }
                // An item in the matrix is not on the ingredient list.
                return false;
            }
        }

        // If the list of ingredients was exhausted, recipe verified.
        if (ingredients.size() == 0)
            return true;

        // Unsatisfied ingredients remain.
        return false;
    }

    boolean verify(ShapedRecipe recipe) {
        String[] shape = recipe.getShape();
        if (shape.length != rows)
            return false;
        if (shape[0].length() != columns)
            return false;

        Map<Character, ItemStack> map = recipe.getIngredientMap();

        return verifyShape(map, shape, false) || verifyShape(map, shape, true);
    }

    private boolean verifyShape(Map<Character, ItemStack> map, String[] shape, boolean mirrored) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                ItemStack ingredient = map.get(shape[i].charAt(j));
                ItemStack item;
                if (mirrored)
                    item = matrix[i][(columns - 1) - j];
                else
                    item = matrix[i][j];

                if (ItemUtils.recipeIngredientSafeEqualsTypeAndData(ingredient, item))
                    continue;
                return false;
            }
        }
        return true;
    }

    ItemStack getResult() {
        return result;
    }
}
