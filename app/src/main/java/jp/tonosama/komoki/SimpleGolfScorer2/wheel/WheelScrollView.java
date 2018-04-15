package jp.tonosama.komoki.SimpleGolfScorer2.wheel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.widget.ScrollView;

@SuppressLint("ClickableViewAccessibility")
class WheelScrollView extends ScrollView {

    private static final long ABSORB_DELAYED_MILLIS = 50;

    private final int mAbsorbHeight;

    private boolean mIsScrollEnabled = true;

    private boolean mIsDragging = false;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private Runnable mAbsorbRunnable = new Runnable() {
        @Override
        public void run() {
            if (mAbsorbHeight < 1) {
                return;
            }
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

    @NonNull
    private WheelScrollListener mListener;

    public WheelScrollView(@NonNull Context context) {
        this(context, 0, new DefaultWheelScrollListener());
    }

    public WheelScrollView(@NonNull Context context, int absorbHeight, @NonNull WheelScrollListener listener) {
        super(context);
        mAbsorbHeight = absorbHeight;
        mListener = listener;
    }

    private void setIsDragging(boolean isDragging) {
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
        mListener.onScrollChanged(top, oldTop);
        mHandler.removeCallbacks(mAbsorbRunnable);
        if (!isDragging()) {
            mHandler.postDelayed(mAbsorbRunnable, ABSORB_DELAYED_MILLIS);
        }
    }
}
