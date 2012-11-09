package me.lyneira.MachinaCore.machina.model;

import gnu.trove.procedure.TIntProcedure;

import java.util.Iterator;

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
    public void forEachChild(int nodeId, TIntProcedure procedure) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int nodeCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public MachinaBlock getBlock(int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MachinaBlock getBlock(int nodeId, int id) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Integer> getBlocks(int nodeId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int addBlock(MachinaBlock block) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int addBlock(MachinaBlock block, int nodeId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void deleteBlock(int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void deleteBlock(int nodeId, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void clearBlocks(int nodeId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putBlock(MachinaBlock newBlock, int id) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void putBlock(MachinaBlock newBlock, int nodeId, int id) {
        // TODO Auto-generated method stub
        
    }
}
