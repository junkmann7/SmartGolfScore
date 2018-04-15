package jp.tonosama.komoki.SimpleGolfScorer2;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("WeakerAccess")
public class WorkerThreadPool {

    private static final int POOL_SIZE = 10;

    private static final int MAX_POOL_SIZE = 50;

    private static final int TIMEOUT = 30;

    @Nullable
    private static ThreadPoolExecutor sThreadPoolExecutor;

    // This class is utility class
    private WorkerThreadPool() {
    }

    /**
     * Returns the worker ThreadPoolExecutor of this application.
     *
     * @return the worker of this application
     */
    @SuppressWarnings("WeakerAccess")
    @NonNull
    public synchronized static ThreadPoolExecutor getWorkerThreadPool() {
        if (sThreadPoolExecutor == null) {
            sThreadPoolExecutor = new ThreadPoolExecutor(POOL_SIZE, MAX_POOL_SIZE, TIMEOUT,
                    TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(POOL_SIZE));
        }
        return sThreadPoolExecutor;
    }

    @SuppressWarnings("WeakerAccess")
    public synchronized static void execute(@NonNull Runnable task) {
        getWorkerThreadPool().execute(task);
    }
}
