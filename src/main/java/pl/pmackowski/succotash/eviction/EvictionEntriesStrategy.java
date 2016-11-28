package pl.pmackowski.succotash.eviction;

/**
 * Created by pmackowski on 2016-11-28.
 */
public interface EvictionEntriesStrategy {

    void start(CacheEvictEntries cacheEvictEntries);

}
