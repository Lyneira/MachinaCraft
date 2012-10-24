package me.lyneira.DummyPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public abstract class DummyInventory implements Inventory {

    private final HumanEntity holder;

    DummyInventory(HumanEntity holder) {
        this.holder = holder;
    }

    @Override
    public HashMap<Integer, ItemStack> addItem(ItemStack... items) {
        return new HashMap<Integer, ItemStack>(0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(int materialId) {
        return new HashMap<Integer, ItemStack>(0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(Material material) {
        return new HashMap<Integer, ItemStack>(0);
    }

    @Override
    public HashMap<Integer, ? extends ItemStack> all(ItemStack item) {
        return new HashMap<Integer, ItemStack>(0);
    }

    @Override
    public void clear() {
    }

    @Override
    public void clear(int index) {
    }

    @Override
    public boolean contains(int materialId) {
        return false;
    }

    @Override
    public boolean contains(Material material) {
        return false;
    }

    @Override
    public boolean contains(ItemStack item) {
        return false;
    }

    @Override
    public boolean contains(int materialId, int amount) {
        return false;
    }

    @Override
    public boolean contains(Material material, int amount) {
        return false;
    }

    @Override
    public boolean contains(ItemStack item, int amount) {
        return false;
    }

    @Override
    public int first(int materialId) {
        return -1;
    }

    @Override
    public int first(Material material) {
        return -1;
    }

    @Override
    public int first(ItemStack item) {
        return -1;
    }

    @Override
    public int firstEmpty() {
        return 0;
    }

    @Override
    public ItemStack[] getContents() {
        return new ItemStack[getSize()];
    }

    @Override
    public HumanEntity getHolder() {
        return holder;
    }

    @Override
    public ItemStack getItem(int index) {
        return null;
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public String getName() {
        return "DummyInventory";
    }

    @Override
    public abstract int getSize();

    @Override
    public String getTitle() {
        return "DummyInventory";
    }

    @Override
    public abstract InventoryType getType();

    @Override
    public List<HumanEntity> getViewers() {
        List<HumanEntity> result = new ArrayList<HumanEntity>(1);
        result.add(holder);
        return result;
    }

    @Override
    public ListIterator<ItemStack> iterator() {
        return new ArrayList<ItemStack>(0).listIterator();
    }

    @Override
    public ListIterator<ItemStack> iterator(int index) {
        return new ArrayList<ItemStack>(0).listIterator();
    }

    @Override
    public void remove(int materialId) {
    }

    @Override
    public void remove(Material material) {
    }

    @Override
    public void remove(ItemStack item) {
    }

    @Override
    public HashMap<Integer, ItemStack> removeItem(ItemStack... items) {
        return new HashMap<Integer, ItemStack>(0);
    }

    @Override
    public void setContents(ItemStack[] items) {
    }

    @Override
    public void setItem(int index, ItemStack item) {
    }

    @Override
    public void setMaxStackSize(int size) {
    }

}
