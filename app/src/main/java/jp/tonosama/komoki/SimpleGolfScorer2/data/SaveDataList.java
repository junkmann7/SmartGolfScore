package jp.tonosama.komoki.SimpleGolfScorer2.data;

import java.util.ArrayList;
import java.util.Collections;

import jp.tonosama.komoki.SimpleGolfScorer2.R;

import android.util.Log;

/**
 * @author Komoki
 */
public class SaveDataList {

    /**  */
    private static final String TAG = SaveDataList.class.getSimpleName();
    /**  */
    public static final boolean DEBUG = false;
    /**  */
    private ArrayList<SaveData> mSaveDataList;

    /**
     * コンストラクタ
     *
     */
    public SaveDataList() {
        mSaveDataList = new ArrayList<>();
        if (DEBUG) {
            Log.d(TAG, "new instance created.");
        }
    }

    public void append(final int idx, final SaveData data) {
        mSaveDataList.add(idx, data);
    }

    public SaveData get(final int index) {
        return mSaveDataList.get(index);
    }

    public void sort(final int sortType) {
        Collections.sort(mSaveDataList, new SaveDataComparator(sortType));
    }

    public int size() {
        return mSaveDataList.size();
    }

    /**
     * clear
     */
    public void clear() {
        mSaveDataList.clear();
    }

    public int getFixedDataNum() {
        int fixedDataNum = 0;
        final int savedDataNum = mSaveDataList.size();
        for (int i = 0; i < savedDataNum; i++) {
            if (mSaveDataList.get(i).isHoleResultFixed()) {
                ++fixedDataNum;
            }
        }
        return fixedDataNum;
    }

    public int getPlayerNum(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayerNum();
    }

    public String getHoleTitle(final int saveNum) {
        return mSaveDataList.get(saveNum).getHoleTitle();
    }

    public String[] getPlayerNames(final int saveNum) {
        return mSaveDataList.get(saveNum).getNames();
    }

    public int getWeather(final int saveNum) {
        return mSaveDataList.get(saveNum).getCondition();
    }

    public int getWeatherImageResourceId(final int saveNum) {
        int[] weather = { R.drawable.weather_shine, R.drawable.weather_cloudy,
                R.drawable.weather_rain, R.drawable.weather_wind, R.drawable.weather_mud, };
        return weather[mSaveDataList.get(saveNum).getCondition()];
    }

    public int[] getAllParValues(final int saveNum) {
        return mSaveDataList.get(saveNum).getEachHolePar();
    }

    public int[] getMyPatting(final int saveNum) {
        return mSaveDataList.get(saveNum).getAbsolutePatting()[0];
    }

    public int[] getHandiCaps(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public int[] getPlayersHandi(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public int[][] getAllScores(final int saveNum) {
        return mSaveDataList.get(saveNum).getAbsoluteScore();
    }
}