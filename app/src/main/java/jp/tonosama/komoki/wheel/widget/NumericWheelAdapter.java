package jp.tonosama.komoki.wheel.widget;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {

    /** The default min value */
    public static final int DEFAULT_MAX_VALUE = 9;

    /** The default max value */
    private static final int DEFAULT_MIN_VALUE = 0;

    /** minValue */
    private int mMinValue;
    /** maxValue */
    private int mMaxValue;

    /** format */
    private String mFormat;

    /**
     * Default constructor
     */
    public NumericWheelAdapter() {
        this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
    }

    /**
     * Constructor
     * 
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     */
    public NumericWheelAdapter(final int minValue, final int maxValue) {
        this(minValue, maxValue, null);
    }

    /**
     * Constructor
     * 
     * @param minValue the wheel min value
     * @param maxValue the wheel max value
     * @param format the format string
     */
    public NumericWheelAdapter(final int minValue, final int maxValue, final String format) {
        this.mMinValue = minValue;
        this.mMaxValue = maxValue;
        this.mFormat = format;
    }

    public String getItem(final int index) {
        if (index >= 0 && index < getItemsCount()) {
            int value = mMinValue + index;
            String item = Integer.toString(value);
            if (mFormat != null) {
                item = String.format(mFormat, value);
            }
            return item;
        }
        return null;
    }

    public int getItemsCount() {
        return mMaxValue - mMinValue + 1;
    }

    public int getMaximumLength() {
        int max = Math.max(Math.abs(mMaxValue), Math.abs(mMinValue));
        int maxLen = Integer.toString(max).length();
        if (mMinValue < 0) {
            maxLen++;
        }
        return maxLen;
    }
}
