package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Tool;
import me.lyneira.util.InventoryManager;
import me.lyneira.util.InventoryTransaction;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * BlockDropperBuilder that also lays roads by replacing blocks under it with
 * those from its chest.
 * 
 * @author Lyneira
 */
public class RoadBuilder extends BlockDropperBuilder {
    protected final State buildState = new Build();

    RoadBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor) {
        super(blueprint, modules, yaw, player, anchor, blueprint.basicFurnaceRoad);
    }

    @Override
    protected void setContainers(BlockLocation anchor) {
        setChest(anchor, blueprint.basicChest);
        setChest(anchor, blueprint.basicChestRoad);
    }

    /**
     * In this stage, the builder replaces existing solid blocks directly below
     * its heads with blocks from the front chest. Replaced blocks are put in
     * the rear chest.
     */
    private class Road implements State {
        private final List<BlockLocation> targets = new ArrayList<BlockLocation>(3);

        @Override
        public State run(BlockLocation anchor) {
            Block inputBlock = anchor.getRelative(blueprint.basicChest.vector(yaw)).getBlock();
            Block outputBlock = anchor.getRelative(blueprint.basicChestRoad.vector(yaw)).getBlock();
            InventoryManager inputManager = new InventoryManager(InventoryManager.getSafeInventory(inputBlock));
            Inventory output = InventoryManager.getSafeInventory(outputBlock);

            Iterator<BlockLocation> targetIterator = targets.iterator();
            while (targetIterator.hasNext()) {
                BlockLocation target = targetIterator.next();
                int typeId = target.getTypeId();
                if (!BlockData.isDrillable(typeId))
                    continue;

                Collection<ItemStack> results = BlockData.breakBlock(target);

                if (!inputManager.find(isBuildingBlock))
                    return moveState;

                if (!useEnergy(anchor, BlockData.getDrillTime(typeId) + buildDelay))
                    return null;

                if (!useTool(anchor))
                    return null;

                if (!EventSimulator.blockBreak(target, player))
                    return null;

                // Initiate the transaction after useTool since the inventory
                // may change because of it.
                InventoryTransaction transaction = new InventoryTransaction(output);
                transaction.add(results);
                // Put results in the container
                if (!transaction.execute())
                    continue;

                target.setEmpty();

                ItemStack replacementItem = inputManager.get();
                typeId = replacementItem.getTypeId();
                byte data = replacementItem.getData().getData();
                if (!canPlace(target, typeId, data, target.getRelative(BlockFace.UP)))
                    return null;

                inputManager.decrement();
                target.getBlock().setTypeIdAndData(typeId, data, false);
            }
            return buildState;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            BlockVector down = new BlockVector(BlockFace.DOWN);
            int time = 0;
            Block inputBlock = anchor.getRelative(blueprint.basicChest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(inputBlock));

            if (!manager.find(isBuildingBlock)) {
                state = buildState;
                return buildState.enqueue(anchor);
            }

            targets.clear();
            for (BlueprintBlock i : heads) {
                BlockLocation target = anchor.getRelative(i.vector(yaw).add(down));
                int typeId = target.getTypeId();
                // Obsidian cannot be replaced by the builder as its head is
                // made of iron.
                if (BlockData.isDrillable(typeId) && typeId != Material.OBSIDIAN.getId() && !(BlockData.isSolid(typeId) && manager.inventory.contains(typeId))) {
                    time += BlockData.getDrillTime(typeId) + buildDelay;
                    targets.add(target);
                }
            }
            if (targets.size() == 0) {
                state = buildState;
                return buildState.enqueue(anchor);
            } else {
                return time;
            }
        }
    }

    @Override
    protected State getStartingState() {
        return new Road();
    }

    /**
     * Uses the appropriate tool for this drill type and returns true if
     * successful.
     * 
     * @param anchor
     *            The anchor of the Drill
     * @return True if a use of the pickaxe was expended.
     */
    private boolean useTool(final BlockLocation anchor) {
        if (!useTool)
            return true;
        return Tool.useInFurnace(((Furnace) anchor.getRelative(furnace.vector(yaw)).getBlock().getState()).getInventory(), ironToolType,
                ((InventoryHolder) anchor.getRelative(blueprint.basicChest.vector(yaw)).getBlock().getState()).getInventory());
    }

    private final static Predicate<ItemStack> ironToolType = new Predicate<ItemStack>() {
        @Override
        public boolean apply(ItemStack item) {
            return item != null && item.getType() == Material.IRON_PICKAXE;
        }
    };
}
