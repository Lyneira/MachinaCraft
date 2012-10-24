package me.lyneira.DummyPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;

public class DummyEnderChestInventory extends DummyInventory {
    DummyEnderChestInventory(HumanEntity holder) {
        super(holder);
    }

    @Override
    public int getSize() {
        return 27;
    }

    @Override
    public InventoryType getType() {
        return InventoryType.ENDER_CHEST;
    }
}
