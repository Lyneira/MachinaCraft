package me.lyneira.MachinaCore;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Abstract class for a {@link Machina} that can move.
 * 
 * @author Lyneira
 */
public abstract class Movable implements Machina {
    protected final MovableBlueprint blueprint;
    private final List<Integer> modules;
    private final int moduleCount;

    private final BlueprintBlock[] unifiedBlueprint;
    private final BlockVector[] unifiedVectors;
    private final int size;
    private final Map<BlockFace, BlueprintDifference> differences = new EnumMap<BlockFace, BlueprintDifference>(BlockFace.class);

    protected BlockRotation yaw;
    protected Player player;

    /**
     * Class that can get and put data and inventory for blocks that need to
     * have these preserved.
     */
    private final class MoveData {
        byte[] data;
        ItemStack[][] inventories;

        void get(BlockLocation anchor, int moduleIndex) {
            // * Copy data bytes for blocks that need it.
            data = blueprint.getBlockData(anchor, yaw, moduleIndex);
            // * Copy inventory contents (as array) for blocks with inventory,
            // and clear those inventories.
            inventories = blueprint.getBlockInventories(anchor, yaw, moduleIndex);
        }

        void put(BlockLocation anchor, int moduleIndex) {
            // * Set data for blocks that need it.
            blueprint.setBlockData(anchor, data, yaw, moduleIndex);
            // * Copy inventory contents into blocks that need it.
            blueprint.setBlockInventories(anchor, inventories, yaw, moduleIndex);
        }
    }

    private final MoveData[] moveData;

    protected Movable(final MovableBlueprint blueprint, final List<Integer> moduleIndices, final BlockRotation yaw, final Player player) {
        this.player = player;
        this.blueprint = blueprint;
        this.yaw = yaw;
        this.modules = moduleIndices;

        moduleCount = moduleIndices.size();
        moveData = new MoveData[moduleCount];
        for (int i = 0; i < moduleCount; i++) {
            moveData[i] = new MoveData();
        }

        unifiedBlueprint = blueprint.unifyBlueprint(moduleIndices);
        size = this.unifiedBlueprint.length;
        unifiedVectors = new BlockVector[size];
        blueprint.unifyVectors(moduleIndices, yaw, unifiedVectors);
    }

