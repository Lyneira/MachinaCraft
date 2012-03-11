package me.lyneira.MachinaFactory;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;

/**
 * A node in a {@link Pipeline}.
 * 
 * @author Lyneira
 */
class PipelineNode {
    /**
     * Points back towards the start of the pipeline.
     */
    final PipelineNode previous;
    final BlockLocation location;
    final Material type;
    final int distance;

    /**
     * Constructs a new root PipelineNode from the given location.
     * 
     * @param location
     */
    PipelineNode(BlockLocation location) {
        this.previous = null;
        this.location = location;
        type = location.getType();
        distance = 0;
    }

    /**
     * Constructs a new PipelineNode for the given location, with the given previous node.
     * @param previous
     * @param location
     * @param type
     */
    PipelineNode(PipelineNode previous, BlockLocation location, Material type) {
        this.previous = previous;
        this.location = location;
        this.type = type;
        distance = previous.distance + 1;
    }

    /**
     * Returns a list of neighbors for this node.
     * @param material
     * @return
     */
    List<PipelineNode> neighbors(Material material) {
        List<PipelineNode> neighbors = new ArrayList<PipelineNode>(4);
        for (BlockRotation i : BlockRotation.values()) {
            addNeighbor(neighbors, location.getRelative(i.getYawFace()), material);
        }
        addNeighbor(neighbors, location.getRelative(BlockFace.UP), material);
        addNeighbor(neighbors, location.getRelative(BlockFace.DOWN), material);
        return neighbors;
    }

    /**
     * Adds a PipelineNode for the given location and material to the given neighbor list, with this node as the previous.
     * @param neighbors
     * @param location
     * @param material
     */
    private void addNeighbor(List<PipelineNode> neighbors, BlockLocation location, Material material) {
        Material type = location.getType();
        if (type == Material.AIR)
            return;

        if (type == material) {
            neighbors.add(new PipelineNode(this, location, type));
        } else {
            neighbors.add(new TargetNode(this, location, type));
        }
    }

    /**
     * Returns a {@link PipelineEndpoint} for this node.
     * @return A PipelineEndpoint, or null if this is not a valid endpoint.
     */
    PipelineEndpoint target(BlockLocation anchor, Player player) {
        return null;
    }
    
    /**
     * Verifies this node.
     * @return True if successful, otherwise false.
     */
    boolean verify() {
        if (type == location.getType())
            return true;
        return false;
    }

    @Override
    public int hashCode() {
        return location.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PipelineNode other = (PipelineNode) obj;

        return location.equals(other.location);
    }
}
