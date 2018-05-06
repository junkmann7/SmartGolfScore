package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.WorkerThreadPool;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

import static jp.tonosama.komoki.SimpleGolfScorer2.viewer.SVPreference.VIEWER_TYPE_ABSOLUTE;
import static jp.tonosama.komoki.SimpleGolfScorer2.viewer.SVPreference.VIEWER_TYPE_RELATIVE;

public class ScoreViewer extends Activity implements OnTouchListener {

    private static final String TAG = ScoreViewer.class.getSimpleName();

    private View[] mScoreAreaList = new View[SGSConfig.TOTAL_HOLE_COUNT];
    /**  */
    public static final String CAPTURE_IMAGE_DIR = "SmartGolfScore";
    /**  */
    private static final int CANCEL_MOVE_VALUE = 20;
    /**  */
    private int mOffsetX = 0;
    /**  */
    private int mOffsetY = 0;
    /**  */
    private boolean mIsHoleSelected;
    /**  */
    private SaveData mScoreData;

    private static final int FIRST_HALF_HOLE_IDX = 101;

    private static final int SECOND_HALF_HOLE_IDX = 102;

    private int mWidth;

    private int mHeight;

    /**
     * Starts table viewer with the given save data
     */
    public static void startViewer(final SaveData saveData) {
        Context context = SGSApplication.getInstance();
        Intent intent = new Intent(context, ScoreViewer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        SaveDataPref.setSelectedSaveIdx(saveData.getSaveIdx());
        context.startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScoreData = SaveDataPref.getSelectedSaveData();
        if (mScoreData == null) {
            finish();
            return;
        }
        setContentView(R.layout.score_viewer);

        ((TextView) findViewById(R.id.score_viewer_title)).setText(mScoreData.getHoleTitle());
        setupButtonAction();

        final ScrollView scrollView = (ScrollView) findViewById(R.id.score_table_scroll_view);
        scrollView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = scrollView.getWidth();
                int height = scrollView.getHeight();
                if (mWidth != width || mHeight != height) {
                    mWidth = width;
                    mHeight = height;
                    createLayout(width, height);
                }
            }
        });

        if (mScoreData.isOutputImageFlg()) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    showOutputProgressDialog(R.string.send_by_email_output_table, new SaveImageCallback() {
                        @Override
                        public void onComplete(@NonNull Uri uri) {
                            Uri imageGraphUri = getIntent().getParcelableExtra(
                                    GraphActivity.EXTRAS_IMAGE_URI_GRAPH);
                            mScoreData.setOutputImageFlg(false);
                            finish();
                            Intent i = new Intent();
                            String subject = getIntent().getStringExtra(Intent.EXTRA_SUBJECT);
                            String text = getIntent().getStringExtra(Intent.EXTRA_TEXT);
                            i.setAction(Intent.ACTION_SEND_MULTIPLE);
                            i.setType("message/rfc822");
                            i.putExtra(Intent.EXTRA_SUBJECT, subject);
                            i.putExtra(Intent.EXTRA_TEXT, text);
                            ArrayList<Uri> mImageUris = new ArrayList<>();
                            mImageUris.add(uri);
                            mImageUris.add(imageGraphUri);
                            i.putExtra(Intent.EXTRA_STREAM, mImageUris);
                            startActivity(i);
                        }
                    });
                }
            }, 1000);
        }
    }

    private void createLayout(int width, int height) {

        final int dpUnit = (int) getResources().getDimension(R.dimen.unit_dp);
        final int widthUnit = (int) ((width - dpUnit * 7f) / 10f);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.score_table_scroll_view);

        createPlayerNameArea(dpUnit, widthUnit);
        createScoreTable(scrollView, width, height, dpUnit, widthUnit);
        createHandicapArea(dpUnit, widthUnit);
        createTotalScoreArea(dpUnit, widthUnit);
    }

    private void createPlayerNameArea(int dpUnit, int widthUnit) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.score_viewer_player_name_area);
        ll.removeAllViews();

        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(createTextView("", -1),
                widthUnit * 2 + dpUnit, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!mScoreData.isPlayerExist(playerIdx)) {
                continue;
            }
            ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView playerNameTv = createTextView(mScoreData.getPlayerNameList().get(playerIdx), -1);
            ll.addView(playerNameTv, 0, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((LinearLayout.LayoutParams) playerNameTv.getLayoutParams()).weight = 1;
        }
        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void createHandicapArea(int dpUnit, int widthUnit) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.score_viewer_handicap_area);
        ll.removeAllViews();

        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(createTextView("Handicap", -1),
                widthUnit * 2 + dpUnit, ViewGroup.LayoutParams.WRAP_CONTENT);
        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!mScoreData.isPlayerExist(playerIdx)) {
                continue;
            }
            ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
            TextView handicapTv = createTextView(String.valueOf(mScoreData.getPlayersHandi().get(playerIdx)), -1);
            ll.addView(handicapTv, 0, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((LinearLayout.LayoutParams) handicapTv.getLayoutParams()).weight = 1;
        }
        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void createTotalScoreArea(int dpUnit, int widthUnit) {
        LinearLayout ll = (LinearLayout) findViewById(R.id.score_viewer_total_score_area);
        ll.removeAllViews();

        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(createTextView("Total", -1),
                widthUnit, ViewGroup.LayoutParams.WRAP_CONTENT);
        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        ll.addView(createTextView(String.valueOf(mScoreData.getTotalParScore()), -1),
                widthUnit, ViewGroup.LayoutParams.WRAP_CONTENT);

        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!mScoreData.isPlayerExist(playerIdx)) {
                continue;
            }
            ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
            String valueStr;
            int totalScore = mScoreData.getTotalScore().get(playerIdx);
            int totalPatScore = mScoreData.getTotalPatScore().get(playerIdx);
            if (0 < totalPatScore) {
                valueStr = totalScore + " (" + totalPatScore + ")";
            } else {
                valueStr = totalScore + "";
            }
            TextView totalTv = createTextView(valueStr, -1);
            ll.addView(totalTv, 0, ViewGroup.LayoutParams.WRAP_CONTENT);
            ((LinearLayout.LayoutParams) totalTv.getLayoutParams()).weight = 1;
        }
        ll.addView(createSeparator(), dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    private void createScoreTable(@NonNull ScrollView scrollView,
                                  int width, final int height, int dpUnit, int widthUnit) {
        scrollView.removeAllViews();
        int heightUnit = (int) ((height - (dpUnit * 21f)) / 20f);
        int rest = height - (heightUnit * 20 + dpUnit * 21);
        DevLog.d(TAG, "createScoreTable w:" + width + " h:" + height +
                " dpUnit:" + dpUnit + " heightUnit:" + heightUnit + " rest:" + rest);

        final LinearLayout verticalLL = new LinearLayout(this);
        verticalLL.setOrientation(LinearLayout.VERTICAL);

        int rowNum = SGSConfig.TOTAL_HOLE_COUNT + 2;
        for (int row = 0; row < rowNum; row++) {

            int holeIdx = getHoleIdxFromRow(row);
            View rowView = createScoreRow(holeIdx, dpUnit, widthUnit);

            verticalLL.addView(createSeparator(), ViewGroup.LayoutParams.MATCH_PARENT, dpUnit);
            int textSizeIndex = SVPreference.getSVTextSizeIndex();
            if (textSizeIndex != 0 || height < width) {
                verticalLL.addView(rowView,
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            } else {
                int rowHeight = rowNum - rest < row ? heightUnit + 1 : heightUnit;
                verticalLL.addView(rowView, ViewGroup.LayoutParams.MATCH_PARENT, rowHeight);
            }
            if (0 <= holeIdx && holeIdx < SGSConfig.TOTAL_HOLE_COUNT) {
                mScoreAreaList[holeIdx] =rowView;
                setBackgroundColor(rowView);
                setupHoleControlUi(rowView, holeIdx);
            }
        }
        verticalLL.addView(createSeparator(), ViewGroup.LayoutParams.MATCH_PARENT, dpUnit);
        scrollView.addView(verticalLL, width, height);
    }

    private View createScoreRow(int holeIdx, float dpUnit, float widthUnit) {

        LinearLayout rowView = new LinearLayout(this);
        rowView.setOrientation(LinearLayout.HORIZONTAL);

        TextView holeTv = createTextView(getHoleNumStr(mScoreData, holeIdx), holeIdx);
        TextView parTv = createTextView(getParScore(mScoreData, holeIdx), holeIdx);

        rowView.addView(createSeparator(), (int) dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        rowView.addView(holeTv, (int) widthUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        rowView.addView(createSeparator(), (int) dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        rowView.addView(parTv, (int) widthUnit, ViewGroup.LayoutParams.MATCH_PARENT);
        rowView.addView(createSeparator(), (int) dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);

        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            if (!mScoreData.isPlayerExist(playerIdx)) {
                continue;
            }
            TextView playerTv = createScoreTextView(mScoreData, playerIdx, holeIdx);
            rowView.addView(playerTv, 0, ViewGroup.LayoutParams.MATCH_PARENT);
            rowView.addView(createSeparator(), (int) dpUnit, ViewGroup.LayoutParams.MATCH_PARENT);
            ((LinearLayout.LayoutParams) playerTv.getLayoutParams()).weight = 1;
        }

        return rowView;
    }

    private View createSeparator() {
        View view = new View(this);
        view.setBackgroundColor(Color.rgb(0xCC, 0xCC, 0xCC));
        return view;
    }

    private TextView createTextView(@NonNull String text, int holeIdx) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, SVPreference.getSVTextSize());
        textView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        textView.setSingleLine();
        if (0 <= holeIdx && holeIdx < SGSConfig.TOTAL_HOLE_COUNT) {
            textView.setTextColor(getResources().getColor(android.R.color.black));
        } else if (holeIdx == FIRST_HALF_HOLE_IDX || holeIdx == SECOND_HALF_HOLE_IDX) {
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setBackgroundColor(Color.rgb(0x90, 0x90, 0x90));
        } else {
            textView.setTextColor(getResources().getColor(android.R.color.white));
        }
        return textView;
    }

    private String getHoleNumStr(@NonNull SaveData scoreData,  int holeIdx) {
        if (holeIdx == FIRST_HALF_HOLE_IDX) {
            return "Out";
        } else if (holeIdx == SECOND_HALF_HOLE_IDX) {
            return "In";
        } else if (!scoreData.getIs18Hround() && (SGSConfig.TOTAL_HOLE_COUNT / 2 <= holeIdx)) {
            return String.valueOf(holeIdx + 1 - 9) + "H";
        } else {
            return String.valueOf(holeIdx + 1) + "H";
        }
    }

    private String getParScore(@NonNull SaveData scoreData, int holeIdx) {
        if (holeIdx == FIRST_HALF_HOLE_IDX) {
            return String.valueOf(scoreData.getHalfParScore(true));
        } else if (holeIdx == SECOND_HALF_HOLE_IDX) {
            return String.valueOf(scoreData.getHalfParScore(false));
        } else {
            return String.valueOf(scoreData.getEachHolePar().get(holeIdx));
        }
    }

    private int getHoleIdxFromRow(int row) {
        //  9: FIRST_HALF
        // 19: SECOND_HALF
        if (row == 9) {
            return FIRST_HALF_HOLE_IDX;
        } else if (row == 19) {
            return SECOND_HALF_HOLE_IDX;
        } else if (row < 9) {
            // 0 ~ 8
            return row;
        } else {
            // 10 ~ 18
            return row - 1;
        }
    }

    private void onSettingButtonClicked(@NonNull Button settingBtn) {

        int scoreViewerType = SVPreference.getScoreViewType();
        if (scoreViewerType == VIEWER_TYPE_ABSOLUTE) {
            scoreViewerType = VIEWER_TYPE_RELATIVE;
            settingBtn.setText(getResources().getString(R.string.btn_change_table_basis_par));
        } else if (scoreViewerType == VIEWER_TYPE_RELATIVE) {
            scoreViewerType = VIEWER_TYPE_ABSOLUTE;
            settingBtn.setText(getResources().getString(R.string.btn_change_table_basis_abs));
        }
        SVPreference.setScoreViewType(scoreViewerType);
        updateAllPlayerScore();
    }

    private void onOutputButtonClicked(final Uri imgUri) {

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
                        Intent is = new Intent();
                        is.setAction(Intent.ACTION_VIEW);
                        is.setType("image/png");
                        is.setData(imgUri);
                        startActivity(is);
                    }
                });
        dialog.setNeutralButton(getResources().getString(R.string.dlg_output_image_nop), null);
        dialog.show();
    }

    private void setupButtonAction() {

        final Button settingBtn = (Button) findViewById(R.id.scoreviewer_setting_button);
        int scoreViewerType = SVPreference.getScoreViewType();
        if (scoreViewerType == VIEWER_TYPE_ABSOLUTE) {
            settingBtn.setText(getResources().getString(R.string.btn_change_table_basis_abs));
        }
        if (scoreViewerType == VIEWER_TYPE_RELATIVE) {
            settingBtn.setText(getResources().getString(R.string.btn_change_table_basis_par));
        }
        settingBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                onSettingButtonClicked(settingBtn);
            }
        });
        Button outputBtn = (Button) findViewById(R.id.scoreviewer_output_button);
        outputBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                showOutputProgressDialog(R.string.output_table_image, new SaveImageCallback() {
                    @Override
                    public void onComplete(@NonNull Uri uri) {
                        onOutputButtonClicked(uri);
                    }
                });
            }
        });
    }

    private TextView createScoreTextView(@NonNull SaveData scoreData, int playerIdx, int holeIdx) {
        if (holeIdx == FIRST_HALF_HOLE_IDX) {
            return createTextView(getHalfScore(scoreData, true, playerIdx), holeIdx);
        } else if (holeIdx == SECOND_HALF_HOLE_IDX) {
            return createTextView(getHalfScore(scoreData, false, playerIdx), holeIdx);
        }
        TextView playerScoreTextView = createTextView("", holeIdx);
        playerScoreTextView.setTag(playerIdx);
        return setupPlayerScoreTextView(
                playerScoreTextView,
                scoreData.getPlayerNameList().get(playerIdx),
                scoreData.getScoresList().get(playerIdx).get(holeIdx),
                scoreData.getEachHolePar().get(holeIdx),
                scoreData.getPattingScoresList().get(playerIdx).get(holeIdx));
    }

    private void updateAllPlayerScore() {
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
                if (!mScoreData.isPlayerExist(playerIdx)) {
                    continue;
                }
                ViewGroup view = (ViewGroup) mScoreAreaList[holeIdx];
                if (view == null) {
                    continue;
                }
                TextView tv = (TextView) view.findViewWithTag(playerIdx);
                setupPlayerScoreTextView(tv,
                        mScoreData.getPlayerNameList().get(playerIdx),
                        mScoreData.getScoresList().get(playerIdx).get(holeIdx),
                        mScoreData.getEachHolePar().get(holeIdx),
                        mScoreData.getPattingScoresList().get(playerIdx).get(holeIdx));
            }
        }
    }

    private TextView setupPlayerScoreTextView(@NonNull TextView tv, @NonNull String name,
                                              final int playerScore, final int parScore, final int patScore) {
        if (playerScore != 0 && name.trim().length() != 0) {
            String patStr = "";
            if (0 < patScore) {
                patStr = " (" + patScore + ")";
            }
            final String scoreUnderStr = String.valueOf(playerScore - parScore) + patStr;
            final String scoreOverStr = "+" + scoreUnderStr;
            final String scoreAbsStr = String.valueOf(playerScore) + patStr;
            switch (SVPreference.getScoreViewType()) {
            case VIEWER_TYPE_RELATIVE:
                if (playerScore < parScore) { // アンダーパーの場合
                    tv.setText(scoreUnderStr);
                    tv.setTextColor(Color.GREEN);
                } else if (playerScore == parScore) { // パーの場合
                    tv.setText(scoreUnderStr);
                    tv.setTextColor(Color.BLUE);
                } else if (playerScore > parScore + 3) { // オーバーパーの場合（＋４以上
                    tv.setText(scoreOverStr);
                    tv.setTextColor(Color.RED);
                } else if (playerScore > parScore) { // オーバーパー）の場合
                    tv.setText(scoreOverStr);
                    tv.setTextColor(Color.BLACK);
                }
                break;
            case VIEWER_TYPE_ABSOLUTE:
                tv.setText(scoreAbsStr);
                if (playerScore < parScore) { // アンダーパーの場合
                    tv.setTextColor(Color.GREEN);
                } else if (playerScore == parScore) { // パーの場合
                    tv.setTextColor(Color.BLUE);
                } else if (playerScore > parScore + 3) { // オーバーパーの場合（＋４以上
                    tv.setTextColor(Color.RED);
                } else if (playerScore > parScore) { // オーバーパー）の場合
                    tv.setTextColor(Color.BLACK);
                }
                break;
            default:
                break;
            }
        }
        return tv;
    }

    private String getHalfScore(@NonNull SaveData scoreData, boolean isFirstHalf, int playerIdx) {
        int halfScore = scoreData.getHalfScore(isFirstHalf).get(playerIdx);
        int patScore = scoreData.getHalfPatScore(isFirstHalf).get(playerIdx);
        if (0 < patScore) {
            return halfScore + " (" + patScore + ")";
        }
        return halfScore + "";
    }

    private void setupHoleControlUi(@NonNull View view, final int holeIdx) {
        view.setOnTouchListener(this);
        view.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                SaveDataPref.updateCurrentHoleIdx(mScoreData, holeIdx);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        super.onCreateOptionsMenu(menu);

        for (SVMenu svMenu : SVMenu.values()) {
            MenuItem menuItem = menu.add(0, svMenu.getMenuId(), Menu.NONE,
                    getResources().getString(svMenu.getTitleResId()));
            menuItem.setIcon(svMenu.getIconResId());
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (SVMenu.getMenu(item.getItemId())) {
            case ChangeTextSize:
                changeTableTextSize();
                break;
            default:
                break;
        }
        return true;
    }

    private void changeTableTextSize() {

        AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
        mDialog.setTitle(getResources().getString(R.string.dlg_chg_text_size));
        mDialog.setSingleChoiceItems(
                this.getResources().getStringArray(R.array.table_text_size_array),
                SVPreference.getSVTextSizeIndex(), new OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int item) {
                        SVPreference.setSVTextSizeIndex(item);
                        createLayout(mWidth, mHeight);
                        dialog.dismiss();
                    }
                });
        mDialog.create().show();
    }

    private static String createFileName(final long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk.mm.ss", dateTaken).toString();
    }

    private Uri getOutputImageUri(int currHole, int textSizeIndex) {

        long dateTaken = System.currentTimeMillis();
        String name = createFileName(dateTaken) + ".png";
        String directory = Environment.getExternalStorageDirectory().toString() + "/"
                + CAPTURE_IMAGE_DIR;
        String filePath = directory + "/" + name;
        final Uri imgUri;
        if (!saveImageFile(directory, name)) {
            imgUri = null;
        } else {
            ContentValues values = new ContentValues(7);
            values.put(Images.Media.TITLE, name);
            values.put(Images.Media.DISPLAY_NAME, name);
            values.put(Images.Media.DATE_TAKEN, dateTaken);
            values.put(Images.Media.MIME_TYPE, "image/png");
            values.put(Images.Media.DATA, filePath);
            ContentResolver cr = getContentResolver();
            imgUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        findViewById(R.id.scoreviewer_setting_button).setVisibility(View.VISIBLE);
        findViewById(R.id.scoreviewer_output_button).setVisibility(View.VISIBLE);
        mScoreAreaList[currHole].setBackgroundColor(Color.alpha(255));
        SVPreference.setSVTextSizeIndex(textSizeIndex);
        mScoreData.setOutputImageFlg(false);
        createLayout(mWidth, mHeight);

        return imgUri;
    }

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mOffsetX = (int) event.getX();
            mOffsetY = (int) event.getY();
            v.setBackgroundColor(Color.argb(255, 0x66, 0xCC, 0xFF));
            mIsHoleSelected = true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            if (Math.abs(diffX) > CANCEL_MOVE_VALUE || Math.abs(diffY) > CANCEL_MOVE_VALUE) {
                mIsHoleSelected = false;
                setBackgroundColor(v);
            }
        } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
            setBackgroundColor(v);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            int diffX = mOffsetX - (int) event.getX();
            int diffY = mOffsetY - (int) event.getY();
            int holeIdx = getViewHoleNumber(v);
            if (mIsHoleSelected && Math.abs(diffX) < CANCEL_MOVE_VALUE
                    && Math.abs(diffY) < CANCEL_MOVE_VALUE) {
                SaveDataPref.updateCurrentHoleIdx(mScoreData, holeIdx);
                finish();
            } else {
                setBackgroundColor(v);
            }
        }
        return true;
    }

    private int getViewHoleNumber(final View view) {
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            if (view == mScoreAreaList[holeIdx]) {
                return holeIdx;
            }
        }
        return 0;
    }

    private void setBackgroundColor(final View view) {
        int holeIdx = getViewHoleNumber(view);
        if (holeIdx != mScoreData.getCurrentHole() || mScoreData.isOutputImageFlg()) {
            view.setBackgroundResource(R.drawable.transparent);
        } else {
            view.setBackgroundColor(Color.argb(255, 170, 238, 255));
        }
    }

    interface SaveImageCallback {

        void onComplete(@NonNull Uri uri);
    }

    private void showOutputProgressDialog(@StringRes int resId,
                                          @NonNull final SaveImageCallback callback) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage(getResources().getString(resId));
        progressDialog.setCancelable(false);
        progressDialog.show();

        final int currHoleIdx = mScoreData.getCurrentHole();
        final int textSizeIndex = SVPreference.getSVTextSizeIndex();

        SVPreference.setSVTextSizeIndex(0);
        findViewById(R.id.scoreviewer_setting_button).setVisibility(View.GONE);
        findViewById(R.id.scoreviewer_output_button).setVisibility(View.GONE);
        mScoreData.setOutputImageFlg(true);
        createLayout(mWidth, mHeight);

        WorkerThreadPool.execute(new Runnable() {

            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    //ignore
                }
                ScoreViewer.this.runOnUiThread(new Runnable() {

                    public void run() {
                        Uri tableUri = getOutputImageUri(currHoleIdx, textSizeIndex);
                        progressDialog.dismiss();
                        callback.onComplete(tableUri);
                    }
                });
            }
        });
    }
}