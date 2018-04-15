package jp.tonosama.komoki.SimpleGolfScorer2.history;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.GraphActivity;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.ScoreViewer;

/**
 * @author Komoki
 */
public class HistoryActivity extends FragmentActivity implements HistoryPagerAdapter.Callback,
        OnPageChangeListener {

    /** タグ名称 */
    private static final String TAG = HistoryActivity.class.getSimpleName();
    /**  */
    public static final String EXTRAS_FIXED_DATA_NUM = "jp.tonosama.komoki.EXTRAS_FIXED_DATA_NUM";
    /**  */
    public static final String EXTRAS_SAVED_DATA_NUM = "jp.tonosama.komoki.EXTRAS_SAVED_DATA_NUM";

    /** ViewPager */
    private ViewPager mViewPager;

    /** スコア管理クラスインスタンス */
    private SaveDataList mSaveDataList;

    @Override
    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);

        setContentView(R.layout.history_viewer);

        // 確定済みデータのみのリストを生成する
        mSaveDataList = initGolfScoreData();
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(this);
        HistoryPagerAdapter pagerAdapter = new HistoryPagerAdapter();
        pagerAdapter.setCallback(this);
        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setCurrentItem(mSaveDataList.getFixedDataNum());
        mViewPager.setCurrentItem(0, true);

        final Button historyDetailBtn = (Button) findViewById(R.id.historyDetailButton);
        setupHistoryDetailButtonAction(historyDetailBtn);
        setupArrowButtonAction();
    }

    /**
     * @param scoreManager GolfScoreManager
     * @param position position
     * @return ラウンドサマリ文字列
     */
    private String generateRoundSummaryStr(final SaveDataList scoreManager, //
            final int position) {

        StringBuilder mailBodyStr = new StringBuilder();
        mailBodyStr.append(LetterMessageUtil.getCommnetAboutCount(this, scoreManager, position));
        mailBodyStr.append(getResources().getString(R.string.comment_round_summary_00));
        mailBodyStr.append(LetterMessageUtil.getCommnetAboutWeather(this, scoreManager, position));
        mailBodyStr.append("\n\n");
        mailBodyStr
                .append(String.format(getResources().getString(R.string.comment_round_summary_02),
                        scoreManager.getHoleTitle(position)));

        if (LetterUtil.getIsShortHole(scoreManager, position)) {
            mailBodyStr.append(getResources().getString(R.string.comment_round_summary_03));
        }
        mailBodyStr.append(getResources().getString(R.string.comment_round_summary_04));

        return mailBodyStr.toString();
    }

    /**
     * @param scoreManager GolfScoreManager
     * @param position position
     * @param playerRank playerRank
     * @param playerNum playerNum
     * @return ラウンド記録文字列
     */
    private String generagteRoundRecordStr(final SaveDataList scoreManager, final int position,
            final int playerRank, final int playerNum) {

        //noinspection StringBufferReplaceableByString
        StringBuilder mailBodyStr = new StringBuilder();
        mailBodyStr.append(String.format(
                getResources().getString(R.string.comment_round_record_00),
                LetterUtil.getPlayerRankings(scoreManager, position)[playerRank],
                scoreManager.getPlayerNames(position).get(playerRank),
                LetterUtil.getMyBasedTotalScore(scoreManager, position, playerRank)));
        mailBodyStr.append(String.format(
                getResources().getString(R.string.comment_round_record_01),
                LetterUtil.getMyTotalScore(scoreManager, position, playerRank)));
        mailBodyStr.append(String.format(
                getResources().getString(R.string.comment_round_record_02),
                LetterUtil.getMyFirstHalfScore(scoreManager, position, playerRank)));
        mailBodyStr.append(String.format(
                getResources().getString(R.string.comment_round_record_03),
                LetterUtil.getMySecondHalfScore(scoreManager, position, playerRank)));
        mailBodyStr.append(String.format(
                getResources().getString(R.string.comment_round_record_04),
                scoreManager.getHandiCaps(position).get(playerRank)));
        mailBodyStr.append(String.format("%n"));
        mailBodyStr.append(LetterMessageUtil.getCommentAboutScore(this, scoreManager, position,
                playerRank));
        mailBodyStr.append(LetterMessageUtil.getCommentAboutRank(this, scoreManager, position,
                playerNum));
        mailBodyStr.append("\n");

        return mailBodyStr.toString();
    }

    /**
     * @param scoreManager GolfScoreManager
     * @param position ポジション
     * @return メール本文文字列
     */
    private String generateMailBodyStr(final SaveDataList scoreManager, final int position) {

        StringBuilder bodyStr = new StringBuilder();
        String roundSummaryStr = generateRoundSummaryStr(scoreManager, position);
        bodyStr.append(roundSummaryStr);

        final int playernum = scoreManager.getPlayerNum(position);
        final int[] rankArray = LetterUtil.getPlayerRankingArray(scoreManager, position);
        final String[] mRivalName = LetterMessageUtil.getCommentAboutRival(this, scoreManager,
                position);
        final String[] mAboutHole = LetterMessageUtil.getCommentAboutHole(this, scoreManager,
                position);
        final Map<Integer, String> playerNames = scoreManager.getPlayerNames(position);

        for (int i = 0; i < playernum; i++) {
            final int playerRank = LetterUtil.getPlayerRankingArray(scoreManager, position)[i];
            String roundRecordStr = generagteRoundRecordStr(scoreManager, position, playerRank, i);
            bodyStr.append(roundRecordStr);

            for (int k = 0; k < playernum; k++) {
                if (playerNames.get(rankArray[i]).equals(playerNames.get(k))) {
                    //noinspection RedundantStringFormatCall
                    bodyStr.append(String.format(mRivalName[k]));
                }
            }
            if (playerNames.get(0).equals(playerNames.get(rankArray[i]))) {
                bodyStr.append(LetterMessageUtil.getCommentAboutPatting(this, scoreManager,
                        position, i));
            }
            bodyStr.append("\n\n");
            for (int k = 0; k < playernum; k++) {
                if (playerNames.get(rankArray[i]).equals(playerNames.get(k))) {
                    //noinspection RedundantStringFormatCall
                    bodyStr.append(String.format(mAboutHole[k]));
                }
            }
            bodyStr.append("\n");
        }
        bodyStr.append(getResources().getString(R.string.comment_round_summary_99));

        return bodyStr.toString();
    }

    /**
     * @param button 詳細ボタン
     */
    private void setupHistoryDetailButtonAction(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                final int position = mViewPager.getCurrentItem();
                final SaveData scoreData = mSaveDataList.get(position);

                String mailSubStr = String.format(
                        getResources().getString(R.string.comment_round_summary_subject),
                        mSaveDataList.getHoleTitle(position));

                String mailBodyStr = generateMailBodyStr(mSaveDataList, position);

                final Intent mIntent = new Intent(HistoryActivity.this, DroidLetterActivity.class);
                mIntent.putExtra(Intent.EXTRA_SUBJECT, mailSubStr);
                mIntent.putExtra(Intent.EXTRA_TEXT, mailBodyStr);
                SaveDataPref.setSelectedSaveIdx(scoreData.getSaveIdx());
                startActivity(mIntent);
            }
        });
    }

    /**
     * @param scoreMgr GolfScoreManager
     * @param idx idx
     * @param tvList tvList
     */
    private void setHistory1(final SaveDataList scoreMgr, final int idx, //
            final TextView[] tvList) {

        Resources res = getResources();
        tvList[0].setText(String.format(res.getString(R.string.hist_ret_round_title),
                scoreMgr.getHoleTitle(idx)));
        tvList[1].setText(String.format(res.getString(R.string.hist_ret_my_rank),
                LetterUtil.getMyRanking(scoreMgr, idx, 0), scoreMgr.getPlayerNum(idx)));
        tvList[2].setText(String.format(res.getString(R.string.hist_ret_my_total_score),
                LetterUtil.getMyFirstHalfScore(scoreMgr, idx, 0),
                LetterUtil.getMySecondHalfScore(scoreMgr, idx, 0)));
        tvList[3].setText(String.format(res.getString(R.string.hist_ret_my_ttl_sum_score),
                LetterUtil.getMyTotalScoreMinusHandi(scoreMgr, idx, 0)));
        tvList[4].setText(String.format(res.getString(R.string.hist_ret_my_score_ave),
                LetterUtil.getMyAverage(scoreMgr, idx, 0)));
        tvList[5].setText(String.format(res.getString(R.string.hist_ret_my_pat_ave),
                LetterUtil.getMyPatAverage(scoreMgr, idx, 0)));
        tvList[6].setText(String.format(res.getString(R.string.hist_ret_my_handi),
                scoreMgr.getHandiCaps(idx).get(0)));
        if (LetterUtil.getMyParOnKeepRate(scoreMgr, idx) > 5.0) {
            tvList[7].setText(String.format(res.getString(R.string.hist_ret_my_par_on_rate),
                    LetterUtil.getMyParOnKeepRate(scoreMgr, idx)));
        } else { //パーオンが１も場合はボギーオン
            tvList[7].setText(String.format(res.getString(R.string.hist_ret_my_bogey_on_rate),
                    LetterUtil.getMyBogeyOnKeepRate(scoreMgr, idx)));
        }
    }

    /**
     * @param scoreMgr GolfScoreManager
     * @param idx idx
     * @param tvList tvList
     */
    private void setHistory2(final SaveDataList scoreMgr, final int idx, //
            final TextView[] tvList) {

        Resources res = getResources();
        tvList[8].setText(String.format(
                res.getString(R.string.hist_ret_my_weak_hole),
                LetterUtil.getMyNotGoodHole(scoreMgr, idx, 0),
                LetterUtil.getMyEachHoleScore(scoreMgr, idx, 0,
                        LetterUtil.getMyNotGoodHole(scoreMgr, idx, 0))));
        tvList[9].setText(String.format(res.getString(R.string.hist_ret_my_attitude),
                LetterMessageUtil.getMyAttitude(this, scoreMgr, idx, 0)));
        tvList[10].setText(String.format(res.getString(R.string.hist_ret_my_pressure),
                LetterMessageUtil.getMyPressureScore(this, scoreMgr, idx, 0)));
        tvList[11].setText(String.format(res.getString(R.string.hist_ret_my_vital),
                LetterMessageUtil.getMyVitalScore(this, scoreMgr, idx, 0)));
        tvList[12].setText(String.format(res.getString(R.string.hist_ret_my_pottential),
                LetterUtil.getMyPottentialScore(scoreMgr, idx, 0)));
        if (LetterUtil.getIsShortHole(scoreMgr, idx)) {
            tvList[13].setVisibility(View.VISIBLE);
        } else {
            tvList[13].setVisibility(View.INVISIBLE);
        }
    }

    /**
     * @param scoreMgr GolfScoreManager
     * @param idx セーブ番号
     * @return 生成されたView
     */
    @SuppressLint("InflateParams")
    private View generateViewMain(final SaveDataList scoreMgr, final int idx) {

        View vg = getLayoutInflater().inflate(R.layout.history_view_content, null);
        ImageView imgMyRankImage = (ImageView) vg.findViewById(R.id.hist_myrank_image);
        TextView txtHoleTitle = (TextView) vg.findViewById(R.id.history_hole_title);
        TextView txtMyRank = (TextView) vg.findViewById(R.id.history_my_rank);
        TextView txtMyTotalScore = (TextView) vg.findViewById(R.id.history_my_total_score);
        TextView txtMyTtlMinHandi = (TextView) vg.findViewById(R.id.history_my_total_sum_score);
        TextView txtMyAveScore = (TextView) vg.findViewById(R.id.history_my_average_score);
        TextView txtMyAvePat = (TextView) vg.findViewById(R.id.history_my_average_pat);
        TextView txtMyHandi = (TextView) vg.findViewById(R.id.history_my_handi);
        TextView txtMyParOnRate = (TextView) vg.findViewById(R.id.history_my_par_on_rate);
        TextView txtMyNotGoodHole = (TextView) vg.findViewById(R.id.history_my_not_good_hole);
        TextView txtMyAttitude = (TextView) vg.findViewById(R.id.history_my_attitude);
        TextView txtMyPressure = (TextView) vg.findViewById(R.id.history_my_pressure);
        TextView txtMyPhysical = (TextView) vg.findViewById(R.id.history_my_physical);
        TextView txtMyPottential = (TextView) vg.findViewById(R.id.history_my_pottential);
        TextView txtIsShortTxt = (TextView) vg.findViewById(R.id.history_is_short_text);
        ImageView imgRoundCondit = (ImageView) vg.findViewById(R.id.history_weather_condition);

        if (scoreMgr.getFixedDataNum() < 1) {
            finish();
            return vg;
        }
        setupButtonAction(vg);
        imgMyRankImage.setImageResource(LetterUtil.getRankingImageResourceByRank(LetterUtil
                .getMyRanking(scoreMgr, idx, 0)));

        TextView[] historyTextViewList = new TextView[] { txtHoleTitle, txtMyRank, txtMyTotalScore,
                txtMyTtlMinHandi, txtMyAveScore, txtMyAvePat, txtMyHandi, txtMyParOnRate,
                txtMyNotGoodHole, txtMyAttitude, txtMyPressure, txtMyPhysical, txtMyPottential,
                txtIsShortTxt };
        setHistory1(scoreMgr, idx, historyTextViewList);
        setHistory2(scoreMgr, idx, historyTextViewList);

        imgRoundCondit.setImageResource(scoreMgr.getWeatherImageResourceId(idx));

        return vg;
    }

    /**
     * @param view View
     */
    private void setupButtonAction(final View view) {

        Button graphButton = (Button) view.findViewById(R.id.history_graphviewButton);
        graphButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final int position = mViewPager.getCurrentItem();
                final SaveData scoreData = mSaveDataList.get(position);
                SaveDataPref.setSelectedSaveIdx(scoreData.getSaveIdx());
                GraphActivity.startViewer(scoreData);
            }
        });
        Button tableButton = (Button) view.findViewById(R.id.history_viewButton);
        tableButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final int position = mViewPager.getCurrentItem();
                final SaveData scoreData = mSaveDataList.get(position);
                SaveDataPref.setSelectedSaveIdx(scoreData.getSaveIdx());
                ScoreViewer.startViewer(scoreData);
            }
        });
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * initGolfScoreData
     * 
     * @return SaveDataList
     */
    private SaveDataList initGolfScoreData() {

        SaveDataList dataList = new SaveDataList();

        int fixedHoleNum = 0;
        for (SaveData saveData : SaveDataPref.getSaveDataMap().values()) {
            if (saveData.getHoleTitle().trim().length() < 1) {
                break;
            }
            if (saveData.isHoleResultFixed()) {
                dataList.append(fixedHoleNum, saveData);
                ++fixedHoleNum;
            }
        }
        dataList.sort();
        return dataList;
    }

    /**
     * @param isNext true: false:
     */
    private void onArrowButtonClick(final boolean isNext) {

        int move = -1;
        if (isNext) {
            move = 1;
        }
        int position = (getCount() + mViewPager.getCurrentItem() + move) % getCount();
        mViewPager.setCurrentItem(position, true);
    }

    /**
     * setupArrowButtonAction
     */
    private void setupArrowButtonAction() {
        findViewById(R.id.prev_history).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                onArrowButtonClick(false);
            }
        });
        findViewById(R.id.next_history).setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                onArrowButtonClick(true);
            }
        });
    }

    public int getCount() {
        return mSaveDataList.getFixedDataNum();
    }

    public View generateView(final int idx) {
        return generateViewMain(mSaveDataList, idx);
    }

    public void onPageScrollStateChanged(final int arg0) {
        //
    }

    public void onPageScrolled(final int arg0, final float arg1, final int arg2) {
        //
    }

    @SuppressLint("SetTextI18n")
    public void onPageSelected(final int position) {
        DevLog.d(TAG, "onPageSelected position: " + position);
        TextView txtCurrentHist = (TextView) findViewById(R.id.current_history);
        txtCurrentHist.setText(String.valueOf(position + 1) + " / "
                + String.valueOf(mSaveDataList.getFixedDataNum()));
    }
}