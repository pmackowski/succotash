package pl.pmackowski.succotash.eviction;

import pl.pmackowski.succotash.CacheValue;

/**
 * Created by pmackowski on 2016-11-28.
 */
public interface EvictionEntryStrategy<K, V> {

    void start(CacheEvictEntry<K, V> cacheEvictEntry);

    void evictAfter(K key, CacheValue<V> cacheValue, long timeToLiveInMillis);
}
