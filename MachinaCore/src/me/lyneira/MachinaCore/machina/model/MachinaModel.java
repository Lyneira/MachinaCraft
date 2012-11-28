package me.lyneira.MachinaCore.machina.model;

import java.util.Arrays;

import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import me.lyneira.MachinaCore.BlockData;
import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.MachinaCore.machina.MachinaUpdate;
import me.lyneira.util.collection.UniqueIdObjectIterator;
import me.lyneira.util.collection.UniqueIdObjectMap;

public class MachinaModel {
    private final UniqueIdObjectMap<ModelNode> nodes;
    private final World world;
    private ModelNode root;
    private int blockCount = 0;

    // Used to keep track of the cumulative origin of a node
    private int x;
    private int y;
    private int z;
    // Used to keep track of the cumulative origin of a modified node
    private int xMod;
    private int yMod;
    private int zMod;

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

    public boolean hasNode(int nodeId) {
        return nodes.get(nodeId) != null;
    }

    public void move(BlockVector vector) {
        move(0, vector);
    }

    public void move(int nodeId, BlockVector vector) {
        if (!hasNode(nodeId)) {
            MachinaCore.warning("Attempted to move an invalid node in machina's model!");
            return;
        }
        ModelNode node = modify(nodeId);
        node.origin = node.origin.add(vector);
    }

    /**
     * Returns a MachinaUpdate holding all necessary data to perform an update
     * to the machina. The update contains only data about modified parts of the
     * model.
     * 
     * @return An update for the machina
     */
    public MachinaUpdate createUpdate() {
        int oldCount = 0;
        int newCount = 0;
        /*
         * Go over the entire tree and do a count of all blocks in modified
         * nodes.
         * 
         * TODO Better to iterate the tree only once and use arraylists?
         */
        for (NodeIterator it = nodeIterator(0); it.hasNext();) {
            ModelNode node = nodes.get(it.peek());
            ModelNode modified = node.modified;
            if (modified == null) {
                it.skip();
                continue;
            } else if (modified != node) {
                oldCount += node.blocks.size();
                newCount += modified.blocks.size();
            }
            // Modified node or subtree, include this node's children
            it.next();
        }
        MachinaBlock[] oldBlocks = new MachinaBlock[oldCount];
        MachinaBlock[] newBlocks = new MachinaBlock[newCount];
        ItemStack[][] inventories = new ItemStack[newCount][];
        /*
         * Now add the nodes to the newly created arrays
         */
        oldCount = 0;
        newCount = 0;
        for (NodeIterator it = nodeIterator(0); it.hasNext();) {
            ModelNode node = nodes.get(it.peek());
            ModelNode modified = node.modified;
            if (modified == null) {
                it.skip();
                continue;
            } else if (modified != node) {
                determineOrigin(node);
                determineOriginModified(modified);
                int capacity = node.blocks.capacity();
                if (modified.blocks.capacity() > capacity) {
                    capacity = modified.blocks.capacity();
                }
                for (int i = 0; i < capacity; i++) {
                    MachinaBlock oldModelBlock = node.blocks.get(i);
                    MachinaBlock newModelBlock = modified.blocks.get(i);
                    if (oldModelBlock != null) {
                        MachinaBlock oldInstanceBlock = new MachinaBlock(oldModelBlock, x, y, z);
                        // TODO Check more extensively for existence of
                        // inventory, do error checking and stuff. Make it a
                        // proper method
                        if (newModelBlock != null && BlockData.hasInventory(oldModelBlock.typeId) && newModelBlock.typeId == oldModelBlock.typeId) {
                            ItemStack[] contents;
                            try {
                                if (oldModelBlock.typeId == BlockData.chestId) {
                                    contents = ((Chest) oldInstanceBlock.getBlock(world).getState()).getBlockInventory().getContents();
                                } else {
                                    contents = ((InventoryHolder) oldInstanceBlock.getBlock(world).getState()).getInventory().getContents();
                                }
                            } catch (Throwable e) {
                                MachinaCore.severe("Unsuccessful attempt to copy inventory from model block " + oldModelBlock.toString() + ", actual block was: " + oldInstanceBlock.getBlock(world).toString());
                                e.printStackTrace();
                                contents = null;
                            }
                            inventories[newCount] = contents;
                        }
                        oldBlocks[oldCount++] = oldInstanceBlock;
                    }
                    if (newModelBlock != null) {
                        newBlocks[newCount++] = new MachinaBlock(newModelBlock, xMod, yMod, zMod);
                    }
                }
            }
            // Modified node or subtree, include this node's children
            it.next();
        }
        return new MachinaUpdate(oldBlocks, newBlocks, inventories);
    }

