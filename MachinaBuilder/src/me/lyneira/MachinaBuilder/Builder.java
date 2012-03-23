package me.lyneira.MachinaBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.BlueprintBlock;
import me.lyneira.MachinaCore.EventSimulator;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.InventoryManager;
import me.lyneira.MachinaCore.InventoryTransaction;
import me.lyneira.MachinaCore.Movable;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.google.common.base.Predicate;

/**
 * Builder operation class
 * 
 * @author Lyneira
 */
public class Builder extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static int moveDelay = 20;

    /**
     * The number of server ticks to wait for a build action.
     */
    private static int buildDelay = 10;

    /**
     * The maximum depth to which the Builder will drop blocks.
     */
    private static int maxDepth = 6;

    /**
     * Whether the builder should use energy.
     */
    private static boolean useEnergy = true;

    /**
     * The amount of energy stored. This is just the number of server ticks left
     * before needing to consume new fuel.
     */
    private int currentEnergy = 0;

    /**
     * The drill's new location if it has moved.
     */
    private BlockLocation newAnchor;

    /**
     * All the heads in this builder.
     */
    private final List<BlueprintBlock> heads = new ArrayList<BlueprintBlock>(3);

    /**
     * {@link BlueprintBlock} pointing to this builder's furnace.
     */
    private final BlueprintBlock furnace;

    private Stage stage = null;
    private final Stage firstStage;
    private final Stage buildStage = new Build();
    private final Stage moveStage = new Move();
    private final Stage roadStage = new Road();

    /**
     * Creates a new drill.
     * 
     * @param plugin
     *            The MachinaCore plugin
     * @param anchor
     *            The anchor location of the drill
     * @param yaw
     *            The direction of the drill
     * @param modules
     *            The active modules for the drill
     */
    Builder(final Blueprint blueprint, final List<Integer> modules, final BlockRotation yaw, Player player, BlockLocation anchor, BlueprintBlock furnace) {
        super(blueprint, modules, yaw, player);

        this.furnace = furnace;
        heads.add(Blueprint.primaryHead);
        if (hasModule(Blueprint.leftModule))
            heads.add(Blueprint.leftHead);
        if (hasModule(Blueprint.rightModule))
            heads.add(Blueprint.rightHead);
        // Set furnace to burning state.
        setFurnace(anchor, true);
        setChest(anchor, Blueprint.chest);
        if (hasModule(Blueprint.backendRoadModule)) {
            setChest(anchor, Blueprint.chestRoad);
            firstStage = roadStage;
        } else
            firstStage = buildStage;
    }

    /**
     * Initiates the current move or build action.
     */
    public HeartBeatEvent heartBeat(final BlockLocation anchor) {
        // Builder will not function for offline players.
        if (!player.isOnline())
            return null;

        newAnchor = null;
        if (stage == null) {
            stage = firstStage;
        } else {
            stage = stage.run(anchor);
        }

        if (stage == null)
            return null;

        if (newAnchor == null) {
            return new HeartBeatEvent(stage.enqueue(anchor));
        } else {
            return new HeartBeatEvent(stage.enqueue(newAnchor), newAnchor);
        }
    }

    /**
     * Rotates the builder to the new direction, if this would not cause a
     * collision.
     * 
     * @param anchor
     *            The anchor of the builder
     * @param newYaw
     *            The new direction
     */
    void doRotate(final BlockLocation anchor, final BlockRotation newYaw) {
        BlockRotation rotateBy = newYaw.subtract(yaw);
        if (rotateBy == BlockRotation.ROTATE_0) {
            return;
        }
        if (detectCollisionRotate(anchor, rotateBy)) {
            return;
        }
        rotate(anchor, rotateBy);
        // Set furnace to correct direction.
        setFurnace(anchor, true);
        setChest(anchor, Blueprint.chest);
        if (hasModule(Blueprint.backendRoadModule))
            setChest(anchor, Blueprint.chestRoad);
        stage = null;
    }

    /**
     * Uses the given amount of energy and returns true if successful.
     * 
     * @param anchor
     *            The anchor of the Builder
     * @param energy
     *            The amount of energy needed for the next action
     * @return True if enough energy could be used up
     */
    private boolean useEnergy(final BlockLocation anchor, final int energy) {
        if (!useEnergy)
            return true;

        while (currentEnergy < energy) {
            int newFuel = Fuel.consume((Furnace) anchor.getRelative(furnace.vector(yaw)).getBlock().getState());
            if (newFuel > 0) {
                currentEnergy += newFuel;
            } else {
                return false;
            }
        }
        currentEnergy -= energy;
        return true;
    }

    /**
     * Simply checks the appropriate deactivate permission to determine whether
     * the player may deactivate the Builder.
     */
    public boolean onLever(final BlockLocation anchor, Player player, ItemStack itemInHand) {
        if ((this.player == player && player.hasPermission("machinabuilder.deactivate-own")) || player.hasPermission("machinabuilder.deactivate-all")) {
            if (itemInHand != null && itemInHand.getType() == Blueprint.rotateMaterial) {
                doRotate(anchor, BlockRotation.yawFromLocation(player.getLocation()));
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Returns the burning furnace to its normal state.
     * 
     * @param anchor
     *            The anchor of the Drill being deactivated
     */
    public void onDeActivate(final BlockLocation anchor) {
        setFurnace(anchor, false);
    }

    /**
     * Sets the builder's furnace to the given state and set correct direction.
     * 
     * @param anchor
     *            The builder's anchor
     * @param burning
     *            Whether the furnace should be burning.
     */
    void setFurnace(final BlockLocation anchor, final boolean burning) {
        Block furnaceBlock = anchor.getRelative(furnace.vector(yaw)).getBlock();
        Fuel.setFurnace(furnaceBlock, yaw.getOpposite(), burning);
    }

    /**
     * Sets a chest facing backwards.
     * 
     * @param anchor
     */
    void setChest(final BlockLocation anchor, final BlueprintBlock chest) {
        Block chestBlock = anchor.getRelative(chest.vector(yaw)).getBlock();
        if (chestBlock.getType() == Material.CHEST)
            chestBlock.setData(yaw.getOpposite().getYawData());
    }

    /**
     * Represents a stage in the operation of the Builder
     */
    private interface Stage {
        Stage run(BlockLocation anchor);

        int enqueue(BlockLocation anchor);
    }

    /**
     * In this stage, the builder replaces existing solid blocks directly below
     * its heads with blocks from the front chest. Replaced blocks are put in
     * the rear chest.
     */
    private class Road implements Stage {
        private final List<BlockLocation> targets = new ArrayList<BlockLocation>(3);

        @Override
        public Stage run(BlockLocation anchor) {
            Block inputBlock = anchor.getRelative(Blueprint.chest.vector(yaw)).getBlock();
            Block outputBlock = anchor.getRelative(Blueprint.chestRoad.vector(yaw)).getBlock();
            InventoryManager inputManager = new InventoryManager(InventoryManager.getSafeInventory(inputBlock));
            Inventory output = InventoryManager.getSafeInventory(outputBlock);

            Iterator<BlockLocation> targetIterator = targets.iterator();
            while (targetIterator.hasNext()) {
                BlockLocation target = targetIterator.next();
                int typeId = target.getTypeId();
                if (!BlockData.isDrillable(typeId))
                    continue;

                InventoryTransaction transaction = new InventoryTransaction(output);
                List<ItemStack> results = BlockData.breakBlock(target);

                if (!inputManager.find(isBuildingBlock))
                    return moveStage;

                if (!useEnergy(anchor, BlockData.getDrillTime(typeId) + buildDelay))
                    return null;

                if (!EventSimulator.blockBreak(target, player, results))
                    return null;

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
            return buildStage;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            BlockVector down = new BlockVector(BlockFace.DOWN);
            int time = 0;
            Block inputBlock = anchor.getRelative(Blueprint.chest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(inputBlock));

            if (!manager.find(isBuildingBlock)) {
                stage = buildStage;
                return buildStage.enqueue(anchor);
            }

            targets.clear();
            for (BlueprintBlock i : heads) {
                BlockLocation target = anchor.getRelative(i.vector(yaw).add(down));
                int typeId = target.getTypeId();
                if (BlockData.isDrillable(typeId) && !(BlockData.isSolid(typeId) && manager.inventory.contains(typeId))) {
                    time += BlockData.getDrillTime(typeId) + buildDelay;
                    targets.add(target);
                }
            }
            if (targets.size() == 0) {
                stage = buildStage;
                return buildStage.enqueue(anchor);
            } else {
                return time;
            }
        }
    }

    /**
     * In this stage, the builder places solid blocks into buildable locations
     * below its heads, up to the maximum depth.
     */
    private class Build implements Stage {
        private final List<BlockLocation> targets = new ArrayList<BlockLocation>(3);
        private int depth;

        @Override
        public Stage run(BlockLocation anchor) {
            Block chestBlock = anchor.getRelative(Blueprint.chest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(chestBlock));

            Iterator<BlockLocation> targetIterator = targets.iterator();
            while (targetIterator.hasNext()) {
                BlockLocation target = targetIterator.next();
                if (!validBuildLocation(target))
                    continue;

                if (!manager.find(isBuildingBlock))
                    return moveStage;

                if (!useEnergy(anchor, buildDelay))
                    return null;

                ItemStack item = manager.get();
                int typeId = item.getTypeId();
                if (!canPlace(target, typeId, (byte) item.getDurability(), target.getRelative(BlockFace.DOWN)))
                    continue;

                byte data = item.getData().getData();

                manager.decrement();
                target.getBlock().setTypeIdAndData(typeId, data, false);
            }
            if (depth == 1) {
                return moveStage;
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
                stage = moveStage;
                return stage.enqueue(anchor);
            } else {
                return buildDelay * numTargets;
            }
        }
    }

    /**
     * In this stage, the builder attempts to move forward.
     */
    private class Move implements Stage {
        /**
         * Moves the drill forward if there is empty space to move into, and
         * ground to stand on.
         * 
         * @param anchor
         *            The anchor of the Drill to move
         */
        @Override
        public Stage run(BlockLocation anchor) {
            // Check if a sign pointing left or right is present and rotate.
            BlockRotation signRotation = readRotationSign(anchor);

            // Check for ground at the new base
            BlockFace face = yaw.getYawFace();
            BlockLocation movedAnchor = anchor.getRelative(face);
            BlockLocation ground = movedAnchor.getRelative(Blueprint.centralBase.vector(yaw).add(BlockFace.DOWN));
            if (!BlockData.isSolid(ground.getTypeId())) {
                return null;
            }

            // Collision detection
            if (detectCollision(anchor, face)) {
                return null;
            }

            // Simulate a block place event to give protection plugins a chance
            // to
            // stop the move
            if (!canMove(movedAnchor, Blueprint.primaryHead)) {
                return null;
            }

            // Use energy
            if (!useEnergy(anchor, moveDelay)) {
                return null;
            }

            // Okay to move.
            moveByFace(anchor, face);

            buildRail(movedAnchor);

            newAnchor = movedAnchor;
            if (signRotation != null) {
                doRotate(newAnchor, signRotation);
            }
            return firstStage;
        }

        private void buildRail(BlockLocation anchor) {
            BlockLocation target = anchor.getRelative(furnace.vector(yaw).add(yaw.getOpposite().getYawFace()));
            BlockLocation ground = target.getRelative(BlockFace.DOWN);

            if (!validBuildLocation(target))
                return;

            if (!BlockData.isSolid(ground.getTypeId()))
                return;

            Block chestBlock = anchor.getRelative(Blueprint.chest.vector(yaw)).getBlock();
            InventoryManager manager = new InventoryManager(InventoryManager.getSafeInventory(chestBlock));

            if (!manager.find(isRail))
                return;

            ItemStack railItem = manager.get();
            int typeId = railItem.getTypeId();
            if (!canPlace(target, typeId, (byte) 0, ground))
                return;

            manager.decrement();
            target.setTypeId(typeId);
        }

        private BlockRotation readRotationSign(BlockLocation anchor) {
            BlockLocation signLocation = anchor.getRelative(Blueprint.primaryHead.vector(yaw).add(yaw.getYawVector(), 2));
            if (!signLocation.checkTypes(Material.SIGN_POST, Material.SIGN))
                return null;
            String[] lines = ((Sign) signLocation.getBlock().getState()).getLines();
            for (String s : lines) {
                if (s == null)
                    continue;
                if (s.equals("->") || s.toLowerCase().equals("right"))
                    return yaw.getRight();
                if (s.equals("<-") || s.toLowerCase().equals("left"))
                    return yaw.getLeft();
            }
            return null;
        }

        @Override
        public int enqueue(BlockLocation anchor) {
            return moveDelay;
        }
    }

    // **** Static stuff ****
    /**
     * Tests whether the {@link ItemStack} is a building block.
     */
    private final static Predicate<ItemStack> isBuildingBlock = new Predicate<ItemStack>() {
        public boolean apply(ItemStack item) {
            return (item != null && BlockData.isSolid(item.getTypeId()));
        }
    };

    /**
     * Tests whether the {@link ItemStack} is a piece of rail.
     */
    private final static Predicate<ItemStack> isRail = new Predicate<ItemStack>() {
        public boolean apply(ItemStack item) {
            if (item == null)
                return false;
            Material material = item.getType();
            return (material.equals(Material.RAILS) || material.equals(Material.POWERED_RAIL) || material.equals(Material.DETECTOR_RAIL));
        }
    };

    /**
     * Returns true if the given target location is valid to build in.
     * 
     * @param target
     * @return True if this location is valid
     */
    private static boolean validBuildLocation(BlockLocation target) {
        return target.checkTypes(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LONG_GRASS, Material.SNOW);
    }

    /**
     * Returns true if the given target location is valid to place a block
     * against.
     * 
     * @param target
     * @return True if this location is valid to place against
     */
    private static boolean validPlaceAgainst(BlockLocation target) {
        return (!target.checkTypes(Material.AIR, Material.WATER, Material.STATIONARY_WATER, Material.LONG_GRASS, Material.SNOW));
    }

    /**
     * Loads the given configuration.
     * 
     * @param configuration
     */
    static void loadConfiguration(ConfigurationSection configuration) {
        moveDelay = Math.max(configuration.getInt("move-delay", moveDelay), 1);
        buildDelay = Math.max(configuration.getInt("build-delay", buildDelay), 1);
        maxDepth = Math.min(Math.max(configuration.getInt("max-depth", maxDepth), 1), 128);
        useEnergy = configuration.getBoolean("use-energy", useEnergy);
    }
}
