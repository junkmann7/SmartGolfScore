package jp.tonosama.komoki.SimpleGolfScorer2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

import static jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig.MAX_PLAYER_NUM;
import static jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig.TOTAL_HOLE_COUNT;


/* =============================================================
 * 【プリファレンスデータとして保存するデータまとめ】
 * -------------------------------------------------------------
 *     [ID]    [型]        [キー]                [内容]
 * -------------------------------------------------------------
 *     [0]     String      SAVED_DATA_NUM        保存データ番号
 *     [1]     String      HOLE_TITLE            ホールタイトル
 *     [2]     String      CUR_HOLE_NUM          現在のホールナンバー
 *     [3]     String      HOLE_PAR_SCORE        各ホールのパー値
 *     [4]     String      PERSON_NAME1          プレイヤー１の名前   // player数依存
 *     [5]     String      PERSON_NAME2          プレイヤー２の名前   // player数依存
 *     [6]     String      PERSON_NAME3          プレイヤー３の名前   // player数依存
 *     [7]     String      PERSON_NAME4          プレイヤー４の名前   // player数依存
 *     [8]     String      PERSON1_SCORE         プレイヤー１のスコア // player数依存
 *     [9]     String      PERSON2_SCORE         プレイヤー２のスコア // player数依存
 *     [10]    String      PERSON3_SCORE         プレイヤー３のスコア // player数依存
 *     [11]    String      PERSON4_SCORE         プレイヤー４のスコア // player数依存
 *     [12]    String      HOLE_MEMO             ホールメモ
 *     [13]    String      PERSON_HANDI          ハンディキャップ    // player数依存
 *     [14]    String      IS_18H_ROUNG          18H or Out/In
 *     [15]    String      IS_HOLE_LOCKED        各ホールのロック状態
 *     [16]    String      PAT_SCORE_1           プレイヤー１のパットスコア
 *     [17]    String      CONDITION             ラウンドのコンディション
 * =============================================================
 */
public final class SaveDataPref {

    private static final int MAX_DATA_SAVE_NUM = 1000;

    private static Map<Integer, SaveData> sSaveDataMap;

    private static int sSelectedSaveIdx;

    private SaveDataPref() {
        //private constructor
    }

    @SuppressLint("UseSparseArrays")
    static void initialize() {
        Context context = SGSApplication.getInstance();
        Map<Integer, SaveData> savedDatamap = new HashMap<>();
        for (int idx = 0; idx < MAX_DATA_SAVE_NUM; idx++) {
            SaveData data = getScoreData(context, idx);
            if (data != null) {
                savedDatamap.put(idx, data);
            }
        }
        sSaveDataMap = new HashMap<>(savedDatamap);
    }

    static boolean isInitialized() {
        return sSaveDataMap != null;
    }

    /**  */
    private static final String[] PREF_DATA_SLOT = new String[MAX_DATA_SAVE_NUM];
    /**  */
    private static final String BACKUP_DIR_NAME = "SmartGolfScore/backup";

    /**  */
    private static final String[] PREF_DATA_KEY = { "SAVED_DATA_NUM", "HOLE_TITLE", "CUR_HOLE_NUM",
            "HOLE_PAR_SCORE", "PERSON_NAME1", "PERSON_NAME2", "PERSON_NAME3", "PERSON_NAME4",
            "PERSON1_SCORE", "PERSON2_SCORE", "PERSON3_SCORE", "PERSON4_SCORE", "HOLE_MEMO",
            "PERSON_HANDI", "IS_18H_ROUNG", "IS_HOLE_LOCKED", "PAT_SCORE_1", "CONDITION", };

    static {
        for (int i = 0; i < MAX_DATA_SAVE_NUM; i++) {
            PREF_DATA_SLOT[i] = "PREF" + String.valueOf(i + 1);
        }
    }

    @SuppressLint("UseSparseArrays")
    public static Map<Integer, SaveData> getSaveDataMap() {
        return sSaveDataMap;
    }

