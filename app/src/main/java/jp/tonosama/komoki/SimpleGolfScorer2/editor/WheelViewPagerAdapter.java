package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.wheel.OnWheelChangedListener;
import jp.tonosama.komoki.SimpleGolfScorer2.wheel.WheelView;

@SuppressLint("UseSparseArrays")
public class WheelViewPagerAdapter extends PagerAdapter {

    @NonNull
    private final Map<Integer, ViewGroup> mViewMap;

    @NonNull
    private final OnWheelChangeListener mOnWheelChangeListener;

    @NonNull
    private final OnPattingButtonClickListener mOnPattingButtonClickListener;

    interface OnWheelChangeListener {

        void onWheelChanged(final int holeNumber, final int playerIdx, final int oldVal, final int newVal);
    }

    interface OnPattingButtonClickListener {

        void onPattingButtonClick(final int holeNumber, final int playerIdx);
    }

    WheelViewPagerAdapter(@NonNull OnWheelChangeListener onWheelChangeListener,
                          @NonNull OnPattingButtonClickListener pattingButtonClickListener) {
        mViewMap = new HashMap<>();
        mOnWheelChangeListener = onWheelChangeListener;
        mOnPattingButtonClickListener = pattingButtonClickListener;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int holeNumber) {

        final Context context = SGSApplication.getInstance();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup child = (ViewGroup) inflater
                .inflate(R.layout.score_editor_picker_area_contents, container, false);
        for (int i = 0; i < child.getChildCount(); i++) {
            final int playerIdx = i;
            WheelView wheelView = (WheelView) ((ViewGroup) child.getChildAt(playerIdx))
                    .getChildAt(0);
            final SaveData saveData = SaveDataPref.getSelectedSaveData();
            if (saveData == null) {
                continue;
            }
            if (!saveData.isPlayerExist(playerIdx)) {
                child.getChildAt(playerIdx).setVisibility(View.GONE);
                continue;
            }
            wheelView.setOnWheelChangedListener(new OnWheelChangedListener() {
                @Override
                public void onChanged(int oldValue, int newValue) {
                    mOnWheelChangeListener.onWheelChanged(holeNumber, playerIdx, oldValue, newValue);
                }
            });
            View pattingButton = ((ViewGroup) child.getChildAt(playerIdx))
                    .getChildAt(1).findViewById(R.id.patting_summery_button);
            pattingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnPattingButtonClickListener.onPattingButtonClick(holeNumber, playerIdx);
                }
            });
        }
        refreshWheelViews(child, holeNumber);
        mViewMap.put(holeNumber, child);
        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return SGSConfig.TOTAL_HOLE_COUNT;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        return view.equals(object);
    }

    void updateWheelViews(final int holeNumber) {
        ViewGroup vg = mViewMap.get(holeNumber);
        if (vg == null) {
            return;
        }
        refreshWheelViews(vg, holeNumber);
    }

    void removePlayers() {
        final SaveData saveData = SaveDataPref.getSelectedSaveData();
        if (saveData == null) {
            return;
        }
        for (ViewGroup vg : mViewMap.values()) {
            for (int playerIdx = 0; playerIdx < vg.getChildCount(); playerIdx++) {
                int visibility = saveData.isPlayerExist(playerIdx) ? View.VISIBLE : View.GONE;
                vg.getChildAt(playerIdx).setVisibility(visibility);
            }
        }
    }

    private void refreshWheelViews(@NonNull ViewGroup vg, final int holeNumber) {
        final SaveData saveData = SaveDataPref.getSelectedSaveData();
        if (saveData == null) {
            return;
        }
        boolean isHoleLocked = saveData.getEachHoleLocked().get(holeNumber);
        for (int playerIdx = 0; playerIdx < vg.getChildCount(); playerIdx++) {
            // Wheel views
            WheelView wheelView = (WheelView) ((ViewGroup) vg.getChildAt(playerIdx)).getChildAt(0);
            wheelView.setCurrentItem(saveData.getScoresList().get(playerIdx).get(holeNumber));
            wheelView.setIsWheelLocked(isHoleLocked);
            // Patting views
            ViewGroup pattingImages = (ViewGroup) ((ViewGroup) vg.getChildAt(playerIdx))
                    .getChildAt(1).findViewById(R.id.patting_summery_images);
            int pattingScore = saveData.getPattingScoresList().get(playerIdx).get(holeNumber);
            for (int value = 0; value < pattingImages.getChildCount(); value++ ) {
                ImageView iv = (ImageView) pattingImages.getChildAt(value);
                if (value + 1 <= pattingScore) {
                    iv.setImageResource(R.drawable.mypat_rating_3);
                } else {
                    iv.setImageResource(R.drawable.mypat_rating_1);
                }
            }
            View pattingButton = ((ViewGroup) vg.getChildAt(playerIdx))
                    .getChildAt(1).findViewById(R.id.patting_summery_button);
            pattingButton.setEnabled(!isHoleLocked);
        }
    }
}
