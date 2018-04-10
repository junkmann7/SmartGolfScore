package jp.tonosama.komoki.SimpleGolfScorer2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SGSConfig {

    private SGSConfig() {
        //private constructor
    }

    public static final int MAX_PLAYER_NUM = 4;

    public static final int TOTAL_HOLE_COUNT = 18;

    public static final Map<Integer, Integer> DEFAULT_HOLEPAR_SHORT_SCORE = new HashMap<Integer, Integer>() {
        {
            for (int i = 0; i < TOTAL_HOLE_COUNT; i++) {
                put(i, 3);
            }
        }
    };

    public static final Map<Integer, Integer> DEFAULT_HOLEPAR_SCORE = new HashMap<Integer, Integer>() {
        {
            for (int i = 0; i < TOTAL_HOLE_COUNT; i++) {
                put(i, 4);
            }
        }
    };
}
