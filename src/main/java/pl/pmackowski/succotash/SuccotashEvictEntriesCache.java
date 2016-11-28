package pl.pmackowski.succotash;

import pl.pmackowski.succotash.eviction.CacheEvictEntries;
import pl.pmackowski.succotash.eviction.EvictionEntriesStrategy;

import java.util.Iterator;

/**
 * Created by pmackowski on 2016-11-25.
 */
public class SuccotashEvictEntriesCache<K, V> extends SuccotashAbstractCache<K, V> implements CacheEvictEntries {

    public SuccotashEvictEntriesCache(EvictionEntriesStrategy evictionEntriesStrategy) {
        evictionEntriesStrategy.start(this);
    }

    @Override
    public void put(K key, V value, long timeToLiveInMillis) {
        this.map.put(key, new CacheValue<>(value, timeToLiveInMillis));
    }

    @Override
    public void evictEntries() {
        Iterator<K> it = map.keySet().iterator();
        while (it.hasNext()) {
            K key = it.next();
            synchronized (map) {
                CacheValue<V> entryValue = map.get(key);
                if (entryValue.shouldBeEvicted()) {
                    map.remove(key);
                }
            }
        }
    }
}
