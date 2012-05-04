package me.lyneira.ItemRelay;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class FurnaceRelay extends ItemRelay {
    private List<BlockLocation> extensionFurnaces = null;

    FurnaceRelay(Blueprint blueprint, BlockLocation anchor, BlockRotation yaw, Player player) throws ComponentActivateException, ComponentDetectException {
        super(blueprint, blueprint.blueprintFurnace, anchor, yaw, player);
        state = furnaceSendItem;
        // Detect additional furnaces here.
        BlockLocation container = container();
        BlockLocation left = container.getRelative(yaw.getLeft().getYawVector());
        BlockLocation right = container.getRelative(yaw.getRight().getYawVector());
        addExtensionFurnace(container.getRelative(BlockFace.UP));
        addExtensionFurnace(container.getRelative(BlockFace.DOWN));
        addExtensionFurnace(left);
        addExtensionFurnace(left.getRelative(BlockFace.UP));
        addExtensionFurnace(left.getRelative(BlockFace.DOWN));
        addExtensionFurnace(right);
        addExtensionFurnace(right.getRelative(BlockFace.UP));
        addExtensionFurnace(right.getRelative(BlockFace.DOWN));

        if (extensionFurnaces == null)
            extensionFurnaces = new ArrayList<BlockLocation>(0);
    }

    private boolean isFurnace(BlockLocation location) {
        switch (location.getType()) {
        case FURNACE:
        case BURNING_FURNACE:
            return true;
        default:
            return false;
        }
    }

    private boolean verifyExtensionFurnace(BlockLocation location) {
        return isFurnace(location) && location.getRelative(yaw.getYawFace()).checkType(Material.IRON_FENCE);
    }

    private void addExtensionFurnace(BlockLocation location) {
        if (!verifyExtensionFurnace(location))
            return;

        if (extensionFurnaces == null) {
            extensionFurnaces = new ArrayList<BlockLocation>(7);
        }
        extensionFurnaces.add(location);
    }

    /**
     * Manually verifies the furnace relay's furnace since it can have two
     * types. Also verify extension furnaces.
     */
    @Override
    public boolean verify() {
        if (!super.verify())
            return false;

        if (!isFurnace(container()))
            return false;

        for (BlockLocation i : extensionFurnaces) {
            if (!verifyExtensionFurnace(i))
                return false;
        }

        return true;
    }

    private void sendItem() throws PipelineException, PacketTypeUnsupportedException {
        if (doSend(container()))
            return;

        for (BlockLocation i : extensionFurnaces) {
            if (doSend(i))
                return;
        }
    }

    private boolean doSend(BlockLocation fromFurnace) throws PipelineException, PacketTypeUnsupportedException {
        FurnaceInventory inventory = (((Furnace) fromFurnace.getBlock().getState()).getInventory());
        ItemStack item = inventory.getResult();
        if (item == null || item.getType() == Material.AIR)
            return false;
        ItemStack toSend = new ItemStack(item);
        toSend.setAmount(1);

        if (pipeline.sendPacket(toSend)) {
            item.setAmount(item.getAmount() - 1);
            inventory.setResult(item);
            age = 0;
            return true;
        }
        return false;
    }

    @Override
    protected BlockLocation container() {
        return anchor.getRelative(blueprint.furnace.vector(yaw));
    }

    protected boolean handle(Inventory inventory) {
        Map<FurnaceLevel, List<FurnaceInventory>> furnacePriority = new EnumMap<FurnaceLevel, List<FurnaceInventory>>(FurnaceLevel.class);
        determinePriority(furnacePriority, (((Furnace) container().getBlock().getState()).getInventory()));

        for (BlockLocation l : extensionFurnaces) {
            determinePriority(furnacePriority, (((Furnace) l.getBlock().getState()).getInventory()));
        }
        
        for (FurnaceLevel level : FurnaceLevel.values()) {
            List<FurnaceInventory> list = furnacePriority.get(level);
            if (list == null)
                continue;
            for (FurnaceInventory i : list) {
                if (FurnaceEndpoint.handle(i, inventory)) {
                    age = 0;
                    return true;
                }
            }
        }
        return false;
    }

    private void determinePriority(Map<FurnaceLevel, List<FurnaceInventory>> furnacePriority, FurnaceInventory inventory) {
        final FurnaceLevel level = furnaceLevel(inventory);
        List<FurnaceInventory> list = furnacePriority.get(level);
        if (list == null) {
            list = new ArrayList<FurnaceInventory>(7);
            furnacePriority.put(level, list);
        }
        list.add(inventory);
    }

    private enum FurnaceLevel {
        EMPTY_FUEL, EMPTY_SMELTING, LOW_FUEL, LOW_SMELTING, OK
    }

    private FurnaceLevel furnaceLevel(FurnaceInventory inventory) {
        final ItemStack fuelItem = inventory.getFuel();
        if (fuelItem == null || fuelItem.getType() == Material.AIR)
            return FurnaceLevel.EMPTY_FUEL;
        final ItemStack smeltItem = inventory.getSmelting();
        if (smeltItem == null || smeltItem.getType() == Material.AIR)
            return FurnaceLevel.EMPTY_SMELTING;

        if (fuelItem.getAmount() < 2)
            return FurnaceLevel.LOW_FUEL;
        if (smeltItem.getAmount() < 2)
            return FurnaceLevel.LOW_SMELTING;

        return FurnaceLevel.OK;
    }

    protected static final State furnaceSendItem = new State() {
        @Override
        public State run(ItemRelay relay) {
            FurnaceRelay furnaceRelay = (FurnaceRelay) relay;
            try {
                furnaceRelay.sendItem();
            } catch (PacketTypeUnsupportedException e) {
                // Can't recover, go to receive only mode.
                furnaceRelay.age = maxAge - 2;
                return receiveOnly;
            } catch (PipelineException e) {
                // Can't recover, go to receive only mode.
                furnaceRelay.age = maxAge - 2;
                return receiveOnly;
            }
            return this;
        }
    };

    /**
     * Listener for inventories.
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
