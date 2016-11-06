package jp.tonosama.komoki.wheel.widget;

import java.util.LinkedList;
import java.util.List;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.text.Layout;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author Komoki
 */
public class WheelView extends View {

    /**  */
    private static final String TAG = WheelView.class.getSimpleName();
    /**  */
    private static final boolean DEBUG = false;
    /**  */
    private boolean mIsWheelLocked = false;
    /**  */
    private float mDensity;

    /** Wheel Values */
    private WheelAdapter mAdapter = null;
    /**  */
    private int mCurrentItem = 0;

    /**  */
    private int mItemsWidth = 0;
    /**  */
    private int mLabelWidth = 0;

    /** Count of visible items */
    private int mVisibleItems = WheelViewConfig.DEF_VISIBLE_ITEMS;

    /**  */
    private TextPaint mItemsPaint;
    /**  */
    private TextPaint mValuePaint;

    /**  */
    private StaticLayout mItemsLayout;
    /**  */
    private StaticLayout mLabelLayout;
    /**  */
    private StaticLayout mValueLayout;

    /**  */
    private String mLabel;
    /**  */
    private Drawable mCenterDrawable;

    /**  */
    private GradientDrawable mTopShadow;
    /**  */
    private GradientDrawable mBottomShadow;

    /** Last touch Y position */
    private float mLastYTouch;

    /**  */
    private boolean mIsScrollingPerformed;
    /**  */
    private float mScrollingOffset;

    /**  */
    private boolean mIsCyclic = false;

    /**  */
    private List<OnWheelChangedListener> mChangingListeners = //
    new LinkedList<OnWheelChangedListener>();
    /**  */
    private List<OnWheelScrollListener> mScrollingListeners = //
    new LinkedList<OnWheelScrollListener>();

    /**  */
    private Context mContext;

    /**
     * @param context
     * @param attrs
     */
    public WheelView(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        setFocusable(true);

        int width, height;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        if (displayMetrics.widthPixels < displayMetrics.heightPixels) {
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        } else {
            width = displayMetrics.heightPixels;
            height = displayMetrics.widthPixels;
        }
        mDensity = displayMetrics.density / 2;

        if (DEBUG) {
            float aspect = (float) height / (float) width;
            Log.d(TAG, "width = " + width + ", height = " + height + ", aspect = " + aspect + "");
            Log.d(TAG, "mDensity = " + mDensity + ", displayMetrics.density = "
                    + displayMetrics.density);
        }
    }

    /**
     * Gets wheel adapter
     * 
     * @return the adapter
     */
    public WheelAdapter getAdapter() {
        return mAdapter;
    }

    /**
     * Sets whell adapter
     * 
     * @param adapter the new wheel adapter
     */
    public void setAdapter(final WheelAdapter adapter) {
        this.mAdapter = adapter;
        invalidateLayouts();
        invalidate();
    }

    /**
     * Gets count of visible items
     *
     * @return the count of visible items
     */
    public int getVisibleItems() {
        return mVisibleItems;
    }

    /**
     * Sets count of visible items
     *
     * @param count the new count
     */
    public void setVisibleItems(final int count) {
        mVisibleItems = count;
        invalidate();
    }

    /**
     * Gets label
     *
     * @return the label
     */
    public String getLabel() {
        return mLabel;
    }

    /**
     * Sets label
     *
     * @param newLabel the label to set
     */
    public void setLabel(final String newLabel) {
        if (mLabel == null || !mLabel.equals(newLabel)) {
            mLabel = newLabel;
            mLabelLayout = null;
            invalidate();
        }
    }

    /**
     * Adds wheel changing listener
     * 
     * @param listener the listener
     */
    public void addChangingListener(final OnWheelChangedListener listener) {
        mChangingListeners.add(listener);
    }

    /**
     * Removes wheel changing listener
     * 
     * @param listener the listener
     */
    public void removeChangingListener(final OnWheelChangedListener listener) {
        mChangingListeners.remove(listener);
    }

    /**
     * Notifies changing listeners
     * 
     * @param oldValue the old wheel value
     * @param newValue the new wheel value
     */
    protected void notifyChangingListeners(final int oldValue, final int newValue) {
        for (OnWheelChangedListener listener : mChangingListeners) {
            listener.onChanged(this, oldValue, newValue);
        }
    }

