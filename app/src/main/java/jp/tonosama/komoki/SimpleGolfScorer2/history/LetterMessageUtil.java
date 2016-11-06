package jp.tonosama.komoki.SimpleGolfScorer2.history;

import android.content.Context;
import android.content.res.Resources;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;

/**
 * @author Komoki
 */
public final class LetterMessageUtil {

    /**
     * コンストラクタ
     */
    private LetterMessageUtil() {
        //
    }

    /**
     * @param context Context
     * @return Vital結果文字列
     */
    private static String[] getVitalResultStr(final Context context) {
        String[] vitalResult = { context.getResources().getString(R.string.myhist_vital_score_0),
                context.getResources().getString(R.string.myhist_vital_score_0),
                context.getResources().getString(R.string.myhist_vital_score_1),
                context.getResources().getString(R.string.myhist_vital_score_2),
                context.getResources().getString(R.string.myhist_vital_score_2) };
        return vitalResult;
    }

    public static String getMyVitalScore(final Context context, final SaveDataList scoreMgr,
            final int saveNum, final int playerNum) {

        int vitalScore = 0;
        String[] mVitalResult = getVitalResultStr(context);
        float[] retScores = new float[6];
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            final int score = scoreMgr.getAllScores(saveNum)[playerNum][i];
            final int par = scoreMgr.getAllParValues(saveNum)[i];
            if (i < Util.TOTAL_HOLE_COUNT / 2) {
                retScores[0] += (score - par);
            } else {
                retScores[1] += (score - par);
            }
            if (0 <= i && i <= 4) {
                retScores[2] += (score - par);
            } else if (4 <= i && i <= 8) {
                retScores[3] += (score - par);
            } else if (9 <= i && i <= 13) {
                retScores[4] += (score - par);
            } else if (13 <= i) {
                retScores[5] += (score - par);
            }
        }
        if (LetterUtil.getMyEachHoleScore(scoreMgr, saveNum, playerNum, 3) > LetterUtil
                .getMyEachHoleScore(scoreMgr, saveNum, playerNum, 5)) {
            vitalScore++;
        }
        if (retScores[0] > retScores[1]) {
            vitalScore++;
        }
        if (retScores[2] > retScores[3]) {
            vitalScore++;
        }
        if (retScores[4] > retScores[5]) {
            vitalScore++;
        }

