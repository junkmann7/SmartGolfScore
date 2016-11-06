package jp.tonosama.komoki.SimpleGolfScorer2.setting;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;
import jp.tonosama.komoki.SimpleGolfScorer2.editor.ScoreEditor;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * @author Komoki
 */
public class SettingsActivity extends Activity {

    /** タグ名 */
    private static final String TAG = SettingsActivity.class.getSimpleName();

    /** 表示プレイヤー数 */
    private int mPlayerNum = 2;

    /** 保存データ */
    private SaveData mSaveData;

    /** タイムスタンプのフォーマット */
    private static final String TIME_STAMP_NAME = "yyyy/MM/dd";

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        if (SaveDataList.DEBUG) {
            Log.d(TAG, "onCreate -> s");
        }
        setContentView(R.layout.new_create);

        int saveIdx = getSaveIdx();
        if (saveIdx < 0) {
            finish();
            return;
        }
        if (getIsNewCreate()) {
            String myName = getIntent().getExtras().getString(Util.EXTRAS_MY_NAME);
            mSaveData = setupInitialValue(saveIdx, myName);
        } else {
            mSaveData = Util.loadScoreDataFromPref(this, saveIdx);
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

        if (SaveDataList.DEBUG) {
            Log.d(TAG, "onCreate -> e");
        }
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

        SaveData sData = new SaveData();
        sData.setSaveIdx(saveIdx);
        sData.setHoleTitle(nowDate);
        sData.getNames()[0] = myName;
        sData.setEachHolePar(Util.DEFAULT_HOLEPAR_SCORE);

        return sData;
    }

