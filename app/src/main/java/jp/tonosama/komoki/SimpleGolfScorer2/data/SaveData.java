package jp.tonosama.komoki.SimpleGolfScorer2.data;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

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
    private int mCurrentHole = 0;
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
    private Map<Integer, Map<Integer, Integer>> mAbsolutePatting = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mEachHolePar = new HashMap<>();
    /**  */
    private Map<Integer, Boolean> mEachHoleLocked = new HashMap<>();
    /**  */
    private Map<Integer, Integer> mPlayersHandi = new HashMap<>();
    /**  */
    private Map<Integer, Map<Integer, Integer>> mDemoSeries = new HashMap<>();
    /**  */
    private String mMemoStr = "";

    private SaveData(int saveIdx) {
        mSaveIdx = saveIdx;
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            mEachHolePar.put(holeIdx, 4);
            mEachHoleLocked.put(holeIdx, false);
        }
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            mPlayerNames.put(playerIdx, "");
            mPlayersAlpha.put(playerIdx, 255);
            mPlayersHandi.put(playerIdx, 0);
            Map<Integer, Integer> scoreList = new HashMap<>();
            for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
                scoreList.put(holeIdx, 0);
            }
            Map<Integer, Integer> patScoreList = new HashMap<>();
            for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
                patScoreList.put(holeIdx, 0);
            }
            Map<Integer, Integer> demoList = new HashMap<>();
            for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT + 1; holeIdx++) {
                demoList.put(holeIdx, 0);
            }
            mAbsoluteScore.put(playerIdx, scoreList);
            mAbsolutePatting.put(playerIdx, patScoreList);
            mDemoSeries.put(playerIdx, demoList);
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
        mCurrentHole = currentHole % SGSConfig.TOTAL_HOLE_COUNT;
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

    public boolean isPlayerExist(int playerIdx) {
        return 0 < mPlayerNames.get(playerIdx).trim().length();
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

    public Map<Integer, Integer> getTotalScore() {
        Map<Integer, Integer> totalList = new HashMap<>(SGSConfig.MAX_PLAYER_NUM);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            totalList.put(i, 0);
        }
        Map<Integer, Map<Integer, Integer>> scores = getScoresList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                int total = totalList.get(playerIdx);
                totalList.put(playerIdx, total + scores.get(playerIdx).get(holeIdx));
            }
        }
        return totalList;
    }

    public Map<Integer, Integer> getHalfScore(final boolean isFirstHalf) {
        Map<Integer, Integer> totalList = new HashMap<>(SGSConfig.MAX_PLAYER_NUM);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            totalList.put(i, 0);
        }
        Map<Integer, Map<Integer, Integer>> scores = getScoresList();
        int offset = SGSConfig.TOTAL_HOLE_COUNT / 2;
        if (isFirstHalf) {
            offset = 0;
        }
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT / 2; holeIdx++) {
            int offsetHoleIdx = offset + holeIdx;
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                int total = totalList.get(playerIdx);
                totalList.put(playerIdx, total + scores.get(playerIdx).get(offsetHoleIdx));
            }
        }
        return totalList;
    }

    public Map<Integer, Integer> getHalfPatScore(final boolean forward) {
        Map<Integer, Integer> totalList = new HashMap<>(SGSConfig.MAX_PLAYER_NUM);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            totalList.put(i, 0);
        }
        Map<Integer, Map<Integer, Integer>> scores = getPattingScoresList();
        int offset = SGSConfig.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT / 2; holeIdx++) {
            int offsetHoleIdx = offset + holeIdx;
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                int total = totalList.get(playerIdx);
                totalList.put(playerIdx, total + scores.get(playerIdx).get(offsetHoleIdx));
            }
        }
        return totalList;
    }

    public Map<Integer, Integer> getTotalPatScore() {
        Map<Integer, Integer> totalList = new HashMap<>(SGSConfig.MAX_PLAYER_NUM);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            totalList.put(i, 0);
        }
        Map<Integer, Map<Integer, Integer>> scores = getPattingScoresList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                int total = totalList.get(playerIdx);
                totalList.put(playerIdx, total + scores.get(playerIdx).get(holeIdx));
            }
        }
        return totalList;
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
        return mAbsolutePatting;
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

    public Map<Integer, Integer> getDemoSeries(final int playerIdx) {
        return mDemoSeries.get(playerIdx);
    }

    public String dumpDemoData() {
        StringBuilder builder = new StringBuilder();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT + 1; holeIdx++) {
            builder.append("HOLE [").append(holeIdx).append("] ");
            for (int playerIdx = 0; playerIdx < getPlayerNum(); playerIdx++) {
                String name = getPlayerNameList().get(playerIdx);
                int value = getDemoSeries(playerIdx).get(holeIdx);
                builder.append(name).append(":").append(value).append(" ");
            }
            builder.append("\n");
        }
        return builder.toString();
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

    public int getBestPlayer() {
        Map<Integer, Integer> scores = new HashMap<>();
        // Setup initial values
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            int handi = getPlayersHandi().get(playerIdx);
            scores.put(playerIdx, -handi);
        }
        // Sum up scores of each hole
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                int prevTotal = scores.get(playerIdx);
                int scoreAtHole = getScoresList().get(playerIdx).get(holeIdx);
                scores.put(playerIdx, prevTotal + scoreAtHole);
            }
        }
        int bestPlayer = -1;
        int bestScore = Integer.MAX_VALUE;
        for (int i = 0; i < getPlayerNum(); i++) {
            if (getPlayerNameList().get(i).trim().length() == 0) {
                continue;
            }
            if (scores.get(i) < bestScore) {
                bestPlayer = i;
                bestScore = scores.get(i);
            }
        }
        return bestPlayer;
    }

    @Override
    public String toString() {
        return "idx:[" + mSaveIdx + "] Hole Tiele: [" + mDemoChartTitle + "]";
    }
}
