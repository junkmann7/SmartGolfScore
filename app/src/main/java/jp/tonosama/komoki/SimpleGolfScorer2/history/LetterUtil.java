package jp.tonosama.komoki.SimpleGolfScorer2.history;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;

/**
 * @author Komoki
 */
public final class LetterUtil {

    /** 追い越す */
    public static final int OVER_STRIDE = 1;
    /** 追いつく */
    public static final int CATCHING_UP = 2;
    /** 追いつかれる */
    public static final int CATCHED_UP = 3;
    /** 追い抜かれる */
    public static final int GET_PASSED = 4;

    /**
     * コンストラクタ
     */
    private LetterUtil() {
        //
    }

    public static float getMyAverage(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        float myAverage = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            myAverage += scoreMgr.getAllScores(saveNum)[playerNum][i]
                    - scoreMgr.getAllParValues(saveNum)[i];
        }
        myAverage /= Util.TOTAL_HOLE_COUNT;

        return myAverage;
    }

    public static float getMyPatAverage(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        float myPatAverage = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            myPatAverage += scoreMgr.getMyPatting(saveNum)[i];
        }
        myPatAverage /= Util.TOTAL_HOLE_COUNT;

        return myPatAverage;
    }

    public static int getMyTotalScore(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int myTotalScore = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            myTotalScore += scoreMgr.getAllScores(saveNum)[playerNum][i];
        }

        return myTotalScore;
    }

    public static int getMyTotalScoreMinusHandi(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int myTotalScoreMinusHandi = getMyTotalScore(scoreMgr, saveNum, playerNum);
        myTotalScoreMinusHandi -= scoreMgr.getPlayersHandi(saveNum)[playerNum];

        return myTotalScoreMinusHandi;
    }

    public static int getMyBasedTotalScore(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int myTotalScore = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            myTotalScore += scoreMgr.getAllScores(saveNum)[playerNum][i]
                    - scoreMgr.getAllParValues(saveNum)[i];
        }

        return myTotalScore;
    }

    /**
     * 指定パー値のホールのスコアを取得
     * 
     * @param saveNum セーブ番号
     * @param playerNum プレイヤー番号
     * @param par パー値
     * @return 指定パーのホールのスコア平均
     */
    public static float getMyEachHoleScore(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum, final int par) {
        float[] eachHoleScore = { 0f, 0f, 0f, 0f };
        int[] eachHoleNum = { 0, 0, 0, 0 };
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            if (scoreMgr.getAllParValues(saveNum)[i] == 2) {
                eachHoleScore[0] += scoreMgr.getAllScores(saveNum)[playerNum][i] - 2;
                eachHoleNum[0]++;
            }
            if (scoreMgr.getAllParValues(saveNum)[i] == 3) {
                eachHoleScore[1] += scoreMgr.getAllScores(saveNum)[playerNum][i] - 3;
                eachHoleNum[1]++;
            }
            if (scoreMgr.getAllParValues(saveNum)[i] == 4) {
                eachHoleScore[2] += scoreMgr.getAllScores(saveNum)[playerNum][i] - 4;
                eachHoleNum[2]++;
            }
            if (scoreMgr.getAllParValues(saveNum)[i] == 5) {
                eachHoleScore[3] += scoreMgr.getAllScores(saveNum)[playerNum][i] - 5;
                eachHoleNum[3]++;
            }
        }
        if (0 < eachHoleNum[0]) {
            eachHoleScore[0] /= eachHoleNum[0];
        }
        if (0 < eachHoleNum[1]) {
            eachHoleScore[1] /= eachHoleNum[1];
        }
        if (0 < eachHoleNum[2]) {
            eachHoleScore[2] /= eachHoleNum[2];
        }
        if (0 < eachHoleNum[3]) {
            eachHoleScore[3] /= eachHoleNum[3];
        }

        return eachHoleScore[par - 2];
    }

    /**
     * 指定プレイヤーの不得意なパー値を取得
     * 
     * @param saveNum セーブ番号
     * @param playerNum プレイヤー番号
     * @return 不得意なパー値
     */
    public static int getMyNotGoodHole(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        float worstScore = getMyEachHoleScore(scoreMgr, saveNum, playerNum, 2);
        int worstHole = 2;
        if (worstScore < getMyEachHoleScore(scoreMgr, saveNum, playerNum, 3)) {
            worstScore = getMyEachHoleScore(scoreMgr, saveNum, playerNum, 3);
            worstHole = 3;
        }
        if (worstScore < getMyEachHoleScore(scoreMgr, saveNum, playerNum, 4)) {
            worstScore = getMyEachHoleScore(scoreMgr, saveNum, playerNum, 4);
            worstHole = 4;
        }
        if (worstScore < getMyEachHoleScore(scoreMgr, saveNum, playerNum, 5)) {
            worstHole = 5;
        }

        return worstHole;
    }

    public static int getMyRanking(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int myRank = 1;
        int myScore = getMyTotalScore(scoreMgr, saveNum, playerNum)
                - scoreMgr.getPlayersHandi(saveNum)[playerNum]; // mod for ver 2.0.1
        for (int i = 0; i < scoreMgr.getPlayerNum(saveNum); i++) {
            if (myScore > (getMyTotalScore(scoreMgr, saveNum, i) - scoreMgr
                    .getPlayersHandi(saveNum)[i])) {
                myRank++; // mod for ver 2.0.1
            }
        }
        return myRank;
    }

    //いいスコアだけを9ホール分集めたら？
    public static int getMyPottentialScore(final SaveDataList scoreMgr, final int saveNum,
            final int rankNum) {
        //String comment="";
        //String Debug="";
        //int myBasedScore;
        int[] scoreSort = new int[Util.TOTAL_HOLE_COUNT];
        int temp = 0;
        int sortCount = 1;
        int pottentialScore = 0;

        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            scoreSort[i] = scoreMgr.getAllScores(saveNum)[rankNum][i]
                    - scoreMgr.getAllParValues(saveNum)[i];
        }
        while (sortCount == 1) {
            sortCount = 0;
            for (int i = 0; i < Util.TOTAL_HOLE_COUNT - 1; i++) {
                if (scoreSort[i] > scoreSort[i + 1]) {
                    temp = scoreSort[i];
                    scoreSort[i] = scoreSort[i + 1];
                    scoreSort[i + 1] = temp;
                    sortCount = 1;
                }
            }

        }
        for (int i = 0; i < 9; i++) {
            pottentialScore += (scoreSort[i] + scoreMgr.getAllParValues(saveNum)[i]);

        }
        pottentialScore *= 2;

        return pottentialScore;
    }

    public static float getMyBogeyOnKeepRate(final SaveDataList scoreMgr, final int saveNum) {
        float parOnRate = 0f;
        int count = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            if (scoreMgr.getMyPatting(saveNum)[i] == 0) {
                if (scoreMgr.getAllScores(saveNum)[0][i] <= //
                scoreMgr.getAllParValues(saveNum)[i] + 1) {
                    parOnRate += 1f;

                }
                count++;
            } else {
                if ((scoreMgr.getAllScores(saveNum)[0][i] - //
                scoreMgr.getMyPatting(saveNum)[i]) <= scoreMgr.getAllParValues(saveNum)[i] - 1) {
                    parOnRate += 1f;
                }
            }
        }

        if (count < 16) {
            return (parOnRate / Util.TOTAL_HOLE_COUNT * 100f);
        } else { //入力が無い場合は0%を返す
            return 0.0F;
        }
    }

    public static float getMyParOnKeepRate(final SaveDataList scoreMgr, final int saveNum) {
        float parOnRate = 0f;
        int count = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            if (scoreMgr.getMyPatting(saveNum)[i] == 0) {
                if (scoreMgr.getAllScores(saveNum)[0][i] <= scoreMgr.getAllParValues(saveNum)[i]) {
                    parOnRate += 1f;

                }
                count++;
            } else {
                if ((scoreMgr.getAllScores(saveNum)[0][i] //
                - scoreMgr.getMyPatting(saveNum)[i]) <= //
                scoreMgr.getAllParValues(saveNum)[i] - 2) {
                    parOnRate += 1f;
                }
            }
        }

        if (count < 16) {
            return (parOnRate / Util.TOTAL_HOLE_COUNT * 100f);
        } else { //入力が無い場合は0%を返す
            return 0.0F;
        }

    }

    public static boolean getIsShortHole(final SaveDataList scoreMgr, final int saveNum) {
        boolean isShortHole = true;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            if (scoreMgr.getAllParValues(saveNum)[i] != 3) {
                isShortHole = false;
            }
        }

        return isShortHole;
    }

    public static int[] getPlayerRankings(final SaveDataList scoreMgr, final int saveNum) {
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int[] playerRanking = new int[playernum];

        for (int i = 0; i < playernum; i++) {
            int myRank = 1;
            for (int j = 0; j < playernum; j++) {
                if ((getMyTotalScore(scoreMgr, saveNum, i) //
                - scoreMgr.getPlayersHandi(saveNum)[i]) > //
                (getMyTotalScore(scoreMgr, saveNum, j) - //
                scoreMgr.getPlayersHandi(saveNum)[j])) {
                    myRank++; // mod for ver 2.0.1
                }
            }
            playerRanking[i] = myRank;
        }

        return playerRanking;
    }

    public static int[] getPlayerRankingsSortedByRank(final SaveDataList scoreMgr,
            final int saveNum) {
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int[] normalRanking = getPlayerRankings(scoreMgr, saveNum);
        int[] sortedRanking = new int[playernum];

        for (int i = 0; i < playernum; i++) {
            sortedRanking[i] = normalRanking[getPlayerRankingArray(scoreMgr, saveNum)[i]];
        }

        return sortedRanking;
    }

    public static int[] getFirstRankings(final SaveDataList scoreMgr, final int saveNum) {
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int[] firstRanking = new int[playernum];
        for (int i = 0; i < playernum; i++) {
            int myRank = 1;
            for (int j = 0; j < playernum; j++) {
                if (getMyFirstHalfScore(scoreMgr, saveNum, i)
                        - scoreMgr.getPlayersHandi(saveNum)[i] > getMyFirstHalfScore(scoreMgr,
                        saveNum, j) - scoreMgr.getPlayersHandi(saveNum)[j]) {
                    myRank++;
                }
            }
            firstRanking[i] = myRank;
        }

        return firstRanking;
    }

    public static int[] getFirstRankingsSortedByRank(final SaveDataList scoreMgr,
            final int saveNum) {
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int[] firstRanking = getFirstRankings(scoreMgr, saveNum);
        int[] sortedRanking = new int[playernum];

        for (int i = 0; i < playernum; i++) {
            sortedRanking[i] = firstRanking[getPlayerRankingArray(scoreMgr, saveNum)[i]];
        }

        return sortedRanking;
    }

    public static int[] getPlayerRankingArray(final SaveDataList scoreMgr, final int saveNum) {
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int[] playerRankings = getPlayerRankings(scoreMgr, saveNum);
        int[] rankingsArray = new int[playernum];

        int count = 0;
        int rank = 1;
        for (int k = 0; k < playernum; k++) {
            for (int j = 0; j < playernum; j++) {
                if (playerRankings[j] == rank) {
                    rankingsArray[count] = j;
                    count++;
                }
            }
            rank++;
            if (count == playernum) {
                break;
            }
        }

        return rankingsArray;
    }

    public static int getMyFirstHalfScore(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int myFirstScore = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT / 2; i++) {
            myFirstScore += scoreMgr.getAllScores(saveNum)[playerNum][i];
        }

        return myFirstScore;
    }

    public static int getMySecondHalfScore(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int mySecondScore = 0;
        for (int i = Util.TOTAL_HOLE_COUNT / 2; i < Util.TOTAL_HOLE_COUNT; i++) {
            mySecondScore += scoreMgr.getAllScores(saveNum)[playerNum][i];
        }

        return mySecondScore;
    }

    public static int getRankingImageResourceByRank(final int rank) {
        int[] images = { R.drawable.rank_gold_parcolation, R.drawable.rank_silver_parcolation,
                R.drawable.rank_bronze_parcolation, R.drawable.mypatter5 };

        return images[rank - 1];
    }

    public static int getMyBestHole(final SaveDataList scoreMgr, final int saveNum,
            final int playerNum) {
        int mScore = 10;
        int tmp = 0;

        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            tmp = scoreMgr.getAllScores(saveNum)[playerNum][i]
                    - scoreMgr.getAllParValues(saveNum)[i];
            if (scoreMgr.getAllScores(saveNum)[playerNum][i] == 1) { // ホールインワンなら
                return -10;
            } else if (mScore > tmp) { // ・それ以外なら
                mScore = tmp;
            }
        }

        return mScore;
    }

    /**
     * @param playerNum int
     * @param saveNum int
     * @param scoreMgr GolfScoreManager
     * @return Par Based Score
     */
    public static int[][] getParBasedScore(final int playerNum, final int saveNum,
            final SaveDataList scoreMgr) {
        int tempSum;
        int[][] mAllScoreBasedPar = new int[playerNum][Util.TOTAL_HOLE_COUNT];
        for (int i = 0; i < playerNum; i++) {
            tempSum = -scoreMgr.getHandiCaps(saveNum)[i]; //ハンディ付きで計算
            for (int k = 0; k < Util.TOTAL_HOLE_COUNT; k++) {
                tempSum += scoreMgr.getAllScores(saveNum)[i][k];
                mAllScoreBasedPar[i][k] = tempSum;
            }
        }
        return mAllScoreBasedPar;
    }

    /**
     * @param temp MySelf
     * @param best MySelf
     * @return MySelf
     */
    public static MySelf myselfCalcBefore(final MySelf temp, final MySelf best) {

        if (best.getBestPad() > temp.getBestPad() && temp.getBestPad() > 0.5) {
            best.setBestPad(temp.getBestPad());
        }
        if (best.getBestHalf() > temp.getBestLastHalf()) {
            best.setBestHalf(temp.getBestLastHalf());
        }
        if (best.getBestHalf() > temp.getBestFirstHalf()) {
            best.setBestHalf(temp.getBestFirstHalf());
        }
        if (best.getBestTotal() > temp.getBestTotal()) {
            best.setBestTotal(temp.getBestTotal());
        }
        if (best.getBestHole() > temp.getBestHole()) {
            best.setBestHole(temp.getBestHole());
        }
        if (best.getBestPottential() > temp.getBestPottential()) {
            best.setBestPottential(temp.getBestPottential());
        }
        if (best.getBestParOn() < temp.getBestParOn()) {
            best.setBestParOn(temp.getBestParOn());
        }
        best.setCount(best.getCount() + 1);
        best.setTotalScoreAvg(best.getTotalScoreAvg() + temp.getTmpAvg()); // あとで割る
        best.setTotalPadAvg(best.getTotalPadAvg() + temp.getBestPad()); // あとで割る

        return best;
    }

    /**
     * @param old MySelf
     * @param cur MySelf
     * @return updateCount
     */
    public static int getMyselfCalcCompareUpdateCount(final MySelf old, final MySelf cur) {
        int updateCount = 0;
        if (old.getBestPad() > cur.getBestPad() && cur.getBestPad() > 0.5) {
            // 0.5はpadが0担ってる場合を回避
            updateCount++;
        } else if (old.getTotalPadAvg() * 0.9 > cur.getBestPad() && cur.getBestPad() > 0.5) {
            // 平均より10%よければ
            updateCount++;
        } else if (old.getTotalPadAvg() * 1.2 < cur.getBestPad() && cur.getBestPad() > 0.5) {
            // 平均より20%悪ければ
            updateCount += 0;
        }
        if (old.getBestHalf() > cur.getBestLastHalf() || //
                old.getBestHalf() > cur.getBestFirstHalf()) {
            updateCount++;
        }
        if (old.getBestTotal() > cur.getBestTotal()) {
            updateCount++;
        } else if (old.getTotalScoreAvg() * 0.9 > cur.getBestTotal()) {
            // 平均より10%よければ
            updateCount++;
        } else if (old.getTotalScoreAvg() * 1.2 < cur.getBestTotal()) {
            // 平均より20%悪ければ
            updateCount += 0;
        }
        if (old.getBestHole() > cur.getBestHole()) {
            updateCount++;
        }
        if (old.getBestPottential() > cur.getBestPottential()) {
            updateCount += 0;
        }
        if (old.getBestParOn() < cur.getBestParOn()) {
            updateCount++;
        }
        return updateCount;
    }

    /**
     * @param playernum int
     * @param mAllScoreBasedPar int[][]
     * @param closeBattleCount int[]
     * @param playerCloseBattleFlag int[]
     * @param mTurningPoint int[]
     * @param mTurningChange int[]
     * @param pIdx int
     */
    public static void getRivalData(final int playernum, final int[][] mAllScoreBasedPar,
            final int[] closeBattleCount, final int[] playerCloseBattleFlag,
            final int[] mTurningPoint, final int[] mTurningChange, final int pIdx) {
        int[] upDown = { 0, 0 };
        for (int i = 0; i < playernum; i++) {
            for (int k = 0; k < Util.TOTAL_HOLE_COUNT; k++) {
                if (i != pIdx) { //同プレイヤーは計算なし
                    if (k > 4) { //5ホール目以降で順位の入れ替わりがあればカウントアップ
                        if ((mAllScoreBasedPar[i][k]) < (mAllScoreBasedPar[pIdx][k])) {
                            upDown[0] = -1; //トータルスコアが下
                        } else if ((mAllScoreBasedPar[i][k]) > (mAllScoreBasedPar[pIdx][k])) {
                            upDown[0] = 1; //トータルスコアが上
                        } else {
                            upDown[0] = 0;
                        }
                    }
                    int forABS = (mAllScoreBasedPar[i][k]) - (mAllScoreBasedPar[pIdx][k]); //平方根用
                    if (forABS * forABS <= 9 && k > 4) { //５ホール明光でで３打以内ならカウントアップ
                        closeBattleCount[i]++;
                    }
                    if (upDown[1] != upDown[0]) { ///順位にライバルとの順位変動があった場合
                        playerCloseBattleFlag[i]++;
                        mTurningPoint[i] = k; //ターニングポイント
                        if (upDown[0] > upDown[1]) { //追い上げ
                            if (upDown[0] == 0) {
                                mTurningChange[i] = CATCHING_UP;
                            } else {
                                mTurningChange[i] = OVER_STRIDE;
                            }
                        } else {
                            if (upDown[0] == 0) {
                                mTurningChange[i] = CATCHED_UP;
                            } else {
                                mTurningChange[i] = GET_PASSED;
                            }
                        }
                    }
                    upDown[1] = upDown[0];
                }
            }
        }
    }
}