        return mVitalResult[vitalScore];
    }

    public static String getMyPressureScore(final Context context, final SaveDataList scoreMgr,
            final int saveNum, final int playerNum) {
        int pressureScore = 0;
        String[] mPressureResult = {
                context.getResources().getString(R.string.myhist_pressure_score_0),
                context.getResources().getString(R.string.myhist_pressure_score_1),
                context.getResources().getString(R.string.myhist_pressure_score_2),
                context.getResources().getString(R.string.myhist_pressure_score_3),
                context.getResources().getString(R.string.myhist_pressure_score_4), };
        int underAve = 0;
        int overAve = 0;
        int underAveNum = 0;
        int overAveNum = 0;
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            // better than my average
            if (LetterUtil.getMyAverage(scoreMgr, saveNum, playerNum) > (scoreMgr
                    .getAllScores(saveNum)[playerNum][i] - scoreMgr.getAllParValues(saveNum)[i])) {
                underAve += scoreMgr.getMyPatting(saveNum)[i];
                underAveNum++;
                // worse than my average
            } else {
                overAve += scoreMgr.getMyPatting(saveNum)[i];
                overAveNum++;
            }
        }
        pressureScore = (underAve / underAveNum) - (overAve / overAveNum);
        if (pressureScore > 1.5f) {
            return mPressureResult[0];
        } else if (pressureScore > 0.5f) {
            return mPressureResult[1];
        } else if (pressureScore > -0.5f) {
            return mPressureResult[2];
        } else if (pressureScore > -1.5f) {
            return mPressureResult[3];
        } else {
            return mPressureResult[4];
        }
    }

    public static String getMyAttitude(final Context context, final SaveDataList scoreMgr,
            final int saveNum, final int playerNum) {
        String[] myAttitude = { context.getResources().getString(R.string.myhist_attitude_score_0),
                context.getResources().getString(R.string.myhist_attitude_score_1),
                context.getResources().getString(R.string.myhist_attitude_score_2),
                context.getResources().getString(R.string.myhist_attitude_score_3) };
        float totalDeviation = 0f;

        float myAverage = LetterUtil.getMyAverage(scoreMgr, saveNum, playerNum);
        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            float myBasedScore = (scoreMgr.getAllScores(saveNum)[playerNum][i] - //
            (float) scoreMgr.getAllParValues(saveNum)[i]);
            float myDeviation = (myBasedScore - myAverage);
            totalDeviation += myDeviation * myDeviation;
        }
        totalDeviation /= Util.TOTAL_HOLE_COUNT;
        float mySd = (float) Math.sqrt(totalDeviation);

        if (mySd <= 0.8f) {
            return myAttitude[0];
        } else if (mySd <= 1.1f) {
            return myAttitude[1];
        } else if (mySd <= 1.5f) {
            return myAttitude[2];
        } else if (mySd <= 2.0f) {
            return myAttitude[3];
        } else {
            return myAttitude[2];
        }
    }

    /**
     * スコアを文言に変える
     * 
     * @param score score
     * @param par par
     * @return スコア文言
     */
    public static String changeComentHole(final Context context, final SaveDataList scoreMgr,
            final int score, final int par) {
        String comment = null;
        int doublePar = par * 2;
        int triplePar = par * 3;
        if (score == -10) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_10));
        } else if (score == -3) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_00));
        } else if (score == -2) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_01));
        } else if (score == -1) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_02));
        } else if (score == -0) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_03));
        } else if (score == 1) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_04));
        } else if (score == 2) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_05));
        } else if (score == doublePar) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_08));
        } else if (score == triplePar) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_09));
        } else if (score == 3) {
            comment = String.format(context.getResources().getString(R.string.comment_changing_06));
        } else {
            comment = String.format(context.getResources().getString(R.string.comment_changing_07),
                    score);
        }
        return comment;
    }

    public static String getCommnetAboutWeather(final Context context,
            final SaveDataList scoreMgr, final int saveNum) {
        String mComment = "";
        int mCondition = 0;

        mCondition = scoreMgr.getWeather(saveNum);
        if (mCondition == 0) {
            mComment = String.format(context.getResources().getString(
                    R.string.comment_weather_shine));
        } else if (mCondition == 1) {
            mComment = String.format(context.getResources().getString(
                    R.string.comment_weather_cloudy));
        } else if (mCondition == 2) {
            mComment = String.format(context.getResources()
                    .getString(R.string.comment_weather_rain));
        } else if (mCondition == 3) {
            mComment = String.format(context.getResources()
                    .getString(R.string.comment_weather_wind));
        } else if (mCondition == 4) {
            mComment = String
                    .format(context.getResources().getString(R.string.comment_weather_mud));
        }

        return mComment;
    }

    /**
     * @param context Context
     * @param i int
     * @param k int
     * @param scoreMgr GolfScoreManager
     * @param saveNum int
     * @param scoreAtPar int
     * @param chipingflag int
     * @return Good Hole Comment Myself
     */
    private static String getGoodHoleCommentMyself(final Context context, final int i, final int k,
            final SaveDataList scoreMgr, final int saveNum, final int scoreAtPar,
            final int chipingflag) {
        int[][] scores = scoreMgr.getAllScores(saveNum);
        int[] parVal = scoreMgr.getAllParValues(saveNum);
        int[] patVal = scoreMgr.getMyPatting(saveNum);
        String str = "";
        Resources res = context.getResources();
        if (patVal[k] == 0) {
            if (scores[i][k] == 1) { //ホールインワン
                str += "";
                // パーなどの感想は下に移動
            } else if (chipingflag > 0) { //チップイン //patting入力が無い場合は表示しない
                str += String.format(res.getString(R.string.comment_hole_02), (k + 1), parVal[k],
                        changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
            } else {
                str += String.format(res.getString(R.string.comment_hole_00), (k + 1), parVal[k],
                        changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
            }
        } else if (patVal[k] == 1) { //1パット
            str += String.format(res.getString(R.string.comment_hole_03), (k + 1), parVal[k],
                    changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
        } else {
            str += String.format(res.getString(R.string.comment_hole_00), (k + 1), parVal[k],
                    changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
        }
        if (scores[i][k] == 1) { //ホールインワン
            str += String.format(res.getString(R.string.comment_hole_01), (k + 1), parVal[k]);
        } else if (scoreAtPar == -3) { //アルバトロス
            str += String.format(res.getString(R.string.comment_hole_16));
        } else if (scoreAtPar == -2) { //イーグル
            str += String.format(res.getString(R.string.comment_hole_17));
        } else if (scoreAtPar == -1) { //バーディ
            str += String.format(res.getString(R.string.comment_hole_18));
        }
        return str;
    }

    /**
     * @param context Context
     * @param i int
     * @param k int
     * @param scoreMgr GolfScoreManager
     * @param saveNum int
     * @param scoreAtPar int
     * @param chipingflag int
     * @return Good Hole Comment
     */
    private static String getGoodHoleComment(final Context context, final int i, final int k,
            final SaveDataList scoreMgr, final int saveNum, final int scoreAtPar,
            final int chipingflag) {
        int[] parVal = scoreMgr.getAllParValues(saveNum);
        String str = "";
        Resources res = context.getResources();
        // 左端が本人だとする。getMyNameがほしいぜ
        if (i != 0) { //本人以外
            str += String.format(res.getString(R.string.comment_hole_00), (k + 1), parVal[k],
                    changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
        } else { // 本人
            str += getGoodHoleCommentMyself(context, i, k, scoreMgr, saveNum, scoreAtPar,
                    chipingflag);
        }
        return str;
    }

    /**
     * @param context Context
     * @param k int
     * @param scoreMgr GolfScoreManager
     * @param parVal int[]
     * @param scoreAtPar int
     * @return Bad Comment
     */
    private static String getBadComment(final Context context, final int k,
            final SaveDataList scoreMgr, final int[] parVal, final int scoreAtPar) {

        String mBadHoleComment = "";
        mBadHoleComment += String.format(
                context.getResources().getString(R.string.comment_hole_00), (k + 1), parVal[k],
                LetterMessageUtil.changeComentHole(context, scoreMgr, scoreAtPar, parVal[k]));
        if (scoreAtPar >= (parVal[k] * 2)) { //ダブルパー以上
            mBadHoleComment += String.format(context.getResources().getString(
                    R.string.comment_hole_21));
        } else if (scoreAtPar >= (parVal[k])) { //トリプルパー以上
            mBadHoleComment += String.format(context.getResources().getString(
                    R.string.comment_hole_20));
        }
        return mBadHoleComment;
    }

    /**
     * @param context Context
     * @param goodHoleCount int
     * @param badHoleCount int
     * @param goodHoleComment String
     * @param badHoleComment String
     * @return Total Hole Comment
     */
    private static String getTotalHoleComment(final Context context, final int goodHoleCount,
            final int badHoleCount, final String goodHoleComment, final String badHoleComment) {

        String str = "";
        str += String.format(context.getResources().getString(R.string.comment_hole_09));
        if (goodHoleCount == 0 && badHoleCount == 0) { //良くも悪くも無い場合
            str += String.format(context.getResources().getString(R.string.comment_hole_04));
        } else {
            if (goodHoleCount >= 1) { //良かったホールがある場合
                str += goodHoleComment;
                str += String.format(context.getResources().getString(R.string.comment_hole_05));
            } else {
                str += String.format(context.getResources().getString(R.string.comment_hole_06));
            }
            if (badHoleCount >= 1) { //悪かったホールがある場合
                str += badHoleComment;
                str += String.format(context.getResources().getString(R.string.comment_hole_07));
            } else {
                str += String.format(context.getResources().getString(R.string.comment_hole_08));
            }
            if (goodHoleCount - badHoleCount > 1) {
                str += String.format(context.getResources().getString(R.string.comment_hole_11));
            } else if (goodHoleCount - badHoleCount > 0) {
                str += String.format(context.getResources().getString(R.string.comment_hole_12));
            } else if (goodHoleCount - badHoleCount == 0 && goodHoleCount == 1) {
                str += String.format(context.getResources().getString(R.string.comment_hole_13));
            } else if (goodHoleCount - badHoleCount == 0 && goodHoleCount > 1) {
                str += String.format(context.getResources().getString(R.string.comment_hole_10));
            } else if (goodHoleCount - badHoleCount < 0) {
                str += String.format(context.getResources().getString(R.string.comment_hole_14));
            } else if (goodHoleCount - badHoleCount > -1) {
                str += String.format(context.getResources().getString(R.string.comment_hole_15));
            }
        }
        return str;
    }

    public static String[] getCommentAboutHole(final Context context,
            final SaveDataList scoreMgr, final int saveNum) {

        String[] mTotalHoleComent = { "", "", "", "" };
        int playernum = scoreMgr.getPlayerNum(saveNum);
        int chipingflag = 0; // patting入力が無い場合は表示しない
        int[][] scores = scoreMgr.getAllScores(saveNum);
        int[] parVal = scoreMgr.getAllParValues(saveNum);

        if (LetterUtil.getMyPatAverage(scoreMgr, saveNum, 0) > 0.5) { //patting入力が無い場合は表示しない
            chipingflag++;
        }
        for (int i = 0; i < playernum; i++) {
            StringBuilder mGoodHoleComment = new StringBuilder();
            StringBuilder mBadHoleComment = new StringBuilder();
            int mGoodHoleCount = 0;
            int mBadHoleCount = 0;
            int scoreAtPar = 0;
            float avg;
            for (int k = 0; k < Util.TOTAL_HOLE_COUNT; k++) {
                avg = LetterUtil.getMyAverage(scoreMgr, saveNum, i);
                scoreAtPar = scores[i][k] - parVal[k];
                if (scoreAtPar - avg > 1.9f) {
                    //bad hole
                    mBadHoleCount++;
                    mBadHoleComment.append(getBadComment(context, k, scoreMgr, parVal, scoreAtPar));
                } else if (scoreAtPar - avg < -2.0f
                        || (scoreAtPar <= 0 && scoreAtPar - avg < -1.5f)) {
                    //good hole
                    mGoodHoleCount++;
                    mGoodHoleComment.append(getGoodHoleComment(context, i, k, scoreMgr, saveNum,
                            scoreAtPar, chipingflag));
                }
            }
            //一人分が終わったので連結する
            mTotalHoleComent[i] += getTotalHoleComment(context, mGoodHoleCount, mBadHoleCount,
                    mGoodHoleComment.toString(), mBadHoleComment.toString());
        }
        return mTotalHoleComent;
    }

    public static String getCommnetAboutCount(final Context context,
            final SaveDataList scoreMgr, final int saveNum) {
        String comment = "";
        if (saveNum >= 200) {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_05));
        } else if (saveNum >= 100) {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_04));
        } else if (saveNum >= 20) {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_03));
        } else if (saveNum >= 5) {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_02));
        } else if (saveNum >= 2) {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_01));
        } else {
            comment = String.format(context.getResources().getString(
                    R.string.comment_round_counter_00));
        }
        return comment;
    }

    /**
     * @param context Context
     * @param closeBattleLevel int
     * @return Rival Num Based Str
     */
    private static String getRivalNumBasedStr(final Context context, final int closeBattleLevel) {
        String str = "";
        if (closeBattleLevel == 0) {
            str += String.format(context.getResources().getString(R.string.comment_rival_04));
        } else if (closeBattleLevel < 2) {
            str += String.format(context.getResources().getString(R.string.comment_rival_17)
                    + context.getResources().getString(R.string.comment_rival_05));
        } else if (closeBattleLevel < 3) {
            str += String.format(context.getResources().getString(R.string.comment_rival_17)
                    + context.getResources().getString(R.string.comment_rival_06));
        } else {
            str += String.format(context.getResources().getString(R.string.comment_rival_17)
                    + context.getResources().getString(R.string.comment_rival_07));
        }
        return str;
    }

    /**
     * @param c Context
     * @param playerNum int
     * @param scoreMgr GolfScoreManager
     * @param saveNum int
     * @param mTurningPoint int[]
     * @param mTurningChange int[]
     * @return Turning Change Based Str
     */
    private static String getTurningChangeBasedStr(final Context c, final int playerNum,
            final SaveDataList scoreMgr, final int saveNum, final int[] mTurningPoint,
            final int[] mTurningChange) {

        int mMaxTurnChg = 0;
        int mMaxTurning = 0;
        String name = "";
        String str = "";
        Resources res = c.getResources();
        for (int t = 0; t < playerNum; t++) {
            if (mMaxTurning < mTurningPoint[t]) {
                mMaxTurning = mTurningPoint[t];
                mMaxTurnChg = mTurningChange[t];
                name = String.format(scoreMgr.getPlayerNames(saveNum)[t]);
            }
        }
        if (mMaxTurning >= 6) {
            str += String.format(res.getString(R.string.comment_rival_08), mMaxTurning);
            if (mMaxTurnChg == LetterUtil.OVER_STRIDE) {
                str += String.format(res.getString(R.string.comment_rival_09), name);
            } else if (mMaxTurnChg == LetterUtil.CATCHING_UP) {
                str += String.format(res.getString(R.string.comment_rival_10), name);
            } else if (mMaxTurnChg == LetterUtil.CATCHED_UP) {
                str += String.format(res.getString(R.string.comment_rival_11), name);
            } else {
                str += String.format(res.getString(R.string.comment_rival_12), name);
            }
            if (mMaxTurning > 15) {
                str += String.format(res.getString(R.string.comment_rival_13), name);
            } else if (mMaxTurning > 9) {
                str += String.format(res.getString(R.string.comment_rival_14), name);
            } else {
                str += String.format(res.getString(R.string.comment_rival_15), name);
            }
        } else {
            str += String.format(res.getString(R.string.comment_rival_16), name);
        }
        return str;
    }

    /**
     * @param context Context
     * @param scoreMgr GolfScoreManager
     * @param saveNum int
     * @param t int
     * @param closeBattleLevel int
     * @return Close Battle Str
     */
    private static String getCloseBattleStr(final Context context, final SaveDataList scoreMgr,
            final int saveNum, final int t, final int closeBattleLevel) {
        String str = "";
        if (closeBattleLevel >= 1) {
            str += String.format(context.getResources().getString(R.string.comment_rival_01));
        }
        str += String.format(context.getResources().getString(R.string.comment_rival_02),
                scoreMgr.getPlayerNames(saveNum)[t]);
        return str;
    }

    public static String[] getCommentAboutRival(final Context cxt, final SaveDataList scoreMgr,
            final int saveNum) {
        int playerNum = scoreMgr.getPlayerNum(saveNum);
        String[] mRivalName = { "", "", "", "" };
        //スコアの調整
        int closeLv = 0;
        int[] closeBattleCnt = new int[playerNum];
        int[] closeBattleFlg = new int[playerNum];
        int[][] mParBasedScore = LetterUtil.getParBasedScore(playerNum, saveNum, scoreMgr);
        for (int pIdx = 0; pIdx < playerNum; pIdx++) {
            int[] mTurningPoint = new int[playerNum];
            int[] mTurningChange = new int[playerNum];
            LetterUtil.getRivalData(playerNum, mParBasedScore, closeBattleCnt, closeBattleFlg,
                    mTurningPoint, mTurningChange, pIdx);
            // 一人分が終了するので初期化とライバルデータ入力
            mRivalName[pIdx] = "";
            for (int t = 0; t < playerNum; t++) {
                //接戦度が高い場合
                if (closeBattleFlg[t] > 4 || closeBattleCnt[t] > 4) {
                    mRivalName[pIdx] += getCloseBattleStr(cxt, scoreMgr, saveNum, t, closeLv);
                    closeLv++;
                }
            }
            if (closeLv != 0) {
                mRivalName[pIdx] += String.format(cxt.getResources().getString(
                        R.string.comment_rival_03));
            }
            for (int t = 0; t < playerNum; t++) { //次回ループのため初期化
                closeBattleFlg[t] = 0;
                closeBattleCnt[t] = 0;
            }
            mRivalName[pIdx] += getRivalNumBasedStr(cxt, closeLv);
            closeLv = 0;
            mRivalName[pIdx] += getTurningChangeBasedStr(cxt, playerNum, scoreMgr, saveNum,
                    mTurningPoint, mTurningChange);
        }
        return mRivalName;
    }

    public static String getCommentAboutScore(final Context context,
            final SaveDataList scoreMgr, final int saveNum, final int rankNum) {

        String comment = "";
        float diffEachHalf = (float) 0.0;

        diffEachHalf = (float) LetterUtil.getMySecondHalfScore(scoreMgr, saveNum, rankNum)
                / (float) LetterUtil.getMyFirstHalfScore(scoreMgr, saveNum, rankNum);

        if (diffEachHalf > 1.25F) { //悪い場合
            comment += String.format(context.getResources().getString(R.string.comment_ranking_12));
        } else if (diffEachHalf > 1.15F) {
            comment += String.format(context.getResources().getString(R.string.comment_ranking_13));
        } else if (diffEachHalf < 0.9F) { //いい場合
            comment += String.format(context.getResources().getString(R.string.comment_ranking_14));
        } else if (diffEachHalf < 0.8F) {
            comment += String.format(context.getResources().getString(R.string.comment_ranking_15));
        } else if (diffEachHalf < 1.05F && diffEachHalf > 0.95F) {
            comment += String.format(context.getResources().getString(R.string.comment_ranking_16));
        }
        return comment;
    }

    /**
     * @param context Context
     * @param firstRank int[]
     * @param lastRank int[]
     * @param rankArray int[]
     * @param scoreMgr GolfScoreManager
     * @param saveNum int
     * @param pIdx int
     * @return Comment About Rank
     */
    private static String getCommentAboutRankMain(final Context context, final int[] firstRank,
            final int[] lastRank, final int[] rankArray, final SaveDataList scoreMgr,
            final int saveNum, final int pIdx) {
        String comment = "";
        int playernum = scoreMgr.getPlayerNum(saveNum);
        Resources res = context.getResources();
        if (lastRank[pIdx] == 1) { //１位だったら
            if (firstRank[pIdx] == 1) {
                comment += String.format(res.getString(R.string.comment_ranking_01));
            } else if (firstRank[pIdx] == playernum) {
                comment += String.format(res.getString(R.string.comment_ranking_02));
            } else {
                comment += String.format(res.getString(R.string.comment_ranking_03));
            }
        } else if (lastRank[pIdx] == playernum) { //4位だったら
            if (firstRank[pIdx] == 1) {
                comment += String.format(res.getString(R.string.comment_ranking_04));
            } else if (firstRank[pIdx] == playernum) {
                comment += String.format(res.getString(R.string.comment_ranking_05));
            } else {
                comment += String.format(res.getString(R.string.comment_ranking_06),
                        scoreMgr.getPlayerNames(saveNum)[rankArray[lastRank[pIdx] - 2]]);
            }
        } else {
            if (firstRank[pIdx] == 2) {
                comment += String.format(res.getString(R.string.comment_ranking_08));
            } else if (firstRank[pIdx] == 3) {
                comment += String.format(res.getString(R.string.comment_ranking_09));
            } else if (firstRank[pIdx] == 1) {
                comment += String.format(res.getString(R.string.comment_ranking_17));
            } else if (firstRank[pIdx] == 4) {
                comment += String.format(res.getString(R.string.comment_ranking_18));
            }
            if (lastRank[pIdx] == 2) {
                comment += String.format(res.getString(R.string.comment_ranking_10));
            } else {
                comment += String.format(res.getString(R.string.comment_ranking_11));
            }
        }
        return comment;
    }

    public static String getCommentAboutRank(final Context context,
            final SaveDataList scoreMgr, final int saveNum, final int pIdx) {

        int[] firstRank = LetterUtil.getFirstRankingsSortedByRank(scoreMgr, saveNum);
        int[] lastRank = LetterUtil.getPlayerRankingsSortedByRank(scoreMgr, saveNum);
        int[] rankArray = LetterUtil.getPlayerRankingArray(scoreMgr, saveNum);

        return getCommentAboutRankMain(context, firstRank, lastRank, rankArray, scoreMgr, saveNum,
                pIdx);
    }

    // 今までのと今回を比較
    public static String myselfCalcCompare(final Context context, final Resources res,
            final SaveDataList scoreMgr, final MySelf old, final MySelf cur) {
        String comment = "";
        if (old.getBestPad() > cur.getBestPad() && cur.getBestPad() > 0.5) {
            comment += String.format(res.getString(R.string.comment_round_myself_00));
        } else if (old.getTotalPadAvg() * 0.9 > cur.getBestPad() && cur.getBestPad() > 0.5) {
            comment += String.format(res.getString(R.string.comment_round_myself_06));
        } else if (old.getTotalPadAvg() * 1.2 < cur.getBestPad() && cur.getBestPad() > 0.5) {
            comment += String.format(res.getString(R.string.comment_round_myself_07));
        }
        if (old.getBestHalf() > cur.getBestLastHalf() || //
                old.getBestHalf() > cur.getBestFirstHalf()) {
            comment += String.format(res.getString(R.string.comment_round_myself_01));
        }
        if (old.getBestTotal() > cur.getBestTotal()) {
            comment += String.format(res.getString(R.string.comment_round_myself_02));
        } else if (old.getTotalScoreAvg() * 0.9 > cur.getBestTotal()) { // 平均より10%よければ
            comment += String.format(res.getString(R.string.comment_round_myself_04));
        } else if (old.getTotalScoreAvg() * 1.2 < cur.getBestTotal()) { // 平均より20%悪ければ
            comment += String.format(res.getString(R.string.comment_round_myself_05));
        }
        if (old.getBestHole() > cur.getBestHole()) {
            comment += String.format(res.getString(R.string.comment_round_myself_03),
                    LetterMessageUtil.changeComentHole(context, scoreMgr, cur.getBestHole(), 10));
        }
        if (old.getBestPottential() > cur.getBestPottential()) {
            comment += String.format(res.getString(R.string.comment_round_myself_11));
        }
        if (old.getBestParOn() < cur.getBestParOn()) {
            comment += String.format(res.getString(R.string.comment_round_myself_12));
        }
        int updateCount = LetterUtil.getMyselfCalcCompareUpdateCount(old, cur);
        if (updateCount > 3) {
            comment += String.format(res.getString(R.string.comment_round_myself_08));
        } else if (updateCount > 2) {
            comment += String.format(res.getString(R.string.comment_round_myself_09));
        } else if (updateCount > 1) {
            comment += String.format(res.getString(R.string.comment_round_myself_10));
        }
        return comment;
    }

    public static String getCommentAboutMyself(final Context context,
            final SaveDataList scoreMgr, final int saveNum, final int playerNum) {
        MySelf best = new MySelf();
        MySelf temp = new MySelf();
        MySelf current = new MySelf();
        Resources res = context.getResources();
        boolean todayRoundFlag; // ショート or ロング
        if (saveNum < 3) { // 2回以下は算出しない
            return "";
        }
        todayRoundFlag = LetterUtil.getIsShortHole(scoreMgr, saveNum);
        // 前回までのを最高を計算する
        for (int i = 0; i < saveNum; i++) {
            if (LetterUtil.getIsShortHole(scoreMgr, i) == todayRoundFlag) { // ショートとロングは一緒にはしない
                temp.setBestPad(LetterUtil.getMyPatAverage(scoreMgr, i, 0));
                temp.setBestFirstHalf(LetterUtil.getMyFirstHalfScore(scoreMgr, i, 0));
                temp.setBestLastHalf(LetterUtil.getMySecondHalfScore(scoreMgr, i, 0));
                temp.setBestTotal(LetterUtil.getMyTotalScore(scoreMgr, i, 0));
                temp.setBestHole(LetterUtil.getMyBestHole(scoreMgr, i, 0));
                temp.setTmpAvg(LetterUtil.getMyAverage(scoreMgr, i, 0));
                temp.setBestPottential(LetterUtil.getMyPottentialScore(scoreMgr, i, 0));
                temp.setBestParOn(LetterUtil.getMyParOnKeepRate(scoreMgr, i));
                best = LetterUtil.myselfCalcBefore(temp, best);
            }
        }
        if (best.getCount() <= 2) {
            return "";
        }
        best.setTotalScoreAvg(best.getTotalScoreAvg() / best.getCount()); //
        best.setTotalPadAvg(best.getTotalPadAvg() / best.getCount()); //
        // 今回のと比較してコメントする
        current.setBestPad(LetterUtil.getMyPatAverage(scoreMgr, saveNum, 0));
        current.setBestFirstHalf(LetterUtil.getMyFirstHalfScore(scoreMgr, saveNum, 0));
        current.setBestLastHalf(LetterUtil.getMySecondHalfScore(scoreMgr, saveNum, 0));
        current.setBestTotal(LetterUtil.getMyTotalScore(scoreMgr, saveNum, 0));
        current.setBestHole(LetterUtil.getMyBestHole(scoreMgr, saveNum, 0));
        current.setBestPottential(LetterUtil.getMyPottentialScore(scoreMgr, saveNum, 0));
        current.setBestParOn(LetterUtil.getMyParOnKeepRate(scoreMgr, saveNum));
        current.setTmpAvg(LetterUtil.getMyAverage(scoreMgr, saveNum, 0));
        return LetterMessageUtil.myselfCalcCompare(context, res, scoreMgr, best, current);
    }

    public static String getCommentAboutPatting(final Context context,
            final SaveDataList scoreMgr, final int saveNum, final int playerNum) {
        String comment = "";

        float myPatAverage = LetterUtil.getMyPatAverage(scoreMgr, saveNum, 0);

        if (myPatAverage < 0.5) { //pat入力していないと思われる場合の回避
            return comment;
        }

        comment += String.format(context.getResources().getString(R.string.comment_patting_00),
                LetterUtil.getMyPatAverage(scoreMgr, saveNum, 0));

        if (myPatAverage < 1.9) {
            comment += String.format(context.getResources().getString(R.string.comment_patting_01));
        } else if (myPatAverage < 2.2) {
            comment += String.format(context.getResources().getString(R.string.comment_patting_02));
        } else if (myPatAverage < 2.5) {
            comment += String.format(context.getResources().getString(R.string.comment_patting_03));
        } else if (myPatAverage < 3.0) {
            comment += String.format(context.getResources().getString(R.string.comment_patting_04));
        } else if (myPatAverage >= 3.0) {
            comment += String.format(context.getResources().getString(R.string.comment_patting_05));
        }

        comment += LetterMessageUtil.getCommentAboutMyself(context, scoreMgr, saveNum, playerNum);
        return comment;
    }
}