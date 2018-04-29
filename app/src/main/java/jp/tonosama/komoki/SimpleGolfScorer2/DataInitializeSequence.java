package jp.tonosama.komoki.SimpleGolfScorer2;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

class DataInitializeSequence {

    interface Callback {

        void onComplete();
    }

    static void start(@NonNull final Callback callback) {

        if (SaveDataPref.isInitialized()) {
            notifyComplete(callback);
            return;
        }
        WorkerThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                SaveDataPref.initialize();
                notifyComplete(callback);
            }
        });
    }

    private static void notifyComplete(@NonNull final Callback callback) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                callback.onComplete();
            }
        });
    }
}
