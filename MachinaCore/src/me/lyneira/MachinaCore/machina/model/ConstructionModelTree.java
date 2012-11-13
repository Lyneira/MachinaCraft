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
 * ModelTree for a machina that will be detected. Modifications to this tree
 * take effect immediately.
 * 
 * @author Lyneira
 * 
 */
public class ConstructionModelTree implements ModelTree {
    private final UniqueIdObjectMap<ModelNode> nodes;
    private ModelNode root;
    private int size = 1;
    
    public ConstructionModelTree() {
        this(10);
    }

    public ConstructionModelTree(int initialCapacity) {
        nodes = new UniqueIdObjectMap<ModelNode>(initialCapacity);
        root = new ModelNode(new BlockVector(0, 0, 0));
        nodes.add(root);
    }

    public ConstructionModelTree(ConstructionModelTree other) {
        nodes = new UniqueIdObjectMap<ModelNode>(other.size);
        // Copy other model node?
        root = new ModelNode(other.root.origin);
        // TODO
    }

    @Override
    public int addNode(BlockVector origin) {
        return (addNode(0, origin));
    }

    @Override
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

    @Override
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

    @Override
    public boolean hasNode(int nodeId) {
        return nodes.get(nodeId) != null;
    }

    @Override
    public TIntIterator children(int nodeId) {
        final ModelNode node = nodes.get(nodeId);
        if (node == null) {
            return null;
        }
        return node.children();
    }

    @Override
    public int nodeCount() {
        return size;
    }

    @Override
    public MachinaBlock getRootBlock(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachinaBlock getBlock(int nodeId, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int addBlock(MachinaBlock block) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addBlock(MachinaBlock block, int nodeId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteBlock(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteBlock(int nodeId, int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearRootBlocks() {
        // TODO Auto-generated method stub

    }

    @Override
    public void clearBlocks(int nodeId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putRootBlock(MachinaBlock newBlock, int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putBlock(MachinaBlock newBlock, int nodeId, int id) {
        // TODO Auto-generated method stub

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
     * @param originX
     * @param originY
     * @param originZ
     * @return
     */
    public ConstructionModelTree construct(World world, BlockRotation rotation, BlockVector origin) {
        // TODO
        // Make sure to create copies of every machinablock in the source tree
        // and if the data value was -1, set it to whatever was detected at that
        // location
        return null;
    }

    /**
     * Converts this model into a MachinaModelTree and returns it.
     * 
     * @return The MachinaModeltree that was created.
     */
    public MachinaModelTree machinaModel() {
        // TODO
        return null;
    }

    private class NodeIterator implements TIntIterator {

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
