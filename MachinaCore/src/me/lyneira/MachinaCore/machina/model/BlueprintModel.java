package me.lyneira.MachinaCore.machina.model;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import org.bukkit.World;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.util.collection.UniqueIdObjectMap;

/**
 * Tree structure for specifying the detect-time blueprint of a machina.
 * 
 * @author Lyneira
 * 
 */
public class BlueprintModel {
    protected final UniqueIdObjectMap<ModelNode> nodes;
    private ModelNode root;
    private int size = 1;

    public BlueprintModel() {
        this(10);
    }

    public BlueprintModel(int initialCapacity) {
        nodes = new UniqueIdObjectMap<ModelNode>(initialCapacity);
        root = new ModelNode(new BlockVector(0, 0, 0));
        nodes.put(root, 0);
    }

    public BlueprintModel(BlueprintModel other) {
        nodes = new UniqueIdObjectMap<ModelNode>(other.nodes.capacity());
        root = new ModelNode(other.root);
        nodes.put(root, 0);
        NodeIterator it = other.nodeIterator(0);
        it.next();
        while (it.hasNext()) {
            final int id = it.next();
            nodes.put(new ModelNode(other.nodes.get(id)), id);
        }
    }
    
    private BlueprintModel(int initialCapacity, ModelNode root) {
        nodes = new UniqueIdObjectMap<ModelNode>(initialCapacity);
        this.root = root;
        nodes.put(root, 0);
    }

    public int addNode(BlockVector origin) {
        return (addNode(0, origin));
    }

    public int addNode(int parentId, BlockVector origin) {
        final ModelNode parent = nodes.get(parentId);
        if (parent == null) {
            throw new IllegalArgumentException("Cannot add a node to a nonexistent parent!");
        }
        final ModelNode newNode = new ModelNode(parentId, origin);
        final int id = nodes.add(newNode);
        parent.addChild(id);
        size++;
        return id;
    }

    public void removeNode(int nodeId) {
        ModelNode node = nodes.get(nodeId);
        if (node == null) {
            return;
        }

        final int parentId = node.parent;
        if (parentId >= 0) {
            // Node is not the root, so remove it from parent
            nodes.get(node.parent).removeChild(nodeId);

            // Walk the tree and remove all subnodes
            for (NodeIterator it = new NodeIterator(nodeId); it.hasNext();) {
                nodes.remove(it.next());
                size--;
            }
        } else {
            throw new UnsupportedOperationException("Cannot remove the root node in a ConstructionModelTree!");
        }

    }

    public boolean hasNode(int nodeId) {
        return nodes.get(nodeId) != null;
    }

    public TIntIterator children(int nodeId) {
        final ModelNode node = nodes.get(nodeId);
        if (node == null) {
            return null;
        }
        return node.children();
    }

    public int nodeCount() {
        return size;
    }

    public MachinaBlock getBlock(int id) {
        MachinaCore.info("Attempting to retrieve block " + id + " from root, dumping tree and root node");
        dumpTree();
        root.dumpBlocks();
        return root.blocks.get(id);
    }

    public MachinaBlock getBlock(int nodeId, int id) {
        ModelNode node = nodes.get(nodeId);
        if (node == null)
            return null;
        return node.blocks.get(id);
    }

    public int addBlock(MachinaBlock block) {
        return root.blocks.add(block);
    }

    public int addBlock(int nodeId, MachinaBlock block) {
        ModelNode node = nodes.get(nodeId);
        if (node == null)
            return -1;
        return node.blocks.add(block);
    }

    public void deleteBlock(int id) {
        root.blocks.remove(id);
    }

    public void deleteBlock(int nodeId, int id) {
        ModelNode node = nodes.get(nodeId);
        if (node == null)
            return;
        node.blocks.remove(id);
    }

    public void clearRootBlocks() {
        root.clearBlocks();
    }

    public void clearBlocks(int nodeId) {
        ModelNode node = nodes.get(nodeId);
        if (node == null)
            return;
        node.clearBlocks();
    }

    public void putBlock(MachinaBlock newBlock, int id) {
        root.blocks.put(newBlock, id);
    }

    public void putBlock(int nodeId, MachinaBlock newBlock, int id) {
        ModelNode node = nodes.get(nodeId);
        if (node == null)
            return;
        node.blocks.put(newBlock, id);
    }
    
    public void dumpTree() {
        MachinaCore.info("Beginning tree dump");
        NodeIterator it = new NodeIterator(0);
        while (it.hasNext()) {
            int nodeId = it.next();
            MachinaCore.info("Dumping node id " + nodeId);
            ModelNode node = nodes.get(nodeId);
            node.dumpBlocks();
        }
    }

    /* *************
     * Other methods
     */
    /**
     * Detects whether this model is present for the given world, rotation and
     * origin and returns a properly rotated copy of this model if successful.
     * Returns null on failure.
     * 
     * @param world
     * @param rotation
     * @param origin
     * @return
     */
    public ConstructionModel construct(World world, BlockRotation rotation, BlockVector origin) {
        
        MachinaCore.info("Constructing root node ");
        ModelNode newRoot = new ModelNode(root.parent, root.origin, root.blocks.capacity());
        newRoot.copyChildren(root);
        
        BlueprintModel constructionBlueprint = new BlueprintModel(nodes.capacity(), newRoot);
        ConstructionModel constructionModel = new ConstructionModel(constructionBlueprint, world, origin);
        
        if (constructionModel.putBlueprintBlocks(0, root.blockIterator(), rotation) == false)
            return null;
        
        NodeIterator it = new NodeIterator(0);
        // We've already added the root, so the while is only necessary for subnodes.
        it.next();
        
        while (it.hasNext()) {
            final int nodeId = it.next();
            MachinaCore.info("Constructing node " + nodeId);
            ModelNode node = nodes.get(nodeId);
            ModelNode newNode = new ModelNode(node.parent, node.origin, node.blocks.capacity());
            newNode.copyChildren(node);
            constructionBlueprint.nodes.put(newNode, nodeId);
            if (constructionModel.putBlueprintBlocks(nodeId, node.blockIterator(), rotation) == false)
                return null;
        }

        return constructionModel;
    }

    /*
     * Nonpublic methods
     */
    NodeIterator nodeIterator(int nodeId) {
        return new NodeIterator(nodeId);
    }

    class NodeIterator implements TIntIterator {

        Deque<Integer> queue = new ArrayDeque<Integer>();

        NodeIterator(int nodeId) {
            queue.add(nodeId);
        }

        @Override
        public boolean hasNext() {
            return queue.size() > 0;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int next() {
            ModelNode node;
            Integer id;
            while (true) {
                id = queue.poll();
                if (id == null) {
                    throw new NoSuchElementException("No elements left in ConstructionModelTree!");
                }

                node = nodes.get(id);

                if (node == null) {
                    MachinaCore.severe("Removal from ConstructionModelTree detected while retrieving next through iterator!");
                } else {
                    break;
                }
            }
            node.forEachChild(addChildren);

            return id;
        };

        private final TIntProcedure addChildren = new TIntProcedure() {
            @Override
            public boolean execute(int value) {
                queue.add(value);
                return true;
            }
        };
    }

}