    /**
     * @param sData SaveData
     */
    private void setupSavedValue(final SaveData sData) {

        SettingsRes.getTitleView(this).setText(sData.getHoleTitle());
        EditText[] nameView = SettingsRes.getNameView(this);
        nameView[0].setText(sData.getNames()[0]);
        nameView[1].setText(sData.getNames()[1]);
        nameView[2].setText(sData.getNames()[2]);
        nameView[3].setText(sData.getNames()[3]);
        SettingsRes.getMemoView(this).setText(sData.getMemoStr());
        EditText[] handiView = SettingsRes.getHandiView(this);
        handiView[0].setText(String.valueOf(sData.getPlayersHandi()[0]));
        handiView[1].setText(String.valueOf(sData.getPlayersHandi()[1]));
        handiView[2].setText(String.valueOf(sData.getPlayersHandi()[2]));
        handiView[3].setText(String.valueOf(sData.getPlayersHandi()[3]));

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
                if (mPlayerNum < Util.MAX_PLAYER_NUM) {
                    mPlayerNum++;
                    ViewGroup[] mPlayerEditArea = SettingsRes
                            .getPlayerEditArea(SettingsActivity.this);
                    for (int i = 0; i < Util.MAX_PLAYER_NUM; i++) {
                        if (i < mPlayerNum) {
                            mPlayerEditArea[i].setVisibility(View.VISIBLE);
                        } else {
                            mPlayerEditArea[i].setVisibility(View.GONE);
                        }
                    }
                }
                if (mPlayerNum == Util.MAX_PLAYER_NUM) {
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

        for (int i = 0; i < Util.MAX_PLAYER_NUM; i++) {
            button[i].setOnClickListener(new View.OnClickListener() {

                public void onClick(final View v) {
                    int j;
                    for (j = 0; j < Util.MAX_PLAYER_NUM; j++) {
                        if (v == button[j]) {
                            break;
                        }
                    }
                    final int pressedNum = j;
                    EditText[] nameView = SettingsRes.getNameView(SettingsActivity.this);
                    final String name = nameView[j].getText().toString();
                    if (getIsNewCreate() || name.trim().length() == 0) {
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

        final String[] mPrefDataKey = Util.PREF_DATA_KEY;
        EditText[] nameView = SettingsRes.getNameView(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(SettingsActivity.this);
        dialog.setIcon(R.drawable.ic_menu_notice);
        dialog.setTitle(getResources().getString(R.string.dlg_delete_player_title));
        dialog.setMessage(String.format(getResources().getString(R.string.dlg_delete_player_msg),
                nameView[pressedNum].getText().toString()));
        dialog.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int whichButton) {
                updatePlayerEditValue(pressedNum);
                SharedPreferences pref = getSharedPreferences(Util.PREF_DATA_SLOT[getSaveIdx()],
                        MODE_PRIVATE);
                String[] playerScores = { pref.getString(mPrefDataKey[8], ""),
                        pref.getString(mPrefDataKey[9], ""), pref.getString(mPrefDataKey[10], ""),
                        pref.getString(mPrefDataKey[11], "") };
                String[] playersPrKey = { mPrefDataKey[8], mPrefDataKey[9], mPrefDataKey[10],
                        mPrefDataKey[11] };
                Editor e = pref.edit();
                for (int i = pressedNum + 1; i < mPlayerNum; i++) {
                    e.putString(playersPrKey[i - 1], playerScores[i]);
                }
                e.putString(playersPrKey[mPlayerNum - 1], "0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,");
                String[] playerNames = { pref.getString(mPrefDataKey[4], ""),
                        pref.getString(mPrefDataKey[5], ""), pref.getString(mPrefDataKey[6], ""),
                        pref.getString(mPrefDataKey[7], "") };
                String[] playerNamesKey = { mPrefDataKey[4], mPrefDataKey[5], mPrefDataKey[6],
                        mPrefDataKey[7], };
                for (int i = pressedNum + 1; i < mPlayerNum; i++) {
                    e.putString(playerNamesKey[i - 1], playerNames[i]);
                }
                e.commit();
                mPlayerNum--;
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
        ImageButton mPlayerAddButton = SettingsRes.getAddButton(this);
        mPlayerAddButton.setImageResource(R.drawable.image_button_add);
        mPlayerAddButton.setEnabled(true);
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
        // 戻るボタン押下時の動作
        findViewById(R.id.main_exit_button).setOnClickListener(new View.OnClickListener() {

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
        String[] names = SettingsRes.getPersonNames(this);
        if (names[0].trim().length() == 0 && names[1].trim().length() == 0
                && names[2].trim().length() == 0 && names[3].trim().length() == 0) {
            Toast.makeText(getApplicationContext(),
                    getResources().getString(R.string.toast_input_player), Toast.LENGTH_SHORT)
                    .show();
            return false;
        }
        // 編集時にプレイヤー未入力があれば開始しない対応
        if (!getIsNewCreate()) {
            for (int i = 0; i < mPlayerNum; i++) {
                if (names[i].trim().length() == 0) {
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
    public void startGolfScorer() {

        String[] personNames = SettingsRes.getPersonNames(this);

        // 開始しない条件を確認
        if (!checkEnableStart()) {
            return;
        }
        // 未記入のプレイヤーを詰める
        int tmp = 0;
        String[] strTmp = { "", "", "", "" };
        for (int i = 0; i < Util.MAX_PLAYER_NUM; i++) {
            if (personNames[i].trim().length() != 0) {
                strTmp[tmp] = personNames[i];
                tmp++;
            }
        }
        System.arraycopy(strTmp, 0, personNames, 0, Util.MAX_PLAYER_NUM);
        // SharedPreferenceへ記録
        savePreference();

        if (getIsNewCreate()) {
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
        intent.putExtra(Util.EXTRAS_SELECTED_IDX, getSaveIdx());
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
    private int[] getPersonHandi(final EditText[] handiEdit) {
        int[] handi = new int[4];
        for (int i = 0; i < handiEdit.length; i++) {
            String val = handiEdit[i].getText().toString().replaceAll("<", "");
            try {
                handi[i] = Integer.parseInt(val);
            } catch (final Exception e) {
                handi[i] = 0;
            }
        }
        return handi;
    }

    /**
     * savePreference
     */
    private void savePreference() {

        SaveData scoreData = mSaveData;

        // ショートホール設定の確認
        CheckBox mIsShortCheck = SettingsRes.getCheckBox(this);
        if (mIsShortCheck.isChecked()) {
            scoreData.setEachHolePar(Util.DEFAULT_HOLEPAR_SHORT_SCORE);
        }
        // タイトル
        scoreData.setHoleTitle(SettingsRes.getHoleTitle(this));
        // プレイヤー名
        scoreData.setNames(SettingsRes.getPersonNames(this));
        // メモ
        scoreData.setMemoStr(SettingsRes.getMemoStr(this));
        // ハンデ
        scoreData.setPlayersHandi(getPersonHandi(SettingsRes.getHandiView(this)));
        // 18H or Out/In
        scoreData.setIs18Hround(getIs18hRound());
        // 天候
        scoreData.setCondition(SettingsRes.getSpinner(this).getSelectedItemPosition());
        // データを保存
        Util.saveScoreData(this, scoreData);
    }

    /**
     * @return idx
     */
    private int getSaveIdx() {
        if (getIntent() == null || getIntent().getExtras() == null) {
            return Util.INVALID;
        }
        return getIntent().getExtras().getInt(Util.EXTRAS_SELECTED_IDX, Util.INVALID);
    }

    /**
     * @return isNewCreate
     */
    private boolean getIsNewCreate() {
        return getIntent() == null || getIntent().getExtras() == null ||
                getIntent().getExtras().getBoolean(Util.EXTRAS_IS_NEW_CREATE, true);
    }
}