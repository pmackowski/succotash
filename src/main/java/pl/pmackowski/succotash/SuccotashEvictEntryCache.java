package pl.pmackowski.succotash;

import pl.pmackowski.succotash.eviction.CacheEvictEntry;
import pl.pmackowski.succotash.eviction.EvictionEntryStrategy;

/**
 * Created by pmackowski on 2016-11-26.
 */
public class SuccotashEvictEntryCache<K, V> extends SuccotashAbstractCache<K, V> implements CacheEvictEntry<K, V> {

    private EvictionEntryStrategy<K, V> evictionEntryStrategy;

    public SuccotashEvictEntryCache(EvictionEntryStrategy<K, V> evictionEntryStrategy) {
        this.evictionEntryStrategy = evictionEntryStrategy;
        evictionEntryStrategy.start(this);
    }

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        CacheValue<V> newValue = new CacheValue<>(value, timeToLiveInMillis);
        this.map.put(key, newValue);
        evictionEntryStrategy.evictAfter(key, newValue, timeToLiveInMillis);
    }

    @Override
    public void evictEntry(K key, CacheValue<V> cacheValue) {
        synchronized (map) {
            CacheValue<V> currentCacheValue = map.get(key);
            if (currentCacheValue.equals(cacheValue)) {
                map.remove(key);
            }
        }
    }
}