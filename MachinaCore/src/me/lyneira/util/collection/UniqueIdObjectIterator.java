package me.lyneira.util.collection;

import java.util.Iterator;

/**
 * An iterator over all elements of this map. If this iterator is used after the
 * map is cleared, the results are undefined.
 * 
 * @author Lyneira
 * 
 * @param <T>
 */
public class UniqueIdObjectIterator<T> implements Iterator<T> {

    private final UniqueIdObjectMap<T> map;
    private final T[] elementData;
    private int cursor = 0;
    private int lastItemId = -1;

    UniqueIdObjectIterator(UniqueIdObjectMap<T> map) {
        this.map = map;
        elementData = map.elementData;
    }

    @Override
    public boolean hasNext() {
        while (cursor < elementData.length) {
            if (elementData[cursor] != null)
                return true;
            cursor++;
        }
        return false;
    }

    @Override
    public T next() {
        lastItemId = cursor;
        return elementData[cursor++];
    }

    /**
     * Returns the id of the last item returned by this iterator.
     * 
     * @return
     */
    public int lastId() {
        return lastItemId;
    }

    @Override
    public void remove() {
        map.remove(lastItemId);
    }

}
