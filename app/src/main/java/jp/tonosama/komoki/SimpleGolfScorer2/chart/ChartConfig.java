package jp.tonosama.komoki.SimpleGolfScorer2.chart;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import jp.tonosama.komoki.SimpleGolfScorer2.SGSApplication;

public final class ChartConfig {
    public static final String[] BASE_LINE_TYPE_TITLE = new String[]{"Auto", "PAR", "+1", "+2", "+3", "Double PAR"};
    public static final String PREF_GRAPH_SETTING = "PREF_GRAPH_SETTING";
    public static final String PREF_GRAPH_BASIS_TYPE_KEY = "PREF_GRAPH_BASIS_TYPE_KEY";

    public enum BaseLineType {

        Auto(0, 0, "Auto"),
        Par(1, 0, "Par"),
        Plus1(2, 1, "+1"),
        Plug2(3, 2, "+2"),
        Plug3(4, 3, "+3"),
        DoubePar(5, 0, "Double Par");

        private int mIndex;
        private int mBaseValue;
        private String mName;

        BaseLineType(int index, int baseValue, String name) {
            mIndex = index;
            mBaseValue = baseValue;
            mName = name;
        }

        public static BaseLineType getBaseLineType(int index) {
            for (BaseLineType baseLineType : BaseLineType.values()) {
                if (baseLineType.getIndex()  == index) {
                    return baseLineType;
                }
            }
            return Auto;
        }

        public int getIndex() {
            return mIndex;
        }

        public int getBaseValue() {
            return mBaseValue;
        }

        public String getName() {
            return mName;
        }
    }

    public static BaseLineType getBaseLineType() {
        Context context = SGSApplication.getInstance();
        SharedPreferences pref = context.getSharedPreferences(ChartConfig.PREF_GRAPH_SETTING,
                Context.MODE_PRIVATE);
        int prefBaseLineIndex = pref.getInt(ChartConfig.PREF_GRAPH_BASIS_TYPE_KEY, 0);
        return BaseLineType.getBaseLineType(prefBaseLineIndex);

    }

    public static final int[] DEFAULT_COLORS = new int[] {
            Color.argb(255, 255, 0, 0),
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 255, 0, 128),
            Color.argb(255, 0, 128, 0) };

    private ChartConfig() {
    }
}