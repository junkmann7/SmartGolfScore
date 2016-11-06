package jp.tonosama.komoki.SimpleGolfScorer2.history;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author Komoki
 */
public class HistoryPagerAdapter extends PagerAdapter {

    /**
     * @author Komoki
     */
    public interface Callback {

        View generateView(final int idx);

        int getCount();
    }

    /**  */
    private Callback mCallback;

    /**
     * @param callback
     */
    public void setCallback(final Callback callback) {
        mCallback = callback;
    }

    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        if (mCallback == null) {
            return null;
        }
        View child = mCallback.generateView(position);
        container.addView(child);
        return child;
    }

    @Override
    public void destroyItem(final ViewGroup container, final int position, final Object object) {
        // コンテナから View を削除
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        if (mCallback != null) {
            return mCallback.getCount();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(final View view, final Object object) {
        // Object 内に View が存在するか判定する
        return view.equals(object);
    }
}
