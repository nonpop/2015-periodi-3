package utils;

/**
 *
 * @param <K>
 * @param <V>
 */
public class Map<K, V> {
    private final List<List<Pair<K, V>>> hashMap;
    private final int capacity;

    public Map(int capacity) {
        this.capacity = capacity;
        hashMap = new List<>(capacity);
        for (int i = 0; i < capacity; ++i) {
            hashMap.add(new List<Pair<K, V>>());
        }
    }

    /**
     * Clear the map.
     */
    public void clear() {
        for (int i = 0; i < capacity; ++i) {
            hashMap.set(i, new List<Pair<K, V>>());
        }
    }

    private int calculateIndex(K key) {
        int i = key.hashCode() % capacity;
        if (i < 0) {
            i += hashMap.size();
        }
        return i;
    }
    
    /**
     * 
     * @param key Must not be null.
     * @param value 
     */
    public void put(K key, V value) {
        int i = calculateIndex(key);
        List<Pair<K, V>> kv = hashMap.get(i);
        kv.add(new Pair<>(key, value)); // NOTE: if the delete operation is implemented,
                                        // it must go through the whole list since
                                        // we might have duplicates there.
    }

    /**
     * 
     * @param key Must not be null.
     * @return 
     */
    public V get(K key) {
        int i = calculateIndex(key);
        List<Pair<K, V>> kv = hashMap.get(i);
        for (Pair<K, V> p : kv) {
            if (p.first.equals(key)) {
                return p.second;
            }
        }
        return null;
    }


    /**
     * 
     * @param key Must not be null.
     * @return 
     */
    public boolean containsKey(K key) {
        int i = calculateIndex(key);
        List<Pair<K, V>> kv = hashMap.get(i);
        for (Pair<K, V> p : kv) {
            if (p.first.equals(key)) {
                return true;
            }
        }
        return false;
    }
}
