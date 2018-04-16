package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

/**
 * @author Komoki
 */
public class ScoreViewer extends Activity implements OnTouchListener {

    /**  */
    private ViewGroup[] mScoreAreaList = new ViewGroup[SGSConfig.TOTAL_HOLE_COUNT];
    /**  */
    public static final String CAPTURE_IMAGE_DIR = "SmartGolfScore";
    /**  */
    private static final int CANCEL_MOVE_VALUE = 20;
    /** */
    private static final String SCORE_TEXT_TAG = "score_text";
    /**  */
    private int mOffsetX = 0;
    /**  */
    private int mOffsetY = 0;
    /**  */
    private boolean mIsHoleSelected;
    /**  */
    private int mScoreViewerType = 0;
    /**  */
    private static final int VIEWER_TYPE_RELATIVE = 0;
    /**  */
    private static final int VIEWER_TYPE_ABSOLUTE = 1;
    /**  */
    private static final int DEFAULT_VIEWER_TYPE = VIEWER_TYPE_RELATIVE;
    /**  */
    public static final String PREF_TABLE_SETTING = "PREF_TABLE_SETTING";
    /**  */
    public static final String PREF_TABLE_VALUE_TYPE_KEY = "PREF_TABLE_VALUE_TYPE_KEY";
    /**  */
    private Button mSettingBtn;
    /**  */
    private Button mOutputBtn;
    /**  */
    private SaveData mScoreData;

    /**
     * Starts table viewer with the given save data
     */
    public static void startViewer(final SaveData saveData) {
        Context context = SGSApplication.getInstance();
        Intent intent = new Intent(context, ScoreViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SaveDataPref.setSelectedSaveIdx(saveData.getSaveIdx());
        context.startActivity(intent);
    }

    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        int selectedIdx = SaveDataPref.getSelectedSaveIdx();
        if (selectedIdx < 0) {
            return;
        }
        setScoreData(SaveDataPref.getSaveDataMap().get(selectedIdx));
        if (getScoreData() == null) {
            setScoreData(SaveDataPref.getSaveDataMap().get(selectedIdx));
        }
        setContentView(R.layout.score_viewer);

        getPlayerScoreViews(getScoreData());
        updateScoreTable(getScoreData());

        // 手紙にグラフを表示する対応
        if (getScoreData().isOutputImageFlg()) {
            showOutputProgressDialog();
        }

        // Sets the text size according to the saved preference
        updateTextSize();
    }

    private void updateTextSize() {
        ViewGroup vg = (ViewGroup) findViewById(R.id.score_viewer_root);
        List<View> viewList = getChildViewList(vg);
        setTextSize(viewList, SVPreference.getSVTextSize());
    }

    private static List<View> getChildViewList(@NonNull ViewGroup vg) {
        int childCount = vg.getChildCount();
        List<View> viewList = new ArrayList<>();
        for (int i = 0; i < childCount; i++) {
            View child = vg.getChildAt(i);
            viewList.add(child);
            if (child instanceof ViewGroup) {
                viewList.addAll(getChildViewList((ViewGroup) child));
            }
        }
        return viewList;
    }