    /**
     * Clears all pending modifications from this model. Public use permitted.
     */
    public void clearModifications() {
        // TODO Deal with added nodes
        for (NodeIterator it = nodeIterator(0); it.hasNext();) {
            ModelNode node = nodes.get(it.peek());
            ModelNode modified = node.modified;
            if (modified == null) {
                it.skip();
                continue;
            }
            node.modified = null;
            it.next();
        }
    }

    /**
     * Applies all pending modifications to this model. Internal use only.
     */
    public void applyModifications() {
        // TODO Deal with removed nodes
        for (NodeIterator it = nodeIterator(0); it.hasNext();) {
            int nodeId = it.peek();
            ModelNode node = nodes.get(nodeId);
            ModelNode modified = node.modified;
            if (modified == null) {
                it.skip();
                continue;
            }
            if (modified == node) {
                node.modified = null;
            } else {
                nodes.put(modified, nodeId);
            }
            it.next();
        }
    }

    /**
     * Marks a node as modified and returns a copy of the node for actual
     * modification.
     * 
     * @param nodeId
     *            The node to modify
     * @return
     */
    private ModelNode modify(int nodeId) {
        ModelNode node = nodes.get(nodeId);
        if (node.modified == null) {
            node.modified = new ModelNode(node);
            /*
             * Mark unmodified parent nodes as having a modified node in their
             * subtree (the path copy needed for a persistent data structure)
             */
            for (int i = node.parent; i != -1;) {
                node = nodes.get(i);

                if (node.modified != null) {
                    // The rest of the path upward is already marked as modified
                    break;
                }
                node.modified = node;
                i = node.parent;
            }
        } else if (node.modified == node) {
            node.modified = new ModelNode(node);
        } else {
            // This node is already modified, the tree does not need updating.
            return node.modified;
        }
        /*
         * The modified node's subtree must be marked as modified as well.
         */
        NodeIterator it = nodeIterator(nodeId);
        it.next();
        while (it.hasNext()) {
            ModelNode subNode = nodes.get(it.peek());
            if (subNode.modified == null || subNode.modified == subNode) {
                subNode.modified = new ModelNode(node);
                it.next();
            } else {
                it.skip();
            }
        }
        return node.modified;
    }

    /**
     * Walk up the tree to determine the full origin and store it in x, y and z.
     * 
     * @param node
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

    /**
     * Assume the given node is a modified node and walk up the tree to
     * determine the full origin, using each parent's modified node if
     * available.
     * 
     * @param node
     *            The node from which to start.
     */
    private void determineOriginModified(ModelNode node) {
        BlockVector nodeOrigin = node.origin;
        xMod = nodeOrigin.x;
        yMod = nodeOrigin.y;
        zMod = nodeOrigin.z;
        for (int i = node.parent; i != -1;) {
            node = nodes.get(i);
            if (node.modified != null) {
                node = node.modified;
            }
            nodeOrigin = node.origin;
            xMod += nodeOrigin.x;
            yMod += nodeOrigin.y;
            zMod += nodeOrigin.z;
            i = node.parent;
        }
    }

    private NodeIterator nodeIterator(int nodeId) {
        return new NodeIterator(nodeId, nodes);
    }
}
