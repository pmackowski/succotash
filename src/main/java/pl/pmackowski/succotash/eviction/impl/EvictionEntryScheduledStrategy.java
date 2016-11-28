package pl.pmackowski.succotash.eviction.impl;

import pl.pmackowski.succotash.CacheValue;
import pl.pmackowski.succotash.eviction.CacheEvictEntry;
import pl.pmackowski.succotash.eviction.EvictionEntryStrategy;
import pl.pmackowski.succotash.internal.ThreadUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by pmackowski on 2016-11-28.
 */
public class EvictionEntryScheduledStrategy<K, V> implements EvictionEntryStrategy<K, V> {

    private static final int EXECUTOR_NUMBER_OF_THREADS = 1;
    private static final long CHECK_CACHE_NO_LONGER_USED_INTERVAL = 1000;

    private ScheduledExecutorService executor;
    private WeakReference<CacheEvictEntry<K, V>> cacheEvictEntryWeakReference;

    @Override
    public void start(CacheEvictEntry<K, V> cacheEvictEntry) {
        this.executor = Executors.newScheduledThreadPool(EXECUTOR_NUMBER_OF_THREADS, ThreadUtils::getDaemonThread);
        this.cacheEvictEntryWeakReference = new WeakReference<>(cacheEvictEntry);
        this.shutdownExecutorIfCacheIsNoLongerUsed();
    }

    @Override
    public void evictAfter(K key, CacheValue<V> cacheValue, long timeToLiveInMillis) {
        CacheEvictEntry<K, V> cacheEvictEntry = cacheEvictEntryWeakReference.get();
        if (cacheEvictEntry != null) {
            executor.schedule(() -> cacheEvictEntry.evictEntry(key, cacheValue), timeToLiveInMillis, TimeUnit.MILLISECONDS);
        }
    }

    private void shutdownExecutorIfCacheIsNoLongerUsed() {
        this.executor.scheduleAtFixedRate(() -> {
            if (cacheEvictEntryWeakReference.get() == null) {
                executor.shutdownNow();
            }
        }, CHECK_CACHE_NO_LONGER_USED_INTERVAL , CHECK_CACHE_NO_LONGER_USED_INTERVAL, TimeUnit.MILLISECONDS);
    }

}
