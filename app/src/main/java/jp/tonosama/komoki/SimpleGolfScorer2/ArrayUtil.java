package jp.tonosama.komoki.SimpleGolfScorer2;

import android.annotation.SuppressLint;

import java.util.HashMap;
import java.util.Map;

@SuppressLint("UseSparseArrays")
public class ArrayUtil {

    private ArrayUtil() {
        //private constructor
    }

    public static String convertToString(Map<Integer, Integer> integerMap) {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < integerMap.size(); i++) {
            str.append(integerMap.get(i)).append(",");
        }
        return str.toString();
    }

    public static Map<Integer, Integer> createMap(int size, int value) {
        Map<Integer, Integer> map = new HashMap<>(size);
        for (int i = 0; i < size; i++) {
            map.put(i, value);
        }
        return map;
    }
}
