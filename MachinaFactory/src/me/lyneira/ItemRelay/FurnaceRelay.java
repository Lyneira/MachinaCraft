package me.lyneira.ItemRelay;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaFactory.ComponentActivateException;
import me.lyneira.MachinaFactory.ComponentDetectException;
import me.lyneira.MachinaFactory.FurnaceEndpoint;
import me.lyneira.MachinaFactory.PacketHandler;
import me.lyneira.MachinaFactory.PacketListener;
import me.lyneira.MachinaFactory.PacketTypeUnsupportedException;
import me.lyneira.MachinaFactory.PipelineEndpoint;
import me.lyneira.MachinaFactory.PipelineException;

import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FurnaceRelay extends ItemRelay {

    FurnaceRelay(Blueprint blueprint, BlockRotation yaw, Player player, BlockLocation anchor) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, blueprint.blueprintFurnace, yaw, player, anchor);
        state = furnaceSendItem;
    }

    /**
     * Manually verifies the furnace relay's furnace since it can have two types.
     */
    @Override
    public boolean verify() {
        if (super.verify()) {
            switch (container().getType()) {
            case FURNACE:
            case BURNING_FURNACE:
                return true;
            default:
                return false;
            }
        }
        return false;
    }

    @Override
    protected BlockLocation container() {
        return anchor.getRelative(blueprint.furnace.vector(yaw));
    }

    protected boolean handle(Inventory inventory) {
        if (FurnaceEndpoint.handle(container(), inventory)) {
            age = 0;
            return true;
        }
        return false;
    }

    protected static final State furnaceSendItem = new State() {
        @Override
        public State run(ItemRelay relay) {
            FurnaceInventory inventory = (((Furnace) relay.container().getBlock().getState()).getInventory());
            ItemStack item = inventory.getResult();
            if (item == null)
                return this;
            ItemStack toSend = new ItemStack(item);
            toSend.setAmount(1);
            try {
                if (relay.pipeline.sendPacket(toSend)) {
                    item.setAmount(item.getAmount() - 1);
                    inventory.setResult(item);
                    relay.age = 0;
                }
            } catch (PacketTypeUnsupportedException e) {
                // Can't recover, go to receive only mode.
                relay.age = maxAge - 2;
                return receiveOnly;
            } catch (PipelineException e) {
                // Can't recover, go to receive only mode.
                relay.age = maxAge - 2;
                return receiveOnly;
            }
            return this;
        }
    };

    /**
     * Listener for item stacks.
     */
    private static final PacketListener<Inventory> inventoryListener = new PacketListener<Inventory>() {
        @Override
        public boolean handle(PipelineEndpoint endpoint, Inventory payload) {
            return ((FurnaceRelay) endpoint).handle(payload);
        }

        @Override
        public Class<Inventory> payloadType() {
            return Inventory.class;
        }
    };

    private static final PacketHandler furnaceHandler = new PacketHandler(inventoryListener);

    @Override
    public PacketHandler getHandler() {
        return furnaceHandler;
    }
}
