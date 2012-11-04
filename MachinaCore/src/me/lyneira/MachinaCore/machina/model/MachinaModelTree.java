package me.lyneira.MachinaCore.machina.model;

import java.util.Iterator;

import me.lyneira.MachinaCore.block.MachinaBlock;

public class MachinaModelTree implements ModelTree {
    
    MachinaModelTree() {
        
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

}
