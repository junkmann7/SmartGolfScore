package jp.tonosama.komoki.SimpleGolfScorer2.setting;

import jp.tonosama.komoki.SimpleGolfScorer2.R;
import jp.tonosama.komoki.SimpleGolfScorer2.Util;
import android.app.Activity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

/**
 * @author Komoki
 */
public final class SettingsRes {

    /**
     * Private Constructor
     */
    private SettingsRes() {
        //
    }

    /**  */
    private static final int[] PLAYER_DEL_BUTTON_RES = { R.id.per1_del_button,
            R.id.per2_del_button, R.id.per3_del_button, R.id.per4_del_button };
    /**  */
    private static final int[] PLAYER_EDIT_AREA_RES = { R.id.per1_name_edit_area,
            R.id.per2_name_edit_area, R.id.per3_name_edit_area, R.id.per4_name_edit_area };

    /**
     * @param activity
     * @return
     */
    public static ViewGroup[] getPlayerEditArea(final Activity activity) {
        ViewGroup[] playerEditArea = new ViewGroup[Util.MAX_PLAYER_NUM];
        for (int i = 0; i < Util.MAX_PLAYER_NUM; i++) {
            playerEditArea[i] = (ViewGroup) activity.findViewById(PLAYER_EDIT_AREA_RES[i]);
        }
        return playerEditArea;
    }

    /**
     * @param activity
     * @return
     */
    public static ImageButton[] getDelButton(final Activity activity) {
        ImageButton[] mPlayerDelButton = new ImageButton[Util.MAX_PLAYER_NUM];
        for (int i = 0; i < Util.MAX_PLAYER_NUM; i++) {
            mPlayerDelButton[i] = (ImageButton) activity.findViewById(PLAYER_DEL_BUTTON_RES[i]);
        }
        return mPlayerDelButton;
    }

    /**
     * @param activity
     * @return
     */
    public static RadioGroup getRadioGroup(final Activity activity) {
        return (RadioGroup) activity.findViewById(R.id.rg_group);
    }

    /**
     * @param activity
     * @return
     */
    public static CheckBox getCheckBox(final Activity activity) {
        return (CheckBox) activity.findViewById(R.id.chck_short_setting);
    }

    /**
     * @param activity
     */
    public static ImageButton getAddButton(final Activity activity) {
        return (ImageButton) activity.findViewById(R.id.player_add_button);
    }

    /**
     * @param activity
     */
    public static Button getStartButton(final Activity activity) {
        return (Button) activity.findViewById(R.id.exit_setting);
    }

    /**
     * @return TitleView
     */
    public static EditText getTitleView(final Activity activity) {
        return ((EditText) activity.findViewById(R.id.hole_title_editor));
    }

    /**
     * @return HandiView
     */
    public static EditText[] getHandiView(final Activity activity) {
        EditText[] handis = new EditText[Util.MAX_PLAYER_NUM];
        handis[0] = ((EditText) activity.findViewById(R.id.per1_handi));
        handis[1] = ((EditText) activity.findViewById(R.id.per2_handi));
        handis[2] = ((EditText) activity.findViewById(R.id.per3_handi));
        handis[3] = ((EditText) activity.findViewById(R.id.per4_handi));
        return handis;
    }

    /**
     * @return NameView
     */
    public static EditText[] getNameView(final Activity activity) {
        EditText[] names = new EditText[Util.MAX_PLAYER_NUM];
        names[0] = (EditText) activity.findViewById(R.id.per1_name_editor);
        names[1] = (EditText) activity.findViewById(R.id.per2_name_editor);
        names[2] = (EditText) activity.findViewById(R.id.per3_name_editor);
        names[3] = (EditText) activity.findViewById(R.id.per4_name_editor);
        return names;
    }

    /**
     * @return MemoView
     */
    public static EditText getMemoView(final Activity activity) {
        return ((EditText) activity.findViewById(R.id.memo_editor_area));
    }

    /**
     * @return Spinner
     */
    public static Spinner getSpinner(final Activity activity) {
        return ((Spinner) activity.findViewById(R.id.round_condition));
    }

    /**
     * getPersonNames
     * 
     * @param nameEdit EditText[]
     * @return Person Names
     */
    public static String[] getPersonNames(final Activity activity) {
        String[] names = new String[4];
        EditText[] nameEdit = getNameView(activity);
        names[0] = nameEdit[0].getText().toString().replaceAll("<", "");
        names[1] = nameEdit[1].getText().toString().replaceAll("<", "");
        names[2] = nameEdit[2].getText().toString().replaceAll("<", "");
        names[3] = nameEdit[3].getText().toString().replaceAll("<", "");
        return names;
    }

    /**
     * @param titleEdit EditText
     * @return Hole Title
     */
    public static String getHoleTitle(final Activity activity) {
        EditText titleEdit = getTitleView(activity);
        return titleEdit.getText().toString().replaceAll("<", "");
    }

    /**
     * @param memoEdit EditText
     * @return Memo Str
     */
    public static String getMemoStr(final Activity activity) {
        EditText memoEdit = getMemoView(activity);
        return memoEdit.getText().toString().replaceAll("<", "");
    }
}
