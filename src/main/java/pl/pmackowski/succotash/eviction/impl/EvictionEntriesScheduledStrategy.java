package pl.pmackowski.succotash.eviction.impl;

import pl.pmackowski.succotash.CacheValue;
import pl.pmackowski.succotash.eviction.CacheEvictEntries;
import pl.pmackowski.succotash.eviction.EvictionEntriesStrategy;
import pl.pmackowski.succotash.internal.ThreadUtils;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by pmackowski on 2016-11-28.
 */
public class EvictionEntriesScheduledStrategy implements EvictionEntriesStrategy {

    private static final int EXECUTOR_NUMBER_OF_THREADS = 1;
    /**
     * Checking every one millisecond could be an overkill.
     * Thanks to {@link CacheValue#getValue() getValue} implementation,
     * there is no need to evict cache value immediately after TTL expires.
     */
    private static final long EVICTION_INTERVAL = 1000;

    @Override
    public void start(CacheEvictEntries cacheEvictEntries) {
        WeakReference<CacheEvictEntries> cacheWeakReference = new WeakReference<>(cacheEvictEntries);
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(EXECUTOR_NUMBER_OF_THREADS, ThreadUtils::getDaemonThread);
        CacheEvictEntries cacheEvictEntriesFromWeakReference = cacheWeakReference.get();
        executor.scheduleAtFixedRate(() -> {
            executor.shutdownNow();
            if (cacheEvictEntriesFromWeakReference != null) {
                cacheEvictEntriesFromWeakReference.evictEntries();
            } else {
                executor.shutdownNow();
            }
        }, EVICTION_INTERVAL , EVICTION_INTERVAL, TimeUnit.MILLISECONDS);
    }

}
