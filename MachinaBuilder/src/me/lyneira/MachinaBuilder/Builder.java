package me.lyneira.MachinaBuilder;

import java.util.List;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;
import me.lyneira.MachinaCore.BlockVector;
import me.lyneira.MachinaCore.Fuel;
import me.lyneira.MachinaCore.HeartBeatEvent;
import me.lyneira.MachinaCore.Movable;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Builder extends Movable {
    /**
     * The number of server ticks to wait for a move action.
     */
    private static final int moveDelay = 20;

    /**
     * The number of server ticks to wait for a build action.
     */
    private static final int buildDelay = 10;

    /**
     * The maximum depth to which the Builder will drop blocks.
     */
    private static final int maxDepth = 6;

    /**
     * The amount of energy stored. This is just the number of server ticks left
     * before needing to consume new fuel.
     */
    private int currentEnergy = 0;

    /**
     * The next target location for the builder.
     */
    private BlockLocation queuedTarget = null;

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
    Builder(final Blueprint blueprint, final List<Integer> modules, final BlockRotation yaw, Player player, BlockLocation anchor) {
        super(blueprint, modules, yaw, player);

        this.player = player;
        // Set furnace to burning state.
        setFurnace(anchor, true);
    }

    /**
     * Initiates the current move or build action.
     */
    public HeartBeatEvent heartBeat(final BlockLocation anchor) {
        // Builder will not function for offline players.
        if (!player.isOnline())
            return null;

        BlockLocation target = nextBuild(anchor);
        if (target == null && queuedTarget == null) {
            BlockLocation newAnchor = doMove(anchor);
            if (newAnchor == null) {
                return null;
            }
            return new HeartBeatEvent(queueAction(newAnchor), newAnchor);
        } else if (target != null && target.equals(queuedTarget) && target.isEmpty()) {
            if (!doBuild(anchor)) {
                return null;
            }
        }
        return new HeartBeatEvent(queueAction(anchor));
    }

    /**
     * Determines the delay for the next action.
     * 
     * @param anchor
     *            The anchor of the Builder
     * @return Delay in server ticks for the next action
     */
    private int queueAction(final BlockLocation anchor) {
        queuedTarget = nextBuild(anchor);
        if (queuedTarget == null) {
            return moveDelay;
        } else {
            return buildDelay;
        }
    }

    /**
     * Drops a block below the head of the Builder.
     * 
     * @param anchor
     *            The anchor of the Builder
     * @return False if there was no energy or blocks left to complete the build
     *         action. True if successful or no location is available to build
     *         on.
     */
    private boolean doBuild(final BlockLocation anchor) {
        Block chestBlock = anchor.getRelative(Blueprint.chest.vector(yaw)).getBlock();
        Inventory inventory = ((Chest) chestBlock.getState()).getInventory();
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            int typeId;
            if (contents[i] != null && BlockData.isSolid(typeId = stack.getTypeId())) {
                // Simulate a build action
                if (!canPlace(queuedTarget, typeId, queuedTarget.getRelative(BlockFace.DOWN))) {
                    return false;
                }
                // Use energy last
                if (!useEnergy(anchor, buildDelay))
                    return false;
                int amount = stack.getAmount();
                if (amount == 1) {
                    inventory.clear(i);
                } else {
                    stack.setAmount(amount - 1);
                    inventory.setItem(i, stack);
                }
                byte data = stack.getData().getData();
                queuedTarget.getBlock().setTypeIdAndData(typeId, data, false);
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the next location the Builder should build on.
     * 
     * @param anchor
     *            The anchor of the Builder
     * @return The next location that can be built on, or null if false.
     */
    private BlockLocation nextBuild(final BlockLocation anchor) {

        BlockLocation head = anchor.getRelative(Blueprint.primaryHead.vector(yaw));

        // Check below main head
        BlockLocation result = nextHeadBuild(head);
        // If no success there, check below left head.
        if (result == null && hasModule(Blueprint.leftModuleId)) {
            head = anchor.getRelative(Blueprint.leftHead.vector(yaw));
            result = nextHeadBuild(head);
        }
        // If no success there, check below right head.
        if (result == null && hasModule(Blueprint.rightModuleId)) {
            head = anchor.getRelative(Blueprint.rightHead.vector(yaw));
            result = nextHeadBuild(head);
        }
        return result;
    }

    /**
     * Returns the next build location below a specific head.
     * 
     * @param head
     *            The head to look under.
     * @return The next location that can be built on, or null if false.
     */
    private BlockLocation nextHeadBuild(final BlockLocation head) {
        BlockVector stepDown = new BlockVector(BlockFace.DOWN);
        BlockLocation target = head.getRelative(stepDown);
        if (target.isEmpty()) {
            BlockLocation next;
            for (int depth = 0; depth < maxDepth; depth++) {
                next = target.getRelative(stepDown);
                if (next.isEmpty())
                    target = next;
                else if (BlockData.isSolid(next.getTypeId()))
                    return target;
                else
                    return null;
            }
        }
        return null;
    }

    /**
     * Moves the drill forward if there is empty space to move into, and ground
     * to stand on.
     * 
     * @param anchor
     *            The anchor of the Drill to move
     * @return The new anchor location of the Drill, or null on failure.
     */
    private BlockLocation doMove(final BlockLocation anchor) {
        // Check for ground at the new base
        BlockFace face = yaw.getYawFace();
        BlockLocation newAnchor = anchor.getRelative(face);
        BlockLocation ground = newAnchor.getRelative(Blueprint.centralBase.vector(yaw).add(BlockFace.DOWN));
        if (!BlockData.isSolid(ground.getTypeId())) {
            return null;
        }

        // Collision detection
        if (detectCollision(anchor, face)) {
            return null;
        }

        // Simulate a block place event to give protection plugins a chance to
        // stop the move
        if (!canMove(newAnchor, Blueprint.primaryHead)) {
            return null;
        }

        // Use energy
        if (!useEnergy(anchor, moveDelay)) {
            return null;
        }

        // Okay to move.
        moveByFace(anchor, face);

        return newAnchor;
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
        while (currentEnergy < energy) {
            int newFuel = Fuel.consume((Furnace) anchor.getRelative(Blueprint.furnace.vector(yaw)).getBlock().getState());
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
        Block furnace = anchor.getRelative(Blueprint.furnace.vector(yaw)).getBlock();
        Fuel.setFurnace(furnace, yaw.getOpposite().getYawFace(), burning);
    }
}
