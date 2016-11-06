package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.setting.SettingsActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * @author Komoki
 */
public final class SEMenuManager {

    /**  */
    private static final String TAG = SEMenuManager.class.getSimpleName();
    /**  */
    private static final boolean DEBUG = false;

    /**
     * Private Constructor
     */
    private SEMenuManager() {
        //
    }

    /**
     * @param item MenuItem
     * @return true: false:
     */
    public static boolean onOptionsItemSelected(final MenuItem item, final Activity mActivity,
            final SaveData scoreData) {
        if (DEBUG) {
            Log.d(TAG,
                    "onOptionsItemSelected item:" + item.toString() + " scoreData:"
                            + scoreData.toString());
        }
        switch (item.getItemId()) {
        case Menu.FIRST + 1: // データ消去
            menuDeleteData(mActivity, scoreData);
            break;
        case Menu.FIRST + 2: // ラウンド設定
            menuRoundSetting(mActivity, scoreData);
            break;
        default:
            break;
        }
        return true;
    }

    /**
     * @param act Activity
     * @param sData GolfScoreData
     */
    private static void menuDeleteData(final Activity act, final SaveData sData) {

        final Resources res = act.getResources();
        AlertDialog.Builder dialog = new AlertDialog.Builder(act);
        dialog.setIcon(R.drawable.ic_menu_delete);
        dialog.setTitle(res.getString(R.string.dlg_delete_title));
        dialog.setMessage(res.getString(R.string.dlg_delete_msg));
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int whichButton) {

                // 現在データ番号を消去して詰める
                for (int i = sData.getSaveIdx(); i < Util.MAX_DATA_SAVE_NUM - 1; i++) {
                    SharedPreferences pref1 = act.getSharedPreferences(Util.PREF_DATA_SLOT[i],
                            Context.MODE_PRIVATE);
                    SharedPreferences pref2 = act.getSharedPreferences(Util.PREF_DATA_SLOT[i + 1],
                            Context.MODE_PRIVATE);
                    Editor e = pref1.edit();
                    for (int j = 0; j < Util.PREF_DATA_KEY.length; j++) {
                        e.putString(Util.PREF_DATA_KEY[j],
                                pref2.getString(Util.PREF_DATA_KEY[j], ""));
                    }
                    e.commit();
                }
                // ラストデータを消去する
                SharedPreferences pref = act.getSharedPreferences(
                        Util.PREF_DATA_SLOT[Util.MAX_DATA_SAVE_NUM - 1], Context.MODE_PRIVATE);
                Editor e = pref.edit();
                for (int i = 0; i < Util.PREF_DATA_KEY.length; i++) {
                    e.putString(Util.PREF_DATA_KEY[i], "");
                }
                e.commit();
                Toast.makeText(act, res.getString(R.string.toast_deleted_data), Toast.LENGTH_SHORT)
                        .show();
                act.finish();
            }
        });
        dialog.setNegativeButton("No", null);
        dialog.create().show();
    }

    /**
     * @param mActivity Activity
     * @param scoreData GolfScoreData
     */
    public static void menuRoundSetting(final Activity mActivity, final SaveData scoreData) {

        Intent intent = new Intent(mActivity, SettingsActivity.class);
        intent.putExtra(Util.EXTRAS_IS_NEW_CREATE, false); // 新規作成でない事を通知
        intent.putExtra(Util.EXTRAS_SELECTED_IDX, scoreData.getSaveIdx()); // 保存データ番号を通知
        mActivity.startActivity(intent);
    }
}