package pl.pmackowski.succotash;

/**
 * Created by pmackowski on 2016-11-25.
 */
public interface Cache<K, V> {

    void put(K key, V value, long timeToLiveInMillis);

    V get(K key);

}
