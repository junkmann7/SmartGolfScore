package jp.tonosama.komoki.SimpleGolfScorer2.data;

import java.util.ArrayList;
import java.util.Collections;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import android.content.Context;
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
    private int[] mPrefNumber;
    /**  */
    private ArrayList<SaveData> mSaveDataList;

    /**
     * コンストラクタ
     * 
     * @param context
     */
    public SaveDataList(final Context context) {
        mSaveDataList = new ArrayList<SaveData>();
        if (DEBUG) {
            Log.d(TAG, "new instance created.");
        }
    }

    /**
     * @param idx
     * @param data
     */
    public void append(final int idx, final SaveData data) {
        mSaveDataList.add(idx, data);
    }

    /**
     * @param index
     * @return
     */
    public SaveData get(final int index) {
        return mSaveDataList.get(index);
    }

    /**
     * @param sortType
     */
    public void sort(final int sortType) {
        Collections.sort(mSaveDataList, new SaveDataComparator(sortType));
    }

    /**
     * @return
     */
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

    public void setHoleTitle(final int saveNum, final String holeTitle) {
        mSaveDataList.get(saveNum).setHoleTitle(holeTitle);
    }

    public String getHoleTitle(final int saveNum) {
        return mSaveDataList.get(saveNum).getHoleTitle();
    }

    public void setPlayerNames(final int saveNum, final String[] playerNames) {
        mSaveDataList.get(saveNum).setNames(playerNames);
    }

    public String[] getPlayerNames(final int saveNum) {
        return mSaveDataList.get(saveNum).getNames();
    }

    public void setWeather(final int saveNum, final int roundCondit) {
        mSaveDataList.get(saveNum).setCondition(roundCondit);
    }

    public int getWeather(final int saveNum) {
        return mSaveDataList.get(saveNum).getCondition();
    }

    public int getWeatherImageResourceId(final int saveNum) {
        int[] weather = { R.drawable.weather_shine, R.drawable.weather_cloudy,
                R.drawable.weather_rain, R.drawable.weather_wind, R.drawable.weather_mud, };
        return weather[mSaveDataList.get(saveNum).getCondition()];
    }

    public void setAllParValues(final int saveNum, final int[] allPar) {
        mSaveDataList.get(saveNum).setEachHolePar(allPar);
    }

    public int[] getAllParValues(final int saveNum) {
        return mSaveDataList.get(saveNum).getEachHolePar();
    }

    public void setMyPatting(final int saveNum, final int[] myPatting) {
        mSaveDataList.get(saveNum).setAbsolutePatting(myPatting);
    }

    public int[] getMyPatting(final int saveNum) {
        return mSaveDataList.get(saveNum).getAbsolutePatting()[0];
    }

    public void setHandiCaps(final int saveNum, final int[] handicaps) {
        mSaveDataList.get(saveNum).setPlayersHandi(handicaps);
    }

    public int[] getHandiCaps(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public void setPrefNumber(final int saveNum, final int prefNumber) {
        mPrefNumber[saveNum] = prefNumber;
    }

    public int getPrefNumber(final int saveNum) {
        return mPrefNumber[saveNum];
    }

    public int[] getPlayersHandi(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public void setAllScores(final int saveNum, final int[][] allScores) {
        mSaveDataList.get(saveNum).setAbsoluteScore(allScores);
    }

    public int[][] getAllScores(final int saveNum) {
        return mSaveDataList.get(saveNum).getAbsoluteScore();
    }
}