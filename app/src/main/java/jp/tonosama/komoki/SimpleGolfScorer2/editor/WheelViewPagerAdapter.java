package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private final WheelChangeListener mListener;

    interface WheelChangeListener {

        void onWheelChanged(final int holeNumber, final int playerIdx, final int oldVal, final int newVal);
    }

    WheelViewPagerAdapter(@NonNull WheelChangeListener listener) {
        mViewMap = new HashMap<>();
        mListener = listener;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int holeNumber) {

        Context context = SGSApplication.getInstance();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup child = (ViewGroup) inflater
                .inflate(R.layout.score_editor_picker_area_contents, container, false);
        for (int i = 0; i < child.getChildCount(); i++) {
            WheelView wheelView = (WheelView) child.getChildAt(i);
            final int playerIdx = i;
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
                    mListener.onWheelChanged(holeNumber, playerIdx, oldValue, newValue);
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
            WheelView wheelView = (WheelView) vg.getChildAt(playerIdx);
            wheelView.setCurrentItem(saveData.getScoresList().get(playerIdx).get(holeNumber));
            wheelView.setIsWheelLocked(isHoleLocked);
        }
    }
}
