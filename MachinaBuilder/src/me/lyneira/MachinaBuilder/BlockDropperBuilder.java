package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.util.InventoryManager;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract builder that drops blocks under its heads.
 *  
 * @author Lyneira
 */
public abstract class BlockDropperBuilder extends Builder {
    
    /**
     * All the heads in this builder.
     */
    protected final List<BlueprintBlock> heads = new ArrayList<BlueprintBlock>(3);
    
    /**
     * The maximum depth to which the Builder will drop blocks.
     */
    protected static int maxDepth = 6;

    BlockDropperBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor, BlueprintBlock furnace) {
        super(blueprint, modules, yaw, player, anchor, furnace, blueprint.basicCentralBase, blueprint.basicHeadPrimary, blueprint.basicChest);
        heads.add(blueprint.basicHeadPrimary);
        if (hasModule(blueprint.moduleBasicLeft))
            heads.add(blueprint.basicHeadLeft);
        if (hasModule(blueprint.moduleBasicRight))
            heads.add(blueprint.basicHeadRight);
    }

    /**
     * In this stage, the builder places solid blocks into buildable locations
     * below its heads, up to the maximum depth.
     */
    protected class Build implements State {
        private final List<BlockLocation> targets = new ArrayList<BlockLocation>(3);
        private int depth;

        @Override
        public State run(BlockLocation anchor) {
            Block chestBlock = anchor.getRelative(blueprint.basicChest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(chestBlock));

            Iterator<BlockLocation> targetIterator = targets.iterator();
            while (targetIterator.hasNext()) {
                BlockLocation target = targetIterator.next();
                if (!validBuildLocation(target))
                    continue;

                if (!manager.find(isBuildingBlock))
                    return moveState;

                if (!useEnergy(anchor, buildDelay))
                    return null;

                ItemStack item = manager.get();
                int typeId = item.getTypeId();
                byte data = item.getData().getData();
                if (!canPlace(target, typeId, data, target.getRelative(BlockFace.DOWN)))
                    continue;

                manager.decrement();
                target.getBlock().setTypeIdAndData(typeId, data, true);
            }
            if (depth == 1) {
                return moveState;
            } else {
                return this;
            }
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            targets.clear();
            BlockVector down = new BlockVector(BlockFace.DOWN);
            depth = 1;

            List<BlockLocation> visible = new ArrayList<BlockLocation>(3);
            for (BlueprintBlock i : heads) {
                visible.add(anchor.getRelative(i.vector(yaw)));
            }

            // Filter the visible locations downward until only the lowest level
            // remains.
            for (int i = 1; i <= maxDepth; i++) {
                Iterator<BlockLocation> it = visible.iterator();

                if (!it.hasNext())
                    break;

                do {
                    BlockLocation target = it.next().getRelative(down, i);
                    if (validBuildLocation(target)) {
                        BlockLocation ground = target.getRelative(down);
                        // A potential target must have ground beneath it to
                        // place on.
                        if (validPlaceAgainst(ground)) {
                            if (i == depth) {
                                targets.add(target);
                            } else {
                                depth = i;
                                targets.clear();
                                targets.add(target);
                            }
                        }
                    } else {
                        it.remove();
                    }
                } while (it.hasNext());
            }

            int numTargets = targets.size();
            if (numTargets == 0) {
                state = moveState;
                return state.enqueue(anchor);
            } else {
                return buildDelay * numTargets;
            }
        }
    }
}
