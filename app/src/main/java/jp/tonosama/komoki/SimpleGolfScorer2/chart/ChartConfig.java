package jp.tonosama.komoki.SimpleGolfScorer2.chart;

import android.graphics.Color;

public final class ChartConfig {
    public static final String[] BASE_LINE_TYPE_TITLE = new String[]{"Auto", "PAR", "+1", "+2", "+3", "Double PAR"};
    public static final String[] BASE_LINE_TYPE_STR = new String[]{"Auto", "PAR", "+1", "+2", "+3", "Double"};
    public static final int[] BASE_LINE_TYPE_INT = new int[]{0, 0, 1, 2, 3, 0};
    public static final int DEFAULT_BASELINE_TYPE = 0;
    public static final String PREF_GRAPH_SETTING = "PREF_GRAPH_SETTING";
    public static final String PREF_GRAPH_BASIS_TYPE_KEY = "PREF_GRAPH_BASIS_TYPE_KEY";

    public static final int[] DEFAULT_COLORS = new int[] {
            Color.argb(255, 255, 0, 0),
            Color.argb(255, 0, 0, 255),
            Color.argb(255, 255, 0, 128),
            Color.argb(255, 0, 128, 0) };

    private ChartConfig() {
    }
}