package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.content.Context;
import android.content.Intent;

import java.util.Map;

import jp.tonosama.komoki.SimpleGolfScorer2.SaveDataPref;
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

        Map<Integer, Integer> alphaList = SaveData.createInitialData(-1).getPlayersAlpha();
        for (int i = 0; i < mScoreData.getPlayerNameList().size(); i++) {
            if (mScoreData.getPlayerNameList().get(i).trim().length() == 0) {
                alphaList.put(i, 0);
            }
        }
        mScoreData.setPlayersAlpha(alphaList);

        Intent intent = new Intent(context, GraphActivity.class);
        intent.putExtra(Intent.EXTRA_TITLE, mScoreData.getHoleTitle());
        SaveDataPref.setSelectedSaveIdx(mScoreData.getSaveIdx());
        context.startActivity(intent);
    }

    public static void startTableActivity(final Context context, final SaveData mScoreData) {

        Map<Integer, Integer> alphaList = SaveData.createInitialData(-1).getPlayersAlpha();
        for (int i = 0; i < mScoreData.getPlayerNameList().size(); i++) {
            if (mScoreData.getPlayerNameList().get(i).trim().length() == 0) {
                alphaList.put(i, 0);
            }
        }
        mScoreData.setPlayersAlpha(alphaList);
        Intent intent = new Intent(context, ScoreViewer.class);
        SaveDataPref.setSelectedSaveIdx(mScoreData.getSaveIdx());
        context.startActivity(intent);
    }
}
