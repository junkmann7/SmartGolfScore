package jp.tonosama.komoki.SimpleGolfScorer2.wheel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;

public class WheelView extends RelativeLayout implements WheelScrollListener {

    private static final String TAG = WheelView.class.getSimpleName();

    private static final int MIN_VALUE = 0;

    private static final int MAX_VALUE = 20;

    private int mCurrentSlot;

    private boolean mIsLocked;

    @NonNull
    private OnWheelChangedListener mListener;

    @NonNull
    private WheelScrollView mScrollView;

    private Drawable mTopShadow;

    private Drawable mBottomShadow;

    private Drawable mCenterDrawable;

    public WheelView(@NonNull Context context) {
        this(context, new DefaultOnWheelChangedListener());
    }

    public WheelView(Context context, @SuppressWarnings("unused") AttributeSet attrs) {
        this(context, new DefaultOnWheelChangedListener());
    }

    public WheelView(@NonNull Context context, @NonNull OnWheelChangedListener listener) {
        super(context);

        mListener = listener;
        initShadow(context);
        setBackgroundResource(R.drawable.wheel_bg);

        View containerView = createCounterLayout(context, MIN_VALUE, MAX_VALUE);
        mScrollView = createScrollView(containerView);

        addView(mScrollView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Top, Bottom shadow
        int shadowHeight = getTextViewHeight();
        mTopShadow.setBounds(0, 0, getWidth(), shadowHeight);
        mTopShadow.draw(canvas);
        mBottomShadow.setBounds(0, getHeight() - shadowHeight, getWidth(), getHeight());
        mBottomShadow.draw(canvas);
        // Center shadow
        int center = getHeight() / 2;
        int offset = shadowHeight / 2;
        mCenterDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        mCenterDrawable.draw(canvas);
    }

    private WheelScrollView createScrollView(@NonNull View containerView) {
        WheelScrollView scrollView = new WheelScrollView(getContext(), getTextViewHeight(), this);
        scrollView.setVerticalScrollBarEnabled(false);
        scrollView.addView(containerView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        return scrollView;
    }

    private void initShadow(@NonNull Context context) {
        mTopShadow = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                WheelConfig.SHADOWS_COLORS);
        mBottomShadow = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                WheelConfig.SHADOWS_COLORS);
        mCenterDrawable = context.getResources().getDrawable(R.drawable.wheel_val);
    }

    private View createCounterLayout(@NonNull Context context, int minValue, int maxValue) {

        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        for (int i = minValue; i < maxValue + 1; i++) {
            TextView tv = new TextView(context);
            tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            tv.setText(String.valueOf(i));
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getTextSize());
            tv.setPaintFlags(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG
                    | Paint.DITHER_FLAG);
            tv.setShadowLayer(getTextSize() / 10f, 0f, 0f, WheelConfig.TEXT_SHADOW_COLOR);
            tv.setTag(String.valueOf(i));
            linearLayout.addView(tv, LayoutParams.MATCH_PARENT, getTextViewHeight());
        }
        return linearLayout;
    }

    private int getTextSize() {
        return (int) getContext().getResources().getDimension(WheelConfig.TEXT_SIZE_RES_ID);
    }

    private int getTextViewHeight() {
        return (int) getContext().getResources().getDimension(WheelConfig.TEXT_VIEW_HEIGHT_RES_ID);
    }

    private int calcCurrentItem(int scrollPosY) {
        int valueHeight = getTextViewHeight();
        int scrollPos = scrollPosY + valueHeight / 2;
        return scrollPos / valueHeight;
    }

    ////////////////////////////////
    // Public method
    ////////////////////////////////

    public void setOnWheelChangedListener(OnWheelChangedListener listener) {
        mListener = listener;
    }

    public void setCurrentItem(final int currentItem) {
        mCurrentSlot = currentItem;
        if (0 < mScrollView.getHeight()) {
            updatePositionAndTextColor();
        } else {
            mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updatePositionAndTextColor();
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    public int getCurrentItem() {
        return calcCurrentItem(mScrollView.getScrollY());
    }

    public void setIsWheelLocked(final boolean isLocked) {
        mIsLocked = isLocked;
        if (0 < mScrollView.getHeight()) {
            updatePositionAndTextColor();
        } else {
            mScrollView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver
                    .OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    updatePositionAndTextColor();
                    mScrollView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            });
        }
    }

    private void updatePositionAndTextColor() {
        int currentItemColor;
        int otherItemColor;
        if (mIsLocked) {
            currentItemColor = WheelConfig.CURRENT_TEXT_COLOR_LOCKED;
            otherItemColor = WheelConfig.OTHER_TEXT_COLOR_LOCKED;
        } else {
            currentItemColor = WheelConfig.CURRENT_TEXT_COLOR_UNLOCK;
            otherItemColor = WheelConfig.OTHER_TEXT_COLOR_UNLOCK;
        }
        for (int i = MIN_VALUE; i < MAX_VALUE + 1; i++) {
            TextView tv = (TextView) mScrollView.findViewWithTag(String.valueOf(i));
            if (i == mCurrentSlot) {
                tv.setTextColor(currentItemColor);
            } else {
                tv.setTextColor(otherItemColor);
            }
        }
        mScrollView.scrollTo(0, getTextViewHeight() * mCurrentSlot);
        mScrollView.setIsScrollEnabled(!mIsLocked);
    }

    ////////////////////

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        DevLog.d(TAG, "onMeasure with:" + widthMeasureSpec + " height:" + heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int width = right - left;
        int height = bottom - top;
        DevLog.d(TAG, "onLayout changed:" + changed + " l:" + left + " t:" + top +
                " r:" + right + " b:" + bottom);
        DevLog.d(TAG, "onLayout width:" + width + " height:" + height);

        int padding = height / 2 - getTextViewHeight() / 2;
        if (0 < mScrollView.getChildCount()) {
            mScrollView.getChildAt(0).setPadding(0, padding, 0, padding);
        }
    }

    @Override
    public void onScrollChanged(int scrollY, int oldScrollY) {
        DevLog.d(TAG, "onScrollChanged  scrollY:" + scrollY + " oldScrollY:" + oldScrollY);

        int newValue = calcCurrentItem(scrollY);
        int oldValue = mCurrentSlot;
        if (oldValue != newValue) {
            mCurrentSlot = newValue;
            mListener.onChanged(oldValue, newValue);
            DevLog.d(TAG, "onScrollChanged  oldValue:" + oldValue + " newValue:" + newValue);
        }
    }

    ////////////////////
}
