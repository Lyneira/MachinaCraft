package me.lyneira.util.collection;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;

/**
 * Weak reference with an id for use by IntWeakObjectMap.
 * 
 * @author Lyneira
 *
 * @param <T>
 */
class WeakIdReference<T> extends WeakReference<T> {
    final int id;

    WeakIdReference(T referent, ReferenceQueue<T> q, int id) {
        super(referent, q);
        this.id = id;
    }
}