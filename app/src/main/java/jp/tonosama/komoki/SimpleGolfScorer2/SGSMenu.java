package jp.tonosama.komoki.SimpleGolfScorer2;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.Menu;

enum SGSMenu {

    Backup(Menu.FIRST, R.string.menu_backup, R.drawable.ic_menu_save),
    Restore(Menu.FIRST + 1, R.string.menu_restore, R.drawable.ic_menu_restore),
    Sort(Menu.FIRST + 2, R.string.menu_sort, R.drawable.ic_menu_sort),
    About(Menu.FIRST + 3, R.string.menu_about, R.drawable.ic_menu_question);

    private int mMenuId;
    private int mTitleResId;
    private int mIconResId;

    SGSMenu(int menuId, @StringRes int menuTitle, @DrawableRes int menuIcon) {
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

    static SGSMenu getMenu(int menuId) {
        for (SGSMenu sgsMenu : SGSMenu.values()) {
            if (menuId == sgsMenu.getMenuId()) {
                return sgsMenu;
            }
        }
        return SGSMenu.About;
    }
}
