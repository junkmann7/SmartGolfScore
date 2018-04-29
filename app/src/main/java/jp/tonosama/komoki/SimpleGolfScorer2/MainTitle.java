package jp.tonosama.komoki.SimpleGolfScorer2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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

import java.util.ArrayList;

import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;
import jp.tonosama.komoki.SimpleGolfScorer2.editor.ScoreEditor;
import jp.tonosama.komoki.SimpleGolfScorer2.history.HistoryActivity;
import jp.tonosama.komoki.SimpleGolfScorer2.setting.SettingsActivity;

/**
 * アプリトップ画面
 * 
 * @author Komoki
 */
public class MainTitle extends Activity {

    /** メニュー管理 */
    private TitleMenuManager mMenuManager;

    /** 保存データリスト */
    private SaveDataList mSaveDataList;

    /** ボタンリスト */
    private ArrayList<Button> mCreateButtons = new ArrayList<>();

    @Override
    public void onResume() {
        super.onResume();
        DataInitializeSequence.start(new DataInitializeSequence.Callback() {
            @Override
            public void onComplete() {
                findViewById(R.id.main_title_loading_icon).setVisibility(View.GONE);
                findViewById(R.id.new_create_btn).setEnabled(true);
                setupData();
            }
        });
    }

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.main_title);
    }

    void setupData() {

        mMenuManager = new TitleMenuManager();
        mSaveDataList = initSaveData(mCreateButtons);

        Button newCreateButton = (Button) findViewById(R.id.new_create_btn);
        setNewCreateButtonAction(newCreateButton);

        // 個人履歴ボタンクリック時の動作を設定
        Button historyButton = (Button) findViewById(R.id.my_history_btn);
        setHistoryButtonAction(historyButton, mSaveDataList);
        int visibility = 0 < mSaveDataList.getFixedDataNum() ? View.VISIBLE : View.GONE;
        historyButton.setVisibility(visibility);
    }

    /**
     * 保存データの読み出し<br>
     * データ数の確認とホールタイトルの取得を行う
     * 
     * @param saveButton セーブボタンリスト
     * @return セーブデータリスト
     */
    private SaveDataList initSaveData(final ArrayList<Button> saveButton) {

        SaveDataList saveDataList = new SaveDataList();
        for (SaveData data : SaveDataPref.getSaveDataMap().values()) {
            String title = data.getHoleTitle();
            if (title.equals("")) {
                break;
            }
            saveDataList.append(data);
        }
        // Load settings for sorting type
        saveDataList.sort();

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
                    SaveDataPref.setSelectedSaveIdx(scoreData.getSaveIdx());
                    startActivity(intent);
                }
            });
        }
    }

    /**
     * 新規作成ボタンクリック時の動作を設定
     * 
     * @param button 新規作成ボタン
     */
    private void setNewCreateButtonAction(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                intent.putExtra(SettingsActivity.EXTRAS_IS_NEW_CREATE, true);
                SaveDataPref.setSelectedSaveIdx(SaveDataPref.getEmptySaveIdx());
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
                intent.putExtra(HistoryActivity.EXTRAS_SAVED_DATA_NUM, dataList.size());
                intent.putExtra(HistoryActivity.EXTRAS_FIXED_DATA_NUM, fixedDataNum);
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
            SaveDataPref.deleteSaveData(dataList.get(i).getSaveIdx());
            buttons.get(i).setVisibility(View.GONE);
        }
        dataList.clear();
    }

    void outputBackupData() {
        SaveDataPref.backupData(new SaveDataPref.BackupCallback() {
            @Override
            public void onSuccess(@NonNull String bkDirName, @NonNull String bkFileName) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainTitle.this);
                dialog.setIcon(R.drawable.ic_menu_save);
                dialog.setTitle(getResources().getString(R.string.dlg_backup_title));
                dialog.setMessage(String.format(getResources().getString(R.string.dlg_backup_msg),
                        bkDirName, bkFileName));
                dialog.setNeutralButton(android.R.string.ok, null);
                dialog.show();
            }

            @Override
            public void onFail() {
                //do nothing
            }
        });
    }

    void backupData(@NonNull String file) {
        showBackupDataDialog(file, SaveDataPref.getBackupDataTitleList(file));
    }

    private void showBackupDataDialog(@NonNull final String file, @NonNull String message) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(MainTitle.this);
        dialog.setIcon(android.R.drawable.ic_menu_info_details);
        dialog.setTitle(getResources().getString(R.string.dlg_restore_confirm_title));
        dialog.setMessage(String.format(getResources().getString(R.string.dlg_restore_confirm_msg),
                message));
        dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int whichButton) {

                initializeAllData(mSaveDataList, mCreateButtons);

                SaveDataPref.restoreBackupData(MainTitle.this, file, new SaveDataPref.RestoreCallback() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(MainTitle.this,
                                getResources().getString(R.string.toast_backup_complete),
                                Toast.LENGTH_SHORT).show();
                        setupData();
                    }
                });
            }
        });
        dialog.setNegativeButton(android.R.string.cancel, null);
        dialog.show();
    }
}