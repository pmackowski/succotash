package pl.pmackowski.succotash;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by pmackowski on 2016-11-26.
 */
abstract class SuccotashAbstractCache<K, V> implements Cache<K, V> {

    protected ConcurrentMap<K, CacheValue<V>> map = new ConcurrentHashMap<>();

    @Override
    public V get(K key) {
        CacheValue<V> cacheValue = map.get(key);
        return cacheValue != null ? cacheValue.getValue() : null;
    }

}
