package utils;

/**
 *
 * @param <T>
 */
public class Set<T> {
    private final List<List<T>> hashTable;
    public int totalBuckets = 0;
    public int totalElements = 0;

    // TODO: rehash and parameterless constructor
    /**
     * 
     * @param capacity Must be > 0.
     */
    public Set(int capacity) {
        assert(capacity > 0);
        hashTable = new List<>(capacity);
        for (int i = 0; i < capacity; ++i) {
            hashTable.add(new List<T>());
        }
    }

    /**
     * Clear the map.
     */
    public void clear() {
        for (int i = 0; i < hashTable.size(); ++i) {
//            hashMap.set(i, new List<Pair<K, V>>());
            hashTable.get(i).clear();     // for some reason HERE this is faster than creating a new object?
        }
    }

    private int calculateIndex(T key) {
        int i = key.hashCode() % hashTable.size();
        if (i < 0) {
            i += hashTable.size();
        }
        return i;
    }
    
    /**
     * 
     * @param element Must not be null.
     */
    public void put(T element) {
        int i = calculateIndex(element);
        List<T> bucket = hashTable.get(i);
        if (bucket.isEmpty()) {
            ++totalBuckets;
        }
        for (i = 0; i < bucket.size(); ++i) {
            if (bucket.get(i).equals(element)) {
                return;
            }
        }
        ++totalElements;
        bucket.add(element);
    }

    public double loadFactor() {
        return 1.0 * totalElements / totalBuckets;
    }

    /**
     * 
     * @param element Must not be null.
     * @return 
     */
    public boolean contains(T element) {
        int i = calculateIndex(element);
        List<T> bucket = hashTable.get(i);
        for (T e : bucket) {
            if (e.equals(element)) {
                return true;
            }
        }
        return false;
    }
}
