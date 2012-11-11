package me.lyneira.MachinaCore.machina.model;

import gnu.trove.iterator.TIntIterator;
import me.lyneira.MachinaCore.block.BlockVector;
import me.lyneira.MachinaCore.block.MachinaBlock;

public class MachinaModelTree implements ModelTree {

    @Override
    public int addNode(BlockVector origin) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addNode(int parentId, BlockVector origin) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void removeNode(int nodeId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean hasNode(int nodeId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public TIntIterator children(int nodeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int nodeCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MachinaBlock getRootBlock(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachinaBlock getBlock(int nodeId, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int addRootBlock(MachinaBlock block) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addBlock(MachinaBlock block, int nodeId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteRootBlock(int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteBlock(int nodeId, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearRootBlocks() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearBlocks(int nodeId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putRootBlock(MachinaBlock newBlock, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putBlock(MachinaBlock newBlock, int nodeId, int id) {
        // TODO Auto-generated method stub
        
    }
}
