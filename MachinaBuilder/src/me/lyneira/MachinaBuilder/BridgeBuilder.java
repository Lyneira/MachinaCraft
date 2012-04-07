package me.lyneira.MachinaBuilder;

import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;
import me.lyneira.MachinaCore.MovableBlueprint;
import me.lyneira.util.InventoryManager;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Builder with a movable head piece that can build bridges.
 * 
 * @author Lyneira
 */
public class BridgeBuilder extends Builder {

    private static final int headMoveDelay = 5;
    private final State retractState = new Retract();
    private final State buildState = new Build();
    private final MovableHead head;
    private final int width;
    private final int height;
    private int offset;
    private final int[][] typeMatrix;
    private final byte[][] dataMatrix;

    BridgeBuilder(Blueprint blueprint, List<Integer> modules, BlockRotation yaw, Player player, BlockLocation anchor, List<Integer> headModules, List<BlueprintBlock> heads, int offset) {
        super(blueprint, modules, yaw, player, anchor, blueprint.bridgeFurnace, blueprint.bridgeCentralBase, blueprint.bridgeHeadPrimary, blueprint.bridgeSupplyChest);
        width = heads.size();
        this.offset = offset;
        head = new MovableHead(blueprint.blueprint, headModules, yaw, player, heads);

        // Set up the build pattern.
        final Inventory inventory = InventoryManager.getSafeInventory(anchor.getRelative(blueprint.bridgePatternChest.vector(yaw)).getBlock());
        final ItemStack[][] matrix = InventoryManager.detectPattern(inventory);
        if (matrix == null) {
            height = 1;
            typeMatrix = new int[1][width];
            dataMatrix = new byte[1][width];
            for (int i = 0; i < width; i++)
                typeMatrix[0][i] = 0;
            return;
        }
        height = matrix.length;
        final int matrixWidth = matrix[0].length;

        typeMatrix = new int[height][width];
        dataMatrix = new byte[height][width];
        if (matrixWidth < width) {
            // Use the left side of the matrix for the entire width of the build
            // pattern.
            for (int j = 0; j < width; j++) { // Columns
                for (int i = 0; i < height; i++) { // Rows
                    ItemStack item = matrix[i][0];
                    if (isBuildingBlock.apply(item)) {
                        typeMatrix[i][j] = item.getTypeId();
                        dataMatrix[i][j] = (byte) item.getDurability();
                    } else {
                        typeMatrix[i][j] = 0;
                    }
                }
            }
        } else {
            // The detected matrix was large enough, fill up from left to right.
            for (int j = 0; j < width; j++) { // Columns
                for (int i = 0; i < height; i++) { // Rows
                    ItemStack item = matrix[i][j];
                    if (isBuildingBlock.apply(item)) {
                        typeMatrix[i][j] = item.getTypeId();
                        dataMatrix[i][j] = (byte) item.getDurability();
                    } else {
                        typeMatrix[i][j] = 0;
                    }
                }
            }
        }
    }

    @Override
    public boolean verify(BlockLocation anchor) {
        if (!head.verify(anchor))
            return false;
        return super.verify(anchor);
    }

    @Override
    protected void setContainers(BlockLocation anchor) {
        setChest(anchor, blueprint.bridgeSupplyChest);
        setChest(anchor, blueprint.bridgePatternChest);
    }

    @Override
    protected State getStartingState() {
        return new Extend();
    }

    /**
     * Attempts to move the head of the bridge builder up or down.
     * 
     * @param anchor
     * @param up
     *            The head will move up if this is true, down otherwise.
     * @return True if the head moved, false if there was a collision
     */
    private boolean moveHead(BlockLocation anchor, boolean up) {
        final int direction;
        final BlockFace face;
        if (up) {
            direction = -1;
            face = BlockFace.UP;
        } else {
            direction = 1;
            face = BlockFace.DOWN;
        }

        if (head.detectCollision(anchor, face))
            return false;

        head.moveByFace(anchor, face);
        offset += direction;

        return true;
    }

    private class Build implements State {
        @Override
        public State run(BlockLocation anchor) {
            return head.doBuild(anchor);
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            final int targets = head.buildTargets(anchor);
            if (targets > 0) {
                return targets * buildDelay;
            } else {
                state = retractState;
                return state.enqueue(anchor);
            }
        }
    }

    private class Extend implements State {
        @Override
        public State run(BlockLocation anchor) {
            if (!moveHead(anchor, false)) {
                return buildState;
            }

            return this;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            final int maxOffset = height - 1;
            if (offset > maxOffset) {
                state = retractState;
                return state.enqueue(anchor);
            }
            if (offset == maxOffset) {
                state = buildState;
                return state.enqueue(anchor);
            }

            return headMoveDelay;
        }
    }

