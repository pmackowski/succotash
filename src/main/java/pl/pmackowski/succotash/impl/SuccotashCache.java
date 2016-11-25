package pl.pmackowski.succotash.impl;

import pl.pmackowski.succotash.Cache;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by pmackowski on 2016-11-25.
 */
public class SuccotashCache<K, V> implements Cache<K, V> {

    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    private ConcurrentMap<K, CacheValue<V>> map = new ConcurrentHashMap<>();

    public SuccotashCache() {
        executor.scheduleAtFixedRate(this::evictEntries, 1 , 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        this.map.put(key, new CacheValue<V>(value, timeToLiveInMillis));
    }

    @Override
    public V get(K key) {
        return map.getOrDefault(key, new CacheValue<V>(null, 0)).getValue();
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
