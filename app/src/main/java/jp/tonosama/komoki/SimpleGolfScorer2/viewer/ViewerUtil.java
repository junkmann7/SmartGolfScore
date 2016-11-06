package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.content.Context;
import android.content.Intent;

import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveData;

/**
 * @author Komoki
 */
public final class ViewerUtil {

    /**
     * Private Constructor
     */
    private ViewerUtil() {
        //
    }

    public static void startGraphActivty(final Context context, final SaveData mScoreData) {

        int[] mAlpha = new int[] { 255, 255, 255, 255 };
        for (int i = 0; i < mScoreData.getNames().length; i++) {
            if (mScoreData.getNames()[i].trim().length() == 0) {
                mAlpha[i] = 0;
            }
        }
        mScoreData.setPlayersAlpha(mAlpha);

        Intent intent = new Intent(context, GraphActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, mScoreData.getHoleTitle());
        intent.putExtra(Util.EXTRAS_SELECTED_IDX, mScoreData.getSaveIdx());
        context.startActivity(intent);
    }

    public static void startTableActivity(final Context context, final SaveData mScoreData) {

        int[] mAlpha = new int[] { 255, 255, 255, 255 };
        for (int i = 0; i < mScoreData.getNames().length; i++) {
            if (mScoreData.getNames()[i].trim().length() == 0) {
                mAlpha[i] = 0;
            }
        }
        mScoreData.setPlayersAlpha(mAlpha);
        Intent intent = new Intent(context, ScoreViewer.class);
        intent.putExtra(Util.EXTRAS_SELECTED_IDX, mScoreData.getSaveIdx());
        context.startActivity(intent);
    }
}
