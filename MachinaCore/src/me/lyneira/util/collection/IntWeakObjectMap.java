package me.lyneira.util.collection;

import java.lang.ref.ReferenceQueue;
import java.util.Arrays;

/**
 * Class for internal use by a collection that needs to assign unique ids to its
 * elements. Also provides O(1) lookup of elements by their id. It keeps only
 * weak references to the objects in it, and ids belonging to garbage collected
 * objects will become available again during the next add operation.
 * 
 * This map expects reasonable use by its end user in order to be less
 * defensively coded: No negative integers given as ids to either get or put.
 * 
 * Additionally, since the references are weak, null values added to this map
 * will immediately count as stale.
 * 
 * @author Lyneira
 * 
 * @param <T>
 */
public class IntWeakObjectMap<T> {

    private WeakIdReference<T>[] elementData;
    private int size;
    private int firstFree;
    private final ReferenceQueue<T> queue = new ReferenceQueue<T>();

    @SuppressWarnings("unchecked")
    public IntWeakObjectMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);

        elementData = new WeakIdReference[initialCapacity];
    }

    private void ensureCapacityInternal(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - elementData.length > 0)
            grow(minCapacity);
    }

    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = elementData.length;
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity - MAX_ARRAY_SIZE > 0)
            newCapacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, newCapacity);
    }

    private static int hugeCapacity(int minCapacity) {
        if (minCapacity < 0) // overflow
            throw new OutOfMemoryError();
        return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    /**
     * Returns the element at the given id, or null if no such element exists.
     * 
     * @param id
     *            The id of the element to get.
     * @return The requested element or null.
     */
    public T get(int id) {
        if (id >= elementData.length) {
            return null;
        }
        /*
         * If the end user of this map's collection is stupid enough to pass in
         * a negative id they could not possibly get from this map, they deserve
         * the exception they get.
         */
        WeakIdReference<T> ref = elementData[id];
        if (ref == null)
            return null;

        return ref.get();
    }

    /**
     * Adds a new element to the map and returns a unique id for it.
     * 
     * @param element
     *            The element to add
     * @return The id for the added element
     */
    public int add(T element) {
        expungeStale();
        ensureCapacityInternal(size + 1);
        final int id = firstFree;
        elementData[id] = new WeakIdReference<T>(element, queue, id);
        size++;

        findNextFree();
        return id;
    }

    /**
     * Puts the element at the specified spot in the map. If there was already
     * an element here, it will be replaced.
     * 
     * @param element
     *            The element to put
     * @param id
     *            The slot to put the element at
     */
    public void put(T element, int id) {
        expungeStale();
        ensureCapacityInternal(id + 1);
        if (elementData[id] == null) {
            size++;
        }
        elementData[id] = new WeakIdReference<T>(element, queue, id);
        if (firstFree == id) {
            findNextFree();
        }
    }

    /**
     * Cleans up references from the array that have been cleared. Ids cleared
     * by this process will become available again for the next add operation,
     * starting with the lowest id found.
     */
    @SuppressWarnings("rawtypes")
    private void expungeStale() {
        for (Object r; (r = queue.poll()) != null;) {
            final int id = ((WeakIdReference) r).id;
            if (id < firstFree) {
                firstFree = id;
            }
            elementData[id] = null;
            size--;
        }
    }

    /**
     * Called when the first free slot has been filled with an element, this
     * method finds the next free element. If there is none, it will point one
     * element outside elementData's capacity.
     */
    private void findNextFree() {
        for (firstFree++; firstFree < elementData.length; firstFree++) {
            if (elementData[firstFree] == null) {
                break;
            }
        }
    }
}