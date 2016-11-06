/*
 *  Copyright 2010 Yuri Kanivets
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package jp.tonosama.komoki.wheel.widget;

/**
 * The simple Array wheel adapter
 * 
 * @param <T> the element type
 */
public class ArrayWheelAdapter<T> implements WheelAdapter {

    /** The default items length */
    public static final int DEFAULT_LENGTH = -1;

    /** items */
    private T[] mItems;
    /** length */
    private int mLength;

    /**
     * Constructor
     * 
     * @param items the items
     * @param length the max items length
     */
    public ArrayWheelAdapter(final T[] items, final int length) {
        this.mItems = items;
        this.mLength = length;
    }

    /**
     * Contructor
     * 
     * @param items the items
     */
    public ArrayWheelAdapter(final T[] items) {
        this(items, DEFAULT_LENGTH);
    }

    public String getItem(final int index) {
        if (index >= 0 && index < mItems.length) {
            return mItems[index].toString();
        }
        return null;
    }

    public int getItemsCount() {
        return mItems.length;
    }

    public int getMaximumLength() {
        return mLength;
    }

}
