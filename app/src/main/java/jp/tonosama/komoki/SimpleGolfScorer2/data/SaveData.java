package jp.tonosama.komoki.SimpleGolfScorer2.data;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.ArrayUtil;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;

/**
 * GolfScoreData
 * 
 * @author Komoki
 */
@SuppressLint("UseSparseArrays")
public class SaveData implements Serializable {

    /**  */
    private final int mSaveIdx;
    /**  */
    private boolean mIs18Hround = true;
    /**  */
    private int mCurrentHole = 1;
    /**  */
    private boolean mOutputImageFlg = false;
    /**  */
    private String mDemoChartTitle = "";
    /**  */
    private Map<Integer, String> mPlayerNames = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mPlayersAlpha = new HashMap<>();
    /**  */
    private Map<Integer, Map<Integer, Integer>> mAbsoluteScore = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mAbsolutePatting = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mEachHolePar = new HashMap<>();
    /**  */
    private Map<Integer, Boolean> mEachHoleLocked = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mPlayersHandi = new HashMap<>();
    /**  */
    private int[] mDemoSeries1 = new int[SGSConfig.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries2 = new int[SGSConfig.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries3 = new int[SGSConfig.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries4 = new int[SGSConfig.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[][] mDemoSeries = { mDemoSeries1, mDemoSeries2, mDemoSeries3, mDemoSeries4 };
    /**  */
    private String mMemoStr = "";

    private SaveData(int saveIdx) {
        mSaveIdx = saveIdx;
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            mAbsolutePatting.put(holeIdx, 0);
            mEachHolePar.put(holeIdx, 4);
            mEachHoleLocked.put(holeIdx, false);
        }
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            mPlayerNames.put(playerIdx, "");
            mPlayersAlpha.put(playerIdx, 255);
            Map<Integer, Integer> scoreList = new HashMap<>();
            for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
                scoreList.put(holeIdx, 0);
            }
            mAbsoluteScore.put(playerIdx, scoreList);
            mPlayersHandi.put(playerIdx, 0);
        }
    }

    public static SaveData createInitialData(int saveIdx) {
        return new SaveData(saveIdx);
    }

    public int getSaveIdx() {
        return mSaveIdx;
    }

    public boolean getIs18Hround() {
        return mIs18Hround;
    }

    /**
     * @return str String
     */
    public String getIs18HroundStr() {
        String str = "0";
        if (mIs18Hround) {
            str = "1";
        }
        return str;
    }

    public void setIs18Hround(final boolean is18Hround) {
        mIs18Hround = is18Hround;
    }

    /**
     * @return true: false:
     */
    public boolean getIsShortHole() {
        boolean isShortHole = true;
        Map<Integer, Integer> parScore = getEachHolePar();
        for (int parVal : parScore.values()) {
            if (parVal != 3) {
                isShortHole = false;
            }
        }
        return isShortHole;
    }

    public int getCurrentHole() {
        return mCurrentHole;
    }

    public void setCurrentHole(final int currentHole) {
        mCurrentHole = currentHole;
    }

    /**  */
    private int mCondition;

    public int getCondition() {
        return mCondition;
    }

    public void setCondition(final int condition) {
        mCondition = condition;
    }

    public boolean isOutputImageFlg() {
        return mOutputImageFlg;
    }

    public void setOutputImageFlg(final boolean outputImageFlg) {
        mOutputImageFlg = outputImageFlg;
    }

    public Map<Integer, String> getPlayerNameList() {
        return mPlayerNames;
    }

    public void setNameList(final Map<Integer, String> names) {
        mPlayerNames = names;
    }

    public Map<Integer, Integer> getPlayersAlpha() {
        return mPlayersAlpha;
    }

    public void setPlayersAlpha(final Map<Integer, Integer> playersAlpha) {
        mPlayersAlpha = playersAlpha;
    }

    public String getMemoStr() {
        return mMemoStr;
    }

    public void setMemoStr(final String memo) {
        mMemoStr = memo;
    }

    public String getHoleTitle() {
        return mDemoChartTitle;
    }

    public void setHoleTitle(final String holeTitle) {
        mDemoChartTitle = holeTitle;
    }

    public Map<Integer, Map<Integer, Integer>> getScoresList() {
        return mAbsoluteScore;
    }

    public void setScoresList(@NonNull Map<Integer, Map<Integer, Integer>> scoresList) {
        mAbsoluteScore = scoresList;
    }

    public int[] getTotalScore() {
        int[] total = { 0, 0, 0, 0 };
        Map<Integer, Map<Integer, Integer>> scores = getScoresList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < total.length; playerIdx++) {
                total[playerIdx] += scores.get(playerIdx).get(holeIdx);
            }
        }
        return total;
    }

    public int[] getHalfScore(final boolean forward) {
        int[] total = { 0, 0, 0, 0 };
        Map<Integer, Map<Integer, Integer>> scores = getScoresList();
        int offset = SGSConfig.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT / 2; holeIdx++) {
            int offsetHoleIdx = offset + holeIdx;
            for (int playerIdx = 0; playerIdx < total.length; playerIdx++) {
                total[playerIdx] += scores.get(playerIdx).get(offsetHoleIdx);
            }
        }
        return total;
    }

    public int[] getTotalPatScore() {
        int[] total = { 0, 0, 0, 0 };
        Map<Integer, Map<Integer, Integer>> scores = getPattingScoresList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < total.length; playerIdx++) {
                total[playerIdx] += scores.get(playerIdx).get(holeIdx);
            }
        }
        return total;
    }

    public int[] getHalfPatScore(final boolean forward) {
        int[] total = { 0, 0, 0, 0 };
        Map<Integer, Map<Integer, Integer>> scores = getPattingScoresList();
        int offset = SGSConfig.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT / 2; holeIdx++) {
            int offsetHoleIdx = offset + holeIdx;
            for (int playerIdx = 0; playerIdx < total.length; playerIdx++) {
                total[playerIdx] += scores.get(playerIdx).get(offsetHoleIdx);
            }
        }
        return total;
    }

    public int getTotalParScore() {
        int total = 0;
        Map<Integer, Integer> scores = getEachHolePar();
        for (int i = 0; i < SGSConfig.TOTAL_HOLE_COUNT; i++) {
            total += scores.get(i);
        }
        return total;
    }

    public int getHalfParScore(final boolean forward) {
        int total = 0;
        Map<Integer, Integer> par = getEachHolePar();
        int offset = SGSConfig.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int i = 0; i < SGSConfig.TOTAL_HOLE_COUNT / 2; i++) {
            int idx = offset + i;
            total += par.get(idx);
        }
        return total;
    }

    public Map<Integer, Map<Integer, Integer>> getPattingScoresList() {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Map<Integer, Integer>> patMap = new HashMap<>();
        patMap.put(0, mAbsolutePatting);
        patMap.put(1, ArrayUtil.createMap(SGSConfig.TOTAL_HOLE_COUNT, 0));
        patMap.put(2, ArrayUtil.createMap(SGSConfig.TOTAL_HOLE_COUNT, 0));
        patMap.put(3, ArrayUtil.createMap(SGSConfig.TOTAL_HOLE_COUNT, 0));
        return patMap;
    }

    public Map<Integer, Integer> getEachHolePar() {
        return mEachHolePar;
    }

    public void setEachHolePar(final Map<Integer, Integer> eachHolePar) {
        this.mEachHolePar = eachHolePar;
    }

    public Map<Integer, Integer> getPlayersHandi() {
        return mPlayersHandi;
    }

    public void setPlayersHandi(final Map<Integer, Integer> playersHandi) {
        this.mPlayersHandi = playersHandi;
    }

    public int[] getDemoSeries(final int idx) {
        return mDemoSeries[idx];
    }

    public Map<Integer, Boolean> getEachHoleLocked() {
        return mEachHoleLocked;
    }

    public boolean isHoleResultFixed() {
        boolean ret = true;
        for (boolean aMEachHoleLocked : mEachHoleLocked.values()) {
            if (!aMEachHoleLocked) {
                ret = false;
            }
        }
        return ret;
    }

    public int getPlayerNum() {
        int playerNum = 0;
        for (String mPlayerName : mPlayerNames.values()) {
            if (mPlayerName.trim().length() != 0) {
                ++playerNum;
            }
        }
        return playerNum;
    }

    @Override
    public String toString() {
        return "idx:[" + mSaveIdx + "] Hole Tiele: [" + mDemoChartTitle + "]";
    }
}
