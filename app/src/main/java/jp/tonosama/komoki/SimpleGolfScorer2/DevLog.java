package jp.tonosama.komoki.SimpleGolfScorer2;

import android.support.annotation.NonNull;
import android.util.Log;

public class DevLog {

    private static boolean sEnable = false;

    private DevLog() {
        //private constructor
    }

    public static void setEnable(boolean enable) {
        sEnable = enable;
    }

    public static void d(@NonNull String tag, @NonNull String msg) {
        if (sEnable) {
            Log.d(tag, msg);
        }
    }
}
