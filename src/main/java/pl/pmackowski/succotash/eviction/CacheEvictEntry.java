package pl.pmackowski.succotash.eviction;

import pl.pmackowski.succotash.CacheValue;

/**
 * Created by pmackowski on 2016-11-28.
 */
public interface CacheEvictEntry<K, V> {

    void evictEntry(K key, CacheValue<V> cacheValue);

}
