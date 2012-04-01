package me.lyneira.DummyPlayer;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

public class DummyInventoryView extends InventoryView {

    private HumanEntity player;
    private Inventory top;
    private Inventory bottom;
    private InventoryType type;

    DummyInventoryView(HumanEntity player, Inventory top, Inventory bottom, InventoryType type) {
        this.player = player;
        this.top = top;
        this.bottom = bottom;
        this.type = type;
    }

    @Override
    public Inventory getBottomInventory() {
        return bottom;
    }

    @Override
    public HumanEntity getPlayer() {
        return player;
    }

    @Override
    public Inventory getTopInventory() {
        return top;
    }

    @Override
    public InventoryType getType() {
        return type;
    }
}
