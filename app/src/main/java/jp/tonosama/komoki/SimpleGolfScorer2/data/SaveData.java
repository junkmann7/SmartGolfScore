package jp.tonosama.komoki.SimpleGolfScorer2.data;

import java.io.Serializable;

import jp.tonosama.komoki.SimpleGolfScorer2.Util;

/**
 * GolfScoreData
 * 
 * @author Komoki
 */
public class SaveData implements Serializable {

    /**  */
    private int mSaveIdx;
    /**  */
    private boolean mIs18Hround = true;
    /**  */
    private int mCurrentHole = 1;
    /**  */
    private boolean mOutputImageFlg = false;
    /**  */
    private String mDemoChartTitle = "";
    /**  */
    private String[] mPlayerNames = { "", "", "", "" };
    /**  */
    private int[] mPlayersAlpha = { 255, 255, 255, 255 };
    /**  */
    private int[] mAbsoluteScore1 = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private int[] mAbsoluteScore2 = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private int[] mAbsoluteScore3 = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private int[] mAbsoluteScore4 = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private int[][] mAbsoluteScore = { mAbsoluteScore1, mAbsoluteScore2, mAbsoluteScore3,
            mAbsoluteScore4 };
    /**  */
    private int[] mAbsolutePatting = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private int[] mEachHolePar = new int[Util.TOTAL_HOLE_COUNT];
    /**  */
    private boolean[] mEachHoleLocked = //
    { false, false, false, false, false, false, false, false, false, false, false, false, false,
            false, false, false, false, false };
    /**  */
    private int[] mPlayersHandi = { 0, 0, 0, 0 };
    /**  */
    private int[] mDemoSeries1 = new int[Util.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries2 = new int[Util.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries3 = new int[Util.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[] mDemoSeries4 = new int[Util.TOTAL_HOLE_COUNT + 1];
    /**  */
    private int[][] mDemoSeries = { mDemoSeries1, mDemoSeries2, mDemoSeries3, mDemoSeries4 };
    /**  */
    private String mMemoStr = "";

    public int getSaveIdx() {
        return mSaveIdx;
    }

    public void setSaveIdx(final int saveIdx) {
        this.mSaveIdx = saveIdx;
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
        int[] parScore = getEachHolePar();
        for (int aParScore : parScore) {
            if (aParScore != 3) {
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

    public String[] getNames() {
        return mPlayerNames;
    }

    public void setNames(final String[] names) {
        mPlayerNames = names;
    }

    public int[] getPlayersAlpha() {
        return mPlayersAlpha;
    }

    public void setPlayersAlpha(final int[] playersAlpha) {
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

    public int[][] getAbsoluteScore() {
        return mAbsoluteScore;
    }

    public int[] getAbsoluteScore(final int idx) {
        return mAbsoluteScore[idx];
    }

    public int[] getTotalScore() {
        int[] total = { 0, 0, 0, 0 };
        int[][] scores = getAbsoluteScore();
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            for (int j = 0; j < total.length; j++) {
                total[j] += scores[j][i];
            }
        }
        return total;
    }

    public int[] getHalfScore(final boolean forward) {
        int[] total = { 0, 0, 0, 0 };
        int[][] scores = getAbsoluteScore();
        int offset = Util.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT / 2; i++) {
            int idx = offset + i;
            for (int j = 0; j < total.length; j++) {
                total[j] += scores[j][idx];
            }
        }
        return total;
    }

    public int[] getTotalPatScore() {
        int[] total = { 0, 0, 0, 0 };
        int[][] scores = getAbsolutePatting();
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            for (int j = 0; j < total.length; j++) {
                total[j] += scores[j][i];
            }
        }
        return total;
    }

    public int[] getHalfPatScore(final boolean forward) {
        int[] total = { 0, 0, 0, 0 };
        int[][] scores = getAbsolutePatting();
        int offset = Util.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT / 2; i++) {
            int idx = offset + i;
            for (int j = 0; j < total.length; j++) {
                total[j] += scores[j][idx];
            }
        }
        return total;
    }

    public int getTotalParScore() {
        int total = 0;
        int[] scores = getEachHolePar();
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            total += scores[i];
        }
        return total;
    }

    public int getHalfParScore(final boolean forward) {
        int total = 0;
        int[] par = getEachHolePar();
        int offset = Util.TOTAL_HOLE_COUNT / 2;
        if (forward) {
            offset = 0;
        }
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT / 2; i++) {
            int idx = offset + i;
            total += par[idx];
        }
        return total;
    }

    public int[][] getAbsolutePatting() {
        int[] defPat = new int[mAbsolutePatting.length];
        return new int[][]{ mAbsolutePatting, defPat, defPat, defPat };
    }

    public int[] getAbsolutePatting(final int idx) {
        return getAbsolutePatting()[idx];
    }

    public int[] getEachHolePar() {
        return mEachHolePar;
    }

    public void setEachHolePar(final int[] eachHolePar) {
        this.mEachHolePar = eachHolePar;
    }

    public int[] getPlayersHandi() {
        return mPlayersHandi;
    }

    public void setPlayersHandi(final int[] playersHandi) {
        this.mPlayersHandi = playersHandi;
    }

    public int[] getDemoSeries(final int idx) {
        return mDemoSeries[idx];
    }

    public boolean[] getEachHoleLocked() {
        return mEachHoleLocked;
    }

    public boolean isHoleResultFixed() {
        boolean ret = true;
        for (boolean aMEachHoleLocked : mEachHoleLocked) {
            if (!aMEachHoleLocked) {
                ret = false;
            }
        }
        return ret;
    }

    public int getPlayerNum() {
        int playerNum = 0;
        for (String mPlayerName : mPlayerNames) {
            if (mPlayerName.trim().length() != 0) {
                ++playerNum;
            }
        }
        return playerNum;
    }
}
