package me.lyneira.MachinaFactoryCore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import me.lyneira.MachinaCore.BlockLocation;

public class Pipeline {
    private final BlockLocation source;
    private final Deque<PipelineNode> route;
    private final int delay;
    /**
     * Maximum size of the search graph.
     */
    private static final int maxSize = 300;
    /** 
     * Speed of the signal in blocks per tick.
     */
    private static final int signalSpeed = 16;

    Pipeline(BlockLocation source) throws PipelineException {
        if (source == null)
            throw new PipelineException(source, "Tried to construct a pipeline with a null source!", new NullPointerException());
        this.source = source;

        // Simplified implementation of Dijkstra's algorithm for finding the
        // shortest path to a suitable target. This implementation leaves out
        // alternative distance-checking and infinite distance checks because
        // all distances are uniform and the graph is discovered on the fly.

        Set<PipelineNode> graph = new HashSet<PipelineNode>(25);
        Queue<PipelineNode> q = new ArrayDeque<PipelineNode>();
        PipelineNode start = new PipelineNode(source);
        PipelineNode target = null;
        graph.add(start);
        q.add(start);
        for (PipelineNode node = q.poll(); node != null && graph.size() < maxSize; node = q.poll()) {
            // TODO: If node is valid listener: do stuff
            // TODO: If node is valid end point: stop
            if (node.validTarget()) {
                target = node;
                break;
            }

            for (PipelineNode i : node.neighbors()) {
                if (!graph.contains(i)) {
                    graph.add(i);
                    q.add(i);
                }
            }
        }

        if (target == null)
            throw new PipelineException(source);

        // Set up our route.
        route = new ArrayDeque<PipelineNode>(target.distance - 1);
        delay = 1 + (target.distance / signalSpeed);
        for (PipelineNode node = target.previous; node.previous != null; node = node.previous) {
            route.addFirst(node);
        }
    }

    void sendMessage(String message) throws PipelineException {
        MachinaFactoryCore.log.info("Pipeline [" + source.toString() + "]: " + message);
    }
}
