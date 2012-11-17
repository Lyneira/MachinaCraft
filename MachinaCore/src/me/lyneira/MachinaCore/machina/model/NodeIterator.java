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

    private final TIntProcedure addChildren = new TIntProcedure() {
        @Override
        public boolean execute(int value) {
            queue.add(value);
            return true;
        }
    };
}