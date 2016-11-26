package pl.pmackowski.succotash;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Created by pmackowski on 2016-11-25.
 */
public class SuccotashNaiveCacheWithIterator<K, V> extends SuccotashAbstractCache<K, V>  {

    public SuccotashNaiveCacheWithIterator() {
        executor.scheduleAtFixedRate(this::evictEntries, 1 , 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        this.map.put(key, new CacheValue<>(value, timeToLiveInMillis));
    }

    @Override
    public V get(K key) {
        return map.getOrDefault(key, new CacheValue<>(null, 0)).getValue();
    }

    private void evictEntries() {
        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            K key = it.next();
            CacheValue<V> entryValue = map.get(key);
            if (entryValue.shouldBeEvicted()) {
                map.remove(key);
            }
        }
    }
}
