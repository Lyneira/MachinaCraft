package me.lyneira.MachinaFactoryCore;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Diode;

import me.lyneira.MachinaCore.BlockLocation;
import me.lyneira.MachinaCore.BlockRotation;

class PipelineNode {
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

    PipelineNode(PipelineNode previous, BlockLocation location, Material type) {
        this.previous = previous;
        this.location = location;
        this.type = type;
        distance = previous.distance + 1;
    }

    List<PipelineNode> neighbors(Material material) {
        List<PipelineNode> neighbors = new ArrayList<PipelineNode>(4);
        for (BlockRotation i : BlockRotation.values()) {
            addNeighbor(neighbors, location.getRelative(i.getYawFace()), material);
        }
        addNeighbor(neighbors, location.getRelative(BlockFace.UP), material);
        addNeighbor(neighbors, location.getRelative(BlockFace.DOWN), material);
        return neighbors;
    }

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

    PipelineEndpoint target() {
        return null;
    }
    
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
