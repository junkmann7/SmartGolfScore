package jp.tonosama.komoki;

import android.app.Activity;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;

import java.lang.reflect.Method;

public class DisplayUtil {

    private DisplayUtil() {
        //private constructor
    }

    /**
     * Get a Real Size(Hardware Size)
     * @param activity {@link Activity}
     * @return {@link Point}
     */
    public static Point getRealSize(Activity activity) {

        Display display = activity.getWindowManager().getDefaultDisplay();
        Point point = new Point(0, 0);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Android 4.2~
            display.getRealSize(point);
            return point;

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            // Android 3.2~
            try {
                Method getRawWidth = Display.class.getMethod("getRawWidth");
                Method getRawHeight = Display.class.getMethod("getRawHeight");
                int width = (Integer) getRawWidth.invoke(display);
                int height = (Integer) getRawHeight.invoke(display);
                point.set(width, height);
                return point;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return point;
    }
}
