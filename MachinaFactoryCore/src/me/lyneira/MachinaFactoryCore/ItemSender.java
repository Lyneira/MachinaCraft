package me.lyneira.MachinaFactoryCore;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.RedstoneTorch;

import com.google.common.base.Predicate;

/**
 * Itemsender component which will send item packets over redstone wire.
 * 
 * @author Lyneira
 */
class ItemSender extends Component {

    private static final int delay = 20;
    private final ItemSenderBlueprint blueprint;
    private Pipeline pipeline;

    ItemSender(ItemSenderBlueprint blueprint, BlockRotation yaw, Player player, BlockLocation anchor, BlockFace leverFace, boolean active) throws ComponentActivateException {
        super(blueprint.blueprint, anchor, yaw, active);
        this.blueprint = blueprint;
        BlockLocation sender = sender();
        setTorch(sender.getBlock());
        try {
            pipeline = new Pipeline(sender);
        } catch (PipelineException e) {
            pipeline = null;
        }
    }

    @Override
    public HeartBeatEvent heartBeat(BlockLocation anchor) {
        if (pipeline != null && doSend()) {
            return new HeartBeatEvent(delay);
        }
        return null;
    }

    @Override
    public void onDeActivate(BlockLocation anchor) {
        super.onDeActivate(anchor);
        setTorch(anchor.getRelative(blueprint.senderInactive.vector(yaw)).getBlock());
    }

    boolean doSend() {
        try {
            InventoryManager manager = new InventoryManager(((InventoryHolder) chest().getBlock().getState()).getInventory());
            if (manager.find(anyItem)) {
                ItemStack item = manager.get();
                item.setAmount(1);
                if (pipeline.sendPacket(item)) {
                    manager.decrement();
                    return true;
                }
            }
        } catch (PipelineException e) {

        }
        return false;
    }

    BlockLocation chest() {
        return anchor.getRelative(blueprint.chest.vector(yaw));
    }

    BlockLocation sender() {
        return anchor.getRelative(blueprint.senderActive.vector(yaw));
    }

    private void setTorch(Block torchBlock) {
        BlockState state = torchBlock.getState();
        try {
            RedstoneTorch torch = (RedstoneTorch) state.getData();
            torch.setFacingDirection(yaw.getYawFace());
            state.setData(torch);
        } catch (Exception e) {
            return;
        }
        state.update();
    }

    private static final Predicate<ItemStack> anyItem = new Predicate<ItemStack>() {

        @Override
        public boolean apply(ItemStack item) {
            return (item != null);
        }

    };
}
