package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSConfig;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Komoki
 */
@SuppressLint("UseSparseArrays")
final class SVRes {

    /**  */
    static final int[] EACH_HOLE_AREA = { R.id.h1h_score_area, R.id.h2h_score_area,
            R.id.h3h_score_area, R.id.h4h_score_area, R.id.h5h_score_area, R.id.h6h_score_area,
            R.id.h7h_score_area, R.id.h8h_score_area, R.id.h9h_score_area, R.id.h10h_score_area,
            R.id.h11h_score_area, R.id.h12h_score_area, R.id.h13h_score_area, R.id.h14h_score_area,
            R.id.h15h_score_area, R.id.h16h_score_area, R.id.h17h_score_area, //
            R.id.h18h_score_area };

    /**  */
    private static final int[] EACH_SCORE_AREA = {
            R.id.hole_per1_score,
            R.id.hole_per2_score,
            R.id.hole_per3_score,
            R.id.hole_per4_score };

    /**  */
    static final int[] TOTAL_SCORE_AREA = { R.id.per1total, R.id.per2total, R.id.per3total,
            R.id.per4total };

    /**  */
    static final int[] SCORE_RESULT_AREA = { R.id.per1_handicap, R.id.per2_handicap, R.id.per3_handicap,
            R.id.per4_handicap};

    /**  */
    static final int[] HALF_RESULT_AREA_1ST = { R.id.per1_score_total_half1,
            R.id.per2_score_total_half1, R.id.per3_score_total_half1, R.id.per4_score_total_half1 };

    /**  */
    static final int[] HALF_RESULT_AREA_2ND = { R.id.per1_score_total_half2,
            R.id.per2_score_total_half2, R.id.per3_score_total_half2, R.id.per4_score_total_half2 };

    /**  */
    static final int[] PLAYER_NAME_AREA = { R.id.viewer_per1_name, R.id.viewer_per2_name,
            R.id.viewer_per3_name, R.id.viewer_per4_name };

    /**
     * コンストラクタ
     */
    private SVRes() {
        //
    }

    static TextView[] getTextViewList(final Activity activity, final int[] res) {
        TextView[] tv = new TextView[res.length];
        for (int i = 0; i < tv.length; i++) {
            tv[i] = ((TextView) activity.findViewById(res[i]));
        }
        return tv;
    }

    static Map<Integer, Map<Integer, TextView>> getPlayerScoreTextView(final Activity activity) {

        Map<Integer, Map<Integer, TextView>> eachPlayerTextViewMap = new HashMap<>();

        for (int playerIdx = 0; playerIdx < SGSConfig.MAX_PLAYER_NUM; playerIdx++) {
            Map<Integer, TextView> playerTextViewMap = new HashMap<>();
            for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
                View holeArea = activity.findViewById(EACH_HOLE_AREA[holeIdx]);
                TextView textView = (TextView) holeArea.findViewById(EACH_SCORE_AREA[playerIdx]);
                playerTextViewMap.put(holeIdx, textView);
            }
            eachPlayerTextViewMap.put(playerIdx, playerTextViewMap);
        }

        return eachPlayerTextViewMap;
    }

    static Map<Integer, TextView> getHoleNumberTextView(final Activity activity) {

        Map<Integer, TextView> holeNumberTextViewMap = new HashMap<>();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            View holeArea = activity.findViewById(EACH_HOLE_AREA[holeIdx]);
            TextView textView = (TextView) holeArea.findViewById(R.id.hole_number);
            holeNumberTextViewMap.put(holeIdx, textView);
        }

        return holeNumberTextViewMap;
    }

    static Map<Integer, TextView> getParNumberTextView(final Activity activity) {

        Map<Integer, TextView> holeNumberTextViewMap = new HashMap<>();
        for (int holeIdx = 0; holeIdx < SGSConfig.TOTAL_HOLE_COUNT; holeIdx++) {
            View holeArea = activity.findViewById(EACH_HOLE_AREA[holeIdx]);
            TextView textView = (TextView) holeArea.findViewById(R.id.hole_par_score);
            holeNumberTextViewMap.put(holeIdx, textView);
        }

        return holeNumberTextViewMap;
    }
}