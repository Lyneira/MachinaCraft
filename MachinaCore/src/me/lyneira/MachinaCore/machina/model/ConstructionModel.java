package me.lyneira.MachinaCore.machina.model;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.util.collection.UniqueIdObjectIterator;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Tree structure used for detecting the blocks in a BlueprintModel and
 * performing extensions on the model. Intermediate step towards creation of a
 * Machina.
 * 
 * @author Lyneira
 * 
 */
public class ConstructionModel extends BlueprintModel {

    private final World world;
    private final BlockVector origin;
    private int x;
    private int y;
    private int z;

    ConstructionModel(World world, BlockVector origin, int initialCapacity, ModelNode root) {
        super(initialCapacity, root);
        this.world = world;
        this.origin = origin;
    }

    public Block getWorldBlock(BlockVector location) {
        return location.getBlock(world, origin.x, origin.y, origin.z);
    }

    public Block getWorldBlock(int nodeId, BlockVector location) {
        if (!hasNode(nodeId))
            return null;
        determineOrigin(nodeId);
        return location.getBlock(world, x, y, z);
    }

    public int extend(BlockVector location) {
        x = origin.x;
        y = origin.y;
        z = origin.z;
        return extendInternal(0, location);
    }

    public int extend(BlockVector location, int[] types) {
        x = origin.x;
        y = origin.y;
        z = origin.z;
        return extendInternal(0, location, types);
    }

    public int extend(BlockVector location, int typeId) {
        x = origin.x;
        y = origin.y;
        z = origin.z;
        return extendInternal(0, location, typeId);
    }

    public int extend(BlockVector location, int typeId, short data) {
        x = origin.x;
        y = origin.y;
        z = origin.z;
        return extendInternal(0, location, typeId, data);
    }

    public int extend(int nodeId, BlockVector location) {
        if (!hasNode(nodeId))
            return -1;
        determineOrigin(nodeId);
        return extendInternal(nodeId, location);
    }

    public int extend(int nodeId, BlockVector location, int[] types) {
        if (!hasNode(nodeId))
            return -1;
        determineOrigin(nodeId);

        return extendInternal(nodeId, location, types);
    }

    public int extend(int nodeId, BlockVector location, int typeId) {
        if (!hasNode(nodeId))
            return -1;
        determineOrigin(nodeId);

        return extendInternal(nodeId, location, typeId);
    }

    public int extend(int nodeId, BlockVector location, int typeId, short data) {
        if (!hasNode(nodeId))
            return -1;
        determineOrigin(nodeId);

        return extendInternal(nodeId, location, typeId, data);
    }

    /**
     * Converts this model into a MachinaModel and returns it.
     * 
     * @return The MachinaModel that was created.
     */
    public MachinaModel machinaModel() {
        return new MachinaModel(world, origin, this);
    }

    /*
     * Nonpublic methods
     */

    /**
     * Iterates over all blocks returned by the given UniqueIdObjectIterator,
     * rotating them by the given rotation and detecting whether they are
     * present in the world. If all are detected successfully, the rotated
     * blocks will be added to the node with their detected data values.
     * Otherwise the state of the tree is undefined and it should no longer be
     * used.
     * 
     * @param nodeId
     *            The node to add blocks to
     * @param it
     *            An iterator over all blocks to be added
     * @param rotation
     *            How to rotate the blocks before detecting
     * @return True if successful, false if not all blocks were detected.
     */
    boolean putBlueprintBlocks(int nodeId, UniqueIdObjectIterator<MachinaBlock> it, BlockRotation rotation) {
        determineOrigin(nodeId);
        ModelNode node = nodes.get(nodeId);
        while (it.hasNext()) {
            final MachinaBlock block = it.next();
            final BlockVector rotated = block.rotateYaw(rotation);
            final Block worldBlock = rotated.getBlock(world, x, y, z);
            final int typeId = worldBlock.getTypeId();
            if (typeId != block.typeId)
                return false;
            final short data = worldBlock.getData();
            if (block.data != -1 && data != block.data) {
                return false;
            }
            node.blocks.put(new MachinaBlock(rotated, typeId, data), it.lastId());
        }

        return true;
    }

    /**
     * Walk up the tree to determine the full origin and store it in x, y and z.
     * 
     * @param nodeId
     *            The node from which to start.
     */
    private void determineOrigin(int nodeId) {
        x = origin.x;
        y = origin.y;
        z = origin.z;
        /*
         * we know the root of a ConstructionModelTree is always 0,0,0 so it's
         * safe to skip the root, otherwise this would be -1
         */
        for (int i = nodeId; i != 0;) {
            ModelNode node = nodes.get(i);
            BlockVector nodeOrigin = node.origin;
            x += nodeOrigin.x;
            y += nodeOrigin.y;
            z += nodeOrigin.z;
            i = node.parent;
        }
    }

    private int extendInternal(int nodeId, BlockVector location) {
        final Block block = location.getBlock(world, x, y, z);
        return addBlock(nodeId, new MachinaBlock(location.x, location.y, location.z, block.getTypeId(), block.getData()));
    }

    private int extendInternal(int nodeId, BlockVector location, int[] types) {
        final Block block = location.getBlock(world, x, y, z);
        final int blockType = block.getTypeId();
        for (int i : types) {
            if (i == blockType) {
                return addBlock(nodeId, new MachinaBlock(location.x, location.y, location.z, i, block.getData()));
            }
        }
        return -1;
    }

    private int extendInternal(int nodeId, BlockVector location, int typeId) {
        final Block block = location.getBlock(world, x, y, z);
        if (block.getTypeId() == typeId) {
            return addBlock(nodeId, new MachinaBlock(location.x, location.y, location.z, typeId, block.getData()));
        }
        return -1;
    }

    private int extendInternal(int nodeId, BlockVector location, int typeId, short data) {
        final Block block = location.getBlock(world, x, y, z);
        if (block.getTypeId() == typeId) {
            if (data == -1) {
                data = block.getData();
            } else if (block.getData() != data) {
                return -1;
            }
            return addBlock(nodeId, new MachinaBlock(location.x, location.y, location.z, typeId, data));
        }
        return -1;
    }

}
