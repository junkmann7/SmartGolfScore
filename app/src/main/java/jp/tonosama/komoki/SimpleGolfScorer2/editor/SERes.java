package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.app.Activity;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import jp.tonosama.komoki.SimpleGolfScorer2.R;

final class SERes {

    private SERes() {
        //private constructor
    }

    static final int MINIMUM_PAR_COUNT = 2;

    static final int[] CURRENT_HOLE_IMG_RES_IDS = { //
    R.drawable.current_hole_01, R.drawable.current_hole_02, R.drawable.current_hole_03,
            R.drawable.current_hole_04, R.drawable.current_hole_05, R.drawable.current_hole_06,
            R.drawable.current_hole_07, R.drawable.current_hole_08, R.drawable.current_hole_09,
            R.drawable.current_hole_10, R.drawable.current_hole_11, R.drawable.current_hole_12,
            R.drawable.current_hole_13, R.drawable.current_hole_14, R.drawable.current_hole_15,
            R.drawable.current_hole_16, R.drawable.current_hole_17, R.drawable.current_hole_18 };

    static final int[] HOLE_NUMBER_IMG_RES_IDS = { //
    R.drawable.hole_number_01, R.drawable.hole_number_02, R.drawable.hole_number_03,
            R.drawable.hole_number_04, R.drawable.hole_number_05, R.drawable.hole_number_06,
            R.drawable.hole_number_07, R.drawable.hole_number_08, R.drawable.hole_number_09,
            R.drawable.hole_number_10, R.drawable.hole_number_11, R.drawable.hole_number_12,
            R.drawable.hole_number_13, R.drawable.hole_number_14, R.drawable.hole_number_15,
            R.drawable.hole_number_16, R.drawable.hole_number_17, R.drawable.hole_number_18, };

    private static final int PREV_ARROW_RES_ID = R.id.arrow_prev_side;

    static Button getPrevArrow(final Activity activity) {
        return (Button) activity.findViewById(PREV_ARROW_RES_ID);
    }

    private static final int NEXT_ARROW_RES_ID = R.id.arrow_next_side;

    static Button getNextArrow(final Activity activity) {
        return (Button) activity.findViewById(NEXT_ARROW_RES_ID);
    }

    private static final int PLAYER_NAME_AREA = R.id.score_editor_player_name_area;

    static ViewGroup getPlayerNameArea(final Activity activity) {
        return (ViewGroup) activity.findViewById(PLAYER_NAME_AREA);
    }

    private static final int TOTAL_SCORE_AREA = R.id.score_editor_total_score_area;

    static ViewGroup getTotalScoreArea(final Activity activity) {
        return (ViewGroup) activity.findViewById(TOTAL_SCORE_AREA);
    }

    private static final int HOLE_ICON_RES_ID = R.id.golf_hole_icon;

    static ImageView getHoleIcon(final Activity activity) {
        return (ImageView) activity.findViewById(HOLE_ICON_RES_ID);
    }

    private static final int VIEW_BUTTON_RES_ID = R.id.viewButton;

    static Button getViewButton(final Activity activity) {
        return (Button) activity.findViewById(VIEW_BUTTON_RES_ID);
    }

    private static final int GRAPH_BUTTON_RES_ID = R.id.graphviewButton;

    static Button getGraphButton(final Activity activity) {
        return (Button) activity.findViewById(GRAPH_BUTTON_RES_ID);
    }

    private static final int CURR_HOLE_NAME_RES_ID = R.id.curr_hole_name;

    static TextView getHoleNameTextView(final Activity activity) {
        return (TextView) activity.findViewById(CURR_HOLE_NAME_RES_ID);
    }

    private static final int UNLOCK_BTN_RES_ID = R.id.unlock_btn;

    static ImageButton getLockButton(final Activity activity) {
        return (ImageButton) activity.findViewById(UNLOCK_BTN_RES_ID);
    }

    private static final int PAR_SPINNER_RES_ID = R.id.curr_hole_par;

    static Spinner getParSpinner(final Activity activity) {
        return (Spinner) activity.findViewById(SERes.PAR_SPINNER_RES_ID);
    }

    static void initParSpinner(final Activity activity) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 1));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 2));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 3));
        Spinner parSpinner = getParSpinner(activity);
        parSpinner.setPrompt("Select Par");
        parSpinner.setAdapter(adapter);
    }
}