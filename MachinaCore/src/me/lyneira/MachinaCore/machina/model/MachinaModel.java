package me.lyneira.MachinaCore.machina.model;

import java.util.Arrays;

import org.bukkit.World;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.util.collection.UniqueIdObjectIterator;
import me.lyneira.util.collection.UniqueIdObjectMap;

public class MachinaModel {
    private final UniqueIdObjectMap<ModelNode> nodes;
    private final World world;
    private ModelNode root;
    private int blockCount = 0;

    // Used to keep track of the cumulative origin of the current node
    private int x;
    private int y;
    private int z;

    MachinaModel(World world, BlockVector origin, BlueprintModel model) {
        nodes = new UniqueIdObjectMap<ModelNode>(model.nodes.capacity());
        this.world = world;

        NodeIterator it = model.nodeIterator(0);
        root = new ModelNode(model.nodes.get(0));
        nodes.put(root, 0);
        root.origin = origin;
        blockCount += root.blocks.size();
        it.next();
        while (it.hasNext()) {
            int nodeId = it.next();
            ModelNode newNode = new ModelNode(model.nodes.get(nodeId));
            nodes.put(newNode, nodeId);
            blockCount += newNode.blocks.size();
        }
    }

    public MachinaBlock[] instance() {
        MachinaBlock[] instance = new MachinaBlock[blockCount];
        int i = 0;
        for (NodeIterator it = nodeIterator(0); it.hasNext();) {
            ModelNode node = nodes.get(it.next());
            determineOrigin(node);
            for (UniqueIdObjectIterator<MachinaBlock> itBlock = node.blockIterator(); itBlock.hasNext();) {
                MachinaBlock block = itBlock.next();
                instance[i++] = new MachinaBlock(x + block.x, y + block.y, z + block.z, block.typeId, block.data);
            }
        }
        if (i < blockCount) {
            MachinaCore.warning("Machina's instance size ended up smaller than expected, some of the blocks in the model overlap! This can cause unpredictable results in behaviour!");
            instance = Arrays.copyOf(instance, i);
        }
        return instance;
    }

    /**
     * Walk up the tree to determine the full origin and store it in x, y and z.
     * 
     * @param nodeId
     *            The node from which to start.
     */
    private void determineOrigin(ModelNode node) {
        BlockVector nodeOrigin = node.origin;
        x = nodeOrigin.x;
        y = nodeOrigin.y;
        z = nodeOrigin.z;
        for (int i = node.parent; i != -1;) {
            node = nodes.get(i);
            nodeOrigin = node.origin;
            x += nodeOrigin.x;
            y += nodeOrigin.y;
            z += nodeOrigin.z;
            i = node.parent;
        }
    }

    private NodeIterator nodeIterator(int nodeId) {
        return new NodeIterator(nodeId, nodes);
    }
}
