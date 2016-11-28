package pl.pmackowski.succotash.eviction;

import pl.pmackowski.succotash.CacheValue;

/**
 * Created by pmackowski on 2016-11-29.
 */
public class EvictionConstants {

    /**
     * Checking every one millisecond could be an overkill.
     * Thanks to {@link CacheValue#getValue() getValue} implementation,
     * there is no need to evict cache value immediately after TTL expires,
     * unless there are many "put" operations on cache with big data and short TTL.
     */
    public static final long EVICTION_INTERVAL = 20;

    private EvictionConstants() {
    }
}
