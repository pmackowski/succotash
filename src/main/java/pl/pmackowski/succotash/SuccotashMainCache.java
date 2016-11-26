package pl.pmackowski.succotash;

import java.util.concurrent.TimeUnit;

/**
 * Created by pmackowski on 2016-11-26.
 */
public class SuccotashMainCache<K, V> extends SuccotashAbstractCache<K, V> {

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        CacheValue<V> newValue = new CacheValue<>(value, timeToLiveInMillis);
        this.map.put(key, newValue);
        executor.schedule(() -> evictEntry(key, newValue), timeToLiveInMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public V get(K key) {
        return map.getOrDefault(key, new CacheValue<>(null, 0)).getValue();
    }

    private void evictEntry(K key, CacheValue<V> cacheValue) {
        CacheValue<V> currentCacheValue = map.get(key);
        if (currentCacheValue.equals(cacheValue)) {
            map.remove(key);
        }
    }
}