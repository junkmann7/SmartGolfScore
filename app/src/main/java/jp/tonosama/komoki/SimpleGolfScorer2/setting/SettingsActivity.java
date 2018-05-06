package jp.tonosama.komoki.SimpleGolfScorer2.setting;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.ArrayUtil;
import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.editor.ScoreEditor;

import static jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig.DEFAULT_HOLEPAR_SCORE;
import static jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig.DEFAULT_HOLEPAR_SHORT_SCORE;
import static jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig.MAX_PLAYER_NUM;

public class SettingsActivity extends Activity {

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private SaveData mSaveData = null;

    private int mPlayerNum = 2;

    /** タイムスタンプのフォーマット */
    private static final String TIME_STAMP_NAME = "yyyy/MM/dd";
    /**  */
    public static final String EXTRAS_IS_NEW_CREATE = "jp.tonosama.komoki.EXTRAS_IS_NEW_CREATE";

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        DevLog.d(TAG, "onCreate -> s");
        setContentView(R.layout.new_create);

        final int saveIdx = SaveDataPref.getSelectedSaveIdx();
        if (isNewCreate()) {
            String myName = SaveDataPref.getMyName();
            mSaveData = setupInitialValue(saveIdx, myName);
        } else {
            mSaveData = SaveDataPref.getSaveDataMap().get(saveIdx);
        }
        setupSavedValue(mSaveData);
        mPlayerNum = Math.max(2, mSaveData.getPlayerNum());
        ViewGroup[] playerEditArea = SettingsRes.getPlayerEditArea(this);
        for (int i = 0; i < playerEditArea.length; i++) {
            if (i < mPlayerNum) {
                playerEditArea[i].setVisibility(View.VISIBLE);
            } else {
                playerEditArea[i].setVisibility(View.GONE);
            }
        }
        setupButtonAction();

