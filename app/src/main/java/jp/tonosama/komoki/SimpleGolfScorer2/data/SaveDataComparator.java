package jp.tonosama.komoki.SimpleGolfScorer2.data;

import java.io.Serializable;
import java.util.Comparator;

import android.content.res.Resources;
import jp.tonosama.komoki.SimpleGolfScorer2.R;

/**
 * @author Komoki
 */
public class SaveDataComparator implements Comparator<Object>, Serializable {

    /** ソート種別：作成降順 */
    private static final int SORT_TYPE_CREATE_DESCENDING = 0;

    /** ソート種別：作成昇順 */
    private static final int SORT_TYPE_CREATE_ASCENDING = 1;

    /** ソート種別：名前降順 */
    private static final int SORT_TYPE_NAME_DESCENDING = 2;

    /** ソート種別：名前昇順 */
    private static final int SORT_TYPE_NAME_ASCENDING = 3;

    /**  */
    private int mSortType;

    /**
     * @param sortType sortType
     */
    SaveDataComparator(final int sortType) {
        mSortType = sortType;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(T, T)
     */
    public int compare(final Object arg0, final Object arg1) {

        SaveData data0 = (SaveData) arg0;
        SaveData data1 = (SaveData) arg1;

        if (mSortType == SORT_TYPE_CREATE_DESCENDING) {
            return (data0.getSaveIdx() - data1.getSaveIdx());
        } else if (mSortType == SORT_TYPE_CREATE_ASCENDING) {
            return -(data0.getSaveIdx() - data1.getSaveIdx());
        } else {
            int compare = 0;
            if (mSortType == SORT_TYPE_NAME_DESCENDING) {
                compare = 1;
            } else if (mSortType == SORT_TYPE_NAME_ASCENDING) {
                compare = -1;
            }
            if (data0.getHoleTitle().compareToIgnoreCase(data1.getHoleTitle()) < 0) {
                return compare;
            } else if (data0.getHoleTitle().compareToIgnoreCase(data1.getHoleTitle()) == 0) {
                return 0;
            } else {
                return -1 * compare;
            }
        }
    }

    /**
     * @param res Resources
     * @return SortTypeStr
     */
    public static String[] getSortTypeStr(final Resources res) {
        return new String[]{ //
        res.getString(R.string.menu_sort_create_descending),
                res.getString(R.string.menu_sort_create_ascending),
                res.getString(R.string.menu_sort_name_descending),
                res.getString(R.string.menu_sort_name_ascending) };
    }
}
