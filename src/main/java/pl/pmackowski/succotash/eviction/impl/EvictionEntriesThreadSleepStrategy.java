package pl.pmackowski.succotash.eviction.impl;

import pl.pmackowski.succotash.CacheValue;
import pl.pmackowski.succotash.eviction.CacheEvictEntries;
import pl.pmackowski.succotash.eviction.EvictionEntriesStrategy;

import java.lang.ref.WeakReference;

/**
 * Created by pmackowski on 2016-11-28.
 */
public class EvictionEntriesThreadSleepStrategy implements EvictionEntriesStrategy {

    @Override
    public void start(CacheEvictEntries cacheEvictEntries) {
        final Thread thread = new Thread(new CacheEvictor(cacheEvictEntries));
        thread.setDaemon(true);
        thread.start();
    }

    private static class CacheEvictor implements Runnable {

        /**
         * Checking every one millisecond could be an overkill.
         * Thanks to {@link CacheValue#getValue() getValue} implementation,
         * there is no need to evict cache value immediately after TTL expires.
         */
        private static final long EVICTION_INTERVAL = 1000;

        private final WeakReference<CacheEvictEntries> cacheWeakReference;

        private CacheEvictor(CacheEvictEntries cacheEvictEntries) {
            this.cacheWeakReference = new WeakReference<>(cacheEvictEntries);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    evict(cacheWeakReference.get());
                    Thread.sleep(EVICTION_INTERVAL);
                }
            } catch (InterruptedException e) {
            }
        }

        private void evict(CacheEvictEntries cache) throws InterruptedException {
            if (cache == null) {
                throw new InterruptedException();
            }
            cache.evictEntries();
        }
    }
}
