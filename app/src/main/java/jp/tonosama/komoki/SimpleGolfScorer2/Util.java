package jp.tonosama.komoki.SimpleGolfScorer2;

import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author Komoki
 */
public final class Util {

    /**
     * Constructor
     */
    private Util() {
        //
    }

    /**  */
    public static final int INVALID = -1;
    /**  */
    public static final int TOTAL_HOLE_COUNT = 18;
    /**  */
    public static final int MAX_DATA_SAVE_NUM = 1000;
    /**  */
    public static final String[] PREF_DATA_SLOT = new String[MAX_DATA_SAVE_NUM];
    /**  */
    public static final String EXTRAS_IMAGE_URI_TABLE = "jp.tonosama.komoki.EXTRAS_IMAGE_URI_TABLE";
    /**  */
    public static final String EXTRAS_IMAGE_URI_GRAPH = "jp.tonosama.komoki.EXTRAS_IMAGE_URI_GRAPH";
    /**  */
    public static final String EXTRAS_FIXED_DATA_NUM = "jp.tonosama.komoki.EXTRAS_FIXED_DATA_NUM";
    /**  */
    public static final String EXTRAS_SAVED_DATA_NUM = "jp.tonosama.komoki.EXTRAS_SAVED_DATA_NUM";
    /**  */
    public static final String EXTRAS_SELECTED_IDX = "jp.tonosama.komoki.EXTRAS_SELECTED_IDX";
    /**  */
    public static final String EXTRAS_IS_NEW_CREATE = "jp.tonosama.komoki.EXTRAS_IS_NEW_CREATE";
    /**  */
    public static final String EXTRAS_MY_NAME = "jp.tonosama.komoki.EXTRAS_MY_NAME";
    /** */
    public static final String EXTRAS_OUT_SAVE_DATA = "jp.tonosama.komoki.EXTRAS_OUTPUT_SAVE_DATA";

    /**  */
    public static final String BACKUP_DIR_NAME = "SmartGolfScore/backup";
    /**  */
    public static final String PREF_SORT_TYPE_SETTING = "PREF_SORT_TYPE_SETTING";
    /**  */
    public static final String PREF_SORT_TYPE_KEY = "PREF_SORT_TYPE_KEY";
    /**  */
    public static final String MARKET_PACKAGE_NAME = "com.android.vending";

    /**  */
    public static final String[] PREF_DATA_KEY = { "SAVED_DATA_NUM", "HOLE_TITLE", "CUR_HOLE_NUM",
            "HOLE_PAR_SCORE", "PERSON_NAME1", "PERSON_NAME2", "PERSON_NAME3", "PERSON_NAME4",
            "PERSON1_SCORE", "PERSON2_SCORE", "PERSON3_SCORE", "PERSON4_SCORE", "HOLE_MEMO",
            "PERSON_HANDI", "IS_18H_ROUNG", "IS_HOLE_LOCKED", "PAT_SCORE_1", "CONDITION", };

