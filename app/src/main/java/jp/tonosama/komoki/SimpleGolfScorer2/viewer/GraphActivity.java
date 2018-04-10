package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

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
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.chart.ChartConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

/**
 * @author Komoki
 */
public class GraphActivity extends Activity {

    /** */
    private int mBaseLineType = ChartConfig.DEFAULT_BASELINE_TYPE;
    /**  */
    private SaveData mScoreData;
    /**  */
    private Button mOutputBtn;
    /**  */
    private Button mSettingBtn;

    protected int getLayoutResourceId() {
        return R.layout.xy_chart_activity;
    }

    private float mDpx;
    private int mMinValueY = Integer.MAX_VALUE;
    private int mMaxValueY = Integer.MIN_VALUE;
    /**  */
    public static final String EXTRAS_IMAGE_URI_GRAPH = "jp.tonosama.komoki.EXTRAS_IMAGE_URI_GRAPH";
    /** */
    public static final String EXTRAS_OUT_SAVE_DATA = "jp.tonosama.komoki.EXTRAS_OUTPUT_SAVE_DATA";

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        initData();
        makeChart();
    }

    protected void initData() {
        int selectedIdx = SaveDataPref.getSelectedSaveIdx();
        if (selectedIdx < 0) {
            return;
        }
        setScoreData(SaveDataPref.getSaveDataMap().get(selectedIdx));
        if (getScoreData() == null) {
            setScoreData(SaveDataPref.getSaveDataMap().get(selectedIdx));
        }
        // get shared preferrence
        SharedPreferences pref = getSharedPreferences(ChartConfig.PREF_GRAPH_SETTING, MODE_PRIVATE);
        mBaseLineType = pref.getInt(ChartConfig.PREF_GRAPH_BASIS_TYPE_KEY,
                ChartConfig.DEFAULT_BASELINE_TYPE);

        updateScoreData();

        mOutputBtn = (Button) findViewById(R.id.graph_output_button);
        mSettingBtn = (Button) findViewById(R.id.graph_setting_button);
        mSettingBtn.setText(String.format(
                getResources().getString(R.string.btn_change_graph_basis),
                ChartConfig.BASE_LINE_TYPE_STR[mBaseLineType]));

        setupOutputButtonAction(mOutputBtn);
        setupSettingButtonAction(mSettingBtn);
        startOutputProgressIfNeed();
    }

    private void makeChart() {

        float dpp = getResources().getDisplayMetrics().densityDpi;
        mDpx = dpp / 160;

        LinearLayout chartlayout = (LinearLayout) findViewById(R.id.chart);
        GraphicalView graphicalView = graphMake();
        chartlayout.removeAllViews();
        chartlayout.addView(graphicalView);
    }

    private GraphicalView graphMake() {

        XYMultipleSeriesDataset dataset;

        // データのタイトル
        String title = getIntent().getStringExtra(Intent.EXTRA_TITLE);
        ((TextView) findViewById(R.id.chart_title_placeholder)).setText(title);

        XYMultipleSeriesRenderer renderer = buildRenderer();

        renderer.setApplyBackgroundColor(false);

        // this is trick to set transparent background
        // チャートの背景色
        renderer.setBackgroundColor(Color.argb(0x00, 0xFF, 0xFF, 0xFF));
        // チャート外側余白の背景色
        renderer.setMarginsColor(Color.argb(0x00, 0xFF, 0xFF, 0xFF));

        renderer.setXLabels(SGSConfig.TOTAL_HOLE_COUNT + 2);
        renderer.setYLabels(SGSConfig.TOTAL_HOLE_COUNT + 2);

        // グリッド表示
        renderer.setShowGrid(true);

        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);

        dataset = buildDataSet();

        int margin = Math.abs(mMaxValueY - mMinValueY) / 10;
        setChartSettings(renderer,
                "",     // title
                "HOLE", // X-axis title
                "",     // y-axis title
                0,      // x Min
                SGSConfig.TOTAL_HOLE_COUNT + 1, // x Max
                mMinValueY - margin,    // y Min
                mMaxValueY + margin,    // y Max
                Color.GRAY,
                Color.GRAY);

        GraphicalView gView = ChartFactory.getLineChartView(
                getApplicationContext(), dataset, renderer);

        LinearLayout cHartEngineLayout = (LinearLayout) findViewById(R.id.chart);
        cHartEngineLayout.addView(gView);
        return gView;
    }

    @SuppressWarnings("SameParameterValue")
    private void setChartSettings(XYMultipleSeriesRenderer renderer,
                                  String title, String xTitle, String yTitle, double xMin,
                                  double xMax, double yMin, double yMax, int axesColor,
                                  int labelsColor) {

        renderer.setChartTitle(title);
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setBarWidth(2 * mDpx);
    }

    private XYMultipleSeriesDataset buildDataSet() {
        XYMultipleSeriesDataset dataSet = new XYMultipleSeriesDataset();
        for (int i = 0; i < mScoreData.getPlayerNum(); i++) {
            addXYSeries(dataSet, mScoreData.getPlayerNameList().get(i), 0, i);
        }
        return dataSet;
    }

    @SuppressWarnings("SameParameterValue")
    private void addXYSeries(XYMultipleSeriesDataset dataSet, String title, int scale, int idx) {
        XYSeries series = new XYSeries(title, scale);

        int total = 0;
        for (int i = 0; i < SGSConfig.TOTAL_HOLE_COUNT; i++) {
            total += mScoreData.getDemoSeries(idx)[i];
            series.add(i, i + 1, total);
            mMinValueY = Math.min(mMinValueY, total);
            mMaxValueY = Math.max(mMaxValueY, total);
        }

        dataSet.addSeries(series);
    }

    private XYMultipleSeriesRenderer buildRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        PointStyle style = PointStyle.CIRCLE;
        for (int i = 0; i < mScoreData.getPlayerNum(); i++) {
            setRenderer(renderer, ChartConfig.DEFAULT_COLORS[i], style);
        }
        return renderer;
    }

    private void setRenderer(XYMultipleSeriesRenderer renderer, int colors, PointStyle styles) {

        renderer.setAxisTitleTextSize(18 * mDpx);
        renderer.setChartTitleTextSize(0 * mDpx);

        renderer.setLabelsTextSize(14 * mDpx);
        renderer.setLegendTextSize(14 * mDpx);
        renderer.setFitLegend(true);

        renderer.setYLabelsPadding(0 * mDpx);
        renderer.setXLabelsPadding(0 * mDpx);

        renderer.setPanEnabled(false, false);
        renderer.setZoomEnabled(false, false);

        renderer.setPointSize(3 * mDpx);

        // グリッド色
        renderer.setGridColor(Color.LTGRAY);
        // upper, left, under, right
        int topmargine = (int) (0 * mDpx);
        int leftmargine = (int) (24 * mDpx);
        int bottommargine = (int) (48 * mDpx);
        int rihgtmargin = (int) (0 * mDpx);
        renderer.setMargins(new int[]{topmargine, leftmargine, bottommargine, rihgtmargin});

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(colors);

        // Point stype の設定
        r.setPointStyle(styles);
        r.setFillPoints(true);
        r.setLineWidth(1 * mDpx);

        renderer.addSeriesRenderer(r);
    }

    /**
     * startOutputProgressIfNeed
     */
    private void startOutputProgressIfNeed() {
        if (getScoreData().isOutputImageFlg()) {
            getScoreData().setOutputImageFlg(false);
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog
                    .setMessage(getResources().getString(R.string.send_by_email_output_graph));
            progressDialog.setCancelable(false);
            progressDialog.show();
            Runnable runnable = new Runnable() {

                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.e("Runnable", "InterruptedException");
                    }
                    GraphActivity.this.runOnUiThread(new Runnable() {

                        public void run() {
                            Uri imageUri = getOutputImageUri();
                            finish();
                            progressDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), ScoreViewer.class);
                            String subject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
                            String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                            intent.putExtra(Intent.EXTRA_TEXT, text);
                            intent.putExtra(EXTRAS_IMAGE_URI_GRAPH, imageUri);
                            getScoreData().setOutputImageFlg(true);
                            SaveDataPref.setSelectedSaveIdx(getScoreData().getSaveIdx());
                            intent.putExtra(EXTRAS_OUT_SAVE_DATA, getScoreData());
                            startActivity(intent);
                        }
                    });
                }
            };
            (new Thread(runnable)).start();
        }
    }

    /**
     * @return Uri
     */
    private Uri getOutputImageUri() {

        mOutputBtn.setVisibility(View.GONE);
        mSettingBtn.setVisibility(View.GONE);

        long dateTaken = System.currentTimeMillis();
        String name = createName(dateTaken) + ".png";
        String directory = Environment.getExternalStorageDirectory().toString() + "/"
                + ScoreViewer.CAPTURE_IMAGE_DIR;
        String filePath = directory + "/" + name;
        if (!saveOutputImageFile(directory, name)) {
            return null;
        }
        ContentValues values = new ContentValues(7);
        values.put(Images.Media.TITLE, name);
        values.put(Images.Media.DISPLAY_NAME, name);
        values.put(Images.Media.DATE_TAKEN, dateTaken);
        values.put(Images.Media.MIME_TYPE, "image/png");
        values.put(Images.Media.DATA, filePath);
        ContentResolver cr = getContentResolver();

        Uri mImgUri = cr.insert(Images.Media.EXTERNAL_CONTENT_URI, values);

        mOutputBtn.setVisibility(View.VISIBLE);
        mSettingBtn.setVisibility(View.VISIBLE);

        return mImgUri;
    }

    /**
     * @param directory ディレクトリ名
     * @param filename  ファイル名
     * @return true:保存成功 false:保存失敗
     */
    private boolean saveOutputImageFile(final String directory, final String filename) {

        View disp = findViewById(R.id.graph_view_captured_area);
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

    /**
     * @return GolfScoreData
     */
    private SaveData getScoreData() {
        return mScoreData;
    }

    /**
     * @param scoreData GolfScoreData
     */
    private void setScoreData(final SaveData scoreData) {
        mScoreData = scoreData;
    }

    /**
     * @param button button
     */
    private void setupOutputButtonAction(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                final Uri imgUri = getOutputImageUri();
                if (imgUri == null) {
                    return;
                }
                AlertDialog.Builder dialog = new AlertDialog.Builder(GraphActivity.this);
                dialog.setIcon(R.drawable.ic_menu_question);
                dialog.setTitle(getResources().getString(R.string.menu_output_graph));
                dialog.setMessage(getResources().getString(R.string.dlg_output_image_msg_graph));
                dialog.setPositiveButton(
                        getResources().getString(R.string.dlg_output_image_by_email),
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
                dialog.setNegativeButton(
                        getResources().getString(R.string.dlg_output_image_by_viewer), null);
                dialog.show();
            }
        });
    }

    /**
     * @param button button
     */
    private void setupSettingButtonAction(final Button button) {

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(GraphActivity.this);
                dialog.setTitle(getResources().getString(R.string.dlg_chgb_title));
                dialog.setSingleChoiceItems(ChartConfig.BASE_LINE_TYPE_TITLE, mBaseLineType,
                        new OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int item) {
                                mBaseLineType = item;
                                dialog.dismiss();
                                updateScoreData();
                                restartGraphActivity();
                                // put shared preferrence
                                SharedPreferences pref = getSharedPreferences(
                                        ChartConfig.PREF_GRAPH_SETTING, MODE_PRIVATE);
                                Editor edit = pref.edit();
                                edit.putInt(ChartConfig.PREF_GRAPH_BASIS_TYPE_KEY, mBaseLineType);
                                edit.commit();
                            }
                        });
                dialog.create().show();
                /**/
            }
        });
    }

    private void updateScoreData() {
        int mBestPlayer = checkBestPlayer(getScoreData());
        // for FailSafe s
        int mNullCheck = 0;
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            mNullCheck += getScoreData().getScoresList().get(0).get(holeIdx);
        }
        if (mNullCheck == 0) {
            finish();
            return;
        }
        // for FailSafe e
        updatePlayerDemoData(getScoreData(), mBestPlayer);
    }

    /**
     * @param golfScoreData golfScoreData
     * @param bestPlayer    bestPlayer
     */
    private void updatePlayerDemoData(final SaveData golfScoreData,
                                      final int bestPlayer) {
        int baseNum;
        if (mBaseLineType == 0) {
            for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
                golfScoreData.getDemoSeries(i)[0] = golfScoreData.getPlayersHandi().get(i)
                        - golfScoreData.getPlayersHandi().get(bestPlayer);
            }
        } else {
            for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
                golfScoreData.getDemoSeries(i)[0] = 0 - golfScoreData.getPlayersHandi().get(i);
            }
        }
        for (int i = 1; i < SGSConfig.TOTAL_HOLE_COUNT + 1; i++) {
            baseNum = golfScoreData.getEachHolePar().get(i - 1);
            if (mBaseLineType != ChartConfig.BASE_LINE_TYPE_INT.length - 1) {
                baseNum = ChartConfig.BASE_LINE_TYPE_INT[mBaseLineType];
            }
            for (int j = 0; j < SGSConfig.MAX_PLAYER_NUM; j++) {
                if (golfScoreData.getScoresList().get(j).get(i - 1) != 0) {
                    golfScoreData.getDemoSeries(j)[i] = golfScoreData.getDemoSeries(j)[i - 1]
                            + (golfScoreData.getScoresList().get(j).get(i - 1)
                            - golfScoreData.getEachHolePar().get(i - 1) - baseNum);
                }
            }
            if (mBaseLineType == 0) {
                for (int j = 0; j < SGSConfig.MAX_PLAYER_NUM; j++) {
                    int score = golfScoreData.getScoresList().get(bestPlayer).get(i - 1);
                    golfScoreData.getDemoSeries(j)[i] = golfScoreData.getDemoSeries(j)[i - 1]
                            + (score - golfScoreData.getScoresList().get(j).get(i - 1));
                }
            }
        }
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            if (golfScoreData.getPlayersAlpha().get(i) == 0) {
                for (int j = 0; j < golfScoreData.getDemoSeries(0).length; j++) {
                    golfScoreData.getDemoSeries(i)[j] = 0;
                }
            }
        }
    }

    /**
     * @param dateTaken dateTaken
     * @return date String
     */
    private static String createName(final long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
    }

    /**
     * restartGraphActivity
     */
    public void restartGraphActivity() {
        Intent i = new Intent(getApplicationContext(), GraphActivity.class);
        i.putExtra(Intent.EXTRA_TITLE, getScoreData().getHoleTitle());
        i.putExtra(Intent.EXTRA_UID, mBaseLineType);
        SaveDataPref.setSelectedSaveIdx(getScoreData().getSaveIdx());
        startActivity(i);
        finish();
    }

    /**
     * @param golfScoreData GolfScoreData
     * @return BestPlayer
     */
    public int checkBestPlayer(final SaveData golfScoreData) {
        int bestPlayer = 0;
        int[] score = {-golfScoreData.getPlayersHandi().get(0), -golfScoreData.getPlayersHandi().get(1),
                -golfScoreData.getPlayersHandi().get(2), -golfScoreData.getPlayersHandi().get(3)};
        for (int i = 0; i < SGSConfig.TOTAL_HOLE_COUNT; i++) {
            score[0] += golfScoreData.getScoresList().get(0).get(i);
            score[1] += golfScoreData.getScoresList().get(1).get(i);
            score[2] += golfScoreData.getScoresList().get(2).get(i);
            score[3] += golfScoreData.getScoresList().get(3).get(i);
        }
        int playerNum = 0;
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            if (golfScoreData.getPlayerNameList().get(i).trim().length() != 0) {
                playerNum++;
            }
        }
        int bestScore = score[0];
        for (int i = 1; i < playerNum; i++) {
            if (score[i] < bestScore) {
                bestPlayer = i;
            }
            bestScore = score[bestPlayer];
        }
        return bestPlayer;
    }
}
