package jp.tonosama.komoki.SimpleGolfScorer2.wheel;

import jp.tonosama.komoki.SimpleGolfScorer2.R;

class WheelConfig {

    static final int TEXT_SIZE_RES_ID = R.dimen.adjusted_size_30dp;

    static final int TEXT_VIEW_HEIGHT_RES_ID = R.dimen.adjusted_size_42dp;

    static final int TEXT_SHADOW_COLOR = 0xFFC0C0C0;

    static final int[] SHADOWS_COLORS = new int[] { 0xFF111111, 0x00AAAAAA, 0x00AAAAAA };

    /** Text color of the current item */
    static final int CURRENT_TEXT_COLOR_UNLOCK = 0xF0000000;
    /**  */
    static final int CURRENT_TEXT_COLOR_LOCKED = 0xFF0070C0;

    /** Text color of the other items */
    static final int OTHER_TEXT_COLOR_UNLOCK = 0xF0000000;
    /**  */
    static final int OTHER_TEXT_COLOR_LOCKED = 0xF0999999;
}
