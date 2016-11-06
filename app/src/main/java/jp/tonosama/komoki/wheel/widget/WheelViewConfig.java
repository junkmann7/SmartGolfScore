package jp.tonosama.komoki.wheel.widget;

/**
 * WheelViewConfig
 * 
 * @author Komoki
 */
public final class WheelViewConfig {

    /** Current value & label text color */
    static final int VALUE_TEXT_COLOR_UNLOCK = 0xF0000000;
    /**  */
    static final int VALUE_TEXT_COLOR_LOCKED = 0xF033A7FF;

    /** Items text color */
    static final int ITEMS_TEXT_COLOR_UNLOCK = 0xF0000000;
    /**  */
    static final int ITEMS_TEXT_COLOR_LOCKED = 0xF0999999;

    /** Top and bottom shadows colors */
    static final int[] SHADOWS_COLORS = new int[] { 0xFF111111, 0x00AAAAAA, 0x00AAAAAA };

    /**  */
    static final int ADDITIONAL_ITEM_HEIGHT = 5;
    /**  */
    static final int ADDITIONAL_ITEMS_SPACE = 5;

    /** Text size */
    static final int TEXT_SIZE = 20;

    /** Top and bottom items offset (to hide that) */
    static final int ITEM_OFFSET = (int) (TEXT_SIZE / 5f / 1.5f);

    /** Label offset */
    static final int LABEL_OFFSET = (int) (8 / 1.5);

    /** Left and right padding value */
    static final int PADDING = (int) (10 / 1.5);

    /** Default count of visible items */
    static final int DEF_VISIBLE_ITEMS = 5;

    /**
     * Constructor
     */
    private WheelViewConfig() {
        //
    }
}
