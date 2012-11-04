package me.lyneira.MachinaCore.machina.model;

import java.util.Iterator;

import org.bukkit.World;

import me.lyneira.MachinaCore.block.BlockRotation;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;
import me.lyneira.util.collection.IntWeakObjectMap;

public class ConstructionModelTree implements ModelTree {
    private final IntWeakObjectMap<ModelNode> nodes;
    
    public ConstructionModelTree(int initialCapacity) {
        nodes = new IntWeakObjectMap<ModelNode>(initialCapacity);
    }
    
    public ConstructionModelTree(ModelTree other) {
        nodes = new IntWeakObjectMap<ModelNode>(other.nodeCount());
        // TODO
    }

    @Override
    public int addNode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addNode(int parentId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void removeNode(int node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int[] getChildren(int node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachinaBlock getBlock(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachinaBlock getBlock(int node, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Integer> getBlocks(int node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int addBlock(MachinaBlock block) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addBlock(MachinaBlock block, int node) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteBlock(int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteBlock(int node, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearBlocks(int node) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putBlock(MachinaBlock newBlock, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putBlock(MachinaBlock newBlock, int node, int id) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public int nodeCount() {
        // TODO Auto-generated method stub
        return 0;
    }
    
    /* *************
     * Other methods
     */
    /**
     * Detects whether this model is present for the given world, rotation and origin and returns a properly rotated copy of this model if successful. Returns null on failure.   
     * @param world
     * @param rotation
     * @param originX
     * @param originY
     * @param originZ
     * @return
     */
    public ConstructionModelTree construct(World world, BlockRotation rotation, BlockVector origin) {
        // TODO
        return null;
    }
    
    /**
     * Converts this model into a MachinaModelTree and returns it.
     * @return The MachinaModeltree that was created.
     */
    public MachinaModelTree machinaModel() {
        // TODO
        return null;
    }

}
