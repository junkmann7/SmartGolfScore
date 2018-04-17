package jp.tonosama.komoki.SimpleGolfScorer2;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

class ProgressWorker {

    interface Delegate {

        void execute(@NonNull ProgressHandler progressHandler);
    }

    interface ProgressHandler {

        void progress(int progress, int total);

        void complete();
    }

    static void start(@NonNull final Activity activity, @NonNull final Delegate delegate) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                startMain(activity, delegate);
            }
        });
    }

    private static void startMain(@NonNull final Activity activity, @NonNull final Delegate delegate) {

        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();

        WorkerThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                delegate.execute(new ProgressHandler() {
                    @Override
                    public void progress(int progress, int total) {
                        progressDialog.setMax(total);
                        ProgressWorker.updateProgress(progressDialog, progress);
                    }

                    @Override
                    public void complete() {
                        dismissProgress(progressDialog);
                    }
                });
            }
        });
    }

    private static void updateProgress(@NonNull final ProgressDialog progressDialog, final int value) {
        if (!progressDialog.isShowing()) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setProgress(value);
            }
        });
    }

    private static void dismissProgress(@NonNull final ProgressDialog progressDialog) {
        if (!progressDialog.isShowing()) {
            return;
        }
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
            }
        });
    }
}
