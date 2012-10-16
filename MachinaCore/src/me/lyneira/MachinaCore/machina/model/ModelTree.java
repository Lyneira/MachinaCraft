package me.lyneira.MachinaCore.machina.model;

import java.util.Iterator;

/**
 * Represents a tree-based model of a machina.
 * 
 * @author Lyneira
 */
public interface ModelTree {

    public ModelNode get(int id);
    
    public int addNode(ModelNode node, int parentId);
    
    public int deleteNode(int id);
    
    public boolean replaceNode(int id, ModelNode newNode);
    
    Iterator<ModelNode> iterator();
}
