package pl.pmackowski.succotash.impl;

/**
 * Created by pmackowski on 2016-11-25.
 */
class CacheValue<V> {

    private V value;
    private long evictTimeInMillis;

    public CacheValue(V value, long timeToLiveInMillis) {
        this.value = value;
        this.evictTimeInMillis = System.currentTimeMillis() + timeToLiveInMillis;
    }

    public V getValue() {
        return value;
    }

    public boolean shouldBeEvicted() {
        return System.currentTimeMillis() >= evictTimeInMillis;
    }

}
