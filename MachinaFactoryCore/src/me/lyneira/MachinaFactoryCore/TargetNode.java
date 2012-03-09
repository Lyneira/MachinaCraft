package me.lyneira.MachinaFactoryCore;

import java.util.ArrayList;
import java.util.List;

import me.lyneira.MachinaCore.BlockLocation;

import org.bukkit.Material;


class TargetNode extends PipelineNode {
    TargetNode(PipelineNode previous, BlockLocation location, Material type) {
        super(previous, location, type);
    }

    @Override
    PipelineEndpoint target() {
        switch (type) {
        case CHEST:
        case DISPENSER:
            return new ContainerEndpoint(location);
        }
        return null;
    }

    /**
     * A targetnode has no potential neighbors.
     */
    @Override
    List<PipelineNode> neighbors(Material material) {
        return new ArrayList<PipelineNode>(0);
    }
}
