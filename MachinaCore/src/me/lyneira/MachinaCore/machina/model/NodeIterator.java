package me.lyneira.MachinaCore.machina.model;

import gnu.trove.iterator.TIntIterator;
import gnu.trove.procedure.TIntProcedure;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.NoSuchElementException;

import me.lyneira.MachinaCore.MachinaCore;
import me.lyneira.util.collection.UniqueIdObjectMap;

class NodeIterator implements TIntIterator {

    private final Deque<Integer> queue = new ArrayDeque<Integer>();
    private final UniqueIdObjectMap<ModelNode> nodes;

    NodeIterator(int nodeId, UniqueIdObjectMap<ModelNode> nodes) {
        queue.add(nodeId);
        this.nodes = nodes;
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
                throw new NoSuchElementException("No elements left in model!");
            }

            node = nodes.get(id);

            if (node == null) {
                MachinaCore.severe("Removal from model detected while retrieving next through iterator!");
            } else {
                break;
            }
        }
        node.forEachChild(addChildren);

        return id;
    };

    /**
     * Returns the next node without advancing the iterator. Can be used to
     * include or exclude the node and its subtree combined with next() or
     * skip() respectively. Like next(), this method should only be called after
     * hasNext() returns true.
     * 
     * @return The next node in the tree
     */
    public int peek() {
        Integer id = queue.peek();
        if (id == null) {
            throw new NoSuchElementException("No elements left in model!");
        }
        return id;
    }

    /**
     * Advances the iterator to the next node. Any nodes in the skipped node's
     * subtree will not be returned by subsequent calls to next(). Like next(),
     * this method should only be called after hasNext() returns true.
     */
    public void skip() {
        queue.remove();
    }

    private final TIntProcedure addChildren = new TIntProcedure() {
        @Override
        public boolean execute(int value) {
            queue.add(value);
            return true;
        }
    };
}