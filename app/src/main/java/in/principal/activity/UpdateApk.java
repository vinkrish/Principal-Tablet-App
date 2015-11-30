package in.principal.activity;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import in.principal.model.TransferModel;
import in.principal.sync.StringConstant;
import in.principal.util.CommonDialogUtils;
import in.principal.util.Constants;
import in.principal.util.NetworkUtils;
import in.principal.util.Util;

/**
 * Created by vinkrish.
 */
public class UpdateApk extends BaseActivity {
    private SharedPreferences sharedPref;
    private ProgressDialog pDialog;
    private String apkFolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_apk);
        pDialog = new ProgressDialog(this);

        sharedPref = getSharedPreferences("db_access", Context.MODE_PRIVATE);
        apkFolder = sharedPref.getString("apk_folder", "v1.3");
    }

    public void updateClicked(View v) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("manual_sync", 1);
        editor.apply();

        if (NetworkUtils.isNetworkConnected(this))
            downloadApk();
        else {
            CommonDialogUtils.displayAlertWhiteDialog(this, "Please check internet connection before proceeding.");
        }
    }

    private void downloadApk() {
        new ApkDownloadTask().execute();
    }

    private class ApkDownloadTask extends AsyncTask<Void, Void, Void>{

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Downloading Apk ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String fileName = "download/" + apkFolder + "/" + "principal.zip";
            TransferManager mTransferManager = new TransferManager(Util.getCredProvider(UpdateApk.this));

            DownloadModel model = new DownloadModel(UpdateApk.this, fileName, mTransferManager);
            model.download();

            return null;
        }


        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
        }

    }

    private void downloadComplete() {
        unZipIt("principal.zip");
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("apk_update", 0);
        editor.apply();

        pDialog.dismiss();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "principal.apk")), "application/vnd.android.package-archive");
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }

    private void exitUpdate() {
        pDialog.dismiss();
    }

    public class DownloadModel extends TransferModel {
        private Download mDownload;
        private ProgressListener mListener;
        private String mKey;
        private Status mStatus;

        public DownloadModel(Context context, String key, TransferManager manager) {
            super(context, Uri.parse(key), manager);
            mKey = key;
            mStatus = Status.IN_PROGRESS;
            mListener = new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent event) {
                    if (event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                        mStatus = Status.COMPLETED;
                        downloadComplete();
                    } else if (event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
                        exitUpdate();
                    }
                }
            };
        }

        @Override
        public Status getStatus() {
            return mStatus;
        }

        @Override
        public Transfer getTransfer() {
            return mDownload;
        }

        public void download() {
            try {
                mStatus = Status.IN_PROGRESS;
                File file = new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_DOWNLOADS),
                        getFileName());

                mDownload = getTransferManager().download(
                        Constants.BUCKET_NAME.toLowerCase(Locale.US), mKey, file);

                if (mListener != null) {
                    mDownload.addProgressListener(mListener);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void abort() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }
    }

    public void unZipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }
            ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), zipFile)));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(dir + File.separator + fileName);
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            File zip = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), zipFile);
            zip.delete();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

    }

}
