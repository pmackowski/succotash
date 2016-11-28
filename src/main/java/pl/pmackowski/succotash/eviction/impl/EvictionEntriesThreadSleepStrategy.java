package pl.pmackowski.succotash.eviction.impl;

import pl.pmackowski.succotash.eviction.CacheEvictEntries;
import pl.pmackowski.succotash.eviction.EvictionEntriesStrategy;

import java.lang.ref.WeakReference;

import static pl.pmackowski.succotash.eviction.EvictionConstants.EVICTION_INTERVAL;

/**
 * Created by pmackowski on 2016-11-28.
 */
public class EvictionEntriesThreadSleepStrategy implements EvictionEntriesStrategy {

    @Override
    public void start(CacheEvictEntries cacheEvictEntries) {
        final Thread thread = new Thread(new CacheEviction(cacheEvictEntries));
        thread.setDaemon(true);
        thread.start();
    }

    private static class CacheEviction implements Runnable {

        private final WeakReference<CacheEvictEntries> cacheWeakReference;

        private CacheEviction(CacheEvictEntries cacheEvictEntries) {
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
