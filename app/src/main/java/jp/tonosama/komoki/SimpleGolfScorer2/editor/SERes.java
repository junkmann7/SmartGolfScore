package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.wheel.widget.NumericWheelAdapter;
import jp.tonosama.komoki.wheel.widget.WheelView;

/**
 * @author Komoki
 */
final class SERes {

    /**
     * Private Constructor
     */
    private SERes() {
        //
    }

    /** Parの最小値 */
    static final int MINIMUM_PAR_COUNT = 2;

    /**  */
    static final int MAIN_LAYOUT_RES_ID = R.layout.golf_score_book;

    /**  */
    static final int[] CURRENT_HOLE_IMG_RES_IDS = { //
    R.drawable.current_hole_01, R.drawable.current_hole_02, R.drawable.current_hole_03,
            R.drawable.current_hole_04, R.drawable.current_hole_05, R.drawable.current_hole_06,
            R.drawable.current_hole_07, R.drawable.current_hole_08, R.drawable.current_hole_09,
            R.drawable.current_hole_10, R.drawable.current_hole_11, R.drawable.current_hole_12,
            R.drawable.current_hole_13, R.drawable.current_hole_14, R.drawable.current_hole_15,
            R.drawable.current_hole_16, R.drawable.current_hole_17, R.drawable.current_hole_18 };

    /**  */
    static final int[] HOLE_NUMBER_IMG_RES_IDS = { //
    R.drawable.hole_number_01, R.drawable.hole_number_02, R.drawable.hole_number_03,
            R.drawable.hole_number_04, R.drawable.hole_number_05, R.drawable.hole_number_06,
            R.drawable.hole_number_07, R.drawable.hole_number_08, R.drawable.hole_number_09,
            R.drawable.hole_number_10, R.drawable.hole_number_11, R.drawable.hole_number_12,
            R.drawable.hole_number_13, R.drawable.hole_number_14, R.drawable.hole_number_15,
            R.drawable.hole_number_16, R.drawable.hole_number_17, R.drawable.hole_number_18, };
    /**  */
    private static final int[] DRUM_PICKER_RES_IDS = { //
    R.id.drumPicker1, R.id.drumPicker2, //
            R.id.drumPicker3, R.id.drumPicker4 };

    /**  */
    static final int[] MY_PAT_IMG_RES_IDS = { //
    R.drawable.mypatter0, R.drawable.mypatter1, //
            R.drawable.mypatter2, R.drawable.mypatter3, //
            R.drawable.mypatter4, R.drawable.mypatter5 };

    /**  */
    static final int DRUM_PICKER_AREA_RES_ID = R.id.picker_area;

    /**  */
    private static final int[] PERSON_NAME_RES_IDS = { //
    R.id.per1_name_area, R.id.per2_name_area, //
            R.id.per3_name_area, R.id.per4_name_area };

    /**  */
    private static final int[] PERSON_SCORE_RES_IDS = { //
    R.id.per1_total_score, R.id.per2_total_score, //
            R.id.per3_total_score, R.id.per4_total_score };

    /**  */
    private static final int PAR_SPINNER_RES_ID = R.id.curr_hole_par;

    static TextView[] getPersonNameTextViews(final Activity activity) {
        TextView[] personNameTextViews = new TextView[PERSON_NAME_RES_IDS.length];
        for (int i = 0; i < PERSON_NAME_RES_IDS.length; i++) {
            personNameTextViews[i] = //
            (TextView) activity.findViewById(SERes.PERSON_NAME_RES_IDS[i]);
        }
        return personNameTextViews;
    }

    static TextView[] getPersonScoreTextViews(final Activity activity) {
        TextView[] personScoreTextViews = new TextView[PERSON_SCORE_RES_IDS.length];
        for (int i = 0; i < PERSON_SCORE_RES_IDS.length; i++) {
            personScoreTextViews[i] = //
            (TextView) activity.findViewById(SERes.PERSON_SCORE_RES_IDS[i]);
        }
        return personScoreTextViews;
    }

    static void initDrumPicker(final Activity activity) {

        final WheelView[] drumPickers = getDrumPicker(activity);
        final int orientation = activity.getResources().getConfiguration().orientation;
        for (WheelView drumPicker : drumPickers) {
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                int drumNum = 4;
                drumPicker.setVisibleItems(drumNum);
            } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                int drumNum = 3;
                drumPicker.setVisibleItems(drumNum);
            }
            drumPicker.setAdapter(new NumericWheelAdapter(0, 20));
            drumPicker.setCyclic(true);
        }
    }

    static Spinner initParSpinner(final Activity activity) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown);
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 1));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 2));
        adapter.add(String.valueOf(MINIMUM_PAR_COUNT + 3));
        Spinner mParSpinner = (Spinner) activity.findViewById(SERes.PAR_SPINNER_RES_ID);
        mParSpinner.setPrompt("Select Par");
        mParSpinner.setAdapter(adapter);
        return mParSpinner;
    }

    static Spinner getParSpinner(final Activity activity) {
        return (Spinner) activity.findViewById(SERes.PAR_SPINNER_RES_ID);
    }

    static WheelView[] getDrumPicker(final Activity activity) {

        final WheelView[] drumPickers = new WheelView[SERes.DRUM_PICKER_RES_IDS.length];
        for (int i = 0; i < drumPickers.length; i++) {
            drumPickers[i] = (WheelView) activity.findViewById(SERes.DRUM_PICKER_RES_IDS[i]);
        }
        return drumPickers;
    }

    static TextView getHoleTitleTextView(final Activity activity) {
        return (TextView) activity.findViewById(R.id.curr_hole_name);
    }

    static ImageButton getLockButton(final Activity activity) {
        return (ImageButton) activity.findViewById(R.id.unlock_btn);
    }

    static RatingBar getRatingBar(final Activity activity) {
        return (RatingBar) activity.findViewById(R.id.mypat_ratingbar);
    }

    static ImageView getCurHoleImg(final Activity activity) {
        return (ImageView) activity.findViewById(R.id.current_player_location);
    }

    static Button getPrevArrwButton(final Activity activity) {
        return (Button) activity.findViewById(R.id.arrow_upside);
    }

    static Button getNextArrwButton(final Activity activity) {
        return (Button) activity.findViewById(R.id.arrow_downside);
    }

    static ViewGroup getFooterArea(final Activity activity) {
        return (ViewGroup) activity.findViewById(R.id.next_hole_area);
    }

    static ImageView getDragImg(final Activity activity) {
        return (ImageView) activity.findViewById(R.id.next_hole_name);
    }

    static View getDrumAreaView(final Activity activity) {
        return activity.findViewById(SERes.DRUM_PICKER_AREA_RES_ID);
    }
}