    @Override
    public boolean verify(final BlockLocation anchor) {
        for (int i = 0; i < size; i++) {
            if (anchor.getRelative(unifiedVectors[i]).getTypeId() != unifiedBlueprint[i].typeId) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the difference sets for a movement in the direction of the given
     * BlockFace.
     * 
     * @param face
     *            The direction to move in
     * @return A {@link BlueprintDifference} for this direction
     */
    private BlueprintDifference getDifference(final BlockFace face) {
        BlueprintDifference result = differences.get(face);
        if (result == null) {
            result = new BlueprintDifference(unifiedBlueprint, unifiedVectors, size, face);
            differences.put(face, result);
        }
        return result;
    }

    /**
     * Detects whether a collision would happen if this blueprint at the given
     * {@link BlockLocation} were to move a distance of 1 in the direction of
     * the given BlockFace.
     * 
     * @param oldAnchor
     *            The anchor for which to calculate. This must be the anchor
     *            before the move.
     * @param face
     *            The direction to move in.
     * @return True if a collision would happen
     */
    protected boolean detectCollision(final BlockLocation oldAnchor, final BlockFace face) {
        for (BlockVector i : getDifference(face).plus) {
            if (!oldAnchor.getRelative(i).isEmptyForCollision()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detects whether a collision would happen if this movable were to teleport
     * to the given location. This function assumes both locations are in the
     * same world.
     * 
     * @param oldAnchor
     *            {@link BlockLocation} of the old anchor.
     * 
     * @param newAnchor
     *            {@link BlockLocation} to teleport to.
     * @return True if a collision would happen
     */
    protected boolean detectCollisionTeleport(final BlockLocation oldAnchor, final BlockLocation newAnchor) {
        BlockVector vector = newAnchor.subtract(oldAnchor);
        return detectCollisionTeleport(oldAnchor, vector);
    }

    /**
     * Detects whether a collision would happen if this movable were to teleport
     * by the given vector.
     * 
     * @param oldAnchor
     *            {@link BlockLocation} of the old anchor.
     * 
     * @param teleportBy
     *            {@link BlockVector} to teleport by.
     * @return True if a collision would happen
     */
    protected boolean detectCollisionTeleport(final BlockLocation oldAnchor, final BlockVector teleportBy) {
        BlockVector[] teleportDifference = BlueprintDifference.teleportDifference(unifiedVectors, size, teleportBy);
        for (BlockVector i : teleportDifference) {
            if (!oldAnchor.getRelative(i).isEmptyForCollision()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Detects whether a collision would happen if this movable were to rotate
     * by the given rotation.
     * 
     * @param anchor
     *            {@link BlockLocation} of the old anchor.
     * 
     * @param rotateBy
     *            {@link BlockRotation} to rotate by.
     * @return True if a collision would happen
     */
    protected boolean detectCollisionRotate(final BlockLocation anchor, final BlockRotation rotateBy) {
        BlockVector[] rotateDifference = BlueprintDifference.rotateDifference(unifiedVectors, size, rotateBy);
        for (BlockVector i : rotateDifference) {
            if (!anchor.getRelative(i).isEmptyForCollision()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Places all blocks of this movable in relation to the given new anchor.
     * Starts from the end of the array so that attached blocks on the front are
     * placed last.
     * 
     * @param newAnchor
     *            {@link BlockLocation} of the new anchor
     */
    protected void put(final BlockLocation newAnchor) {
        for (int i = size - 1; i >= 0; i--) {
            newAnchor.getRelative(unifiedVectors[i]).setTypeId(unifiedBlueprint[i].typeId);
        }
    }

    /**
     * Clears the blocks that should become empty after the movable has moved in
     * the direction of the given face.
     * 
     * @param oldAnchor
     *            {@link BlockLocation} of the anchor to clear behind. This must
     *            be the anchor before the move.
     * @param face
     *            The direction to move in.
     */
    protected void clearBehind(final BlockLocation oldAnchor, final BlockFace face) {
        for (BlockVector i : getDifference(face).minus) {
            oldAnchor.getRelative(i).setEmpty();
        }
    }

    /**
     * Clears all blocks this movable is made up of.
     * 
     * @param anchor
     *            {@link BlockLocation} of the anchor to clear around.
     */
    protected void clearFull(final BlockLocation anchor) {
        for (BlockVector i : unifiedVectors) {
            anchor.getRelative(i).setEmpty();
        }
    }

    /**
     * Moves the machina a distance of 1 in the direction of the given
     * {@link BlockFace}.
     * 
     * @param oldAnchor
     *            The current anchor
     * @param face
     *            The direction to move in
     * @return The new anchor
     */
    protected BlockLocation moveByFace(final BlockLocation oldAnchor, final BlockFace face) {
        BlockLocation newAnchor = oldAnchor.getRelative(face);

        for (int i = 0; i < moduleCount; i++) {
            moveData[i].get(oldAnchor, modules.get(i));
        }

        // Destroy the negative difference, with attachables included first
        clearBehind(oldAnchor, face);
        // Put new blocks, attachables last
        put(newAnchor);

        for (int i = 0; i < moduleCount; i++) {
            moveData[i].put(newAnchor, modules.get(i));
        }
        return newAnchor;
    }

    /**
     * Teleports the machina at oldAnchor to newAnchor.
     * 
     * @param oldAnchor
     *            The current anchor
     * @param newAnchor
     *            The location to teleport to
     */
    protected void teleport(final BlockLocation oldAnchor, final BlockLocation newAnchor) {
        for (int i = 0; i < moduleCount; i++) {
            moveData[i].get(oldAnchor, modules.get(i));
        }

        // Destroy the entire machina, with attachables included first
        clearFull(oldAnchor);
        // Put new blocks, attachables last
        put(newAnchor);

        for (int i = 0; i < moduleCount; i++) {
            moveData[i].put(newAnchor, modules.get(i));
        }
    }

    /**
     * Rotates the machina by the given {@link BlockRotation}.
     * 
     * @param anchor
     *            The current anchor
     * @param rotateBy
     *            The amount to rotate by
     */
    protected void rotate(final BlockLocation anchor, final BlockRotation rotateBy) {
        if (rotateBy == BlockRotation.ROTATE_0) {
            return;
        }

        for (int i = 0; i < moduleCount; i++) {
            moveData[i].get(anchor, modules.get(i));
        }

        // * Destroy the negative difference, with attachables included
        // first
        clearFull(anchor);

        yaw = yaw.add(rotateBy);
        // Re-initialize the vectors, also clear the differences since they are only valid for a given yaw.
        blueprint.unifyVectors(modules, yaw, unifiedVectors);
        differences.clear();

        // * Put new blocks, attachables last
        put(anchor);

        for (int i = 0; i < moduleCount; i++) {
            moveData[i].put(anchor, modules.get(i));
        }
    }

    /**
     * Function for a moving machina to test whether it's allowed to move to a
     * new location by protection plugins. Returns true if the player could
     * build the new block. The event used to test this will always be cancelled
     * for any monitoring plugins.
     * 
     * 
     * @param newAnchor
     *            The new anchor location of the machina.
     * @param keyIndex
     *            The index of the key block to be placed.
     * @param material
     *            The Material to place.
     * @return True if the player may place a block at the location.
     */
    protected boolean canMove(BlockLocation newAnchor, BlueprintBlock block) {
        BlockLocation target = newAnchor.getRelative(block.vector(yaw));
        BlockLocation placedAgainst = target.getRelative(yaw.getOpposite().getYawFace());
        return EventSimulator.blockPlacePretend(target, block.typeId, placedAgainst, player);
    }

    /**
     * Simulates a block place event on behalf of the player who started the
     * machina. Returns true if the player could build the new block.
     * 
     * @param target
     *            The target location to place at
     * @param typeId
     *            The typeId of the block to place
     * @param placedAgainst
     *            The block that it will be placed against
     * @return True if the player may place a block at the location
     */
    protected boolean canPlace(BlockLocation target, int typeId, byte data, BlockLocation placedAgainst) {
        return EventSimulator.blockPlace(target, typeId, data, placedAgainst, player);
    }

    /**
     * Checks if the given module id is active for this machina.
     * 
     * @param id
     * @return True if the module is active.
     */
    protected boolean hasModule(int id) {
        return modules.contains(id);
    }
}
