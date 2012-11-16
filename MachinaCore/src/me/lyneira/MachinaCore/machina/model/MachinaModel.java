package me.lyneira.MachinaCore.machina.model;

import org.bukkit.World;

import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.machina.model.BlueprintModel.NodeIterator;
import me.lyneira.util.collection.UniqueIdObjectMap;

public class MachinaModel {
    private final UniqueIdObjectMap<ModelNode> nodes;
    private final World world;
    private ModelNode root;
    
    MachinaModel(World world, BlockVector origin, BlueprintModel model) {
        nodes = new UniqueIdObjectMap<ModelNode>(model.nodes.capacity());
        this.world = world;
        
        NodeIterator it = model.nodeIterator(0);
        root = new ModelNode(model.nodes.get(0));
        nodes.put(root, 0);
        root.origin = origin;
        it.next();
        while (it.hasNext()) {
            int nodeId = it.next();
            nodes.put(new ModelNode(model.nodes.get(nodeId)), nodeId);
        }
    }
}