    /**
     * Adds wheel scrolling listener
     * 
     * @param listener the listener
     */
    public void addScrollingListener(final OnWheelScrollListener listener) {
        mScrollingListeners.add(listener);
    }

    /**
     * Removes wheel scrolling listener
     * 
     * @param listener the listener
     */
    public void removeScrollingListener(final OnWheelScrollListener listener) {
        mScrollingListeners.remove(listener);
    }

    /**
     * Notifies listeners about starting scrolling
     */
    protected void notifyScrollingListenersAboutStart() {
        for (OnWheelScrollListener listener : mScrollingListeners) {
            listener.onScrollingStarted(this);
        }
    }

    /**
     * Notifies listeners about ending scrolling
     */
    protected void notifyScrollingListenersAboutEnd() {
        for (OnWheelScrollListener listener : mScrollingListeners) {
            listener.onScrollingFinished(this);
        }
    }

    /**
     * Gets current value
     *
     * @return the current value
     */
    public int getCurrentItem() {
        return mCurrentItem;
    }

    /**
     * Sets the current item
     *
     * @param index the item index
     */
    public void setCurrentItem(final int index) {
        if (index != mCurrentItem) {
            invalidateLayouts();

            int old = mCurrentItem;
            mCurrentItem = index;

            notifyChangingListeners(old, mCurrentItem);

            invalidate();
        }
    }

    /**
     * Tests if wheel is cyclic. That means before the 1st item there is shown the last one
     * 
     * @return true if wheel is cyclic
     */
    public boolean isCyclic() {
        return mIsCyclic;
    }

    /**
     * Set wheel cyclic flag
     * 
     * @param isCyclic the flag to set
     */
    public void setCyclic(final boolean isCyclic) {
        this.mIsCyclic = isCyclic;

        invalidate();
        invalidateLayouts();
    }

    /**
     * Invalidates layouts
     */
    private void invalidateLayouts() {
        mItemsLayout = null;
        mValueLayout = null;
        mScrollingOffset = 0;
    }

    /**
     * Initializes resources
     */
    private void initResourcesIfNecessary() {
        if (mItemsPaint == null) {
            mItemsPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG);
            //itemsPaint.density = getResources().getDisplayMetrics().density;
            mItemsPaint.setTextSize(mContext.getResources().getDimension(
                    R.dimen.score_wheet_text_size));
        }

