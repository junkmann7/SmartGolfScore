package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.content.Context;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;

class SVPreference {

    private static final String PREF_NAME = "pref_score_viewer";

    private static final String KEY_TEXT_SIZE = "key_text_size";

    private SVPreference() {
        //private constructor
    }

    enum SVTextSize {
        NORMAL(0, R.dimen.adjusted_text_size_16dp),
        LARGE(1, R.dimen.adjusted_text_size_17dp),
        XLARGE(2, R.dimen.adjusted_text_size_18dp);

        private int mIndex;

        private int mSizeRes;

        SVTextSize(int index, int sizeRes) {
            mIndex = index;
            mSizeRes = sizeRes;
        }

        int getIndex() {
            return mIndex;
        }

        int getSizeRes() {
            return mSizeRes;
        }

        static SVTextSize getSVTextSize(int value) {
            for (SVTextSize svTextSize : SVTextSize.values()) {
                if (svTextSize.getIndex() == value) {
                    return svTextSize;
                }
            }
            return SVTextSize.NORMAL;
        }
    }

    static int getSVTextSizeIndex() {
        Context context = SGSApplication.getInstance();
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_TEXT_SIZE, 0);
    }

    static float getSVTextSize() {
        Context context = SGSApplication.getInstance();
        int index = getSVTextSizeIndex();
        int resId = SVTextSize.getSVTextSize(index).getSizeRes();
        return context.getResources().getDimension(resId);
    }

    static void setSVTextSize(int value) {
        Context context = SGSApplication.getInstance();
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putInt(KEY_TEXT_SIZE, value)
                .commit();
    }
}
