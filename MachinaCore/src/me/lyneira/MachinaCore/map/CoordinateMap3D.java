package me.lyneira.MachinaCore.map;

import java.util.Map;

import me.lyneira.MachinaCore.BlockVector;

import gnu.trove.map.hash.THashMap;

/**
 * A hashmap implementation for 3d coordinates. It aims to provide fast support
 * for lookup, insertion and retrieval by being specialized for keys of 3
 * integer coordinates, in the form of BlockVectors. The underlying HashMap
 * implementation is GNU Trove's THashMap.
 * 
 * @author Lyneira
 * 
 * @param <V>
 *            The values for which to create a map.
 */
public class CoordinateMap3D<V> extends THashMap<BlockVector, V> {
    /**
     * Creates a new <code>CoordinateMap3D</code> instance with the default
     * capacity and load factor.
     */
    public CoordinateMap3D() {
        super();
    }

    /**
     * Creates a new <code>CoordinateMap3D</code> instance with a prime capacity
     * equal to or greater than <tt>initialCapacity</tt> and with the default
     * load factor.
     * 
     * @param initialCapacity
     *            an <code>int</code> value
     */
    public CoordinateMap3D(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * Creates a new <code>THashMap</code> instance with a prime capacity equal
     * to or greater than <tt>initialCapacity</tt> and with the specified load
     * factor.
     * 
     * @param initialCapacity
     *            an <code>int</code> value
     * @param loadFactor
     *            a <code>float</code> value
     */
    public CoordinateMap3D(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * Creates a new <code>THashMap</code> instance which contains the key/value
     * pairs in <tt>map</tt>.
     * 
     * @param map
     *            a <code>Map</code> value
     */
    public CoordinateMap3D(Map<? extends BlockVector, ? extends V> map) {
        this(map.size());
        putAll(map);
    }

    /**
     * Creates a new <code>THashMap</code> instance which contains the key/value
     * pairs in <tt>map</tt>.
     * 
     * @param map
     *            a <code>Map</code> value
     */
    public CoordinateMap3D(CoordinateMap3D<? extends V> map) {
        this(map.size());
        putAll(map);
    }
}