    /**  */
    public static final String DEFAULT_HOLEPAR_SCORE_STR = "4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,4,";
    /**  */
    public static final int[] DEFAULT_HOLEPAR_SCORE = { 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4,
            4, 4, 4, 4 };
    /**  */
    public static final int[] DEFAULT_HOLEPAR_SHORT_SCORE = { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3,
            3, 3, 3, 3, 3, 3 };
    /**  */
    public static final int[] DEFAULT_PERSON_SCORE = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0 };
    /**  */
    public static final boolean[] DEFAULT_IS_HOLE_LOCKED = { false, false, false, false, false,
            false, false, false, false, false, false, false, false, false, false, false, false,
            false };
    /**  */
    public static final String DEFAULT_PERSON_SCORE_STR = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,";
    /**  */
    public static final String DEFAULT_IS_HOLE_LOCKED_STR = "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,";
    /**  */
    public static final int MAX_PLAYER_NUM = 4;

    static {
        for (int i = 0; i < MAX_DATA_SAVE_NUM; i++) {
            PREF_DATA_SLOT[i] = "PREF" + String.valueOf(i + 1);
        }
    }

    /**
     * @param context context
     * @param idx idx
     * @return
     */
    public static SaveData loadScoreDataFromPref(final Context context, final int idx) {

        SharedPreferences pref = context.getSharedPreferences(PREF_DATA_SLOT[idx],
                Context.MODE_PRIVATE);
        SaveData scoreData = new SaveData();
        // 保存データ番号
        scoreData.setSaveIdx(idx);
        // from 01 to 07 prefs
        loadScoreData01to07(pref, scoreData);
        // from 08 to 11 prefs
        loadScoreData08to11(pref, scoreData);
        // from 12 to 14 prefs
        loadScoreData12to14(pref, scoreData);
        // from 15 to 17 prefs
        loadScoreData15to17(pref, scoreData);

        return scoreData;
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData01to07(final SharedPreferences pref, final SaveData data) {
        String[] str = null;
        // タイトル
        String holeTitle = //
        pref.getString(PREF_DATA_KEY[1], "");
        data.setHoleTitle(holeTitle);
        // 現在ホール番号
        String curHoleNum = //
        pref.getString(PREF_DATA_KEY[2], "0");
        try {
            data.setCurrentHole(Integer.parseInt(curHoleNum));
        } catch (Exception e) {
            data.setCurrentHole(0);
        }
        // 各ホールのパー値
        String holeParScore = //
        pref.getString(PREF_DATA_KEY[3], DEFAULT_HOLEPAR_SCORE_STR);
        str = holeParScore.split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getEachHolePar()[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getEachHolePar()[x] = 4;
            }
        }
        // プレイヤーの名前
        data.getNames()[0] = pref.getString(PREF_DATA_KEY[4], "");
        data.getNames()[1] = pref.getString(PREF_DATA_KEY[5], "");
        data.getNames()[2] = pref.getString(PREF_DATA_KEY[6], "");
        data.getNames()[3] = pref.getString(PREF_DATA_KEY[7], "");
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData08to11(final SharedPreferences pref, final SaveData data) {
        String[] str = null;
        // 各プレイヤー１のスコア
        str = pref.getString(PREF_DATA_KEY[8], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getAbsoluteScore(0)[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getAbsoluteScore(0)[x] = 0;
            }
        }
        // 各プレイヤー２のスコア
        str = pref.getString(PREF_DATA_KEY[9], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getAbsoluteScore(1)[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getAbsoluteScore(1)[x] = 0;
            }
        }
        // 各プレイヤー３のスコア
        str = pref.getString(PREF_DATA_KEY[10], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getAbsoluteScore(2)[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getAbsoluteScore(2)[x] = 0;
            }
        }
        // 各プレイヤー４のスコア
        str = pref.getString(PREF_DATA_KEY[11], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getAbsoluteScore(3)[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getAbsoluteScore(3)[x] = 0;
            }
        }
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData12to14(final SharedPreferences pref, final SaveData data) {
        String[] str = null;
        // メモ
        String homeMemo = pref.getString(PREF_DATA_KEY[12], "");
        data.setMemoStr(homeMemo);
        // ハンディキャップ
        str = pref.getString(PREF_DATA_KEY[13], "0,0,0,0,").split(",");
        for (int x = 0; x < 4; x++) {
            try {
                data.getPlayersHandi()[x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getPlayersHandi()[x] = 0;
            }
        }
        // １８ホール or ハーフ
        String is18Hround = pref.getString(PREF_DATA_KEY[14], "1");
        try {
            data.setIs18Hround(1 == Integer.parseInt(is18Hround));
        } catch (Exception e) {
            data.setIs18Hround(true);
        }
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData15to17(final SharedPreferences pref, final SaveData data) {
        String[] str = null;
        // 各ホールの確定済み判定
        str = pref.getString(PREF_DATA_KEY[15], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getEachHoleLocked()[x] = (1 == Integer.parseInt(str[x]));
            } catch (Exception e) {
                data.getEachHoleLocked()[x] = false;
            }
        }
        // プレイヤー１のパットスコア
        str = pref.getString(PREF_DATA_KEY[16], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getAbsolutePatting()[0][x] = Integer.parseInt(str[x]);
            } catch (Exception e) {
                data.getAbsolutePatting()[0][x] = 0;
            }
        }
        // 天気情報
        String condition = pref.getString(PREF_DATA_KEY[17], "0");
        try {
            data.setCondition(Integer.parseInt(condition));
        } catch (Exception e) {
            data.setCondition(0);
        }
    }

    /* =============================================================
     * 【プリファレンスデータとして保存するデータまとめ】
     * -------------------------------------------------------------
     *     [ID]    [型]        [キー]                [内容]
     * -------------------------------------------------------------
     *     [0]     String      SAVED_DATA_NUM        保存データ番号
     *     [1]     String      HOLE_TITLE            ホールタイトル
     *     [2]     String      CUR_HOLE_NUM          現在のホールナンバー
     *     [3]     String      HOLE_PAR_SCORE        各ホールのパー値
     *     [4]     String      PERSON_NAME1          プレイヤー１の名前
     *     [5]     String      PERSON_NAME2          プレイヤー２の名前
     *     [6]     String      PERSON_NAME3          プレイヤー３の名前
     *     [7]     String      PERSON_NAME4          プレイヤー４の名前
     *     [8]     String      PERSON1_SCORE         プレイヤー１のスコア
     *     [9]     String      PERSON2_SCORE         プレイヤー２のスコア
     *     [10]    String      PERSON3_SCORE         プレイヤー３のスコア
     *     [11]    String      PERSON4_SCORE         プレイヤー４のスコア
     *     [12]    String      HOLE_MEMO             ホールメモ
     *     [13]    String      PERSON_HANDI          ハンディキャップ
     *     [14]    String      IS_18H_ROUNG          18H or Out/In
     *     [15]    String      IS_HOLE_LOCKED        各ホールのロック状態
     *     [16]    String      PAT_SCORE_1           プレイヤー１のパットスコア
     *     [17]    String      CONDITION             ラウンドのコンディション
     * =============================================================
     */

    /**
     * @param context Context
     * @param scoreData GolfScoreData
     * @param saveNum int
     */
    public static void saveScoreData(final Context context, final SaveData scoreData) {
        String[] scores = new String[4];
        StringBuilder strHandi = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            scores[i] = "";
            strHandi.append(String.valueOf(scoreData.getPlayersHandi()[i]) + ",");
        }
        String isHoleLockedStr = "";
        String patScore = "";
        String parScores = "";
        StringBuilder strBuilderIsLocked = new StringBuilder();
        StringBuilder strBuilderMyPat = new StringBuilder();

        for (int i = 0; i < Util.TOTAL_HOLE_COUNT; i++) {
            parScores += String.valueOf(scoreData.getEachHolePar()[i]) + ",";
            scores[0] += String.valueOf(scoreData.getAbsoluteScore(0)[i]) + ",";
            scores[1] += String.valueOf(scoreData.getAbsoluteScore(1)[i]) + ",";
            scores[2] += String.valueOf(scoreData.getAbsoluteScore(2)[i]) + ",";
            scores[3] += String.valueOf(scoreData.getAbsoluteScore(3)[i]) + ",";
            strBuilderMyPat.append(String.valueOf(scoreData.getAbsolutePatting()[0][i]));
            strBuilderMyPat.append(",");
            if (scoreData.getEachHoleLocked()[i]) {
                strBuilderIsLocked.append("1");
                strBuilderIsLocked.append(",");
            } else {
                strBuilderIsLocked.append("0");
                strBuilderIsLocked.append(",");
            }
        }
        isHoleLockedStr += strBuilderIsLocked.toString();
        patScore += strBuilderMyPat.toString();

        saveScoreDataToPreference(context, scoreData, parScores, scores, isHoleLockedStr, patScore,
                strHandi.toString());
    }

    /**
     * @param context Context
     * @param scoreData GolfScoreData
     * @param parScores Par Scores
     * @param scores Scores
     * @param isHoleLockedStr Is Hole Locked
     * @param patScore Pat Score
     * @param perHandiStr perHandiStr
     */
    private static void saveScoreDataToPreference(final Context context, final SaveData scoreData,
            final String parScores, final String[] scores, final String isHoleLockedStr,
            final String patScore, final String perHandiStr) {

        // ステップ３ - プリファレンスデータを保存
        SharedPreferences pref = context.getSharedPreferences(
                PREF_DATA_SLOT[scoreData.getSaveIdx()], Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString(PREF_DATA_KEY[0], String.valueOf(scoreData.getSaveIdx())); // 保存データ番号
        e.putString(PREF_DATA_KEY[1], scoreData.getHoleTitle()); // ホールタイトル
        e.putString(PREF_DATA_KEY[2], String.valueOf(scoreData.getCurrentHole())); // ホール番号
        e.putString(PREF_DATA_KEY[3], parScores); // 各ホールのパー値
        e.putString(PREF_DATA_KEY[4], scoreData.getNames()[0]); // プレイヤー１の名前
        e.putString(PREF_DATA_KEY[5], scoreData.getNames()[1]); // プレイヤー２の名前
        e.putString(PREF_DATA_KEY[6], scoreData.getNames()[2]); // プレイヤー３の名前
        e.putString(PREF_DATA_KEY[7], scoreData.getNames()[3]); // プレイヤー４の名前
        e.putString(PREF_DATA_KEY[8], scores[0]); // プレイヤー１のスコア
        e.putString(PREF_DATA_KEY[9], scores[1]); // プレイヤー２のスコア
        e.putString(PREF_DATA_KEY[10], scores[2]); // プレイヤー３のスコア
        e.putString(PREF_DATA_KEY[11], scores[3]); // プレイヤー４のスコア
        e.putString(PREF_DATA_KEY[12], scoreData.getMemoStr()); // ホールメモ
        e.putString(PREF_DATA_KEY[13], perHandiStr); // ハンディキャップ
        e.putString(PREF_DATA_KEY[14], scoreData.getIs18HroundStr()); // 18H or Out/In
        e.putString(PREF_DATA_KEY[15], isHoleLockedStr); // 各ホールのロック状態
        e.putString(PREF_DATA_KEY[16], patScore); // プレイヤー１のパットスコア
        e.putString(PREF_DATA_KEY[17], String.valueOf(scoreData.getCondition())); // ラウンドのコンディション
        e.commit();
    }
}
