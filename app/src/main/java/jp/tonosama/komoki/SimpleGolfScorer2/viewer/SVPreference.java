package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.content.Context;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;

class SVPreference {

    private static final String PREF_NAME = "pref_score_viewer";

    private static final String KEY_TEXT_SIZE = "key_text_size";

    private static final String PREF_TABLE_SETTING = "PREF_TABLE_SETTING";

    private static final String PREF_TABLE_VALUE_TYPE_KEY = "PREF_TABLE_VALUE_TYPE_KEY";

    static final int VIEWER_TYPE_RELATIVE = 0;

    static final int VIEWER_TYPE_ABSOLUTE = 1;

    private static final int DEFAULT_VIEWER_TYPE = VIEWER_TYPE_RELATIVE;

    private SVPreference() {
        //private constructor
    }

    enum SVTextSize {
        SMALL(0, R.dimen.adjusted_size_16dp),
        NORMAL(1, R.dimen.adjusted_size_18dp),
        LARGE(2, R.dimen.adjusted_size_20dp),
        XLARGE(3, R.dimen.adjusted_size_22dp);

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
            return SVTextSize.SMALL;
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

    static void setSVTextSizeIndex(int value) {
        Context context = SGSApplication.getInstance();
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit().putInt(KEY_TEXT_SIZE, value)
                .commit();
    }

    static int getScoreViewType() {
        Context context = SGSApplication.getInstance();
        return context.getSharedPreferences(PREF_TABLE_SETTING, Context.MODE_PRIVATE)
                .getInt(PREF_TABLE_VALUE_TYPE_KEY, DEFAULT_VIEWER_TYPE);
    }

    static void setScoreViewType(int scoreViewerType) {
        Context context = SGSApplication.getInstance();
        context.getSharedPreferences(PREF_TABLE_SETTING, Context.MODE_PRIVATE)
                .edit()
                .putInt(PREF_TABLE_VALUE_TYPE_KEY, scoreViewerType)
                .commit();
    }
}
