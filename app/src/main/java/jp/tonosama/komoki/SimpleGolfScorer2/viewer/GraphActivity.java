package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

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
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.text.format.DateFormat;
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

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.chart.ChartConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

/**
 * @author Komoki
 */
public class GraphActivity extends Activity {

    private static final String TAG = GraphActivity.class.getSimpleName();

    /** */
    private ChartConfig.BaseLineType mBaseLineType = ChartConfig.BaseLineType.Auto;
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

    /**
     * Starts graph viewer with the given save data
     */
    public static void startViewer(final SaveData saveData) {
        Context context = SGSApplication.getInstance();
        SaveDataPref.setSelectedSaveIdx(saveData.getSaveIdx());
        Intent intent = new Intent(context, GraphActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, saveData.getHoleTitle());
        context.startActivity(intent);
    }

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

        mBaseLineType = ChartConfig.getBaseLineType();

        updateScoreData();

        mOutputBtn = (Button) findViewById(R.id.graph_output_button);
        mSettingBtn = (Button) findViewById(R.id.graph_setting_button);
        mSettingBtn.setText(String.format(getResources().getString(R.string.btn_change_graph_basis), mBaseLineType.getName()));

        setupOutputButtonAction(mOutputBtn);
        setupSettingButtonAction(mSettingBtn);
        startOutputProgressIfNeed();
    }

    private void makeChart() {

        float dpp = getResources().getDisplayMetrics().densityDpi;
        mDpx = dpp / 160;

        LinearLayout chartLayout = (LinearLayout) findViewById(R.id.chart);
        GraphicalView graphicalView = graphMake();
        chartLayout.removeAllViews();
        chartLayout.addView(graphicalView);
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

        renderer.setXLabels(SGSConfig.TOTAL_GRAPH_X_LABEL_COUNT);
        renderer.setYLabels(SGSConfig.TOTAL_GRAPH_X_LABEL_COUNT + 1);

        // グリッド表示
        renderer.setShowGrid(true);

        renderer.setXLabelsAlign(Paint.Align.CENTER);
        renderer.setYLabelsAlign(Paint.Align.RIGHT);

        dataset = buildDataSet();

        int margin = Math.abs(mMaxValueY - mMinValueY) / 10;
        setChartSettings(renderer,
                "",     // title
                "", // X-axis title
                "",     // y-axis title
                0,      // x Min
                SGSConfig.TOTAL_GRAPH_X_LABEL_COUNT, // x Max
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
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            addXYSeries(dataSet, mScoreData.getPlayerNameList().get(playerIdx), 0, playerIdx);
        }
        return dataSet;
    }

    @SuppressWarnings("SameParameterValue")
    private void addXYSeries(XYMultipleSeriesDataset dataSet, String playerName, int scale, int playerIdx) {
        XYSeries series = new XYSeries(playerName, scale);

        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_GRAPH_DATA_COUNT; holeIdx++) {
            int value =  mScoreData.getDemoSeries(playerIdx).get(holeIdx);
            series.add(holeIdx, holeIdx + 1, value);
            if (playerName.trim().length() != 0) {
                mMinValueY = Math.min(mMinValueY, value);
                mMaxValueY = Math.max(mMaxValueY, value);
            }
        }

        dataSet.addSeries(series);
    }

    private XYMultipleSeriesRenderer buildRenderer() {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
        PointStyle style = PointStyle.CIRCLE;
        for (int i = 0; i < SGSConfig.MAX_PLAYER_NUM; i++) {
            int alpha = getScoreData().getPlayerNameList().get(i).trim().length() == 0 ? 0 : 255;
            int presetColor = ChartConfig.DEFAULT_COLORS[i];
            int labelColor = Color.argb(alpha,
                    Color.red(presetColor),
                    Color.green(presetColor),
                    Color.blue(presetColor));
            setRenderer(renderer, labelColor, style);
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
                        DevLog.d("Runnable", "InterruptedException");
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
                dialog.setSingleChoiceItems(
                        ChartConfig.BASE_LINE_TYPE_TITLE,
                        mBaseLineType.getIndex(),
                        new OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int index) {
                                mBaseLineType = ChartConfig.BaseLineType.getBaseLineType(index);
                                dialog.dismiss();
                                updateScoreData();
                                restartGraphActivity();
                                // put shared preferrence
                                SharedPreferences pref = getSharedPreferences(
                                        ChartConfig.PREF_GRAPH_SETTING, MODE_PRIVATE);
                                Editor edit = pref.edit();
                                edit.putInt(ChartConfig.PREF_GRAPH_BASIS_TYPE_KEY, index);
                                edit.commit();
                            }
                        });
                dialog.create().show();
                /**/
            }
        });
    }

    private void updateScoreData() {

        int nullCheck = 0;
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            nullCheck += getScoreData().getScoresList().get(0).get(holeIdx);
        }
        if (nullCheck == 0) {
            finish();
            return;
        }

        updatePlayerDemoData(getScoreData());
        DevLog.d(TAG, getScoreData().dumpDemoData());
    }

    private void updatePlayerDemoData(final SaveData saveData) {

        final int bestPlayerIdx = saveData.getBestPlayer();

        // Setup initial value of each player
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            int initialScore;
            int playerHandi = saveData.getPlayersHandi().get(playerIdx);
            if (!mBaseLineType.equals(ChartConfig.BaseLineType.Auto)) {
                initialScore = 0 - playerHandi;
            } else {
                int bestPlayerHandi = saveData.getPlayersHandi().get(bestPlayerIdx);
                initialScore = playerHandi - bestPlayerHandi;
            }
            saveData.getDemoSeries(playerIdx).put(0, initialScore);
        }
        for (int holeIdx = 1; holeIdx < SGSConfig.TOTAL_GRAPH_DATA_COUNT; holeIdx++) {

            // Par score of previous hole
            int baseNum;
            int parValue = saveData.getEachHolePar().get(holeIdx - 1);
            if (mBaseLineType.equals(ChartConfig.BaseLineType.DoubePar)) {
                baseNum = parValue;
            } else {
                baseNum = mBaseLineType.getBaseValue();
            }
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                if (saveData.getScoresList().get(playerIdx).get(holeIdx - 1) == 0) {
                    continue;
                }
                int playerDemoValue = saveData.getDemoSeries(playerIdx).get(holeIdx - 1);
                int playerScoreValue = saveData.getScoresList().get(playerIdx).get(holeIdx - 1);
                int demoValue = playerDemoValue + playerScoreValue - parValue - baseNum;
                saveData.getDemoSeries(playerIdx).put(holeIdx, demoValue);
            }
            if (mBaseLineType.equals(ChartConfig.BaseLineType.Auto)) {
                for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                    int score = saveData.getScoresList().get(bestPlayerIdx).get(holeIdx - 1);
                    saveData.getDemoSeries(playerIdx).put(holeIdx, saveData.getDemoSeries(playerIdx).get(holeIdx - 1)
                            + (score - saveData.getScoresList().get(playerIdx).get(holeIdx - 1)));
                }
            }
        }
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (saveData.getPlayersAlpha().get(playerIdx) != 0) {
                continue;
            }
            for (int j = 0; j < saveData.getDemoSeries(0).size(); j++) {
                saveData.getDemoSeries(playerIdx).put(j, 0);
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
}