    public static void setSelectedSaveIdx(int selectedSaveIdx) {
        sSelectedSaveIdx = selectedSaveIdx;
    }

    @Nullable
    public static SaveData getSelectedSaveData() {
        if (sSaveDataMap == null) {
            return null;
        }
        return sSaveDataMap.get(sSelectedSaveIdx);
    }

    public static int getSelectedSaveIdx() {
        return sSelectedSaveIdx;
    }

    public static String getMyName() {
        for (SaveData saveData : sSaveDataMap.values()) {
            String name = saveData.getPlayerNameList().get(0);
            if (name != null && !name.equals("")) {
                return name;
            }
        }
        return "";
    }

    static int getEmptySaveIdx() {
        for (int i = 0; i < MAX_DATA_SAVE_NUM; i++) {
            if (sSaveDataMap.get(i) == null) {
                return i;
            }
        }
        return -1;
    }

    public static void deleteSaveData(final int saveIndex) {
        SaveData saveData = sSaveDataMap.get(saveIndex);
        if (saveData == null) {
            return;
        }
        saveData.setHoleTitle("");
        saveScoreData(saveData);
        sSaveDataMap.remove(saveIndex);
    }

    /**
     * @param scoreData GolfScoreData
     */
    public static void saveScoreData(final SaveData scoreData) {

        //update cache
        sSaveDataMap.put(scoreData.getSaveIdx(), scoreData);

        String[] scores = new String[4];
        StringBuilder strHandi = new StringBuilder();
        for (int i = 0; i < scores.length; i++) {
            scores[i] = "";
            strHandi.append(String.valueOf(scoreData.getPlayersHandi().get(i))).append(",");
        }
        String isHoleLockedStr = "";
        String patScore = "";
        String parScores = "";
        StringBuilder strBuilderIsLocked = new StringBuilder();
        StringBuilder strBuilderMyPat = new StringBuilder();

        for (int holeIdx = 0; holeIdx < TOTAL_HOLE_COUNT; holeIdx++) {
            //noinspection StringConcatenationInLoop
            parScores += String.valueOf(scoreData.getEachHolePar().get(holeIdx)) + ",";
            scores[0] += String.valueOf(scoreData.getScoresList().get(0).get(holeIdx)) + ",";
            scores[1] += String.valueOf(scoreData.getScoresList().get(1).get(holeIdx)) + ",";
            scores[2] += String.valueOf(scoreData.getScoresList().get(2).get(holeIdx)) + ",";
            scores[3] += String.valueOf(scoreData.getScoresList().get(3).get(holeIdx)) + ",";
            strBuilderMyPat.append(String.valueOf(scoreData.getPattingScoresList().get(0).get(holeIdx)));
            strBuilderMyPat.append(",");
            if (scoreData.getEachHoleLocked().get(holeIdx)) {
                strBuilderIsLocked.append("1");
                strBuilderIsLocked.append(",");
            } else {
                strBuilderIsLocked.append("0");
                strBuilderIsLocked.append(",");
            }
        }
        isHoleLockedStr += strBuilderIsLocked.toString();
        patScore += strBuilderMyPat.toString();

        saveScoreDataToPreference(scoreData, parScores, scores, isHoleLockedStr, patScore,
                strHandi.toString());

    }

    interface BackupCallback {

        void onSuccess(@NonNull String bkDirName, @NonNull String  bkFileName);

        void onFail();
    }

