package me.lyneira.Fabricator;

import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * Value object representing a transaction for executing a recipe.
 * 
 * @author Lyneira
 */
class Transaction {
    final List<ItemStack> ingredients;
    final ItemStack result;

    Transaction(List<ItemStack> ingredients, ItemStack result) {
        this.ingredients = ingredients;
        this.result = result;
    }
}
