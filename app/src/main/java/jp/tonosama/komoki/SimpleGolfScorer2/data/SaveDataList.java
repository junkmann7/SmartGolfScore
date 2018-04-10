package jp.tonosama.komoki.SimpleGolfScorer2.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;

/**
 * @author Komoki
 */
public class SaveDataList {

    /**  */
    private static final String TAG = SaveDataList.class.getSimpleName();
    /**  */
    private ArrayList<SaveData> mSaveDataList;

    /**
     * コンストラクタ
     *
     */
    public SaveDataList() {
        mSaveDataList = new ArrayList<>();
        DevLog.d(TAG, "new instance created.");
    }

    public void append(final SaveData data) {
        mSaveDataList.add(mSaveDataList.size(), data);
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

    public Map<Integer, String> getPlayerNames(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayerNameList();
    }

    public int getWeather(final int saveNum) {
        return mSaveDataList.get(saveNum).getCondition();
    }

    public int getWeatherImageResourceId(final int saveNum) {
        int[] weather = { R.drawable.weather_shine, R.drawable.weather_cloudy,
                R.drawable.weather_rain, R.drawable.weather_wind, R.drawable.weather_mud, };
        return weather[mSaveDataList.get(saveNum).getCondition()];
    }

    public Map<Integer, Integer> getAllParValues(final int saveNum) {
        return mSaveDataList.get(saveNum).getEachHolePar();
    }

    public Map<Integer, Integer> getMyPatting(final int saveNum) {
        return mSaveDataList.get(saveNum).getPattingScoresList().get(0);
    }

    public Map<Integer, Integer> getHandiCaps(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public Map<Integer, Integer> getPlayersHandi(final int saveNum) {
        return mSaveDataList.get(saveNum).getPlayersHandi();
    }

    public Map<Integer, Map<Integer, Integer>> getAllScores(final int saveNum) {
        return mSaveDataList.get(saveNum).getScoresList();
    }
}