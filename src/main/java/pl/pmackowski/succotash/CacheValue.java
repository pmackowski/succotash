package pl.pmackowski.succotash;

import java.util.Objects;

/**
 * Created by pmackowski on 2016-11-25.
 */
public class CacheValue<V> {

    private final V value;
    private final long evictTimeInMillis;

    public CacheValue(V value, long timeToLiveInMillis) {
        this.value = value;
        this.evictTimeInMillis = System.currentTimeMillis() + timeToLiveInMillis;
    }

    public V getValue() {
        if (shouldBeEvicted()) {
            return null;
        }
        return value;
    }

    public boolean shouldBeEvicted() {
        return System.currentTimeMillis() >= evictTimeInMillis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CacheValue<?> that = (CacheValue<?>) o;
        return Objects.equals(evictTimeInMillis, that.evictTimeInMillis) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, evictTimeInMillis);
    }
}
