package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

class DragUi {

    interface DragUiInterface {

        SaveData getData();
    }

    /**  */
    private DragUiInterface mInterface;

    /**  */
    private int mAnimVal;
    /**  */
    private boolean mIsAnimating;
    /**  */
    private int mMoveValue = 1;
    /**  */
    private int mOffsetX;
    /**  */
    private int mCurrentX = 5;
    /**  */
    static final int DEFAULT_X = 5;

    DragUi(final DragUiInterface dragInterface) {
        mInterface = dragInterface;
    }

    private int getCurrentX() {
        return mCurrentX;
    }

    void setCurrentX(final int currentX) {
        this.mCurrentX = currentX;
    }

    boolean isAnimating() {
        return mIsAnimating;
    }

    void setIsAnimating(final boolean isAnimating) {
        this.mIsAnimating = isAnimating;
    }

    int getAnimVal(final boolean isNext) {
        int animVal = -400;
        if (isNext) {
            animVal = -animVal;
        }
        return animVal;
    }

    int getAnimVal() {
        return mAnimVal;
    }

    void setAnimVal(final int animVal) {
        this.mAnimVal = animVal;
    }

    int getMoveValue() {
        return mMoveValue;
    }

    void setMoveValue(final int moveValue) {
        this.mMoveValue = moveValue;
    }

    /**
     * フッターエリアをタッチ&ドラッグ動作
     */
    void setFooterAreaAction(final Activity activity, final AnimationListener listener,
                             final View mFooterArea, final Button mPrevArrw,
                             final View mDragImag, final View mCurrentPosImg) {

        mFooterArea.setOnTouchListener(new View.OnTouchListener() {

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(final View v, final MotionEvent event) {
                int x = (int) event.getX() - mPrevArrw.getWidth()
                        - (mCurrentPosImg.getWidth() / 14);
                int holeLength = mDragImag.getMeasuredWidth() / 20;
                SaveData data = mInterface.getData();
                // ドラッグしたら
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
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
                    actionMoveMain(activity, data, holeLength, mCurrentPosImg);
                } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // タッチダウンしたら
                    actionDownMain(x, mDragImag, mCurrentPosImg);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    // タッチアップしたら
                    actionUpMain(activity, listener, data, holeLength);
                }
                return true;
            }
        });
    }

    /**
     * @param activity Activity
     * @param sData SaveData
     * @param holeLength int
     * @param mCurrentPosImg View
     */
    private void actionMoveMain(final Activity activity, final SaveData sData,
            final int holeLength, final View mCurrentPosImg) {

        int tmpHole = getTmpHole(holeLength);
        mCurrentPosImg.setVisibility(View.VISIBLE);
        mCurrentPosImg.layout(getCurrentX(), 0, getCurrentX() + mCurrentPosImg.getWidth(),
                mCurrentPosImg.getHeight());
        if (sData.getIs18Hround()) {
            ((ImageView) activity.findViewById(R.id.golf_hole_icon))
                    .setImageResource(R.drawable.golf_hole_icon);
            SERes.getHoleNameTextView(activity).setBackgroundResource(
                    SERes.HOLE_NUMBER_IMG_RES_IDS[tmpHole - 1]);
        } else {
            if (tmpHole < 10) {
                ((ImageView) activity.findViewById(R.id.golf_hole_icon))
                        .setImageResource(R.drawable.golf_hole_icon_out);
                SERes.getHoleNameTextView(activity).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[tmpHole - 1]);
            } else {
                ((ImageView) activity.findViewById(R.id.golf_hole_icon))
                        .setImageResource(R.drawable.golf_hole_icon_in);
                SERes.getHoleNameTextView(activity).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[tmpHole - 10]);
            }
        }
    }

    /**
     * @param x int
     * @param mDragImag View
     * @param mCurrentPosImg View
     */
    private void actionDownMain(final int x, final View mDragImag, final View mCurrentPosImg) {
        mOffsetX = x;
        setCurrentX(x);
        mDragImag.setVisibility(View.INVISIBLE);
        mCurrentPosImg.setVisibility(View.INVISIBLE);
        mCurrentPosImg.layout(getCurrentX(), 0, getCurrentX() + mCurrentPosImg.getWidth(),
                mCurrentPosImg.getHeight());
    }

    /**
     * @param activity Activity
     * @param listener AnimationListener
     * @param sData SaveData
     * @param holeLength int
     */
    private void actionUpMain(final Activity activity, final AnimationListener listener,
            final SaveData sData, final int holeLength) {
        int tmpHole = getTmpHole(holeLength);
        int moveVal = 0;
        if (sData.getCurrentHole() > tmpHole) {
            moveVal = getAnimVal(true);
        } else if (sData.getCurrentHole() < tmpHole) {
            moveVal = getAnimVal(false);
        }
        // アニメーション対応
        // (右から中心,中心から右方向,下から中心,中心から下方向)
        TranslateAnimation trans = new TranslateAnimation(0, moveVal, 0, 0);
        AlphaAnimation alpha = new AlphaAnimation(1, 0);
        trans.setDuration(100);
        alpha.setDuration(100);
        trans.setInterpolator(new DecelerateInterpolator());
        alpha.setInterpolator(new AccelerateInterpolator());
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(trans);
        set.addAnimation(alpha);
        set.setAnimationListener(listener);
        setAnimVal(-moveVal);
        setMoveValue(tmpHole - sData.getCurrentHole());
        setIsAnimating(true);
        SERes.getDrumPickerArea(activity).startAnimation(set);
    }

    /**
     * @param holeLength int
     * @return tmpHole
     */
    private int getTmpHole(final int holeLength) {
        int tmpHole = getCurrentX() / holeLength + 1;
        if (tmpHole < 1) {
            tmpHole = 1;
        }
        if (tmpHole > 18) {
            tmpHole = 18;
        }
        return tmpHole;
    }
}