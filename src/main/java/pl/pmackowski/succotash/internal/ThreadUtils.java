package pl.pmackowski.succotash.internal;

import java.util.concurrent.Executors;

/**
 * Created by pmackowski on 2016-11-28.
 */
public class ThreadUtils {

    public static Thread getDaemonThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setDaemon(true);
        return thread;
    }

}
