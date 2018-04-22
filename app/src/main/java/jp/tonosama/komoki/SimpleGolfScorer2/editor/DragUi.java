package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;

class DragUi {

    interface DragListener {

        void onHoleChanged(int holeNumber);
    }

    private int mOffsetX;

    private int mCurrentX = 5;

    private boolean mIsDragging;

    private final DragListener mListener;

    @NonNull
    private View mPrevArrowButton;
    @NonNull
    private View mCurrLocFixedImage;
    @NonNull
    private View mCurrLocDraggingImage;

    DragUi(@NonNull Activity activity, @NonNull DragListener listener) {

        mListener = listener;

        mPrevArrowButton = activity.findViewById(R.id.arrow_upside);
        mCurrLocFixedImage = activity.findViewById(R.id.curr_hole_fixed_location);
        mCurrLocDraggingImage = activity.findViewById(R.id.curr_hole_dragging_location);

        View footerArea = activity.findViewById(R.id.next_hole_area);
        footerArea.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(final View v, final MotionEvent event) {

                int x = (int) event.getX() - mPrevArrowButton.getWidth()
                        - (mCurrLocDraggingImage.getWidth() / 14);
                int holeLength = mCurrLocFixedImage.getMeasuredWidth() / 20;
                int holeNumber = getDraggingHoleNumber(holeLength);

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mIsDragging = true;
                        actionDownMain(x);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        actionMoveMain(x, holeLength, holeNumber);
                        break;
                    case MotionEvent.ACTION_UP:
                        mIsDragging = false;
                        actionUpMain(holeNumber);
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private int getCurrentX() {
        return mCurrentX;
    }

    private void setCurrentX(final int currentX) {
        this.mCurrentX = currentX;
    }

    void updateCurrentLocation(int holeNumber) {
        if (mIsDragging) {
            return;
        }
        mCurrLocDraggingImage.setVisibility(View.INVISIBLE);
        mCurrLocFixedImage.setVisibility(View.VISIBLE);
        ((ImageView) mCurrLocFixedImage).setImageResource(SERes.CURRENT_HOLE_IMG_RES_IDS[holeNumber]);
    }

    private void actionMoveMain(int x, int holeLength, int holeNumber) {
        int diffX = (mOffsetX - x);
        setCurrentX(getCurrentX() - diffX);
        mOffsetX = x;
        if (getCurrentX() < 0) {
            setCurrentX(0);
            mOffsetX = 0;
        }
        if (getCurrentX() > holeLength * 18) {
            setCurrentX(holeLength * 18);
            mOffsetX = holeLength * 18;
        }
        mCurrLocDraggingImage.setVisibility(View.VISIBLE);
        mCurrLocDraggingImage.layout(
                getCurrentX(),
                0,
                getCurrentX() + mCurrLocDraggingImage.getWidth(),
                mCurrLocDraggingImage.getHeight());
        mListener.onHoleChanged(holeNumber);
    }

    private void actionUpMain(int holeNumber) {
        updateCurrentLocation(holeNumber);
    }

    private void actionDownMain(int x) {
        mOffsetX = x;
        setCurrentX(x);
        mCurrLocFixedImage.setVisibility(View.INVISIBLE);
        mCurrLocDraggingImage.setVisibility(View.INVISIBLE);
        mCurrLocDraggingImage.layout(
                getCurrentX(),
                0,
                getCurrentX() + mCurrLocDraggingImage.getWidth(),
                mCurrLocDraggingImage.getHeight());
    }

    private int getDraggingHoleNumber(final int holeLength) {
        int tmpHole = getCurrentX() / holeLength;
        if (tmpHole < 0) {
            tmpHole = 0;
        }
        if (SGSConfig.TOTAL_HOLE_COUNT <= tmpHole) {
            tmpHole = SGSConfig.TOTAL_HOLE_COUNT - 1;
        }
        return tmpHole;
    }
}