package jp.tonosama.komoki.SimpleGolfScorer2.history;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.DevLog;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.viewer.GraphActivity;

/**
 * @author Komoki
 */
public class DroidLetterActivity extends Activity {

    /**  */
    private static final String TAG = DroidLetterActivity.class.getSimpleName();

    /**  */
    private String mDroidCommentSubjStr;
    /**  */
    private String mDroidCommentBodyStr;

    /**  */
    private SaveData mScoreData;

    public void onCreate(final Bundle icicle) {
        super.onCreate(icicle);
        DevLog.d(TAG, "onCreate -> s");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.droid_letter);

        int selectedIdx = SaveDataPref.getSelectedSaveIdx();
        if (selectedIdx < 0) {
            return;
        }
        mScoreData = SaveDataPref.getSaveDataMap().get(selectedIdx);

        Bundle extras = getIntent().getExtras();
        TextView droidCommentBodyTxtView = (TextView) findViewById(R.id.droid_letter_body);
        if (extras != null) {
            mDroidCommentSubjStr = extras.getString(Intent.EXTRA_SUBJECT);
            mDroidCommentBodyStr = extras.getString(Intent.EXTRA_TEXT);
        }
        droidCommentBodyTxtView.setText(mDroidCommentBodyStr);

        // ドロイドレター送信ボタン
        setSendMessageBtnAction();

        DevLog.d(TAG, "onCreate -> e");
    }

    /**
     * ドロイドレター送信
     */
    private void setSendMessageBtnAction() {

        Button sendLetterBtn = (Button) findViewById(R.id.send_letter_btn);
        sendLetterBtn.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(DroidLetterActivity.this);
                dialog.setIcon(R.drawable.ic_menu_question);
                dialog.setTitle(getResources().getString(R.string.send_by_email_dlg_title));
                dialog.setMessage(getResources().getString(R.string.send_by_email_dlg_message));
                // 画像付きで送信ボタン
                dialog.setPositiveButton(
                        getResources().getString(R.string.send_by_email_contains_img),
                        new DialogInterface.OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int which) {
                                // グラフとスコア表の結果を画像として添付する
                                sendMessageWidthImage();
                            }
                        });
                // 画像無しで送信ボタン
                dialog.setNegativeButton(
                        getResources().getString(R.string.send_by_email_only_message),
                        new DialogInterface.OnClickListener() {

                            public void onClick(final DialogInterface dialog, final int which) {
                                // =====================================================
                                // テキストのみ送る
                                Intent i = new Intent();
                                i.setAction(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_SUBJECT, mDroidCommentSubjStr);
                                i.putExtra(Intent.EXTRA_TEXT, mDroidCommentBodyStr);
                                startActivity(i);
                            }
                        });
                dialog.create().show();
            }
        });
    }

    /**
     * グラフとスコア表の結果を画像として添付する
     */
    private void sendMessageWidthImage() {

        Map<Integer, Integer> alphaG = SaveData.createInitialData(-1).getPlayersAlpha();
        if (mScoreData.getPlayerNameList().get(0).trim().length() == 0) {
            alphaG.put(0, 0);
        }
        if (mScoreData.getPlayerNameList().get(1).trim().length() == 0) {
            alphaG.put(1, 0);
        }
        if (mScoreData.getPlayerNameList().get(2).trim().length() == 0) {
            alphaG.put(2, 0);
        }
        if (mScoreData.getPlayerNameList().get(3).trim().length() == 0) {
            alphaG.put(3, 0);
        }
        mScoreData.setPlayersAlpha(alphaG);
        mScoreData.setOutputImageFlg(true);

        Intent i = new Intent(getApplicationContext(), GraphActivity.class);
        i.putExtra(Intent.EXTRA_SUBJECT, mDroidCommentSubjStr);
        i.putExtra(Intent.EXTRA_TEXT, mDroidCommentBodyStr);
        i.putExtra(Intent.EXTRA_TITLE, mScoreData.getHoleTitle());
        SaveDataPref.setSelectedSaveIdx(mScoreData.getSaveIdx());
        i.putExtra(GraphActivity.EXTRAS_OUT_SAVE_DATA, mScoreData);
        startActivity(i);
    }
}