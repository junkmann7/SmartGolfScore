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
    private int[] mDemoXaxisData = //
    { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18 };
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

    /**
     * @return
     */
    public int getSaveIdx() {
        return mSaveIdx;
    }

    /**
     * @param saveIdx
     */
    public void setSaveIdx(final int saveIdx) {
        this.mSaveIdx = saveIdx;
    }

    /**
     * @return
     */
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

    /**
     * @param is18Hround
     */
    public void setIs18Hround(final boolean is18Hround) {
        mIs18Hround = is18Hround;
    }

    /**
     * @return true: false:
     */
    public boolean getIsShortHole() {
        boolean isShortHole = true;
        int[] parScore = getEachHolePar();
        for (int i = 0; i < parScore.length; i++) {
            if (parScore[i] != 3) {
                isShortHole = false;
            }
        }
        return isShortHole;
    }

    /**
     * @return
     */
    public int getCurrentHole() {
        return mCurrentHole;
    }

    /**
     * @param currentHole
     */
    public void setCurrentHole(final int currentHole) {
        mCurrentHole = currentHole;
    }

    /**  */
    private int mCondition;

    /**
     * @return
     */
    public int getCondition() {
        return mCondition;
    }

    /**
     * @param condition
     */
    public void setCondition(final int condition) {
        mCondition = condition;
    }

    /**
     * @return
     */
    public boolean isOutputImageFlg() {
        return mOutputImageFlg;
    }

    /**
     * @param outputImageFlg
     */
    public void setOutputImageFlg(final boolean outputImageFlg) {
        mOutputImageFlg = outputImageFlg;
    }

    /**  */
    private String[] mDemoAxesLabels = { "HOLE", "SCORE" };

    /**
     * @return
     */
    public String[] getDemoAxesLabels() {
        return mDemoAxesLabels;
    }

    /**
     * @param demoAxesLabels
     */
    public void setDemoAxesLabels(final String[] demoAxesLabels) {
        mDemoAxesLabels = demoAxesLabels;
    }

    /**
     * @return
     */
    public int[] getDemoXaxisData() {
        return mDemoXaxisData;
    }

    /**
     * @param demoXaxisData
     */
    public void setDemoXaxisData(final int[] demoXaxisData) {
        mDemoXaxisData = demoXaxisData;
    }

    /**
     * @return
     */
    public String[] getNames() {
        return mPlayerNames;
    }

    /**
     * @param names
     */
    public void setNames(final String[] names) {
        mPlayerNames = names;
    }

    /**
     * @return
     */
    public int[] getPlayersAlpha() {
        return mPlayersAlpha;
    }

    /**
     * @param playersAlpha
     */
    public void setPlayersAlpha(final int[] playersAlpha) {
        mPlayersAlpha = playersAlpha;
    }

    public String getMemoStr() {
        return mMemoStr;
    }

    /**
     * @param memo
     */
    public void setMemoStr(final String memo) {
        mMemoStr = memo;
    }

    public String getHoleTitle() {
        return mDemoChartTitle;
    }

    public void setHoleTitle(final String holeTitle) {
        mDemoChartTitle = holeTitle;
    }

    /**
     * @return
     */
    public int[][] getAbsoluteScore() {
        return mAbsoluteScore;
    }

    /**
     * @param idx
     * @return
     */
    public int[] getAbsoluteScore(final int idx) {
        return mAbsoluteScore[idx];
    }

    /**
     * @param absoluteScore
     * @param idx
     */
    public void setAbsoluteScore(final int[] absoluteScore, final int idx) {
        mAbsoluteScore[idx] = absoluteScore;
    }

    /**
     * @param absoluteScore
     */
    public void setAbsoluteScore(final int[][] absoluteScore) {
        mAbsoluteScore = absoluteScore;
    }

    /**
     * @return
     */
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

    /**
     * @param forward
     * @return
     */
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

    /**
     * @return
     */
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

    /**
     * @param forward
     * @return
     */
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

    /**
     * @return
     */
    public int getTotalParScore() {
        int total = 0;
        int[] scores = getEachHolePar();
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            total += scores[i];
        }
        return total;
    }

    /**
     * @param forward
     * @return
     */
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

    /**
     * @return
     */
    public int[][] getAbsolutePatting() {
        int[] defPat = new int[mAbsolutePatting.length];
        int[][] pattingList = { mAbsolutePatting, defPat, defPat, defPat };
        return pattingList;
    }

    /**
     * @return
     */
    public int[] getAbsolutePatting(final int idx) {
        return getAbsolutePatting()[idx];
    }

    public void setAbsolutePatting(final int[] absolutePatting) {
        this.mAbsolutePatting = absolutePatting;
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

    /**
     * @param idx
     * @return
     */
    public int[] getDemoSeries(final int idx) {
        return mDemoSeries[idx];
    }

    /**
     * @param demoSeries
     * @param idx
     */
    public void setDemoSeries(final int[] demoSeries, final int idx) {
        mDemoSeries[idx] = demoSeries;
    }

    /**
     * @return
     */
    public int[][] getDemoSeriesList() {
        int[][] scores = { mDemoSeries1, mDemoSeries2, mDemoSeries3, mDemoSeries4 };
        return scores;
    }

    public boolean[] getEachHoleLocked() {
        return mEachHoleLocked;
    }

    public void setEachHoleLocked(final boolean[] eachHoleLocked) {
        this.mEachHoleLocked = eachHoleLocked;
    }

    public boolean isHoleResultFixed() {
        boolean ret = true;
        for (int i = 0; i < mEachHoleLocked.length; i++) {
            if (!mEachHoleLocked[i]) {
                ret = false;
            }
        }
        return ret;
    }

    public int getPlayerNum() {
        int playerNum = 0;
        for (int i = 0; i < mPlayerNames.length; i++) {
            if (mPlayerNames[i].trim().length() != 0) {
                ++playerNum;
            }
        }
        return playerNum;
    }
}
