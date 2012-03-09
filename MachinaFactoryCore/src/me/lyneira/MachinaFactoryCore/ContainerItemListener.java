package me.lyneira.MachinaFactoryCore;

import org.bukkit.inventory.ItemStack;


class ContainerItemListener implements PacketListener<ItemStack> {

    @Override
    public boolean handle(PipelineEndpoint endpoint, ItemStack payload) {
        return ((ContainerEndpoint) endpoint).handleItem(payload);
    }

    @Override
    public Class<ItemStack> payloadType() {
        return ItemStack.class;
    }
}
