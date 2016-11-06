package jp.tonosama.komoki.SimpleGolfScorer2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;
import jp.tonosama.komoki.SimpleGolfScorer2.editor.ScoreEditor;
import jp.tonosama.komoki.SimpleGolfScorer2.history.HistoryActivity;
import jp.tonosama.komoki.SimpleGolfScorer2.setting.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

/**
 * アプリトップ画面
 * 
 * @author Komoki
 */
public class MainTitle extends Activity {

    /** タグ */
    private static final String TAG = MainTitle.class.getSimpleName();

    /** メニュー管理 */
    private TitleMenuManager mMenuManager;

    /** 保存データリスト */
    private SaveDataList mSaveDataList;

    /** ボタンリスト */
    private ArrayList<Button> mCreateButtons = new ArrayList<>();

    @Override
    public void onResume() {
        mSaveDataList = initSaveData(mCreateButtons);
        super.onResume();
    }

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_title);

        mMenuManager = new TitleMenuManager();
        mSaveDataList = initSaveData(mCreateButtons);

        // 新規作成ボタンクリック時の動作を設定
        Button newCreateButton = (Button) findViewById(R.id.new_create_btn);

        // 保存データ限界の場合、新規作成ボタンを無効に
        if (Util.MAX_DATA_SAVE_NUM <= mSaveDataList.size()) {
            newCreateButton.setEnabled(false);
        }
        String myName = "";
        if (mSaveDataList.size() > 0) {
            SharedPreferences pref0 = getSharedPreferences(Util.PREF_DATA_SLOT[0], MODE_PRIVATE);
            myName = pref0.getString(Util.PREF_DATA_KEY[4], "");
        }
        setNewCreateButtonAction(newCreateButton, mSaveDataList, myName);

        // 個人履歴ボタンクリック時の動作を設定
        Button myHistoryButton = (Button) findViewById(R.id.my_history_btn);
        setHistoryButtonAction(myHistoryButton, mSaveDataList);
        if (mSaveDataList.size() < 1) {
            myHistoryButton.setVisibility(View.GONE);
        }
    }

    /**
     * reStartActivity
     */
    void reStartActivity() {
        mSaveDataList = initSaveData(mCreateButtons);
    }

    /**
     * 保存データの読み出し<br>
     * データ数の確認とホールタイトルの取得を行う
     * 
     * @param saveButton セーブボタンリスト
     * @return セーブデータリスト
     */
    private SaveDataList initSaveData(final ArrayList<Button> saveButton) {

        SaveDataList saveDataList = new SaveDataList(this);
        for (int idx = 0; idx < Util.MAX_DATA_SAVE_NUM; idx++) {
            SaveData data = Util.loadScoreDataFromPref(this, idx);
            String title = data.getHoleTitle();
            if (title.equals("")) {
                break;
            }
            saveDataList.append(idx, data);
        }
        SharedPreferences mSortPref = getSharedPreferences(Util.PREF_SORT_TYPE_SETTING,
                MODE_PRIVATE);
        int sortType = mSortPref.getInt(Util.PREF_SORT_TYPE_KEY, 0);
        saveDataList.sort(sortType);

        // 保存データ用ボタンを作成
        createSaveButtons(saveDataList, saveButton);

        // 作成済みボタンを押下
        setSaveButtonAction(saveDataList, saveButton);

        return saveDataList;
    }

    /**
     * 保存データ用ボタンを作成
     * 
     * @param dataList データリスト
     * @param buttons ボタンリスト
     */
    private void createSaveButtons(final SaveDataList dataList, final ArrayList<Button> buttons) {

        LinearLayout saveListArea = (LinearLayout) findViewById(R.id.MainTitle_SaveData_ListArea);
        saveListArea.removeAllViews();
        ((ScrollView) saveListArea.getParent()).scrollTo(0, 0);

        final float textSize = getResources().getDimension(R.dimen.main_title_btn_textsize);
        final LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        for (int i = 0; i < dataList.size(); i++) {
            Button dataButton = new Button(MainTitle.this);
            int buttonResId = 1000 + i;
            dataButton.setId(buttonResId);
            dataButton.setGravity(Gravity.CENTER_VERTICAL);
            dataButton.setVisibility(View.VISIBLE);
            dataButton.setText(dataList.get(i).getHoleTitle());
            dataButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            dataButton.setPadding((int) textSize, dataButton.getPaddingTop(),
                    dataButton.getPaddingRight(), dataButton.getPaddingBottom());
            if (dataList.get(i).isHoleResultFixed()) {
                dataButton.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources()
                        .getDrawable(R.drawable.image_button_star_2), null);
            }
            buttons.add(i, dataButton);
            saveListArea.addView(dataButton, lp);
        }
    }

    /**
     * 作成済みボタン押下時の動作を設定
     * 
     * @param dataList データリスト
     * @param buttons ボタンリスト
     */
    private void setSaveButtonAction(final SaveDataList dataList, final ArrayList<Button> buttons) {

        for (int i = 0; i < dataList.size(); i++) {
            buttons.get(i).setOnClickListener(new View.OnClickListener() {

                public void onClick(final View v) {
                    int j;
                    for (j = 0; j < dataList.size(); j++) {
                        if (v.equals(buttons.get(j))) {
                            break;
                        }
                    }
                    Intent intent = new Intent(getApplicationContext(), ScoreEditor.class);
                    SaveData scoreData = dataList.get(j);
                    intent.putExtra(Util.EXTRAS_SELECTED_IDX, scoreData.getSaveIdx());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 新規作成ボタンクリック時の動作を設定
     * 
     * @param button 新規作成ボタン
     * @param dataList セーブデータリスト
     * @param myName プレイヤー名
     */
    private void setNewCreateButtonAction(final Button button, final SaveDataList dataList,
            final String myName) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(Util.EXTRAS_IS_NEW_CREATE, true);
                intent.putExtra(Util.EXTRAS_SELECTED_IDX, dataList.size());
                intent.putExtra(Util.EXTRAS_MY_NAME, myName);
                startActivity(intent);
            }
        });
    }

    /**
     * 個人履歴ボタンクリック時の動作を設定
     * 
     * @param button 個人履歴ボタン
     * @param dataList データリスト
     */
    private void setHistoryButtonAction(final Button button, final SaveDataList dataList) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                int fixedDataNum = 0;
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).isHoleResultFixed()) {
                        ++fixedDataNum;
                    }
                }
                if (dataList.size() < 1 || fixedDataNum < 1) {
                    Toast.makeText(MainTitle.this,
                            getResources().getString(R.string.hist_ret_data_not_found),
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainTitle.this, HistoryActivity.class);
                intent.putExtra(Util.EXTRAS_SAVED_DATA_NUM, dataList.size());
                intent.putExtra(Util.EXTRAS_FIXED_DATA_NUM, fixedDataNum);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean ret = super.onCreateOptionsMenu(menu);
        mMenuManager.onCreateOptionsMenu(menu, this);
        return ret;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return mMenuManager.onOptionsItemSelected(item, this);
    }

    /**
     * @param dataList セーブデータリスト
     * @param buttons ボタンリスト
     */
    private void initializeAllData(final SaveDataList dataList, final ArrayList<Button> buttons) {
        for (int i = 0; i < dataList.size(); i++) {
            SharedPreferences pref = getSharedPreferences(Util.PREF_DATA_SLOT[i], MODE_PRIVATE);
            Editor e = pref.edit();
            for (int x = 0; x < Util.PREF_DATA_KEY.length; x++) {
                e.putString(Util.PREF_DATA_KEY[x], "");
            }
            e.commit();
            buttons.get(i).setVisibility(View.GONE);
        }
        dataList.clear();
    }

    /**
     * @param context コンテキスト
     */
    void outputBackupData(final Context context) {

        long mDateTaken = System.currentTimeMillis();
        String mBkFileName = DateFormat.format("yyyyMMdd_kkmmss", mDateTaken).toString() + ".txt";
        String mBkDirName = Environment.getExternalStorageDirectory() + "/" + Util.BACKUP_DIR_NAME;

        StringBuilder outputStr = new StringBuilder();
        for (int i = 0; i < mSaveDataList.size(); i++) {
            SharedPreferences pref = getSharedPreferences(Util.PREF_DATA_SLOT[i], MODE_PRIVATE);
            outputStr.append("<PREFERRENCE>\r\n");
            for (int j = 1; j < Util.PREF_DATA_KEY.length; j++) {
                outputStr.append("<KEY>").append(pref.getString(Util.PREF_DATA_KEY[j], "")).append("\r\n");
            }
            outputStr.append("\r\n");
        }
        outputFile(context, mBkDirName, mBkFileName, outputStr.toString());
    }

    /**
     * @param context コンテキスト
     * @param dirName ディレクトリ名
     * @param fileName ファイル名
     * @param outputStr 出力文字列
     */
    private void outputFile(final Context context, final String dirName, final String fileName,
            final String outputStr) {

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
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainTitle.this);
                dialog.setIcon(R.drawable.ic_menu_save);
                dialog.setTitle(getResources().getString(R.string.dlg_backup_title));
                dialog.setMessage(String.format(getResources().getString(R.string.dlg_backup_msg),
                        dirName, fileName));
                dialog.setNeutralButton(android.R.string.ok, null);
                dialog.show();
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
    }

    /**
     * @param path ファイルパス
     * @param file ファイル名
     * @param context コンテキスト
     */
    void loadBackupData(final String path, final String file, final Context context) {
        BufferedReader readLine = null;
        StringBuilder loadedData = new StringBuilder();
        try {
            readLine = new BufferedReader(new FileReader(path + "/" + file));
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
        final String[] mBackupPrefData = loadedData.toString().split("<PREFERRENCE>");
        String[] bkupTitleArray = new String[mBackupPrefData.length];
        StringBuilder titleList = new StringBuilder();
        for (int i = 1; i < mBackupPrefData.length; i++) {
            String[] mBackupPrefKey = mBackupPrefData[i].split("<KEY>");
            bkupTitleArray[i] = mBackupPrefKey[1];
            titleList.append(String.format(Locale.getDefault(), "[%03d] %s%n%n", i,
                    bkupTitleArray[i].replaceAll("\n", "")));
            if (SaveDataList.DEBUG) {
                Log.v(TAG, "[" + i + "] " + "HOLE_TITLE = " + mBackupPrefKey[1]);
            }
        }
        // 読み込みデータ内容確認ダイアログを表示
        showLoadDataDialog(titleList.toString(), mBackupPrefData);
    }

    /**
     * @param message ダイアログのメッセージ
     * @param dataList データリスト
     */
    private void showLoadDataDialog(final String message, final String[] dataList) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainTitle.this);
        dialog.setIcon(android.R.drawable.ic_menu_info_details);
        dialog.setTitle(getResources().getString(R.string.dlg_restore_confirm_title));
        dialog.setMessage(String.format(getResources().getString(R.string.dlg_restore_confirm_msg),
                message));
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int whichButton) {

                initializeAllData(mSaveDataList, mCreateButtons);
                for (int i = 1; i < dataList.length; i++) {
                    String[] mBackupPrefKey = dataList[i].split("<KEY>");
                    SharedPreferences pref = getSharedPreferences("PREF" + String.valueOf(i),
                            MODE_PRIVATE);
                    Editor e = pref.edit();
                    e.putString(Util.PREF_DATA_KEY[0], "0");
                    for (int j = 1; j < mBackupPrefKey.length; j++) {
                        if (j != 12) {
                            e.putString(Util.PREF_DATA_KEY[j],
                                    mBackupPrefKey[j].replaceAll("\n", ""));
                        } else {
                            e.putString(Util.PREF_DATA_KEY[j], mBackupPrefKey[j]);
                        }
                        if (SaveDataList.DEBUG) {
                            Log.v(TAG, "PREF[" + i + "] " + "KEY[" + j + "] " + mBackupPrefKey[j]);
                        }
                    }
                    e.commit();
                }
                Toast.makeText(MainTitle.this,
                        getResources().getString(R.string.toast_backup_complete),
                        Toast.LENGTH_SHORT).show();
                reStartActivity();
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.show();
    }
}