package me.lyneira.util.collection;

/**
 * Contract interface for an object that will be stored in an IntWeakObjectMap
 * and needs to store its id. The object's id can be set by the map's managing collection when added,
 * and the implementing class should hold this id without ever changing it.
 * 
 * @author Lyneira
 */
public interface IdHolder {
    
    /**
     * Set this object's id. Only intended for use by the managing class of an IntWeakObjectMap.
     * 
     * @param id The id to set
     */
    public void setId(int id);
    
    /**
     * Returns this object's id.
     * @return The id of this object
     */
    public int getId();
}
