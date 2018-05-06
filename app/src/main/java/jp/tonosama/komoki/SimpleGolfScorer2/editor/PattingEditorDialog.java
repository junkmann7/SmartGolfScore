package jp.tonosama.komoki.SimpleGolfScorer2.editor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;

import jp.tonosama.komoki.SimpleGolfScorer2.R;

public class PattingEditorDialog {

    private static final int[] MY_PAT_IMG_RES_IDS = { //
            R.drawable.mypatter0, R.drawable.mypatter1, //
            R.drawable.mypatter2, R.drawable.mypatter3, //
            R.drawable.mypatter4, R.drawable.mypatter5 };

    interface PattingEditorCallback {

        void onPattingChanged(final int holeNumber, final int playerIdx, final int newVal);
    }

    private PattingEditorDialog() {
        //private constructor
    }

    @SuppressLint("ClickableViewAccessibility")
    static void show(@NonNull Activity activity, final int holeNumber, final int playerIdx,
                     final int currentValue,
                     @NonNull final PattingEditorCallback callback) {
        LayoutInflater inflater = (LayoutInflater) activity.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            return;
        }
        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.patting_editor_layout, null);
        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.mypat_ratingbar);
        ratingBar.setRating(currentValue);
        final ImageView ratingIv = (ImageView) view.findViewById(R.id.my_pat_img);
        setRatingImage(ratingIv, currentValue);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int rating = (int) ratingBar.getRating();
                setRatingImage(ratingIv, rating);
                callback.onPattingChanged(holeNumber, playerIdx, (int) ratingBar.getRating());
                return v.onTouchEvent(event);
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                callback.onPattingChanged(holeNumber, playerIdx, (int) rating);
            }
        });
        new AlertDialog.Builder(activity)
                .setView(view)
                .create()
                .show();
    }

    private static void setRatingImage(@NonNull ImageView ratingIv, int rating) {
        ratingIv.setImageResource(MY_PAT_IMG_RES_IDS[rating]);
    }
}
