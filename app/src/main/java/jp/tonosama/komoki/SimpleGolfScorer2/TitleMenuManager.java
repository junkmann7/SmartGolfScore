package jp.tonosama.komoki.SimpleGolfScorer2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.net.Uri;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataComparator;
import jp.tonosama.komoki.SimpleGolfScorer2.data.SaveDataList;

/**
 * @author Komoki
 */
class TitleMenuManager {

    private static final String TAG = TitleMenuManager.class.getSimpleName();

    private static final String MARKET_PACKAGE_NAME = "com.android.vending";

    void onCreateOptionsMenu(final Menu menu, final Context context) {

        Resources res = context.getResources();

        for (SGSMenu sgsMenu : SGSMenu.values()) {
            MenuItem menuItem = menu.add(
                    0,
                    sgsMenu.getMenuId(),
                    Menu.NONE,
                    res.getString(sgsMenu.getTitleResId()));
            menuItem.setIcon(sgsMenu.getIconResId());
        }
    }

    boolean onOptionsItemSelected(final MenuItem item, final Context context) {
        DevLog.d(TAG, "onOptionsItemSelected item:" + item.toString());

        switch (SGSMenu.getMenu(item.getItemId())) {
            case Sort:
                actionChangeSort(context);
                break;
            case Backup:
                actionDataBackup(context);
                break;
            case Restore:
                actionDataRestore(context);
                break;
            case About:
                actionAbout(context);
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * actionAbout
     * 
     * @param context Context
     */
    private void actionAbout(final Context context) {

        final Resources res = context.getResources();
        String aboutDialogTitle = res.getString(R.string.app_name);
        String aboutDialogMsg1 = "";
        aboutDialogMsg1 += res.getString(R.string.about_author) + " ";
        aboutDialogMsg1 += res.getString(R.string.about_author_name) + "\n";
        aboutDialogMsg1 += res.getString(R.string.about_email) + " ";
        aboutDialogMsg1 += res.getString(R.string.about_email_address);

        String aboutDialogMsg2 = "\n";
        aboutDialogMsg2 += res.getString(R.string.about_introduction) + "\n\n";
        aboutDialogMsg2 += res.getString(R.string.about_function_summary);

        TextView tv1 = new TextView(context);
        tv1.setTextSize(18);
        tv1.setAutoLinkMask(Linkify.EMAIL_ADDRESSES);
        tv1.setText(aboutDialogMsg1);

        TextView tv2 = new TextView(context);
        tv2.setTextSize(18);
        tv2.setText(aboutDialogMsg2);

        LinearLayout aboutLinearLayout = new LinearLayout(context);
        aboutLinearLayout.setOrientation(LinearLayout.VERTICAL);
        aboutLinearLayout.setPadding(5, 0, 5, 0);
        aboutLinearLayout.addView(tv1, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        aboutLinearLayout.addView(tv2, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        ScrollView wrapScrollView = new ScrollView(context);
        wrapScrollView.addView(aboutLinearLayout, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        showAboutDialog(context, aboutDialogTitle, wrapScrollView);
    }

    /**
     * showAboutDialog
     * 
     * @param context Context
     * @param title ダイアログタイトル
     * @param view ダイアログView
     */
    private void showAboutDialog(final Context context, final String title, final View view) {
        final Resources res = context.getResources();
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(context);
        aboutDialog.setIcon(R.drawable.icon);
        try {
            String dialogTitle = title
                    + "\nver "
                    + context.getPackageManager().getPackageInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA).versionName;
            aboutDialog.setTitle(dialogTitle);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        aboutDialog.setView(view);
        aboutDialog.setPositiveButton(R.string.about_app_detail_btn,
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setPackage(MARKET_PACKAGE_NAME);
                        intent.setData(Uri.parse(res.getString(R.string.about_url_market)));
                        context.startActivity(intent);
                    }
                });
        aboutDialog.setNeutralButton(R.string.about_app_share_btn,
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("text/*");
                        intent.putExtra(
                                Intent.EXTRA_TEXT,
                                res.getString(R.string.about_introduction) + "\n\n"
                                        + res.getString(R.string.about_url_market));
                        context.startActivity(Intent.createChooser(intent,
                                res.getString(R.string.about_app_share_btn)));
                    }
                });
        aboutDialog.setNegativeButton(R.string.about_app_license,
                new DialogInterface.OnClickListener() {

                    public void onClick(final DialogInterface dialog, final int which) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        WebView webView = new WebView(context);
                        webView.loadUrl("file:///android_asset/licenses.html");
                        builder.setView(webView);
                        builder.show();
                    }
                });
        aboutDialog.show();
    }

    /**
     * actionDataRestore
     * 
     * @param context Context
     */
    private void actionDataRestore(final Context context) {

        Resources res = context.getResources();
        final List<String> bkFileList = SaveDataPref.getBackupFileList();
        if (bkFileList.size() < 1) {
            Toast.makeText(context, res.getString(R.string.toast_backup_not_found),
                    Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder restoreDialog = new AlertDialog.Builder(context);
        restoreDialog.setIcon(R.drawable.ic_menu_restore);
        restoreDialog.setTitle(res.getString(R.string.dlg_restore_title));
        restoreDialog.setItems(bkFileList.toArray(new String[]{}), new OnClickListener() {

            public void onClick(final DialogInterface dialog, final int item) {
                ((MainTitle) context).backupData(bkFileList.get(item));
            }
        });
        restoreDialog.show();
    }

    /**
     * actionDataBackup
     * 
     * @param context Context
     */
    private void actionDataBackup(final Context context) {

        Resources res = context.getResources();
        AlertDialog.Builder backupDialog = new AlertDialog.Builder(context);
        backupDialog.setIcon(R.drawable.ic_menu_save);
        backupDialog.setTitle(res.getString(R.string.dlg_backup_title));
        backupDialog.setMessage(res.getString(R.string.dlg_backup_guide_msg));
        backupDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, final int which) {
                ((MainTitle) context).outputBackupData();
            }
        });
        backupDialog.setNegativeButton(android.R.string.cancel, null);
        backupDialog.show();
    }

    /**
     * actionChangeSort
     * 
     * @param context Context
     */
    private void actionChangeSort(final Context context) {
        Resources res = context.getResources();
        int sortType = SaveDataList.getSortType();
        String[] sortNames = SaveDataComparator.getSortTypeStr(res);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(res.getString(R.string.menu_sort));
        dialog.setSingleChoiceItems(sortNames, sortType, new OnClickListener() {

            public void onClick(final DialogInterface dialog, final int item) {
                dialog.dismiss();
                SaveDataList.saveSortType(item);
                ((MainTitle) context).setupData();
            }
        });
        dialog.create().show();
    }
}