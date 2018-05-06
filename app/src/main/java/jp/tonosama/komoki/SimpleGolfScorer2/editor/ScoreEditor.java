package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.GraphActivity;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.ScoreViewer;

public class ScoreEditor extends Activity implements WheelViewPagerAdapter.OnWheelChangeListener,
        ViewPager.OnPageChangeListener, View.OnClickListener, DragUi.DragListener,
        WheelViewPagerAdapter.OnPattingButtonClickListener {

    private DragUi mDragUi;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.score_editor);

        if (SaveDataPref.getSelectedSaveData() == null) {
            finish();
        } else {
            setupWheelViews();
            mDragUi = new DragUi(this, this);
            SERes.initParSpinner(this);
            setupUiAction();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getViewPager().getCurrentItem() != getCurrentHoleNumber()) {
            getViewPager().setCurrentItem(getCurrentHoleNumber());
        } else {
            refreshEditor(getCurrentHoleNumber(), false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        saveCurrentState();
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        MenuItem menu2 = menu.add(0, Menu.FIRST + 1, Menu.NONE,
                getResources().getString(R.string.menu_delete));
        menu2.setIcon(R.drawable.ic_menu_delete);
        MenuItem menu3 = menu.add(0, Menu.FIRST + 2, Menu.NONE,
                getResources().getString(R.string.menu_setting));
        menu3.setIcon(R.drawable.image_button_setting);
        return res;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        return SEMenuManager.onOptionsItemSelected(item, this, getSelectedSaveData());
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //DO NOTHING
    }

    @Override
    public void onPageSelected(int position) {
        refreshEditor(position, false);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //DO NOTHING
    }

    @Override
    public void onHoleChanged(int holeNumber) {
        getViewPager().setCurrentItem(holeNumber, false);
    }

    @Override
    public void onWheelChanged(int holeNumber, int playerIdx, int oldVal, int newVal) {
        final SaveData saveData = getSelectedSaveData();
        saveData.getScoresList().get(playerIdx).put(holeNumber, newVal);
        refreshEditor(holeNumber, true);
    }

    @Override
    public void onPattingButtonClick(int holeNumber, int playerIdx) {
        final SaveData saveData = getSelectedSaveData();
        PattingEditorDialog.show(this, holeNumber, playerIdx,
                saveData.getPattingScoresList().get(playerIdx).get(holeNumber),
                new PattingEditorDialog.PattingEditorCallback() {
                    @Override
                    public void onPattingChanged(int holeNumber, int playerIdx, int newVal) {
                        final SaveData saveData = getSelectedSaveData();
                        saveData.getPattingScoresList().get(playerIdx).put(holeNumber, newVal);
                        refreshEditor(holeNumber, false);
                    }
                });
    }

    private void setupWheelViews() {
        ViewPager viewPager = (ViewPager) findViewById(R.id.score_editor_picker_pager);
        viewPager.setAdapter(new WheelViewPagerAdapter(this, this));
        viewPager.setOnPageChangeListener(this);
    }

    private void setupUiAction() {

        // Spinner changed listener
        setSpinnerSelectAction(SERes.getParSpinner(this));

        // UnlockButton クリック動作
        SERes.getLockButton(this).setOnClickListener(this);

        // Viewer ボタンクリック動作
        SERes.getViewButton(this).setOnClickListener(this);
        SERes.getGraphButton(this).setOnClickListener(this);

        // 前へ/次へボタンクリック時の動作
        SERes.getPrevArrow(this).setOnClickListener(this);
        SERes.getNextArrow(this).setOnClickListener(this);

        // 戻るボタンクリック時の動作
        findViewById(R.id.toolbar_back_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_back_btn).setOnClickListener(this);

        // 設定ボタンクリック時の動作
        findViewById(R.id.toolbar_menu_btn).setVisibility(View.VISIBLE);
        findViewById(R.id.toolbar_menu_btn).setOnClickListener(this);
    }

    private void setSpinnerSelectAction(final Spinner spinner) {

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(final AdapterView<?> parent, final View view,
                                       final int position, final long id) {
                final SaveData saveData = getSelectedSaveData();
                final int curHoleIdx = getCurrentHoleNumber();
                saveData.getEachHolePar().put(curHoleIdx, position + SERes.MINIMUM_PAR_COUNT);
            }

            public void onNothingSelected(final AdapterView<?> arg0) {
                Toast.makeText(ScoreEditor.this,
                        getResources().getString(R.string.toast_nothing_is_selected),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private ViewPager getViewPager() {
        return (ViewPager) findViewById(R.id.score_editor_picker_pager);
    }

    private void requestMoveHole(int holeNumber) {
        getViewPager()
                .setCurrentItem(holeNumber % SGSConfig.TOTAL_HOLE_COUNT, true);
    }

    private void refreshEditor(final int holeNumber, boolean ignoreWheel) {
        final SaveData saveData = getSelectedSaveData();
        ((TextView) findViewById(R.id.toolbar_title)).setText(getSelectedSaveData().getHoleTitle());
        final int oldCurHole = getCurrentHoleNumber();
        int newCurHole = holeNumber % SGSConfig.TOTAL_HOLE_COUNT;
        saveData.setCurrentHole(newCurHole);
        // プレイヤー名をセット
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            getPersonNameText(playerIdx).setText(getSelectedSaveData().getPlayerNameList().get(playerIdx));
        }
        // ホール移動時、数値入力済みの場合に移動元ホールをロックする対応
        boolean isHoleLockRequired = true;
        if (oldCurHole == newCurHole) {
            isHoleLockRequired = false;
        }
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!saveData.isPlayerExist(playerIdx)) {
                continue;
            }
            Map<Integer, Integer> score = saveData.getScoresList().get(playerIdx);
            if (score.get(oldCurHole) == 0) {
                isHoleLockRequired = false;
            }
        }
        if (isHoleLockRequired) {
            saveData.getEachHoleLocked().put(oldCurHole, true);
        }
        // Updating wheel views
        if (!ignoreWheel) {
            if (getViewPager() != null && getViewPager().getAdapter() != null) {
                ((WheelViewPagerAdapter) getViewPager().getAdapter()).updateWheelViews(oldCurHole);
            }
        }
        //
        refreshUnLockButton();
        // 
        refreshDragAndDrum();
        //
        removeInvisiblePersonDrum(saveData);
        //
        refreshSpinner();
        // 
        refreshPicker();
    }

    ////////

    private TextView getPersonNameText(int playerIdx) {
        ViewGroup vg = SERes.getPlayerNameArea(this);
        return (TextView) vg.getChildAt(playerIdx);
    }

    private TextView getPersonScoreTextView(int playerIdx) {
        ViewGroup vg = SERes.getTotalScoreArea(this);
        return (TextView) ((ViewGroup) vg.getChildAt(playerIdx)).getChildAt(1);
    }

    private void refreshUnLockButton() {
        final SaveData saveData = getSelectedSaveData();
        ImageButton lockButton = SERes.getLockButton(this);
        if (saveData.getEachHoleLocked().get(saveData.getCurrentHole())) {
            lockButton.setImageResource(R.drawable.ic_menu_lock);
        } else {
            lockButton.setImageResource(R.drawable.ic_menu_unlock);
        }
    }

    private void refreshDragAndDrum() {
        final SaveData saveData = getSelectedSaveData();
        mDragUi.updateCurrentLocation(saveData.getCurrentHole());
        if (saveData.getIs18Hround()) {
            SERes.getHoleIcon(this)
                    .setImageResource(R.drawable.golf_hole_icon);
            SERes.getHoleNameTextView(ScoreEditor.this).setBackgroundResource(
                    SERes.HOLE_NUMBER_IMG_RES_IDS[saveData.getCurrentHole()]);
        } else {
            if (saveData.getCurrentHole() < 9) {
                SERes.getHoleIcon(this)
                        .setImageResource(R.drawable.golf_hole_icon_out);
                SERes.getHoleNameTextView(ScoreEditor.this).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[saveData.getCurrentHole()]);
            } else {
                SERes.getHoleIcon(this)
                        .setImageResource(R.drawable.golf_hole_icon_in);
                SERes.getHoleNameTextView(ScoreEditor.this).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[saveData.getCurrentHole() % 9]);
            }
        }
    }

    private void refreshSpinner() {
        final SaveData saveData = getSelectedSaveData();
        final int curHoleIdx = saveData.getCurrentHole();
        int nextPar = saveData.getEachHolePar().get(curHoleIdx) - SERes.MINIMUM_PAR_COUNT;
        SERes.getParSpinner(this).setSelection(nextPar);
    }

    private void removeInvisiblePersonDrum(final SaveData sData) {
        ((WheelViewPagerAdapter) getViewPager().getAdapter()).removePlayers();
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!sData.isPlayerExist(playerIdx)) {
                getPersonNameText(playerIdx).setVisibility(View.GONE);
                SERes.getTotalScoreArea(this).getChildAt(playerIdx)
                        .setVisibility(View.GONE);
            }
        }
    }

    private void refreshPicker() {
        final SaveData saveData = getSelectedSaveData();
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            getPersonScoreTextView(playerIdx).setText(String.valueOf(saveData.getTotalScore().get(playerIdx)));
        }
    }

    private void saveCurrentState() {
        final SaveData saveData = getSelectedSaveData();
        // 現在の状態を保持
        // パー値を保存
        int par = SERes.getParSpinner(this).getSelectedItemPosition() + SERes.MINIMUM_PAR_COUNT;
        saveData.getEachHolePar().put(saveData.getCurrentHole(), par);
        // プリファレンスに保存
        SaveDataPref.saveScoreData(saveData);
    }

    private int getCurrentHoleNumber() {
        return getSelectedSaveData().getCurrentHole();
    }

    @NonNull
    private SaveData getSelectedSaveData() {
        final SaveData saveData = SaveDataPref.getSelectedSaveData();
        if (saveData != null) {
            return saveData;
        } else {
            return SaveData.createInitialData(SaveDataPref.getSelectedSaveIdx());
        }
    }

    @Override
    public void onClick(View v) {

        if (v.equals(findViewById(R.id.toolbar_back_btn))) {
            finish();
        } else if (v.equals(findViewById(R.id.toolbar_menu_btn))) {
            SEMenuManager.menuRoundSetting(ScoreEditor.this, getSelectedSaveData());
        } else if (v.equals(SERes.getPrevArrow(this))) {
            requestMoveHole(getCurrentHoleNumber() - 1);
        } else if (v.equals(SERes.getNextArrow(this))) {
            requestMoveHole(getCurrentHoleNumber() + 1);
        } else if (v.equals(SERes.getViewButton(this))) {
            saveCurrentState();
            ScoreViewer.startViewer(getSelectedSaveData());
        } else if (v.equals(SERes.getGraphButton(this))) {
            saveCurrentState();
            GraphActivity.startViewer(getSelectedSaveData());
        } else if (v.equals(SERes.getLockButton(this))) {
            final SaveData scoreData = getSelectedSaveData();
            final int curHoleIdx = scoreData.getCurrentHole();
            final boolean isLocked = scoreData.getEachHoleLocked().get(curHoleIdx);
            scoreData.getEachHoleLocked().put(curHoleIdx, !isLocked);
            refreshEditor(getCurrentHoleNumber(), false);
        }
    }
}
