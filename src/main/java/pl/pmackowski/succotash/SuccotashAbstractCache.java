package pl.pmackowski.succotash;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by pmackowski on 2016-11-26.
 */
abstract class SuccotashAbstractCache<K, V> implements Cache<K, V> {

    private final int EXECUTOR_NUMBER_OF_THREADS = 1;

    protected ScheduledExecutorService executor;
    protected ConcurrentMap<K, CacheValue<V>> map = new ConcurrentHashMap<>();

    public SuccotashAbstractCache() {
        this.executor = Executors.newScheduledThreadPool(EXECUTOR_NUMBER_OF_THREADS, this::setDaemonThread);
    }

    private Thread setDaemonThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    }
}