        if (mValuePaint == null) {
            mValuePaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.FAKE_BOLD_TEXT_FLAG
                    | Paint.DITHER_FLAG);
            //valuePaint.density = getResources().getDisplayMetrics().density;
            mValuePaint.setTextSize(mContext.getResources().getDimension(
                    R.dimen.score_wheet_text_size));
            mValuePaint.setShadowLayer(0.1f, 0, 0.1f, 0xFFC0C0C0);
        }

        if (mCenterDrawable == null) {
            mCenterDrawable = getContext().getResources().getDrawable(R.drawable.wheel_val);
        }

        if (mTopShadow == null) {
            mTopShadow = new GradientDrawable(Orientation.TOP_BOTTOM,
                    WheelViewConfig.SHADOWS_COLORS);
        }

        if (mBottomShadow == null) {
            mBottomShadow = new GradientDrawable(Orientation.BOTTOM_TOP,
                    WheelViewConfig.SHADOWS_COLORS);
        }

        setBackgroundResource(R.drawable.wheel_bg);
    }

    /**
     * Calculates desired height for layout
     *
     * @param layout the source layout
     * @return the desired layout height
     */
    private int getDesiredHeight(final Layout layout) {
        if (layout == null) {
            return 0;
        }

        int linecount = layout.getLineCount();
        int desired = layout.getLineTop(linecount - 2)
                - (int) (WheelViewConfig.ITEM_OFFSET * mDensity) * 2
                - (int) (WheelViewConfig.ADDITIONAL_ITEM_HEIGHT * mDensity);

        // Check against our minimum height
        desired = Math.max(desired, getSuggestedMinimumHeight());

        return desired;
    }

    /**
     * @param index index
     * @return textItem
     */
    private String getTextItem(final int index) {
        int newIndex = index;
        if (mAdapter == null || mAdapter.getItemsCount() == 0) {
            return null;
        }
        int count = mAdapter.getItemsCount();
        if ((newIndex < 0 || newIndex >= count) && !mIsCyclic) {
            return null;
        } else {
            while (newIndex < 0) {
                newIndex = count + newIndex;
            }
        }
        newIndex %= count;
        return mAdapter.getItem(newIndex);
    }

    /**
     * Builds text depending on current value
     *
     * @param useCurrentValue useCurrentValue
     * @return the text
     */
    private String buildText(final boolean useCurrentValue) {
        StringBuilder itemsText = new StringBuilder();
        int addItems = mVisibleItems / 2 + 1;
        for (int i = mCurrentItem - addItems; i <= mCurrentItem + addItems; i++) {
            if (useCurrentValue || i != mCurrentItem) {
                String text = getTextItem(i);
                if (text != null) {
                    itemsText.append(text);
                }
            }
            if (i < mCurrentItem + addItems) {
                itemsText.append("\n");
            }
        }
        return itemsText.toString();
    }

    /**
     * Returns the max item length that can be present
     * 
     * @return the max length
     */
    private int getMaxTextLength() {
        WheelAdapter adapter = getAdapter();
        if (adapter == null) {
            return 0;
        }
        int adapterLength = adapter.getMaximumLength();
        if (adapterLength > 0) {
            return adapterLength;
        }
        String maxText = null;
        int addItems = mVisibleItems / 2;
        for (int i = Math.max(mCurrentItem - addItems, 0); i < Math.min(mCurrentItem
                + mVisibleItems, adapter.getItemsCount()); i++) {
            String text = adapter.getItem(i);
            if (text != null && (maxText == null || maxText.length() < text.length())) {
                maxText = text;
            }
        }
        int maxLength = 0;
        if (maxText != null) {
            maxLength = maxText.length();
        }
        return maxLength;
    }

    /**
     * Calculates control width and creates text layouts
     * 
     * @param widthSize the input layout width
     * @param mode the layout mode
     * @return the calculated control width
     */
    private int calculateLayoutWidth(final int widthSize, final int mode) {
        initResourcesIfNecessary();
        int width = widthSize;
        int maxLength = getMaxTextLength();
        if (maxLength > 0) {
            float textWidth = (float) Math.ceil(Layout.getDesiredWidth("0", mItemsPaint));
            mItemsWidth = (int) (maxLength * textWidth);
        } else {
            mItemsWidth = 0;
        }
        mItemsWidth += (int) (WheelViewConfig.ADDITIONAL_ITEMS_SPACE * mDensity);
        mLabelWidth = 0;
        if (mLabel != null && mLabel.length() > 0) {
            mLabelWidth = (int) Math.ceil(Layout.getDesiredWidth(mLabel, mValuePaint));
        }
        boolean recalculate = false;
        if (mode == MeasureSpec.EXACTLY) {
            width = widthSize;
            recalculate = true;
        } else {
            width = mItemsWidth + mLabelWidth + 2 * (int) (WheelViewConfig.PADDING * mDensity);
            if (mLabelWidth > 0) {
                width += (int) (WheelViewConfig.LABEL_OFFSET * mDensity);
            }
            // Check against our minimum width
            width = Math.max(width, getSuggestedMinimumWidth());
            if (mode == MeasureSpec.AT_MOST && widthSize < width) {
                width = widthSize;
                recalculate = true;
            }
        }
        if (recalculate) {
            // recalculate width
            recalculateWidth(width);
        }
        if (mItemsWidth > 0) {
            createLayouts(mItemsWidth, mLabelWidth);
        }
        return width;
    }

    /**
     * recalculateWidth
     * 
     * @param width width
     */
    private void recalculateWidth(final int width) {
        int pureWidth = width - (int) (WheelViewConfig.LABEL_OFFSET * mDensity) - 2
                * (int) (WheelViewConfig.PADDING * mDensity);
        if (pureWidth <= 0) {
            mLabelWidth = 0;
            mItemsWidth = 0;
        }
        if (mLabelWidth > 0) {
            double newWidthItems = (double) mItemsWidth * pureWidth / (mItemsWidth + mLabelWidth);
            mItemsWidth = (int) newWidthItems;
            mLabelWidth = pureWidth - mItemsWidth;
        } else {
            mItemsWidth = pureWidth + (int) (WheelViewConfig.LABEL_OFFSET * mDensity); // no label
        }
    }

    /**
     * Creates layouts
     * 
     * @param widthItems width of items layout
     * @param widthLabel width of label layout
     */
    private void createLayouts(final int widthItems, final int widthLabel) {
        if (mItemsLayout == null || mItemsLayout.getWidth() > widthItems) {
            Alignment alignment = Layout.Alignment.ALIGN_CENTER;
            if (widthLabel > 0) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            }
            mItemsLayout = new StaticLayout(buildText(mIsScrollingPerformed), mItemsPaint,
                    widthItems, alignment, 1,
                    (int) (WheelViewConfig.ADDITIONAL_ITEM_HEIGHT * mDensity), false);
        } else {
            mItemsLayout.increaseWidthTo(widthItems);
        }
        if (!mIsScrollingPerformed
                && (mValueLayout == null || mValueLayout.getWidth() > widthItems)) {
            String text = "";
            if (getAdapter() != null) {
                text = getAdapter().getItem(mCurrentItem);
            }
            Alignment alignment = Layout.Alignment.ALIGN_CENTER;
            if (widthLabel > 0) {
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
            }
            mValueLayout = new StaticLayout(text, mValuePaint, widthItems, alignment, 1,
                    (int) (WheelViewConfig.ADDITIONAL_ITEM_HEIGHT * mDensity), false);
        } else if (mIsScrollingPerformed) {
            mValueLayout = null;
        } else {
            mValueLayout.increaseWidthTo(widthItems);
        }
        if (widthLabel > 0) {
            if (mLabelLayout == null || mLabelLayout.getWidth() > widthLabel) {
                mLabelLayout = new StaticLayout(mLabel, mValuePaint, widthLabel,
                        Layout.Alignment.ALIGN_NORMAL, 1,
                        (int) (WheelViewConfig.ADDITIONAL_ITEM_HEIGHT * mDensity), false);
            } else {
                mLabelLayout.increaseWidthTo(widthLabel);
            }
        }
    }

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = calculateLayoutWidth(widthSize, widthMode);
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            height = getDesiredHeight(mItemsLayout);

            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        super.onDraw(canvas);

        if (mItemsLayout == null) {
            if (mItemsWidth == 0) {
                calculateLayoutWidth(getWidth(), MeasureSpec.EXACTLY);
            } else {
                createLayouts(mItemsWidth, mLabelWidth);
            }
        }
        if (mItemsWidth > 0) {
            canvas.save();
            // Skip padding space and hide a part of top and bottom items
            canvas.translate((int) (WheelViewConfig.PADDING * mDensity),
                    -(int) (WheelViewConfig.ITEM_OFFSET * mDensity));
            drawItems(canvas);
            drawValue(canvas);
            canvas.restore();
        }
        drawCenterRect(canvas);
        drawShadows(canvas);
    }

    /**
     * Draws shadows on top and bottom of control
     * 
     * @param canvas the canvas for drawing
     */
    private void drawShadows(final Canvas canvas) {
        mTopShadow.setBounds(0, 0, getWidth(), getHeight() / mVisibleItems);
        mTopShadow.draw(canvas);
        mBottomShadow.setBounds(0, getHeight() - getHeight() / mVisibleItems, getWidth(),
                getHeight());
        mBottomShadow.draw(canvas);
    }

    /**
     * Draws value and label layout
     * 
     * @param canvas the canvas for drawing
     */
    private void drawValue(final Canvas canvas) {
        if (mIsWheelLocked) {
            mValuePaint.setColor(WheelViewConfig.VALUE_TEXT_COLOR_LOCKED);
        } else {
            mValuePaint.setColor(WheelViewConfig.VALUE_TEXT_COLOR_UNLOCK);
        }
        mValuePaint.drawableState = getDrawableState();
        Rect bounds = new Rect();
        mItemsLayout.getLineBounds(mVisibleItems / 2, bounds);
        // draw label
        if (mLabelLayout != null) {
            canvas.save();
            canvas.translate(mItemsLayout.getWidth()
                    + (int) (WheelViewConfig.LABEL_OFFSET * mDensity), bounds.top);
            mLabelLayout.draw(canvas);
            canvas.restore();
        }
        // draw current value
        if (mValueLayout != null) {
            canvas.save();
            canvas.translate(0, bounds.top + mScrollingOffset);
            mValueLayout.draw(canvas);
            canvas.restore();
        }
    }

    /**
     * Draws items
     * 
     * @param canvas the canvas for drawing
     */
    private void drawItems(final Canvas canvas) {
        canvas.save();
        Rect bounds = new Rect();
        mItemsLayout.getLineBounds(1, bounds);
        canvas.translate(0, -bounds.top + mScrollingOffset);
        if (mIsWheelLocked) {
            mItemsPaint.setColor(WheelViewConfig.ITEMS_TEXT_COLOR_LOCKED);
        } else {
            mItemsPaint.setColor(WheelViewConfig.ITEMS_TEXT_COLOR_UNLOCK);
        }
        mItemsPaint.drawableState = getDrawableState();
        mItemsLayout.draw(canvas);
        canvas.restore();
    }

    /**
     * Draws rect for current value
     * 
     * @param canvas the canvas for drawing
     */
    private void drawCenterRect(final Canvas canvas) {
        int center = getHeight() / 2;
        int offset = getHeight() / mVisibleItems / 2;
        mCenterDrawable.setBounds(0, center - offset, getWidth(), center + offset);
        mCenterDrawable.draw(canvas);
    }

    @Override
    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouchEvent(final MotionEvent event) {
        WheelAdapter wheelAdapter = getAdapter();
        if (wheelAdapter == null) {
            return true;
        }
        // ホイールロック中は変更させない対応
        if (mIsWheelLocked) {
            return true;
        }
        switch (event.getAction()) {
        case MotionEvent.ACTION_DOWN:
            mLastYTouch = event.getY();
            break;

        case MotionEvent.ACTION_MOVE:
            if (!mIsScrollingPerformed) {
                mIsScrollingPerformed = true;
                notifyScrollingListenersAboutStart();
                invalidateLayouts();
            }
            doScroll(event.getY() - mLastYTouch);
            mLastYTouch = event.getY();
            break;

        case MotionEvent.ACTION_UP:
            onHandleTouchUp();
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * onHandleTouchUp
     */
    private void onHandleTouchUp() {
        if (mIsScrollingPerformed) {
            float itemHeight = getHeight() / (float) mVisibleItems;
            if (Math.abs(mScrollingOffset) > itemHeight / 2) {
                int scrollVal = (int) (-itemHeight / 2);
                if (mScrollingOffset > 0) {
                    scrollVal = (int) (itemHeight / 2);
                }
                doScroll(scrollVal);
            }
            notifyScrollingListenersAboutEnd();
            mIsScrollingPerformed = false;
            invalidateLayouts();
            invalidate();
        }
    }

    /**
     * Scrolls the wheel
     * 
     * @param delta the scrolling value
     */
    private void doScroll(final float delta) {

        float newDelta = delta;
        newDelta += mScrollingOffset;

        float fCount = mVisibleItems * newDelta / getHeight();
        int count = (int) fCount;
        int pos = mCurrentItem - count;
        if (mIsCyclic && mAdapter.getItemsCount() > 0) {
            pos += mAdapter.getItemsCount();
            pos %= mAdapter.getItemsCount();
        } else {
            pos = Math.max(pos, 0);
            pos = Math.min(pos, mAdapter.getItemsCount() - 1);
        }
        if (pos != mCurrentItem) {
            setCurrentItem(pos);
        } else {
            invalidate();
        }
        mScrollingOffset = (fCount - count) * getHeight() / mVisibleItems;
    }

    /**
     * キーイベント処理
     */
    public boolean onKeyDown(final int keyCode, final KeyEvent event) {
        // ホイールロック中は変更させない対応
        if (!mIsWheelLocked) {
            float itemHeight = (float) getHeight() / mVisibleItems;
            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                doScroll(-itemHeight);
                notifyScrollingListenersAboutEnd();
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                doScroll(itemHeight);
                notifyScrollingListenersAboutEnd();
                return true;
            }
        }
        return false;
    }

    // --------------------------------------------------- //
    // カスタマイズ箇所
    public void setIsWheelLocked(final boolean lock) {
        this.mIsWheelLocked = lock;
    }

    public boolean getIsWheelLocked() {
        return this.mIsWheelLocked;
    }
    // --------------------------------------------------- //
}
