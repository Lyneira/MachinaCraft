package me.lyneira.Fabricator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import me.lyneira.MachinaFactory.ComponentActivateException;

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
        // Get the entire contents of the left 3x3 slots from the chest
        // inventory.
        ItemStack[][] chestRecipeContents = new ItemStack[][] { new ItemStack[] { inventory.getItem(0), //
                inventory.getItem(1), //
                inventory.getItem(2), //
        }, new ItemStack[] { inventory.getItem(9), //
                inventory.getItem(10), //
                inventory.getItem(11), //
        }, new ItemStack[] { inventory.getItem(18), //
                inventory.getItem(19), //
                inventory.getItem(20), //
        } };
        int iMin = 3;
        int iMax = -1;
        int jMin = 3;
        int jMax = -1;
        // Determine the size and position of the recipe here.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (chestRecipeContents[i][j] != null) {
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
        rows = 1 + iMax - iMin;
        columns = 1 + jMax - jMin;
        if (rows < 1 || columns < 1)
            throw new ComponentActivateException();

        // Create correctly sized matrix
        matrix = new ItemStack[rows][columns];
        for (int i = iMin; i <= iMax; i++) {
            for (int j = jMin; j <= jMax; j++) {
                matrix[i - iMin][j - jMin] = chestRecipeContents[i][j];
            }
        }
    }

    /**
     * Iterates through the given Recipe iterator and finds the recipe laid out on this verifier's inventory. 
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
                if (itemCompareCollect(item, ingredient)) {
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
                    if (itemCompareCollect(item, ingredient)) {
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

                    if (itemCompareShapeless(recipeItem, ingredient)) {
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
        if (shape.length != columns)
            return false;
        if (shape[0].length() != rows)
            return false;

        // Shape string arrays are inconsistent with the positioning of the
        // characters within them, so they are serialized here.
        String shapeSerialized = shape[0];
        Map<Character, ItemStack> map = recipe.getIngredientMap();
        for (int i = 1; i < columns; i++) {
            shapeSerialized = shapeSerialized.concat(shape[i]);
        }

        if (verifyShape(map, shapeSerialized, false) || verifyShape(map, shapeSerialized, true))
            return true;
        return false;
    }

    private boolean verifyShape(Map<Character, ItemStack> map, String shapeSerialized, boolean mirrored) {
        int index = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                char c = shapeSerialized.charAt(index++);
                ItemStack ingredient = map.get(c);
                ItemStack item;
                if (mirrored)
                    item = matrix[i][(columns - 1) - j];
                else
                    item = matrix[i][j];

                if (itemCompareShaped(item, ingredient))
                    continue;
                return false;
            }
        }
        return true;
    }
    
    ItemStack getResult() {
        return result;
    }

    /**
     * Compares an itemstack to a recipe ingredient and returns true if they
     * match. A data value of -1 in the ingredient allows any data value to
     * match.
     * 
     * @param item
     * @param ingredient
     * @return True if item matches ingredient.
     */
    private final static boolean itemCompareShaped(ItemStack item, ItemStack ingredient) {
        if (item == null) {
            if (ingredient == null) {
                return true;
            }
            return false;
        } else if (ingredient == null) {
            return false;
        }

        if (item.getTypeId() != ingredient.getTypeId())
            return false;

        short ingredientData = ingredient.getDurability();
        if (ingredientData == -1 || ingredientData == item.getDurability()) {
            return true;
        }
        return false;
    }

    /**
     * Compares an itemstack to a recipe ingredient and returns true if they
     * match. A data value of -1 in the ingredient allows any data value to
     * match. No nullchecks.
     * 
     * @param item
     * @param ingredient
     * @return True if item matches ingredient.
     */
    private final static boolean itemCompareShapeless(ItemStack item, ItemStack ingredient) {
        if (item.getTypeId() != ingredient.getTypeId())
            return false;

        short ingredientData = ingredient.getDurability();
        if (ingredientData == -1 || ingredientData == item.getDurability()) {
            return true;
        }
        return false;
    }

    /**
     * Compares an itemstack to a recipe ingredient and returns true if they
     * match. Matches on exact data values. No nullchecks.
     * 
     * @param item
     * @param ingredient
     * @return True if item matches ingredient.
     */
    private final static boolean itemCompareCollect(ItemStack item, ItemStack ingredient) {
        if (item.getTypeId() != ingredient.getTypeId())
            return false;

        if (ingredient.getDurability() == item.getDurability()) {
            return true;
        }
        return false;
    }
}
