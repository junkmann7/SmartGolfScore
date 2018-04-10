package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
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
import android.text.format.DateFormat;
import android.util.Log;
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

import jp.tonosama.komoki.SimpleGolfScorer2.R;
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
    private static final int CANCELE_MOVE_VALUE = 20;
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
                    Log.e("Runnable", "InterruptedException");
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

    public void getPlayerScoreViews(final SaveData scoreData) {

        SharedPreferences pref = getSharedPreferences(PREF_TABLE_SETTING, MODE_PRIVATE);
        mScoreViewerType = pref.getInt(PREF_TABLE_VALUE_TYPE_KEY, DEFAULT_VIEWER_TYPE);
        Map<Integer, String> personNames = scoreData.getPlayerNameList();
        if (!scoreData.getIs18Hround()) {
            for (int i = 9; i < 18; i++) {
                String holeNumStr = String.valueOf(i + 1 - 9) + "H";
                ((TextView) findViewById(SVRes.HOLE_NUM_AREA[i])).setText(holeNumStr);
            }
        }
        ((TextView) findViewById(R.id.score_viewer_title)).setText(scoreData.getHoleTitle());

        TextView[][] playerScores = SVRes.getPlayerScoreTextViewList(this);
        for (int i = 0; i < SGSConfig.TOTAL_HOLE_COUNT; i++) {
            for (int j = 1; j < SGSConfig.MAX_PLAYER_NUM; j++) {
                if (personNames.get(j).trim().length() == 0) {
                    playerScores[j][i].setVisibility(View.GONE);
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
        ViewGroup curHoleArea = (ViewGroup) findViewById(SVRes.EACH_HOLE_AREA[scoreData
                .getCurrentHole() - 1]);
        curHoleArea.setBackgroundColor(Color.rgb(170, 238, 255));

        final int totalHoleCount = SGSConfig.TOTAL_HOLE_COUNT;
        TextView[] parScoreTV = SVRes.getTextViewList(this, SVRes.PAR_AREA);
        TextView[][] tvList = SVRes.getPlayerScoreTextViewList(this);

        // 各ホールの結果を出力
        Map<Integer, Integer> parScore = scoreData.getEachHolePar();
        Map<Integer, Map<Integer, Integer>> scores = scoreData.getScoresList();
        Map<Integer, Map<Integer, Integer>> patScore = scoreData.getPattingScoresList();
        Map<Integer, String> names = scoreData.getPlayerNameList();
        for (int i = 0; i < totalHoleCount; i++) {
            if (parScore.get(i) != 0) {
                String scoreStr = "Par" + String.valueOf(parScore.get(i));
                parScoreTV[i].setText(scoreStr);
            }
            for (int j = 0; j < SGSConfig.MAX_PLAYER_NUM; j++) {
                updateScore(tvList[j][i], names.get(j), scores.get(j).get(i),
                        parScore.get(i), patScore.get(j).get(i));
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
                SaveDataPref.updateCurrentHoleIdx(scoreData.getSaveIdx(), j + 1);
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

        //---------------------------------------------------------------------
        // カスタマイズ箇所
        MenuItem menu1 = menu.add(0, Menu.FIRST, Menu.NONE,
                getResources().getString(R.string.menu_output_score));
        menu1.setIcon(R.drawable.image_button_table);
        MenuItem menu2 = menu.add(0, Menu.FIRST + 1, Menu.NONE,
                getResources().getString(R.string.menu_chg_tble));
        menu2.setIcon(R.drawable.imane_button_setting);
        //---------------------------------------------------------------------
        return true;
    }

    /**
     * @param dateTaken 時刻ミリ秒
     * @return 時刻文字列
     */
    private static String createName(final long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {

        case Menu.FIRST: // Output

            Uri imgUri = getOutputImageUri();
            if (imgUri == null) {
                return false;
            }
            Intent is = new Intent();
            is.setAction(Intent.ACTION_VIEW);
            is.setType("image/png");
            is.setData(imgUri);
            startActivity(is);

            return true;

        case Menu.FIRST + 1:
            changeBasis();
            return true;
        default:
            break;
        }
        return false;
    }

    /**
     * changeBasis
     */
    private void changeBasis() {

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(getResources().getString(R.string.dlg_chg_tble_title));
        mDialog.setSingleChoiceItems(
                this.getResources().getStringArray(R.array.table_dispstyle_array),
                mScoreViewerType, new OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int item) {
                        mScoreViewerType = item;
                        dialog.dismiss();
                        updateScoreTable(getScoreData());
                        SharedPreferences mTablePref = getSharedPreferences(PREF_TABLE_SETTING,
                                MODE_PRIVATE);
                        Editor mEditor = mTablePref.edit();
                        mEditor.putInt(PREF_TABLE_VALUE_TYPE_KEY, mScoreViewerType);
                        mEditor.commit();

                        if (mScoreViewerType == 0) {
                            mSettingBtn.setText(getResources().getString(
                                    R.string.btn_change_table_basis_par));
                        } else {
                            mSettingBtn.setText(getResources().getString(
                                    R.string.btn_change_table_basis_abs));
                        }
                    }
                });
        mDialog.create().show();
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
            v.setBackgroundColor(Color.argb(255, 85, 119, 255));
            mIsHoleSelected = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            if (Math.abs(diffX) > CANCELE_MOVE_VALUE || Math.abs(diffY) > CANCELE_MOVE_VALUE) {
                mIsHoleSelected = false;
                changeBgColor(v, mScoreAreaList[curHole - 1]);
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            changeBgColor(v, mScoreAreaList[curHole - 1]);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            int j = getViewHoleNumber(v);
            if (mIsHoleSelected && Math.abs(diffX) < CANCELE_MOVE_VALUE
                    && Math.abs(diffY) < CANCELE_MOVE_VALUE) {
                SaveDataPref.updateCurrentHoleIdx(getScoreData().getSaveIdx(), j + 1);
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