    static void backupData(@NonNull BackupCallback callback) {

        long mDateTaken = System.currentTimeMillis();
        String bkFileName = DateFormat.format("yyyyMMdd_kkmmss", mDateTaken).toString() + ".txt";
        String bkDirName = Environment.getExternalStorageDirectory() + "/" + BACKUP_DIR_NAME;

        StringBuilder outputStr = new StringBuilder();
        for (int i = 0; i < sSaveDataMap.values().size(); i++) {
            SharedPreferences pref = SGSApplication.getInstance()
                    .getSharedPreferences(SaveDataPref.PREF_DATA_SLOT[i], Context.MODE_PRIVATE);
            outputStr.append("<PREFERRENCE>\r\n");
            for (int j = 1; j < SaveDataPref.PREF_DATA_KEY.length; j++) {
                outputStr
                        .append("<KEY>")
                        .append(pref.getString(SaveDataPref.PREF_DATA_KEY[j], ""))
                        .append("\r\n");
            }
            outputStr.append("\r\n");
        }
        if (outputFile(bkDirName, bkFileName, outputStr.toString())) {
            callback.onSuccess(bkDirName, bkFileName);
        } else {
            callback.onFail();
        }
    }

    static List<String> getBackupFileList() {

        final File bkFileDir = new File(Environment.getExternalStorageDirectory() + "/"
                + BACKUP_DIR_NAME);
        if (!bkFileDir.exists()) {
            return Collections.emptyList();
        }
        final String[] bkFileList = bkFileDir.list();
        if (bkFileList == null || bkFileList.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.asList(bkFileList);
    }

    private static String[] getBackupPrefData(@NonNull String file) {
        Context context = SGSApplication.getInstance();
        String bkDirName = Environment.getExternalStorageDirectory() + "/" + BACKUP_DIR_NAME;
        BufferedReader readLine = null;
        StringBuilder loadedData = new StringBuilder();
        try {
            readLine = new BufferedReader(new FileReader(bkDirName + "/" + file));
            while (readLine.ready()) {
                String line = readLine.readLine();
                loadedData.append(line).append("\n");
            }
        } catch (FileNotFoundException ex) {
            Toast.makeText(context, "Error! FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(context, "Error! IOException", Toast.LENGTH_SHORT).show();
        } finally {
            if (readLine != null) {
                try {
                    readLine.close();
                } catch (IOException ex) {
                    Toast.makeText(context, "Error! IOException", Toast.LENGTH_SHORT).show();
                }
            }
        }
        return loadedData.toString().split("<PREFERRENCE>");
    }

    static String getBackupDataTitleList(@NonNull String file) {
        final String[] backupPrefData = getBackupPrefData(file);
        String[] bkupTitleArray = new String[backupPrefData.length];
        StringBuilder titleList = new StringBuilder();
        for (int i = 1; i < backupPrefData.length; i++) {
            String[] backupPrefKey = backupPrefData[i].split("<KEY>");
            bkupTitleArray[i] = backupPrefKey[1];
            titleList.append(String.format(Locale.getDefault(), "[%03d] %s%n%n", i,
                    bkupTitleArray[i].replaceAll("\n", "")));
        }
        return titleList.toString();
    }

    interface RestoreCallback {

        void onComplete();
    }

    static void restoreBackupData(@NonNull Activity activity,
                                  @NonNull final String file,
                                  @NonNull final RestoreCallback callback) {
        ProgressWorker.start(activity, new ProgressWorker.Delegate() {
            @Override
            public void execute(@NonNull final ProgressWorker.ProgressHandler progressHandler) {

                final Context context = SGSApplication.getInstance();
                String[] backupPrefData = getBackupPrefData(file);

                int total = backupPrefData.length - 1;
                int progress = 0;

                for (int saveIdx = 1; saveIdx < backupPrefData.length; saveIdx++) {
                    String[] backupPrefKey = backupPrefData[saveIdx].split("<KEY>");
                    SharedPreferences pref = context.getSharedPreferences("PREF" + String.valueOf(saveIdx),
                            Context.MODE_PRIVATE);
                    Editor e = pref.edit();
                    e.putString(SaveDataPref.PREF_DATA_KEY[0], "0");
                    for (int prefKeyIdx = 1; prefKeyIdx < backupPrefKey.length; prefKeyIdx++) {
                        final String data = backupPrefKey[prefKeyIdx];
                        // Remove all CRLF except for user memo.
                        if (prefKeyIdx != 12) {
                            e.putString(SaveDataPref.PREF_DATA_KEY[prefKeyIdx],
                                    data.replaceAll("\n", ""));
                        } else {
                            e.putString(SaveDataPref.PREF_DATA_KEY[prefKeyIdx], data);
                        }
                    }
                    e.commit();
                    progressHandler.progress(++progress, total);
                }
                initialize();
                progressHandler.complete();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onComplete();
                    }
                });
            }
        });
    }

    public static void updateCurrentHoleIdx(@NonNull SaveData saveData, int holeIdx) {
        saveData.setCurrentHole(holeIdx);
        Context context = SGSApplication.getInstance();
        SharedPreferences pref = context.getSharedPreferences(
                SaveDataPref.PREF_DATA_SLOT[saveData.getSaveIdx()], Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString(SaveDataPref.PREF_DATA_KEY[2], String.valueOf(holeIdx));
        e.commit();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //// private method
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static SaveData getScoreData(final Context context, final int idx) {

        SharedPreferences pref = context.getSharedPreferences(PREF_DATA_SLOT[idx], Context.MODE_PRIVATE);
        SaveData saveData = SaveData.createInitialData(idx);
        // from 01 to 07 prefs
        loadScoreData01to07(pref, saveData);
        // Checks invalid data
        if (saveData.getHoleTitle().equals("")) {
            return null;
        }
        // from 08 to 11 prefs
        loadScoreData08to11(pref, saveData);
        // from 12 to 14 prefs
        loadScoreData12to14(pref, saveData);
        // from 15 to 17 prefs
        loadScoreData15to17(pref, saveData);

        return saveData;
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData01to07(final SharedPreferences pref, final SaveData data) {
        String[] str;
        // タイトル
        String holeTitle = pref.getString(PREF_DATA_KEY[1], "");
        if (holeTitle.equals("")) {
            return;
        }
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
        Map<Integer, Integer> defaultParList = SaveData.createInitialData(-1).getEachHolePar();
        String holeParScore = //
        pref.getString(PREF_DATA_KEY[3], ArrayUtil.convertToString(defaultParList));
        str = holeParScore.split(",");
        for (int x = 0; x < TOTAL_HOLE_COUNT; x++) {
            try {
                data.getEachHolePar().put(x, Integer.parseInt(str[x]));
            } catch (Exception e) {
                data.getEachHolePar().put(x, 4);
            }
        }
        // プレイヤーの名前
        data.getPlayerNameList().put(0, pref.getString(PREF_DATA_KEY[4], ""));
        data.getPlayerNameList().put(1, pref.getString(PREF_DATA_KEY[5], ""));
        data.getPlayerNameList().put(2, pref.getString(PREF_DATA_KEY[6], ""));
        data.getPlayerNameList().put(3, pref.getString(PREF_DATA_KEY[7], ""));
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData08to11(final SharedPreferences pref, final SaveData data) {
        String defaultScoreListStr = ArrayUtil.convertToString(SaveData.createInitialData(-1)
                .getScoresList().get(0));
        List<String[]> scoreDataList = new ArrayList<>();

        // 各プレイヤーのスコア
        scoreDataList.add(pref.getString(PREF_DATA_KEY[8], defaultScoreListStr).split(","));
        scoreDataList.add(pref.getString(PREF_DATA_KEY[9], defaultScoreListStr).split(","));
        scoreDataList.add(pref.getString(PREF_DATA_KEY[10], defaultScoreListStr).split(","));
        scoreDataList.add(pref.getString(PREF_DATA_KEY[11], defaultScoreListStr).split(","));

        for (int holeIdx = 0; holeIdx < TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < MAX_PLAYER_NUM; playerIdx++) {
                String score = scoreDataList.get(playerIdx)[holeIdx];
                Map<Integer, Integer> scoreList = data.getScoresList().get(playerIdx);
                try {
                    scoreList.put(holeIdx, Integer.parseInt(score));
                } catch (Exception e) {
                    scoreList.put(holeIdx, 0);
                }
            }
        }
    }

    /**
     * @param pref pref
     * @param data scoreData
     */
    private static void loadScoreData12to14(final SharedPreferences pref, final SaveData data) {
        String[] str;
        // メモ
        String homeMemo = pref.getString(PREF_DATA_KEY[12], "");
        data.setMemoStr(homeMemo);
        // ハンディキャップ
        str = pref.getString(PREF_DATA_KEY[13], "0,0,0,0,").split(",");
        for (int x = 0; x < 4; x++) {
            try {
                data.getPlayersHandi().put(x, Integer.parseInt(str[x]));
            } catch (Exception e) {
                data.getPlayersHandi().put(x, 0);
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
        String[] str;
        // 各ホールの確定済み判定
        str = pref.getString(PREF_DATA_KEY[15], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int holeIdx = 0; holeIdx < TOTAL_HOLE_COUNT; holeIdx++) {
            try {
                data.getEachHoleLocked().put(holeIdx, (1 == Integer.parseInt(str[holeIdx])));
            } catch (Exception e) {
                data.getEachHoleLocked().put(holeIdx, false);
            }
        }
        // プレイヤー１のパットスコア
        str = pref.getString(PREF_DATA_KEY[16], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,").split(",");
        for (int holeIdx = 0; holeIdx < TOTAL_HOLE_COUNT; holeIdx++) {
            try {
                data.getPattingScoresList().get(0).put(holeIdx, Integer.parseInt(str[holeIdx]));
            } catch (Exception e) {
                data.getPattingScoresList().get(0).put(holeIdx, 0);
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

    /**
     * @param scoreData GolfScoreData
     * @param parScores Par Scores
     * @param scores Scores
     * @param isHoleLockedStr Is Hole Locked
     * @param patScore Pat Score
     * @param perHandiStr perHandiStr
     */
    private static void saveScoreDataToPreference(final SaveData scoreData,
            final String parScores, final String[] scores, final String isHoleLockedStr,
            final String patScore, final String perHandiStr) {

        // ステップ３ - プリファレンスデータを保存
        SharedPreferences pref = SGSApplication.getInstance().getSharedPreferences(
                PREF_DATA_SLOT[scoreData.getSaveIdx()], Context.MODE_PRIVATE);
        Editor e = pref.edit();
        e.putString(PREF_DATA_KEY[0], String.valueOf(scoreData.getSaveIdx())); // 保存データ番号
        e.putString(PREF_DATA_KEY[1], scoreData.getHoleTitle()); // ホールタイトル
        e.putString(PREF_DATA_KEY[2], String.valueOf(scoreData.getCurrentHole())); // ホール番号
        e.putString(PREF_DATA_KEY[3], parScores); // 各ホールのパー値
        e.putString(PREF_DATA_KEY[4], scoreData.getPlayerNameList().get(0)); // プレイヤー１の名前
        e.putString(PREF_DATA_KEY[5], scoreData.getPlayerNameList().get(1)); // プレイヤー２の名前
        e.putString(PREF_DATA_KEY[6], scoreData.getPlayerNameList().get(2)); // プレイヤー３の名前
        e.putString(PREF_DATA_KEY[7], scoreData.getPlayerNameList().get(3)); // プレイヤー４の名前
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

    private static boolean outputFile(@NonNull String dirName, @NonNull String fileName,
                                      @NonNull String outputStr) {

        Context context = SGSApplication.getInstance();
        FileOutputStream fos = null;
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                if (dir.mkdirs()) {
                    throw new IOException();
                }
            }
            File file = new File(dirName, fileName);
            if (file.createNewFile()) {
                fos = new FileOutputStream(file);
                OutputStreamWriter osw = new OutputStreamWriter(fos);
                osw.write(outputStr);
                osw.flush();
                osw.close();
                return true;
            }
        } catch (FileNotFoundException ex) {
            Toast.makeText(context, "Error! FileNotFoundException", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(context, "Error! Please insert SD card.", Toast.LENGTH_SHORT).show();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
