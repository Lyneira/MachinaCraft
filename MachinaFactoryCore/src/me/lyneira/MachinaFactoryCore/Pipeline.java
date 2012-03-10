package me.lyneira.MachinaFactoryCore;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.Machina;

/**
 * A pipeline from a {@link Machina} to a {@link PipelineEndpoint}. Allows the machina to send
 * any payload to its endpoint.
 * 
 * @author Lyneira
 * 
 */
public class Pipeline {
    private final BlockLocation source;
    private final Deque<PipelineNode> route;
    private final PipelineEndpoint endpoint;
    public final int distance;
    /**
     * Maximum size of the search graph.
     */
    private static final int maxSize = 300;

    /**
     * Attempts to construct a new pipeline from the given source block to a
     * valid endpoint. The search will expand outward in all valid directions
     * until it finds the shortest path.
     * 
     * @param source
     * @throws PipelineException
     *             No valid endpoint was found.
     */
    public Pipeline(Player player, BlockLocation source) throws PipelineException {
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
        PipelineNode endnode = null;
        PipelineEndpoint endpoint = null;
        graph.add(start);
        q.add(start);
        for (PipelineNode node = q.poll(); node != null && graph.size() < maxSize; node = q.poll()) {
            // TODO: If node is valid listener: do stuff
            endpoint = node.target(player);
            if (endpoint != null) {
                endnode = node;
                break;
            }
            for (PipelineNode i : node.neighbors(Material.WOOD)) {
                if (!graph.contains(i)) {
                    graph.add(i);
                    q.add(i);
                }
            }
        }

        if (endpoint == null)
            throw new PipelineException(source);

        // Set up our route.
        route = new ArrayDeque<PipelineNode>(endnode.distance - 1);
        distance = endnode.distance;
        for (PipelineNode node = endnode.previous; node.previous != null; node = node.previous) {
            route.addFirst(node);
        }
        this.endpoint = endpoint;
    }

    /**
     * Sends a packet with the given payload to the endpoint of this pipeline.
     * @param payload A payload.
     * @return True if the payload was successfully handled.
     * @throws PipelineException The pipeline is no longer intact.
     */
    public <P> boolean sendPacket(P payload) throws PipelineException {
        verify();
        return endpoint.getHandler().handle(endpoint, payload);
    }

    /**
     * Verifies the pipeline and its endpoint.
     * @throws PipelineException The pipeline is no longer intact.
     */
    private void verify() throws PipelineException {
        for (PipelineNode node : route) {
            if (!node.verify()) {
                throw new PipelineException(source);
            }
        }
        if (!endpoint.verify())
            throw new PipelineException(source);
    }
}
