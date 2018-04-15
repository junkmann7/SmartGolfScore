package jp.tonosama.komoki.SimpleGolfScorer2.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.widget.ScrollView;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;

class WheelScrollView extends ScrollView {

    private static final String TAG = WheelScrollView.class.getSimpleName();

    private static final long ABSORB_DELAYED_MILLIS = 50;

    private boolean mIsScrollEnabled = true;

    private int mAbsorbHeight;

    private boolean mIsDragging = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mAbsorbRunnable = new Runnable() {
        @Override
        public void run() {
            final int scrollY = getScrollY();
            int prevAbsorbY = (scrollY / mAbsorbHeight) * mAbsorbHeight;
            int nextAbsorbY = (scrollY / mAbsorbHeight + 1) * mAbsorbHeight;
            if (Math.abs(scrollY - prevAbsorbY) < Math.abs(scrollY - nextAbsorbY)) {
                smoothScrollTo(0, prevAbsorbY);
            } else {
                smoothScrollTo(0, nextAbsorbY);
            }
        }
    };

    interface ScrollListener {

        void onScrollChanged(int left, int top, int oldLeft, int oldTop);
    }

    @Nullable
    private ScrollListener mListener;

    public WheelScrollView(@NonNull Context context) {
        super(context);
    }

    public WheelScrollView(@NonNull Context context, int absorbHeight, @NonNull ScrollListener listener) {
        super(context);
        mAbsorbHeight = absorbHeight;
        mListener = listener;
    }

    private void setIsDragging(boolean isDragging) {
        DevLog.d(TAG, "setIsDragging " + isDragging);
        mIsDragging = isDragging;
        if (!isDragging) {
            mHandler.postDelayed(mAbsorbRunnable, ABSORB_DELAYED_MILLIS);
        }
    }

    private boolean isDragging() {
        return mIsDragging;
    }

    void setIsScrollEnabled(boolean enable) {
        mIsScrollEnabled = enable;
    }

    ////////////////////////

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!mIsScrollEnabled) {
            return true;
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                setIsDragging(true);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                setIsDragging(false);
                break;
            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    protected void onScrollChanged(int left, int top, int oldLeft, int oldTop) {
        super.onScrollChanged(left, top, oldLeft, oldTop);
        if (mListener != null) {
            mListener.onScrollChanged(left, top, oldLeft, oldTop);
        }
        mHandler.removeCallbacks(mAbsorbRunnable);
        if (!isDragging()) {
            mHandler.postDelayed(mAbsorbRunnable, ABSORB_DELAYED_MILLIS);
        }
    }
}
