package me.lyneira.MachinaFactoryCore;

import java.util.List;

import org.bukkit.Material;

import me.lyneira.MachinaCore.BlockLocation;

class PipelineNode {
    final BlockLocation location;
    final PipelineNode previous;
    final int distance;
    final Material type;

    /**
     * Constructs a new root PipelineNode from the given location. 
     * @param location
     */
    PipelineNode(BlockLocation location) {
        this.location = location;
        this.previous = null;
        distance = 0;
        type = location.getType();
    }

    private PipelineNode(BlockLocation location, PipelineNode previous) {
        this.location = location;
        this.previous = previous;
        distance = previous.distance + 1;
        type = location.getType();
    }
    
    List<PipelineNode> neighbors() {
        // TODO find only valid neighbors
        return null;
    }
    
    boolean validTarget() {
        // TODO
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
