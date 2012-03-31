package me.lyneira.MachinaFactory;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.Machina;

/**
 * A pipeline from a {@link Machina} to a {@link PipelineEndpoint}. Allows the
 * machina to send any payload to its endpoint.
 * 
 * @author Lyneira
 * 
 */
public class Pipeline {
    /**
     * Maximum size of the search graph.
     */
    private static final int maxSize = 300;

    private BlockLocation source;
    private BlockLocation destination = null;
    private Deque<PipelineNode> route = null;
    private PipelineEndpoint endpoint = null;

    /**
     * Attempts to construct a new pipeline from the given source block to a
     * valid endpoint. The search will expand outward in all valid directions
     * until it finds the shortest path.
     * 
     * * <b>Important:</b> Setting <b>anchor</b> incorrectly or null will allow
     * a machina to potentially hook up with itself and cause an endless loop!
     * 
     * @param anchor
     *            The anchor of the machina creating this pipeline.
     * 
     * @param player
     *            The player activating this machina.
     * @param source
     *            The location from which to start the search.
     */
    public Pipeline(final BlockLocation anchor, final Player player, BlockLocation source) {
        if (source == null)
            throw new NullPointerException("Tried to construct a pipeline with a null source!");
        this.source = source;

        // Schedule finding the route for later, as doing it now could lead to
        // an endless loop.
        Runnable findRoute = new Runnable() {
            @Override
            public void run() {
                findRoute(anchor, player);
            }
        };
        MachinaFactory.plugin.getServer().getScheduler().scheduleSyncDelayedTask(MachinaFactory.plugin, findRoute);
    }

    /**
     * Sends a packet with the given payload to the endpoint of this pipeline.
     * 
     * @param payload
     *            A payload.
     * @return True if the payload was successfully handled.
     * @throws PipelineException
     *             The pipeline is no longer intact.
     * @throws PacketTypeUnsupportedException
     *             The packet type could not be handled by the receiving end.
     */
    public <P> boolean sendPacket(P payload) throws PipelineException, PacketTypeUnsupportedException {
        verify();
        return endpoint.getHandler().handle(endpoint, payload);
    }

    /**
     * Verifies the pipeline and its endpoint.
     * 
     * @throws PipelineException
     *             The pipeline is not intact.
     */
    private void verify() throws PipelineException {
        if (route == null)
            throw new PipelineException(source);

        for (PipelineNode node : route) {
            if (!node.verify())
                throw new PipelineException(source);
        }

        if (endpoint instanceof Machina && !MachinaFactory.machinaCore.exists(destination))
            throw new PipelineException(source);

        if (!endpoint.verify())
            throw new PipelineException(source);
    }

    /**
     * Simplified implementation of Dijkstra's algorithm for finding the
     * shortest path to a suitable target. This implementation leaves out
     * alternative distance-checking and infinite distance checks because all
     * distances are uniform and the graph is discovered on the fly.
     * 
     * * <b>Important:</b> Setting <b>anchor</b> incorrectly or null will allow
     * a machina to potentially hook up with itself and cause an endless loop!
     * 
     * @param anchor
     *            The anchor of the machina creating this pipeline.
     * 
     * @param player
     *            The player activating this machina.
     */
    private void findRoute(BlockLocation anchor, Player player) {
        Set<PipelineNode> graph = new HashSet<PipelineNode>(25);
        Queue<PipelineNode> q = new ArrayDeque<PipelineNode>();
        PipelineNode start = new PipelineNode(source);
        PipelineNode endnode = null;
        graph.add(start);
        q.add(start);
        for (PipelineNode node = q.poll(); node != null && graph.size() < maxSize; node = q.poll()) {
            // TODO: If node is valid listener: do stuff?
            endpoint = node.target(anchor, player);
            if (endpoint != null) {
                endnode = node;
                break;
            }
            for (PipelineNode i : node.neighbors(ComponentBlueprint.pipelineMaterial)) {
                if (!graph.contains(i)) {
                    graph.add(i);
                    q.add(i);
                }
            }
        }

        if (endpoint == null)
            return;

        // Set up our route.
        destination = endnode.location;
        route = new ArrayDeque<PipelineNode>(endnode.distance - 1);
        for (PipelineNode node = endnode.previous; node.previous != null; node = node.previous) {
            route.addFirst(node);
        }
    }
}
