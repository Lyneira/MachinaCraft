package me.lyneira.DummyPlayer;

import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class DummyEntityEquipment implements EntityEquipment {
    private final DummyPlayer holder;

    public DummyEntityEquipment(DummyPlayer holder) {
        this.holder = holder;
    }

    @Override
    public void clear() {
    }

    @Override
    public ItemStack[] getArmorContents() {
        return new ItemStack[4];
    }

    @Override
    public ItemStack getBoots() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public float getBootsDropChance() {
        return 0;
    }

    @Override
    public ItemStack getChestplate() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public float getChestplateDropChance() {
        return 0;
    }

    @Override
    public ItemStack getHelmet() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public float getHelmetDropChance() {
        return 0;
    }

    @Override
    public Entity getHolder() {
        return holder;
    }

    @Override
    public ItemStack getItemInHand() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public float getItemInHandDropChance() {
        return 0;
    }

    @Override
    public ItemStack getLeggings() {
        return new ItemStack(Material.AIR);
    }

    @Override
    public float getLeggingsDropChance() {
        return 0;
    }

    @Override
    public void setArmorContents(ItemStack[] items) {
    }

    @Override
    public void setBoots(ItemStack boots) {
    }

    @Override
    public void setBootsDropChance(float chance) {
    }

    @Override
    public void setChestplate(ItemStack chestplate) {
    }

    @Override
    public void setChestplateDropChance(float chance) {
    }

    @Override
    public void setHelmet(ItemStack helmet) {
    }

    @Override
    public void setHelmetDropChance(float chance) {
    }

    @Override
    public void setItemInHand(ItemStack stack) {
    }

    @Override
    public void setItemInHandDropChance(float chance) {
    }

    @Override
    public void setLeggings(ItemStack leggings) {
    }

    @Override
    public void setLeggingsDropChance(float chance) {
    }

}
