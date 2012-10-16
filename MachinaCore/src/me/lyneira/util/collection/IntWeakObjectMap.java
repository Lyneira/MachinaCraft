package me.lyneira.util.collection;

import java.lang.ref.ReferenceQueue;
import java.util.Arrays;

/**
 * Class for internal use by a collection that needs to assign unique ids to its
 * elements. Also provides O(1) lookup of elements by their id. It keeps only
 * weak references to the objects in it, and ids belonging to garbage collected
 * objects will become available again during the next add operation.
 * 
 * @author Lyneira
 * 
 * @param <T>
 */
public class IntWeakObjectMap<T> {

    private WeakIdReference<T>[] elementData;
    private int capacity;
    private int size;
    private int firstFree;
    private final ReferenceQueue<T> queue = new ReferenceQueue<T>();

    @SuppressWarnings("unchecked")
    public IntWeakObjectMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);

        capacity = initialCapacity;
        elementData = new WeakIdReference[capacity];
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
        capacity = oldCapacity + (oldCapacity >> 1);
        if (capacity - minCapacity < 0)
            capacity = minCapacity;
        if (capacity - MAX_ARRAY_SIZE > 0)
            capacity = hugeCapacity(minCapacity);
        // minCapacity is usually close to size, so this is a win:
        elementData = Arrays.copyOf(elementData, capacity);
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
        if (id >= capacity) {
            return null;
        }
        WeakIdReference<T> ref = elementData[id];
        if (ref == null)
            return null;

        return ref.get();
    }

    /**
     * Adds a new element to the map and returns a unique id for it.
     * 
     * @param element The element to add
     * @return The id for the added element
     */
    public int add(T element) {
        expungeStale();
        ensureCapacityInternal(size + 1);
        final int id = firstFree;
        elementData[id] = new WeakIdReference<T>(element, queue, id);
        size++;
        // Find the next free spot.
        for (firstFree++; firstFree < capacity; firstFree++) {
            if (elementData[firstFree] == null) {
                break;
            }
        }
        return id;
    }

    /**
     * Cleans up references from the array that have been cleared. Ids cleared
     * by this process will become available again for the next add operation,
     * starting with the lowest id found.
     */
    @SuppressWarnings("rawtypes")
    private void expungeStale() {
        for (WeakIdReference r; (r = (WeakIdReference) queue.poll()) != null;) {
            final int id = r.id;
            if (id < firstFree) {
                firstFree = id;
            }
            elementData[id] = null;
            size--;
        }
    }
}
