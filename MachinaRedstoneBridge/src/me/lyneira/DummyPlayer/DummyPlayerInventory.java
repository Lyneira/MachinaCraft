package me.lyneira.DummyPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class DummyPlayerInventory extends DummyInventory implements PlayerInventory {
    DummyPlayerInventory(HumanEntity holder) {
        super(holder);
    }

    @Override
    public ItemStack[] getArmorContents() {
        return new ItemStack[4];
    }

    @Override
    public ItemStack getBoots() {
        return null;
    }

    @Override
    public ItemStack getChestplate() {
        return null;
    }

    @Override
    public int getHeldItemSlot() {
        return 0;
    }

    @Override
    public ItemStack getHelmet() {
        return null;
    }

    @Override
    public ItemStack getItemInHand() {
        return null;
    }

    @Override
    public ItemStack getLeggings() {
        return null;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
    }

    @Override
    public void setBoots(ItemStack boots) {
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
    }

    @Override
    public void setHelmet(ItemStack helmet) {
    }

    @Override
    public void setItemInHand(ItemStack stack) {
    }

    @Override
    public void setLeggings(ItemStack leggings) {
    }

    @Override
    public ItemStack[] getContents() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getSize() {
        return 36;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.PLAYER;
    }
}