        DevLog.d(TAG, "onCreate -> e");
    }

    /**
     * @param saveIdx int
     * @param myName String
     * @return SaveData
     */
    private SaveData setupInitialValue(final int saveIdx, final String myName) {

        SettingsRes.getNameView(this)[0].setText(myName);
        String nowDate = new SimpleDateFormat(TIME_STAMP_NAME, Locale.US).format(new Date(System
                .currentTimeMillis()));

        SaveData data = SaveData.createInitialData(saveIdx);
        data.setHoleTitle(nowDate);
        data.getPlayerNameList().put(0, myName);
        data.setEachHolePar(DEFAULT_HOLEPAR_SCORE);

        return data;
    }

    /**
     * @param sData SaveData
     */
    private void setupSavedValue(final SaveData sData) {

        SettingsRes.getTitleView(this).setText(sData.getHoleTitle());
        EditText[] nameView = SettingsRes.getNameView(this);
        nameView[0].setText(sData.getPlayerNameList().get(0));
        nameView[1].setText(sData.getPlayerNameList().get(1));
        nameView[2].setText(sData.getPlayerNameList().get(2));
        nameView[3].setText(sData.getPlayerNameList().get(3));
        SettingsRes.getMemoView(this).setText(sData.getMemoStr());
        EditText[] handiView = SettingsRes.getHandiView(this);
        handiView[0].setText(String.valueOf(sData.getPlayersHandi().get(0)));
        handiView[1].setText(String.valueOf(sData.getPlayersHandi().get(1)));
        handiView[2].setText(String.valueOf(sData.getPlayersHandi().get(2)));
        handiView[3].setText(String.valueOf(sData.getPlayersHandi().get(3)));

        RadioGroup mRadioGroup = SettingsRes.getRadioGroup(this);
        if (sData.getIs18Hround()) {
            mRadioGroup.check(R.id.radio_edit_18h);
        } else {
            mRadioGroup.check(R.id.radio_edit_outin);
        }
        CheckBox mIsShortCheck = SettingsRes.getCheckBox(this);
        mIsShortCheck.setChecked(sData.getIsShortHole());
        setupRoundConditSpinner(sData.getCondition());
    }

    /**
     * @param roundCondit ラウンド条件
     */
    private void setupRoundConditSpinner(final int roundCondit) {

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        adapter.add(getResources().getString(R.string.weather_shine));
        adapter.add(getResources().getString(R.string.weather_cloudy));
        adapter.add(getResources().getString(R.string.weather_rain));
        adapter.add(getResources().getString(R.string.weather_wind));
        adapter.add(getResources().getString(R.string.weather_mud));
        Spinner spinner = SettingsRes.getSpinner(this);
        spinner.setPrompt(getResources().getString(R.string.weather_select_title));
        spinner.setAdapter(adapter);
        spinner.setSelection(roundCondit);
    }

    /**
     * @return true: 18H Round false: Not 18H Round
     */
    private boolean getIs18hRound() {
        RadioGroup radioGroup = SettingsRes.getRadioGroup(this);
        int checkedId = radioGroup.getCheckedRadioButtonId();
        return checkedId == R.id.radio_edit_18h;
    }

    /**
     * プレイヤー追加ボタン押下時の動作
     * 
     * @param button プレイヤー追加ボタン
     */
    private void setupPlayerAddButtonAction(final ImageButton button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                if (mPlayerNum < MAX_PLAYER_NUM) {
                    mPlayerNum++;
                    ViewGroup[] mPlayerEditArea = SettingsRes
                            .getPlayerEditArea(SettingsActivity.this);
                    for (int i = 0; i < MAX_PLAYER_NUM; i++) {
                        if (i < mPlayerNum) {
                            mPlayerEditArea[i].setVisibility(View.VISIBLE);
                        } else {
                            mPlayerEditArea[i].setVisibility(View.GONE);
                        }
                    }
                }
                if (mPlayerNum == MAX_PLAYER_NUM) {
                    button.setImageResource(R.drawable.image_button_add_disable);
                    button.setEnabled(false);
                }
            }
        });
    }

    /**
     * プレイヤー削除ボタン押下時の動作
     * 
     * @param button プレイヤー削除ボタン
     */
    private void setupPlayerDeleteButtonAction(final ImageButton[] button) {

        for (int i = 0; i < MAX_PLAYER_NUM; i++) {
            button[i].setOnClickListener(new View.OnClickListener() {

                public void onClick(final View v) {
                    int j;
                    for (j = 0; j < MAX_PLAYER_NUM; j++) {
                        if (v == button[j]) {
                            break;
                        }
                    }
                    final int pressedNum = j;
                    EditText[] nameView = SettingsRes.getNameView(SettingsActivity.this);
                    final String name = nameView[j].getText().toString();
                    if (isNewCreate() || name.trim().length() == 0) {
                        nameView[j].setText("");
                        for (int i = j + 1; i < mPlayerNum; i++) {
                            nameView[i - 1].setText(nameView[i].getText().toString());
                        }
                        nameView[mPlayerNum - 1].setText("");
                        ViewGroup[] mPlayerEditArea = SettingsRes
                                .getPlayerEditArea(SettingsActivity.this);
                        mPlayerEditArea[mPlayerNum - 1].setVisibility(View.GONE);
                        ImageButton mPlayerAddButton = SettingsRes
                                .getAddButton(SettingsActivity.this);
                        mPlayerAddButton.setImageResource(R.drawable.image_button_add);
                        mPlayerAddButton.setEnabled(true);
                        mPlayerNum--;
                    } else {
                        // プレイヤー削除の確認ダイアログ表示
                        showConfirmDeletePlayerDialog(pressedNum);
                    }
                }
            });
        }
    }

    /**
     * プレイヤー削除の確認ダイアログ表示
     * 
     * @param pressedNum プレイヤー番号
     */
    private void showConfirmDeletePlayerDialog(final int pressedNum) {

        EditText[] nameView = SettingsRes.getNameView(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
        dialog.setIcon(R.drawable.ic_menu_notice);
        dialog.setTitle(getResources().getString(R.string.dlg_delete_player_title));
        dialog.setMessage(String.format(getResources().getString(R.string.dlg_delete_player_msg),
                nameView[pressedNum].getText().toString()));
        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int whichButton) {

                // Saves currently modified settings
                saveCurrentSettings();

                // Remove and reorder list
                int lastPlayerIdx = SGSConfig.MAX_PLAYER_NUM - 1;

                // Name list
                Map<Integer, String> nameList = mSaveData.getPlayerNameList();
                nameList.remove(pressedNum);
                for (int i = pressedNum + 1; i < SGSConfig.MAX_PLAYER_NUM; i++) {
                    nameList.put(i - 1, nameList.get(i));
                }
                nameList.put(lastPlayerIdx, "");
                mSaveData.setNameList(nameList);

                // Score list
                Map<Integer, Map<Integer, Integer>> scoresList = mSaveData.getScoresList();
                scoresList.remove(pressedNum);
                for (int i = pressedNum + 1; i < SGSConfig.MAX_PLAYER_NUM; i++) {
                    scoresList.put(i - 1, scoresList.get(i));
                }
                scoresList.put(lastPlayerIdx, ArrayUtil.createMap(SGSConfig.TOTAL_HOLE_COUNT, 0));
                mSaveData.setScoresList(scoresList);

                // Handi list
                Map<Integer, Integer> handiList = mSaveData.getPlayersHandi();
                handiList.remove(pressedNum);
                for (int i = pressedNum + 1; i < SGSConfig.MAX_PLAYER_NUM; i++) {
                    handiList.put(i - 1, handiList.get(i));
                }
                handiList.put(lastPlayerIdx, 0);
                mSaveData.setPlayersHandi(handiList);

                // Save updated data
                SaveDataPref.saveScoreData(mSaveData);

                // Updates visibility of buttons
                updatePlayerEditValue(pressedNum);

                // decrement player num
                mPlayerNum -= 1;
            }
        });
        dialog.setNegativeButton(android.R.string.no, null);
        dialog.create().show();
    }

    /**
     * @param pressedNum プレイヤー番号
     */
    private void updatePlayerEditValue(final int pressedNum) {

        EditText[] nameView = SettingsRes.getNameView(this);
        nameView[pressedNum].setText("");
        EditText[] handiView = SettingsRes.getHandiView(this);
        for (int i = pressedNum + 1; i < mPlayerNum; i++) {
            handiView[i - 1].setText(handiView[i].getText().toString());
            nameView[i - 1].setText(nameView[i].getText().toString());
        }
        nameView[mPlayerNum - 1].setText("");
        ViewGroup[] mPlayerEditArea = SettingsRes.getPlayerEditArea(this);
        mPlayerEditArea[mPlayerNum - 1].setVisibility(View.GONE);
        ImageButton playerAddButton = SettingsRes.getAddButton(this);
        playerAddButton.setImageResource(R.drawable.image_button_add);
        playerAddButton.setEnabled(true);
    }

    /**
     * setupButtonAction
     */
    public void setupButtonAction() {

        // プレイヤー追加ボタン押下時の動作
        setupPlayerAddButtonAction(SettingsRes.getAddButton(this));

        // プレイヤー削除ボタン押下時の動作
        setupPlayerDeleteButtonAction(SettingsRes.getDelButton(this));

        // 開始ボタン押下時の動作
        SettingsRes.getStartButton(this).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                // スコア編集画面を起動
                startGolfScorer();
            }
        });
        // メモのトグルボタン押下時の動作
        findViewById(R.id.memo_toggle_button).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                EditText memoView = SettingsRes.getMemoView(SettingsActivity.this);
                if (memoView.getVisibility() == View.VISIBLE) {
                    memoView.setVisibility(View.GONE);
                } else {
                    memoView.setVisibility(View.VISIBLE);
                }
            }
        });
        // Title string
        ((TextView) findViewById(R.id.toolbar_title)).setText(R.string.round_setting);
        // 戻るボタン押下時の動作
        findViewById(R.id.toolbar_back_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_back_btn).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                finish();
            }
        });
    }

    /**
     * @return true:開始可 false:開始不可
     */
    private boolean checkEnableStart() {

        // タイトル未入力の場合は開始しない対応
        String holeTitle = SettingsRes.getTitleView(this).getText().toString().replaceAll("<", "");
        if (holeTitle.trim().length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_input_title), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        // プレイヤー未入力の場合は開始しない対応
        Map<Integer, String> names = SettingsRes.getPersonNames(this);
        if (names.get(0).trim().length() == 0 && names.get(1).trim().length() == 0
                && names.get(2).trim().length() == 0 && names.get(3).trim().length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_input_player), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        // 編集時にプレイヤー未入力があれば開始しない対応
        if (!isNewCreate()) {
            for (int i = 0; i < mPlayerNum; i++) {
                if (names.get(i).trim().length() == 0) {
                    Toast.makeText(getApplicationContext(),
                            getResources().getString(R.string.toast_input_player),
                            Toast.LENGTH_SHORT).show();
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * startGolfScorer
     */
    private void startGolfScorer() {

        Map<Integer, String> personNames = SettingsRes.getPersonNames(this);

        // 開始しない条件を確認
        if (!checkEnableStart()) {
            return;
        }
        // 未記入のプレイヤーを詰める
        int tmp = 0;
        String[] strTmp = { "", "", "", "" };
        for (int i = 0; i < MAX_PLAYER_NUM; i++) {
            if (personNames.get(i).trim().length() != 0) {
                strTmp[tmp] = personNames.get(i);
                tmp++;
            }
        }
        System.arraycopy(strTmp, 0, personNames.values().toArray(), 0, MAX_PLAYER_NUM);
        // SharedPreferenceへ記録
        saveCurrentSettings();

        if (isNewCreate()) {
            startMainScoreEditor();
        } else {
            Toast.makeText(this, getResources().getString(R.string.toast_saved_settings),
                    Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    /**
     * startMainScoreEditor
     */
    private void startMainScoreEditor() {

        Intent intent = new Intent(getApplicationContext(), ScoreEditor.class);
        startActivity(intent);
        Toast.makeText(getApplicationContext(),
                getResources().getString(R.string.toast_created_new_data), Toast.LENGTH_SHORT)
                .show();
        finish();
    }

    /**
     * getPersonHandi
     * 
     * @param handiEdit EditText[]
     * @return PersonHandi
     */
    private Map<Integer, Integer> getPersonHandi(final EditText[] handiEdit) {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Integer> handi = new HashMap<>();
        for (int i = 0; i < handiEdit.length; i++) {
            String val = handiEdit[i].getText().toString().replaceAll("<", "");
            try {
                handi.put(i, Integer.parseInt(val));
            } catch (final Exception e) {
                handi.put(i, 0);
            }
        }
        return handi;
    }

    private void saveCurrentSettings() {

        // ショートホール設定の確認
        CheckBox mIsShortCheck = SettingsRes.getCheckBox(this);
        if (mIsShortCheck.isChecked()) {
            mSaveData.setEachHolePar(DEFAULT_HOLEPAR_SHORT_SCORE);
        }
        // タイトル
        mSaveData.setHoleTitle(SettingsRes.getHoleTitle(this));
        // プレイヤー名
        mSaveData.setNameList(SettingsRes.getPersonNames(this));
        // メモ
        mSaveData.setMemoStr(SettingsRes.getMemoStr(this));
        // ハンデ
        mSaveData.setPlayersHandi(getPersonHandi(SettingsRes.getHandiView(this)));
        // 18H or Out/In
        mSaveData.setIs18Hround(getIs18hRound());
        // 天候
        mSaveData.setCondition(SettingsRes.getSpinner(this).getSelectedItemPosition());
        // データを保存
        SaveDataPref.saveScoreData(mSaveData);
    }

    private boolean isNewCreate() {
        return getIntent() == null || getIntent().getExtras() == null ||
                getIntent().getExtras().getBoolean(EXTRAS_IS_NEW_CREATE, true);
    }
}