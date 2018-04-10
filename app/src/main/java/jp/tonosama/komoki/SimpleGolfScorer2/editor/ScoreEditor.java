package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.editor.DragUi.DragUiInterface;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.ViewerUtil;
import jp.tonosama.komoki.wheel.widget.OnWheelChangedListener;
import jp.tonosama.komoki.wheel.widget.WheelView;

/**
 * @author Komoki
 */
public class ScoreEditor extends Activity implements AnimationListener, DragUiInterface {

    /** タグ名 */
    private static final String TAG = ScoreEditor.class.getSimpleName();

    /** ドラッグ操作UI */
    private DragUi mDragUi;

    @Override
    public void onResume() {
        super.onResume();

        refreshEditor(getData(), 0);
    }

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        setContentView(SERes.MAIN_LAYOUT_RES_ID);

        // ParSpinnerのセット
        SERes.initParSpinner(this);
        // drumPickerを初期化
        SERes.initDrumPicker(this);
        //
        Button prevArrw = SERes.getPrevArrwButton(this);
        Button nextArrw = SERes.getNextArrwButton(this);
        mDragUi = new DragUi(this);
        setupUiAction(mDragUi, prevArrw, nextArrw);
    }

    /**
     * setupUiAction
     * 
     * @param dragUi DragUi
     * @param prevArrw Button
     * @param nextArrw Button
     */
    private void setupUiAction(final DragUi dragUi, final Button prevArrw,
                               final Button nextArrw) {

        // Spinner changed listener
        setSpinnerSelectAction(SERes.getParSpinner(this));
        // Wheel changed listener
        setDrumPickerChangeAction();
        // RatingBar changed listener
        setRatingBarChangeAction(SERes.getRatingBar(this));
        // UnlockButton クリック動作
        setUnLockButtonAction(SERes.getLockButton(this));
        // Viewer ボタンクリック動作
        setViewerButtonAction();
        // フッターエリアをタッチ&ドラッグ動作
        setFooterAreaAction(dragUi, prevArrw, nextArrw);
        // 前へ/次へボタンクリック時の動作
        setArrowButtonAction(prevArrw, nextArrw);
        // 戻るボタンクリック時の動作
        setExitButtonAction();
        // 設定ボタンクリック時の動作
        setSettingButtonAction();
    }

    /**
     * setExitButtonAction
     */
    private void setExitButtonAction() {

        findViewById(R.id.main_exit_button).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                finish();
            }
        });
    }

    /**
     * setExitButtonAction
     */
    private void setSettingButtonAction() {

        findViewById(R.id.infoButton).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                SEMenuManager.menuRoundSetting(ScoreEditor.this, getData());
            }
        });
    }

    /**
     * setArrowButtonAction
     * 
     * @param prevArrw Button
     * @param nextArrw Button
     */
    private void setArrowButtonAction(final Button prevArrw, final Button nextArrw) {

        // 前のホールへ の矢印ボタンクリック
        prevArrw.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                onArrowButtonClick(prevArrw);
            }
        });
        // 次のホールへ の矢印ボタン
        nextArrw.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                onArrowButtonClick(nextArrw);
            }
        });
    }

    /**
     * @param view ImageView
     */
    private void onArrowButtonClick(final Button view) {

        int moveX = findViewById(SERes.DRUM_PICKER_AREA_RES_ID).getWidth() + 10;
        int moveSlot = -1;
        boolean isNext = view.equals(SERes.getNextArrwButton(this));
        if (isNext) {
            moveX = -findViewById(SERes.DRUM_PICKER_AREA_RES_ID).getWidth() - 10;
            moveSlot = 1;
        }
        if (mDragUi.isAnimating()) {
            return;
        }
        // アニメーション対応
        // (右から中心,中心から右方向,下から中心,中心から下方向)
        View drumPickerArea = findViewById(SERes.DRUM_PICKER_AREA_RES_ID);
        TranslateAnimation trans = new TranslateAnimation(0, moveX, 0, 0);
        trans.setDuration(200);
        trans.setInterpolator(new DecelerateInterpolator());
        AnimationSet set = new AnimationSet(true);
        set.addAnimation(trans);
        set.setAnimationListener(this);
        mDragUi.setAnimVal(mDragUi.getAnimVal(isNext));
        mDragUi.setMoveValue(moveSlot);
        mDragUi.setIsAnimating(true);
        drumPickerArea.startAnimation(set);
    }

    public boolean onCreateOptionsMenu(final Menu menu) {
        boolean res = super.onCreateOptionsMenu(menu);
        MenuItem menu2 = menu.add(0, Menu.FIRST + 1, Menu.NONE,
                getResources().getString(R.string.menu_delete));
        menu2.setIcon(R.drawable.ic_menu_delete);
        MenuItem menu3 = menu.add(0, Menu.FIRST + 2, Menu.NONE,
                getResources().getString(R.string.menu_setting));
        menu3.setIcon(R.drawable.imane_button_setting);
        return res;
    }

    public boolean onOptionsItemSelected(final MenuItem item) {
        return SEMenuManager.onOptionsItemSelected(item, this, getData());
    }

    /**
     * @param sData SaveData
     * @param holeMove int
     */
    private void refreshEditor(final SaveData sData, final int holeMove) {
        ((TextView) findViewById(R.id.hole_title)).setText(getData().getHoleTitle());
        final int oldCurHole = sData.getCurrentHole();
        int newCurHole = (oldCurHole + holeMove) % SGSConfig.TOTAL_HOLE_COUNT;
        if (newCurHole == 0) {
            newCurHole = SGSConfig.TOTAL_HOLE_COUNT;
        }
        sData.setCurrentHole(newCurHole);
        // プレイヤー名をセット
        TextView[] personNameTextViews = SERes.getPersonNameTextViews(this);
        for (int i = 0; i < personNameTextViews.length; i++) {
            personNameTextViews[i].setText(getData().getPlayerNameList().get(i));
        }
        // ホール移動時、数値入力済みの場合に移動元ホールをロックする対応
        final WheelView[] drum = SERes.getDrumPicker(ScoreEditor.this);
        if (holeMove != 0
                && (drum[0].getCurrentItem() != 0 || sData.getPlayerNameList().get(0).trim().length() < 1)
                && (drum[1].getCurrentItem() != 0 || sData.getPlayerNameList().get(1).trim().length() < 1)
                && (drum[2].getCurrentItem() != 0 || sData.getPlayerNameList().get(2).trim().length() < 1)
                && (drum[3].getCurrentItem() != 0 || sData.getPlayerNameList().get(3).trim().length() < 1)) {
            if (!sData.getEachHoleLocked().get(oldCurHole - 1)) {
                sData.getEachHoleLocked().put(oldCurHole - 1, true);
            }
        }
        // ドラムをロックする対応
        final boolean isLocked = sData.getEachHoleLocked().get(newCurHole - 1);
        for (WheelView aDrum : drum) {
            aDrum.setIsWheelLocked(isLocked);
            aDrum.invalidate();
        }
        // Rating の更新
        refreshEditorRating(sData);
        // 
        refreshDragAndDrum(sData, drum);
        //
        refreshSpinner(sData);
        // 
        refreshPicker(sData);
    }

    /**
     * @param sData GolfScoreData
     */
    private void refreshEditorRating(final SaveData sData) {

        RatingBar ratingBar = SERes.getRatingBar(this);
        // RatingBar のカレント数値エリアに値をセット
        ratingBar.setIsIndicator(false);
        ratingBar.setRating(sData.getPattingScoresList().get(0).get(sData.getCurrentHole() - 1));
        ImageView iv = (ImageView) findViewById(R.id.my_pat_img);
        iv.setImageResource(SERes.MY_PAT_IMG_RES_IDS[sData.getPattingScoresList().get(0).get(sData
                .getCurrentHole() - 1)]);
        // RatingBarをロックする対応
        ratingBar.setIsIndicator(sData.getEachHoleLocked().get(sData.getCurrentHole() - 1));
        // ロック/アンロックボタンを表示する対応
        ImageButton lockButton = SERes.getLockButton(this);
        if (sData.getEachHoleLocked().get(sData.getCurrentHole() - 1)) {
            lockButton.setImageResource(R.drawable.ic_menu_lock);
            ratingBar.setBackgroundColor(0x66000000);
        } else {
            lockButton.setImageResource(R.drawable.ic_menu_unlock);
            ratingBar.setBackgroundColor(0x00000000);
        }
    }

    /**
     * @param sData GolfScoreData
     * @param drum WheelView[]
     */
    private void refreshDragAndDrum(final SaveData sData, final WheelView[] drum) {
        // ホール名と矢印のセット
        ImageView dragImag = SERes.getDragImg(this);
        dragImag.setVisibility(View.VISIBLE);
        SERes.getCurHoleImg(this).setVisibility(View.GONE);
        if (sData.getIs18Hround()) {
            ((ImageView) findViewById(R.id.golf_hole_icon))
                    .setImageResource(R.drawable.golf_hole_icon);
            SERes.getHoleTitleTextView(ScoreEditor.this).setBackgroundResource(
                    SERes.HOLE_NUMBER_IMG_RES_IDS[sData.getCurrentHole() - 1]);
        } else {
            if (sData.getCurrentHole() < 10) {
                ((ImageView) findViewById(R.id.golf_hole_icon))
                        .setImageResource(R.drawable.golf_hole_icon_out);
                SERes.getHoleTitleTextView(ScoreEditor.this).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[sData.getCurrentHole() - 1]);
            } else {
                ((ImageView) findViewById(R.id.golf_hole_icon))
                        .setImageResource(R.drawable.golf_hole_icon_in);
                SERes.getHoleTitleTextView(ScoreEditor.this).setBackgroundResource(
                        SERes.HOLE_NUMBER_IMG_RES_IDS[sData.getCurrentHole() - 10]);
            }
        }
        // 現在ホール位置を表示する対応
        dragImag.setImageResource(SERes.CURRENT_HOLE_IMG_RES_IDS[sData.getCurrentHole() - 1]);
        // いない人の Picker を消す対応
        removeInvisiPersonDrum(drum, sData);
        // drumPicker のカレント数値エリアに値をセット
        for (int i = 0; i < drum.length; i++) {
            // !! 現在ホールのスコアデータが書き換わる !!
            final int curHoleIdx = sData.getCurrentHole() - 1;
            final int score = sData.getScoresList().get(i).get(curHoleIdx);
            drum[i].setCurrentItem(score);
        }
    }

    /**
     * @param sData GolfScoreData
     */
    private void refreshSpinner(final SaveData sData) {
        final int curHoleIdx = sData.getCurrentHole() - 1;
        int nextPar = sData.getEachHolePar().get(curHoleIdx) - SERes.MINIMUM_PAR_COUNT;
        SERes.getParSpinner(this).setSelection(nextPar);
    }

    /**
     * @param drum WheelView[]
     * @param sData GolfScoreData
     */
    private void removeInvisiPersonDrum(final WheelView[] drum, final SaveData sData) {
        TextView[] personNameTextViews = SERes.getPersonNameTextViews(this);
        TextView[] personScoreTextViews = SERes.getPersonScoreTextViews(this);
        for (int i = 0; i < drum.length; i++) {
            if (sData.getPlayerNameList().get(i).trim().length() == 0) {
                personNameTextViews[i].setVisibility(View.GONE);
                drum[i].setVisibility(View.GONE);
                personScoreTextViews[i].setVisibility(View.GONE);
            }
        }
    }

    public void refreshPicker(final SaveData saveData) {
        // トータルスコアを表示
        TextView[] personScoreTextViews = SERes.getPersonScoreTextViews(this);
        for (int i = 0; i < personScoreTextViews.length; i++) {
            personScoreTextViews[i].setText(String.valueOf(saveData.getTotalScore()[i]));
        }
    }

    /**
     * @param scoreData GolfScoreData
     */
    private void saveCurrentState(final SaveData scoreData) {
        if (scoreData == null) {
            return;
        }
        // 現在の状態を保持
        // パー値を保存
        int par = SERes.getParSpinner(this).getSelectedItemPosition() + SERes.MINIMUM_PAR_COUNT;
        scoreData.getEachHolePar().put(scoreData.getCurrentHole() - 1, par);
        // 各プレイヤーのスコア値を保存
        final WheelView[] drumPickers = SERes.getDrumPicker(this);
        for (int i = 0; i < drumPickers.length; i++) {
            int score = drumPickers[i].getCurrentItem();
            scoreData.getScoresList().get(i).put(scoreData.getCurrentHole() - 1, score);
        }
        // パットのスコア値を保存
        RatingBar ratingBar = SERes.getRatingBar(this);
        int pat = (int) ratingBar.getRating();
        scoreData.getPattingScoresList().get(0).put(scoreData.getCurrentHole() - 1, pat);
        // プリファレンスに保存
        SaveDataPref.saveScoreData(scoreData);
    }

    @Override
    public void onPause() {
        super.onPause();
        saveCurrentState(getData());
    }

    /**
     * onAnimationEndPrev
     * 
     * @param view View
     * @param animVal int
     */
    private void onAnimationEndMain(final View view, final int animVal) {

        DevLog.d(TAG, "onAnimationEndMain() curHole:" + getData().getCurrentHole() + " moveVal:"
                + mDragUi.getMoveValue());

        refreshEditor(getData(), mDragUi.getMoveValue());
        saveCurrentState(getData());

        mDragUi.setCurrentX(DragUi.DEFAULT_X);
        view.layout(DragUi.DEFAULT_X, 0, DragUi.DEFAULT_X + view.getWidth(), view.getHeight());

        // アニメーション対応
        // (右から中心,中心から右方向,下から中心,中心から下方向)
        TranslateAnimation trans = new TranslateAnimation(animVal, 0, 0, 0);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);

        trans.setDuration(150);
        alpha.setDuration(150);

        trans.setInterpolator(new DecelerateInterpolator());
        alpha.setInterpolator(new AccelerateInterpolator());

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(trans);
        set.addAnimation(alpha);

        view.startAnimation(set);
    }

    public void onAnimationEnd(final Animation animation) {

        View drumPickerArea = findViewById(SERes.DRUM_PICKER_AREA_RES_ID);
        onAnimationEndMain(drumPickerArea, mDragUi.getAnimVal());
        mDragUi.setIsAnimating(false);
    }

    public void onAnimationRepeat(final Animation animation) {
        //
    }

    public void onAnimationStart(final Animation animation) {
        //
    }

    /**
     * setDrumPickerChangeAction
     */
    private void setDrumPickerChangeAction() {

        for (int i = 0; i < SERes.getDrumPicker(this).length; i++) {
            SERes.getDrumPicker(this)[i].addChangingListener(new OnWheelChangedListener() {

                public void onChanged(final WheelView wheel, final int oldVal, final int newVal) {
                    if (oldVal == newVal) {
                        return;
                    }
                    View[] wheels = SERes.getDrumPicker(ScoreEditor.this);
                    int playerIdx = 0;
                    for (int i = 0; i < wheels.length; i++) {
                        if (wheel.equals(wheels[i])) {
                            playerIdx = i;
                            break;
                        }
                    }
                    int curHoleIdx = getData().getCurrentHole() - 1;
                    getData().getScoresList().get(playerIdx).put(curHoleIdx, newVal);
                    refreshPicker(getData());
                }
            });
        }
    }

    /**
     * @param spinner Spinner
     */
    private void setSpinnerSelectAction(final Spinner spinner) {

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            public void onItemSelected(final AdapterView<?> parent, final View view,
                    final int position, final long id) {
                SaveData sData = getData();
                final int curHoleIdx = sData.getCurrentHole() - 1;
                sData.getEachHolePar().put(curHoleIdx, position + SERes.MINIMUM_PAR_COUNT);
            }

            public void onNothingSelected(final AdapterView<?> arg0) {
                Toast.makeText(ScoreEditor.this,
                        getResources().getString(R.string.toast_nothing_is_selected),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * setViewerButtonAction
     */
    private void setViewerButtonAction() {

        // View Button クリック動作
        Button viewerButton = (Button) findViewById(R.id.viewButton);
        viewerButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                saveCurrentState(getData());
                ViewerUtil.startTableActivity(ScoreEditor.this, getData());
            }
        });
        // Graph Buton クリック動作
        Button graphButton = (Button) findViewById(R.id.graphviewButton);
        graphButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                saveCurrentState(getData());
                ViewerUtil.startGraphActivty(ScoreEditor.this, getData());
            }
        });
    }

    /**
     * setFooterAreaAction
     * 
     * @param dragUi DragUi
     * @param prevArrw Button
     * @param nextArrw Button
     */
    private void setFooterAreaAction(final DragUi dragUi, final Button prevArrw,
            final Button nextArrw) {
        ViewGroup footerArea = SERes.getFooterArea(this);
        ImageView dragImag = SERes.getDragImg(this);
        dragUi.setFooterAreaAction(this, this, footerArea, prevArrw, nextArrw, dragImag,
                SERes.getCurHoleImg(this));
    }

    /**
     * @param ratingBar RatingBar
     */
    private void setRatingBarChangeAction(final RatingBar ratingBar) {

        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

            public void onRatingChanged(final RatingBar ratingBar, final float rating,
                    final boolean fromUser) {

                int pat = (int) rating;
                ImageView iv = (ImageView) findViewById(R.id.my_pat_img);
                iv.setImageResource(SERes.MY_PAT_IMG_RES_IDS[pat]);
                if (fromUser) {
                    int curHoleIdx = getData().getCurrentHole() - 1;
                    getData().getPattingScoresList().get(0).put(curHoleIdx, pat);
                }
            }
        });
    }

    /**
     * @param button View
     */
    private void setUnLockButtonAction(final View button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final SaveData scoreData = getData();
                final int curHoleIdx = scoreData.getCurrentHole() - 1;
                final boolean isLocked = scoreData.getEachHoleLocked().get(curHoleIdx);
                scoreData.getEachHoleLocked().put(curHoleIdx, !isLocked);
                refreshEditor(scoreData, 0);
            }
        });
    }

    @Override
    public SaveData getData() {
        int selectedIdx = SaveDataPref.getSelectedSaveIdx();
        return SaveDataPref.getSaveDataMap().get(selectedIdx);
    }
}