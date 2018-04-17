package jp.tonosama.komoki.SimpleGolfScorer2;

import android.app.Application;

import java.lang.ref.WeakReference;

public class SGSApplication extends Application {

    private static WeakReference<SGSApplication> sInstance;

    public void onCreate() {
        super.onCreate();
        sInstance = new WeakReference<>(this);
    }

    public static Application getInstance() {
        return sInstance.get();
    }
}