    private class Retract implements State {
        @Override
        public State run(BlockLocation anchor) {

            if (!moveHead(anchor, true)) {
                return null;
            }

            return buildState;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            if (offset == 0) {
                state = moveState;
                return state.enqueue(anchor);
            }

            return headMoveDelay;
        }
    }

    // **** Movable overrides ****
    // Overriddes to make sure the head is treated as part of the base.
    @Override
    protected boolean detectCollision(BlockLocation oldAnchor, BlockFace face) {
        // Because the bridge builder will only move when the head is raised and
        // the head completely shields the rest of the builder, we only have to
        // detect collisions for the head.
        return head.detectCollision(oldAnchor, face);
    }

    @Override
    protected boolean detectCollisionRotate(final BlockLocation anchor, final BlockRotation rotateBy) {
        // Sadly because collision detection between the base and head is
        // separate, it will prevent the bridge builder from doing 180 degree
        // turns.
        if (head.detectCollisionRotate(anchor, rotateBy))
            return true;
        return super.detectCollisionRotate(anchor, rotateBy);
    }

    @Override
    protected BlockLocation moveByFace(final BlockLocation oldAnchor, final BlockFace face) {
        head.moveByFace(oldAnchor, face);
        return super.moveByFace(oldAnchor, face);
    }

    /**
     * Pre: detectCollisionRotate for the head returned false.
     */
    @Override
    protected void rotate(final BlockLocation anchor, final BlockRotation rotateBy) {
        head.rotate(anchor, rotateBy);
        super.rotate(anchor, rotateBy);
    }

    /**
     * Sub-machina used for the movable headpiece.
     */
    private class MovableHead extends Movable {
        /**
         * All the heads in this builder.
         */
        protected final List<BlueprintBlock> heads;

        protected MovableHead(MovableBlueprint blueprint, List<Integer> moduleIndices, BlockRotation yaw, Player player, List<BlueprintBlock> heads) {
            super(blueprint, moduleIndices, yaw, player);
            this.heads = heads;
        }

        protected State doBuild(BlockLocation anchor) {
            final InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(anchor.getRelative(BridgeBuilder.this.blueprint.bridgeSupplyChest.vector(yaw)).getBlock()));
            int placed = 0;
            int toPlace = 0;
            for (int i = 0; i < width; i++) {
                final int typeId = typeMatrix[offset][i];
                if (typeId == 0)
                    continue;
                final BlockLocation target = anchor.getRelative(BlockFace.DOWN, offset + 1).getRelative(heads.get(i).vector(yaw));
                if (!validBuildLocation(target))
                    continue;
                toPlace++;
                final byte data = dataMatrix[offset][i];
                if (!manager.findItemTypeAndData(typeId, data))
                    continue;
                if (!useEnergy(anchor, buildDelay))
                    return null;

                if (canPlace(target, typeId, data, target.getRelative(BlockFace.UP))) {
                    manager.decrement();
                    target.getBlock().setTypeIdAndData(typeId, data, true);
                    placed++;
                }
            }
            if (placed == toPlace) {
                return retractState;
            } else {
                return null;
            }
        }

        protected int buildTargets(BlockLocation anchor) {
            int targets = 0;
            for (int i = 0; i < width; i++) {
                if (typeMatrix[offset][i] == 0)
                    continue;
                final BlockLocation target = anchor.getRelative(BlockFace.DOWN, offset + 1).getRelative(heads.get(i).vector(yaw));
                if (validBuildLocation(target)) {
                    targets++;
                }
            }
            return targets;
        }

        @Override
        public boolean verify(BlockLocation anchor) {
            return super.verify(anchor.getRelative(BlockFace.DOWN, offset));
        }

        @Override
        protected boolean detectCollision(BlockLocation oldAnchor, BlockFace face) {
            return super.detectCollision(oldAnchor.getRelative(BlockFace.DOWN, offset), face);
        }

        @Override
        protected boolean detectCollisionRotate(final BlockLocation anchor, final BlockRotation rotateBy) {
            // Not allowed to rotate if offset is not 0
            if (offset != 0)
                return true;
            return super.detectCollisionRotate(anchor, rotateBy);
        }

        @Override
        protected BlockLocation moveByFace(final BlockLocation oldAnchor, final BlockFace face) {
            return super.moveByFace(oldAnchor.getRelative(BlockFace.DOWN, offset), face);
        }

        @Override
        protected final void rotate(final BlockLocation anchor, final BlockRotation rotateBy) {
            // Assume offset = 0
            super.rotate(anchor, rotateBy);
        }

        // **** Unused methods ****
        @Override
        public final HeartBeatEvent heartBeat(BlockLocation anchor) {
            return null;
        }

        @Override
        public final boolean onLever(BlockLocation anchor, Player player, ItemStack itemInHand) {
            return false;
        }

        @Override
        public final void onDeActivate(BlockLocation anchor) {
        }
    }

}
