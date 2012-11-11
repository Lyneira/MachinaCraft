package me.lyneira.util.collection;

import java.util.Arrays;

/**
 * Class for internal use by a collection that needs to assign unique ids to its
 * elements. Also provides O(1) lookup of elements by their id.
 * 
 * This map expects reasonable use by its end user in order to be less
 * defensively coded: No negative integers given as ids to get, put or remove.
 * 
 * This map does not permit null values.
 * 
 * @author Lyneira
 * 
 * @param <T>
 */
public class UniqueIdObjectMap<T> {

    private T[] elementData;
    private int size;
    private int firstFree;

    /**
     * Constructs a new map with space for at least the specified number of
     * elements.
     * 
     * @param initialCapacity
     *            The initial capacity of the map
     */
    @SuppressWarnings("unchecked")
    public UniqueIdObjectMap(int initialCapacity) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);

        elementData = (T[]) new Object[initialCapacity];
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
     * Returns this map's size.
     * 
     * @return The size of the map
     */
    public int size() {
        return size;
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
         * If the end user of this map's collection passes in a negative id they
         * could not possibly get from this map, they deserve the exception they
         * get.
         */
        return elementData[id];
    }

    /**
     * Adds a new element to the map and returns a unique id for it.
     * 
     * @param element
     *            The element to add
     * @return The id for the added element
     */
    public int add(T element) {
        if (element == null) {
            throw new NullPointerException("Cannot add null elements to UniqueIdObjectMap!");
        }
        ensureCapacityInternal(size + 1);
        final int id = firstFree;
        elementData[id] = element;
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
        if (element == null) {
            throw new NullPointerException("Cannot add null elements to UniqueIdObjectMap!");
        }
        ensureCapacityInternal(id + 1);
        if (elementData[id] == null) {
            size++;
        }
        elementData[id] = element;
        if (firstFree == id) {
            findNextFree();
        }
    }

    public void remove(int id) {
        if (id >= elementData.length) {
            return;
        }
        if (elementData[id] != null) {
            size--;
            elementData[id] = null;
        }
        if (id < firstFree) {
            firstFree = id;
        }
    }

    @SuppressWarnings("unchecked")
    public void clear() {
        elementData = (T[]) new Object[elementData.length];
        size = 0;
        firstFree = 0;
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