    private static void setTextSize(@NonNull List<View> viewList, float textSize) {
        for (View view : viewList) {
            if (!(view instanceof TextView)) {
                continue;
            }
            if (SCORE_TEXT_TAG.equals(view.getTag())) {
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }
    }

    /**
     * @return SaveData
     */
    private SaveData getScoreData() {
        return mScoreData;
    }

    /**
     * @param data SaveData
     */
    private void setScoreData(final SaveData data) {
        mScoreData = data;
    }

    /**
     * showOutputProgressDialog
     */
    private void showOutputProgressDialog() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(R.string.send_by_email_output_table));
        progressDialog.setCancelable(false);
        progressDialog.show();
        Runnable runnable = new Runnable() {

            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    DevLog.d("Runnable", "InterruptedException");
                }
                ScoreViewer.this.runOnUiThread(new Runnable() {

                    public void run() {
                        Uri imageTableUri = getOutputImageUri();
                        Uri imageGraphUri = getIntent().getParcelableExtra(
                                GraphActivity.EXTRAS_IMAGE_URI_GRAPH);
                        finish();
                        progressDialog.dismiss();
                        Intent i = new Intent();
                        String subject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
                        String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                        i.setAction(Intent.ACTION_SEND_MULTIPLE);
                        i.setType("message/rfc822");
                        i.putExtra(Intent.EXTRA_SUBJECT, subject);
                        i.putExtra(Intent.EXTRA_TEXT, text);
                        ArrayList<Uri> mImageUris = new ArrayList<>();
                        mImageUris.add(imageTableUri);
                        mImageUris.add(imageGraphUri);
                        i.putExtra(Intent.EXTRA_STREAM, mImageUris);
                        startActivity(i);
                    }
                });
            }
        };
        (new Thread(runnable)).start();
    }

    private void getPlayerScoreViews(final SaveData scoreData) {

        // HOLE TITLE
        ((TextView) findViewById(R.id.score_viewer_title)).setText(scoreData.getHoleTitle());

        SharedPreferences pref = getSharedPreferences(PREF_TABLE_SETTING, MODE_PRIVATE);
        mScoreViewerType = pref.getInt(PREF_TABLE_VALUE_TYPE_KEY, DEFAULT_VIEWER_TYPE);
        // HOLE Number
        Map<Integer, String> personNames = scoreData.getPlayerNameList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            String holeNumStr;
            if (!scoreData.getIs18Hround() &&
                    (SGSConfig.TOTAL_HOLE_COUNT/2 <= holeIdx && holeIdx < SGSConfig.TOTAL_HOLE_COUNT)) {
                holeNumStr = String.valueOf(holeIdx + 1 - 9) + "H";
            } else {
                holeNumStr = String.valueOf(holeIdx + 1) + "H";
            }
            SVRes.getHoleNumberTextView(this).get(holeIdx).setText(holeNumStr);
        }

        Map<Integer, Map<Integer, TextView>> playerScores = SVRes.getPlayerScoreTextView(this);
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 1; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                if (personNames.get(playerIdx).trim().length() == 0) {
                    playerScores.get(playerIdx).get(holeIdx).setVisibility(View.GONE);
                }
            }
        }
        TextView[] firstHalfScore = SVRes.getTextViewList(this, SVRes.HALF_RESULT_AREA_1ST);
        TextView[] secondHalfScore = SVRes.getTextViewList(this, SVRes.HALF_RESULT_AREA_2ND);
        TextView[] handiScoreTV = SVRes.getTextViewList(this, SVRes.SCORE_RESULT_AREA);
        TextView[] totalScoreTV = SVRes.getTextViewList(this, SVRes.TOTAL_SCORE_AREA);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            if (personNames.get(i).trim().length() == 0) {
                firstHalfScore[i].setVisibility(View.GONE);
                secondHalfScore[i].setVisibility(View.GONE);
                handiScoreTV[i].setVisibility(View.GONE);
                totalScoreTV[i].setVisibility(View.GONE);
                findViewById(SVRes.PLAYER_NAME_AREA[i]).setVisibility(View.GONE);
            } else {
                ((TextView) findViewById(SVRes.PLAYER_NAME_AREA[i])).setText(personNames.get(i));
            }
        }
        //
        setupButtonAction(scoreData);
    }

    /**
     * @param scoreData スコアデータ
     */
    private void setupSettingButtonAction(final SaveData scoreData) {

        // Change Basis
        if (mScoreViewerType == VIEWER_TYPE_ABSOLUTE) {
            mScoreViewerType = VIEWER_TYPE_RELATIVE;
            mSettingBtn.setText(getResources().getString(R.string.btn_change_table_basis_par));
        } else if (mScoreViewerType == VIEWER_TYPE_RELATIVE) {
            mScoreViewerType = VIEWER_TYPE_ABSOLUTE;
            mSettingBtn.setText(getResources().getString(R.string.btn_change_table_basis_abs));
        }
        updateScoreTable(scoreData);
        SharedPreferences mTablePref = getSharedPreferences(PREF_TABLE_SETTING, MODE_PRIVATE);
        Editor mEditor = mTablePref.edit();
        mEditor.putInt(PREF_TABLE_VALUE_TYPE_KEY, mScoreViewerType);
        mEditor.commit();
    }

    /**
     * @param imgUri 画像データのURI
     */
    private void setupOutputButtonAction(final Uri imgUri) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(ScoreViewer.this);
        dialog.setIcon(R.drawable.ic_menu_question);
        dialog.setTitle(getResources().getString(R.string.menu_output_score));
        dialog.setMessage(getResources().getString(R.string.dlg_output_image_msg_score));
        dialog.setPositiveButton(getResources().getString(R.string.dlg_output_image_by_email),
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        // Output to E-Mail
                        Intent mIntent = new Intent();
                        mIntent.setAction(Intent.ACTION_SEND);
                        mIntent.setType("message/rfc822");
                        mIntent.putExtra(Intent.EXTRA_STREAM, imgUri);
                        startActivity(mIntent);
                    }
                });
        dialog.setNegativeButton(getResources().getString(R.string.dlg_output_image_by_viewer),
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        // Output to Viewer
                        /*
                        Intent is = new Intent();
                        is.setAction(Intent.ACTION_VIEW);
                        is.setType("image/png");
                        is.setData(imgUri);
                        startActivity(is);*/
                    }
                });
        dialog.show();
    }

    /**
     * @param scoreData スコアデータ
     */
    private void setupButtonAction(final SaveData scoreData) {

        // スコア表の設定ボタン
        mSettingBtn = (Button) findViewById(R.id.scoreviewer_setting_button);
        if (mScoreViewerType == VIEWER_TYPE_ABSOLUTE) {
            mSettingBtn.setText(getResources().getString(R.string.btn_change_table_basis_abs));
        }
        if (mScoreViewerType == VIEWER_TYPE_RELATIVE) {
            mSettingBtn.setText(getResources().getString(R.string.btn_change_table_basis_par));
        }
        mSettingBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                setupSettingButtonAction(scoreData);
            }
        });
        mOutputBtn = (Button) findViewById(R.id.scoreviewer_output_button);
        mOutputBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final Uri imgUri = getOutputImageUri();
                if (imgUri == null) {
                    return;
                }
                setupOutputButtonAction(imgUri);
            }
        });
    }

    /**
     * @param tv テキストビュー
     * @param name 名前
     * @param perScore スコア
     * @param parScore パースコア
     * @param patScore パットスコア
     */
    private void updateScore(final TextView tv, final String name, final int perScore,
            final int parScore, final int patScore) {
        if (perScore != 0 && name.trim().length() != 0) {
            String patStr = "";
            if (0 < patScore) {
                patStr = " (" + patScore + ")";
            }
            final String scoreUnderStr = String.valueOf(perScore - parScore) + patStr;
            final String scoreOverStr = "+" + scoreUnderStr;
            final String scoreAbsStr = String.valueOf(perScore) + patStr;
            switch (mScoreViewerType) {
            case VIEWER_TYPE_RELATIVE:
                if (perScore < parScore) { // アンダーパーの場合
                    tv.setText(scoreUnderStr);
                    tv.setTextColor(Color.GREEN);
                } else if (perScore == parScore) { // パーの場合
                    tv.setText(scoreUnderStr);
                    tv.setTextColor(Color.BLUE);
                } else if (perScore > parScore + 3) { // オーバーパーの場合（＋４以上
                    tv.setText(scoreOverStr);
                    tv.setTextColor(Color.RED);
                } else if (perScore > parScore) { // オーバーパー）の場合
                    tv.setText(scoreOverStr);
                    tv.setTextColor(Color.BLACK);
                }
                break;
            case VIEWER_TYPE_ABSOLUTE:
                tv.setText(scoreAbsStr);
                if (perScore < parScore) { // アンダーパーの場合
                    tv.setTextColor(Color.GREEN);
                } else if (perScore == parScore) { // パーの場合
                    tv.setTextColor(Color.BLUE);
                } else if (perScore > parScore + 3) { // オーバーパーの場合（＋４以上
                    tv.setTextColor(Color.RED);
                } else if (perScore > parScore) { // オーバーパー）の場合
                    tv.setTextColor(Color.BLACK);
                }
                break;
            default:
                break;
            }
        }
    }

    /**
     * スコア表を更新する
     * 
     * @param scoreData スコアデータ
     */
    private void updateScoreTable(final SaveData scoreData) {
        if (scoreData.getCurrentHole() == 0) {
            finish();
            return;
        }
        ViewGroup curHoleArea = (ViewGroup) findViewById(
                SVRes.EACH_HOLE_AREA[scoreData.getCurrentHole() - 1]);
        curHoleArea.setBackgroundColor(Color.rgb(170, 238, 255));

        Map<Integer, TextView> parScoreTV = SVRes.getParNumberTextView(this);
        Map<Integer, Map<Integer, TextView>> playerScoreTextViewMap
                = SVRes.getPlayerScoreTextView(this);

        // 各ホールの結果を出力
        Map<Integer, Integer> parScore = scoreData.getEachHolePar();
        Map<Integer, Map<Integer, Integer>> scores = scoreData.getScoresList();
        Map<Integer, Map<Integer, Integer>> patScore = scoreData.getPattingScoresList();
        Map<Integer, String> names = scoreData.getPlayerNameList();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            if (parScore.get(holeIdx) != 0) {
                String scoreStr = String.valueOf(parScore.get(holeIdx));
                parScoreTV.get(holeIdx).setText(scoreStr);
            }
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                updateScore(
                        playerScoreTextViewMap.get(playerIdx).get(holeIdx),
                        names.get(playerIdx),
                        scores.get(playerIdx).get(holeIdx),
                        parScore.get(holeIdx),
                        patScore.get(playerIdx).get(holeIdx));
            }
        }
        // トータルスコアとハンデの結果を出力
        updateTotalAndHandi(scoreData, names);

        // ハーフの結果を出力する
        updateHalfScore(scoreData, names);

        // Parの合計値を出力
        updateParScore(scoreData);

        // ホール選択とフォーカス移動の制御
        setupHoleControlUi(scoreData);
    }

    /**
     * @param scoreData スコアデータ
     */
    private void updateParScore(final SaveData scoreData) {

        int totalPar = scoreData.getTotalParScore();
        int totalFirstHalfPar = scoreData.getHalfParScore(true);
        int totalSecondHalfPar = scoreData.getHalfParScore(false);
        ((TextView) findViewById(R.id.score_total_half1))
                .setText(String.valueOf(totalFirstHalfPar));
        ((TextView) findViewById(R.id.score_total_half2)).setText(String
                .valueOf(totalSecondHalfPar));
        ((TextView) findViewById(R.id.total_par_score)).setText(String.valueOf(totalPar));
    }

    /**
     * @param scoreData スコアデータ
     * @param names 名前
     */
    private void updateTotalAndHandi(final SaveData scoreData, final Map<Integer, String> names) {

        int[] totalScore = scoreData.getTotalScore();
        int[] totalPat = scoreData.getTotalPatScore();
        Map<Integer, Integer> playersHandi = scoreData.getPlayersHandi();
        TextView[] handiScoreTV = SVRes.getTextViewList(this, SVRes.SCORE_RESULT_AREA);
        TextView[] totalScoreTV = SVRes.getTextViewList(this, SVRes.TOTAL_SCORE_AREA);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            if (names.get(0).trim().length() != 0) {
                String patStr = "";
                if (0 < totalPat[i]) {
                    patStr = " (" + String.valueOf(totalPat[i]) + ")";
                }
                String scoreStr = String.valueOf(totalScore[i] - playersHandi.get(i)) + patStr;
                totalScoreTV[i].setText(scoreStr);
                handiScoreTV[i].setText(String.valueOf(playersHandi.get(i)));
            }
        }
    }

    /**
     * @param scoreData スコアデータ
     * @param names 名前
     */
    private void updateHalfScore(final SaveData scoreData, final Map<Integer, String> names) {

        int[] totalFirstHalfScore = scoreData.getHalfScore(true);
        int[] totalSecondHalfScore = scoreData.getHalfScore(false);
        int[] totalFirstHalfPat = scoreData.getHalfPatScore(true);
        int[] totalSecondHalfPat = scoreData.getHalfPatScore(false);
        TextView[] firstHalfScore = SVRes.getTextViewList(this, SVRes.HALF_RESULT_AREA_1ST);
        TextView[] secondHalfScore = SVRes.getTextViewList(this, SVRes.HALF_RESULT_AREA_2ND);
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            if (names.get(0).trim().length() != 0) {
                // 前半
                String patStr1st = "";
                if (0 < totalFirstHalfPat[i]) {
                    patStr1st = " (" + String.valueOf(totalFirstHalfPat[i]) + ")";
                }
                final String score1stHalfStr = String.valueOf(totalFirstHalfScore[i]) + patStr1st;
                firstHalfScore[i].setText(score1stHalfStr);
                // 後半
                String patStr2nd = "";
                if (0 < totalSecondHalfPat[i]) {
                    patStr2nd = " (" + String.valueOf(totalSecondHalfPat[i]) + ")";
                }
                final String score2ndHalfStr = String.valueOf(totalSecondHalfScore[i]) + patStr2nd;
                secondHalfScore[i].setText(score2ndHalfStr);
            }
        }
    }

    /**
     * @param vg ViewGroup
     * @param scoreData スコアデータ
     */
    private void setupFocusChangeListener(final ViewGroup vg, final SaveData scoreData) {

        vg.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            public void onFocusChange(final View v, final boolean hasFocus) {
                if (hasFocus) {
                    int j;
                    for (j = 0; j < SGSConfig.TOTAL_HOLE_COUNT; j++) {
                        if (v == mScoreAreaList[j]) {
                            break;
                        }
                    }
                    mScoreAreaList[j].setBackgroundColor(Color.rgb(85, 119, 255));
                } else {
                    int j;
                    for (j = 0; j < SGSConfig.TOTAL_HOLE_COUNT; j++) {
                        if (v == mScoreAreaList[j]) {
                            break;
                        }
                    }
                    if (j != scoreData.getCurrentHole() - 1) {
                        mScoreAreaList[j].setBackgroundColor(Color.alpha(255));
                    } else {
                        mScoreAreaList[j].setBackgroundColor(Color.rgb(170, 238, 255));
                    }
                }
            }
        });
    }

    /**
     * @param vg ViewGroup
     * @param scoreData スコアデータ
     */
    private void setupOnClickListener(final ViewGroup vg, final SaveData scoreData) {

        vg.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                int j;
                for (j = 0; j < SGSConfig.TOTAL_HOLE_COUNT; j++) {
                    if (v == mScoreAreaList[j]) {
                        break;
                    }
                }
                SaveDataPref.updateCurrentHoleIdx(scoreData, j + 1);
                finish();
            }
        });
    }

    /**
     * @param scoreData スコアデータ
     */
    private void setupHoleControlUi(final SaveData scoreData) {

        final int totalHoleCount = SGSConfig.TOTAL_HOLE_COUNT;

        // ScoreViewer でホール選択できるようにする対応
        for (int i = 0; i < totalHoleCount; i++) {
            mScoreAreaList[i] = (ViewGroup) findViewById(SVRes.EACH_HOLE_AREA[i]);
        }
        for (int i = 0; i < totalHoleCount; i++) {
            mScoreAreaList[i].setOnTouchListener(this);
        }
        // スコアビューワでフォーカス移動できるようにする対応
        for (int i = 0; i < totalHoleCount; i++) {
            setupFocusChangeListener(mScoreAreaList[i], scoreData);
        }
        // スコアビューワでフォーカスクリックでホール選択できるようにする対応
        for (int i = 0; i < totalHoleCount; i++) {
            setupOnClickListener(mScoreAreaList[i], scoreData);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        for (SVMenu svMenu : SVMenu.values()) {
            MenuItem menuItem = menu.add(0, svMenu.getMenuId(), Menu.NONE,
                    getResources().getString(svMenu.getTitleResId()));
            menuItem.setIcon(svMenu.getIconResId());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (SVMenu.getMenu(item.getItemId())) {
            case ChangeTextSize:
                changeTableTextSize();
                break;
            default:
                break;
        }
        return true;
    }

    private void changeTableTextSize() {

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(getResources().getString(R.string.dlg_chg_text_size));
        mDialog.setSingleChoiceItems(
                this.getResources().getStringArray(R.array.table_text_size_array),
                SVPreference.getSVTextSizeIndex(), new OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int item) {
                        SVPreference.setSVTextSize(item);
                        updateTextSize();
                        dialog.dismiss();
                    }
                });
        mDialog.create().show();
    }

    /**
     * @param dateTaken 時刻ミリ秒
     * @return 時刻文字列
     */
    private static String createName(final long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
    }

    public Uri getOutputImageUri() {

        int currHole = getScoreData().getCurrentHole();
        // キャプチャするViewの指定と取得
        mOutputBtn.setVisibility(View.GONE);
        mSettingBtn.setVisibility(View.GONE);
        mScoreAreaList[currHole - 1].setBackgroundColor(Color.alpha(0));

        long dateTaken = System.currentTimeMillis();
        String name = createName(dateTaken) + ".png";
        String directory = Environment.getExternalStorageDirectory().toString() + "/"
                + CAPTURE_IMAGE_DIR;
        String filePath = directory + "/" + name;
        if (!saveImageFile(directory, name)) {
            return null;
        }
        ContentValues values = new ContentValues(7);
        values.put(Images.Media.TITLE, name);
        values.put(Images.Media.DISPLAY_NAME, name);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/png");
        values.put(Images.Media.DATA, filePath);
        ContentResolver cr = getContentResolver();
        Uri imgUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        mOutputBtn.setVisibility(View.VISIBLE);
        mSettingBtn.setVisibility(View.VISIBLE);
        mScoreAreaList[currHole - 1].setBackgroundColor(Color.alpha(255));

        return imgUri;
    }

    /**
     * @param directory ディレクトリ名
     * @param filename ファイル名
     * @return true:保存成功 false:保存失敗
     */
    private boolean saveImageFile(final String directory, final String filename) {

        View disp = findViewById(R.id.graph_view_captured_area);
        disp.setBackgroundResource(R.drawable.back_land);
        disp.setDrawingCacheEnabled(true);
        Bitmap source = disp.getDrawingCache();
        if (source == null) {
            return false;
        }
        OutputStream outputStream = null;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new IOException();
                }
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                source.compress(CompressFormat.PNG, 100, outputStream);
            }
        } catch (FileNotFoundException ex) {
            Toast.makeText(this, "FileNotFoundException", Toast.LENGTH_SHORT).show();
            return false;
        } catch (IOException ex) {
            Toast.makeText(this, "Please insert SD card.", Toast.LENGTH_SHORT).show();
            return false;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    public boolean onTouch(final View v, final MotionEvent event) {
        int curHole = getScoreData().getCurrentHole();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mOffsetX = (int) event.getX();
            mOffsetY = (int) event.getY();
            v.setBackgroundColor(Color.argb(255, 0x66, 0xCC, 0xFF));
            mIsHoleSelected = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            if (Math.abs(diffX) > CANCEL_MOVE_VALUE || Math.abs(diffY) > CANCEL_MOVE_VALUE) {
                mIsHoleSelected = false;
                changeBgColor(v, mScoreAreaList[curHole - 1]);
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            changeBgColor(v, mScoreAreaList[curHole - 1]);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            int j = getViewHoleNumber(v);
            if (mIsHoleSelected && Math.abs(diffX) < CANCEL_MOVE_VALUE
                    && Math.abs(diffY) < CANCEL_MOVE_VALUE) {
                SaveDataPref.updateCurrentHoleIdx(getScoreData(), j + 1);
                finish();
            } else {
                changeBgColor(v, mScoreAreaList[curHole - 1]);
            }
        }
        return true;
    }

    /**
     * @param view View
     * @return ホール番号
     */
    private int getViewHoleNumber(final View view) {
        int j;
        for (j = 0; j < SGSConfig.TOTAL_HOLE_COUNT; j++) {
            if (view == mScoreAreaList[j]) {
                break;
            }
        }
        return j;
    }

    /**
     * @param view View
     * @param currView 現在ホールのView
     */
    private void changeBgColor(final View view, final View currView) {

        if (view != currView) {
            view.setBackgroundResource(R.drawable.transparent);
        } else {
            view.setBackgroundColor(Color.argb(255, 170, 238, 255));
        }
    }
}