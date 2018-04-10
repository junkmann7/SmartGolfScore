package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;
import jp.tonosama.komoki.SimpleGolfScorer2.setting.SettingsActivity;

/**
 * @author Komoki
 */
final class SEMenuManager {

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
    static boolean onOptionsItemSelected(final MenuItem item, final Activity mActivity,
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
                SaveDataPref.deleteSaveData(sData.getSaveIdx());
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
    static void menuRoundSetting(final Activity mActivity, final SaveData scoreData) {

        Intent intent = new Intent(mActivity, SettingsActivity.class);
        intent.putExtra(SettingsActivity.EXTRAS_IS_NEW_CREATE, false); // 新規作成でない事を通知
        SaveDataPref.setSelectedSaveIdx(scoreData.getSaveIdx());
        mActivity.startActivity(intent);
    }
}