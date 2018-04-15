package jp.tonosama.komoki.SimpleGolfScorer2.viewer;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Menu;

import jp.tonosama.komoki.SimpleGolfScorer2.R;

enum SVMenu {

    ChangeTextSize(
            Menu.FIRST,
            R.string.menu_score_viewer_text_size_setting,
            R.drawable.imane_button_setting);

    private int mMenuId;
    private int mTitleResId;
    private int mIconResId;

    SVMenu(int menuId, @StringRes int menuTitle, @DrawableRes int menuIcon) {
        mMenuId = menuId;
        mTitleResId = menuTitle;
        mIconResId = menuIcon;
    }

    int getMenuId() {
        return mMenuId;
    }

    int getTitleResId() {
        return mTitleResId;
    }

    int getIconResId() {
        return mIconResId;
    }

    static SVMenu getMenu(int menuId) {
        for (SVMenu sgsMenu : SVMenu.values()) {
            if (menuId == sgsMenu.getMenuId()) {
                return sgsMenu;
            }
        }
        return SVMenu.ChangeTextSize;
    }
}
