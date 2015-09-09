package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.model.TransferModel;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.Constants;
import in.principal.util.SharedPreferenceUtil;
import in.principal.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("deprecation")
public class IntermediateDownloadTask extends AsyncTask<String, String, String> implements StringConstant {
    private TransferManager mTransferManager;
    private String fileName;
    private Context context;
    private boolean downloadCompleted, exception;
    private SQLiteDatabase sqliteDatabase;
    private JSONObject jsonReceived;
    private String deviceId, zipFile;
    private int schoolId;

    public IntermediateDownloadTask(Context context, String fileName) {
        this.context = context;
        zipFile = fileName;
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        Temp t = TempDao.selectTemp(sqliteDatabase);
        deviceId = t.getDeviceId();
        schoolId = t.getSchoolId();
        this.fileName = "download/" + schoolId + "/zipped_folder/" + fileName;
    }

    @Override
    protected String doInBackground(String... params) {
        exception = false;
        downloadCompleted = false;
        mTransferManager = new TransferManager(Util.getCredProvider(context));

        DownloadModel model = new DownloadModel(context, fileName, mTransferManager);
        model.download();

        while (!downloadCompleted) {
            Log.d("downloading", "...");
        }

        if (!exception) {
            unZipIt(zipFile);

            StringBuffer sb = new StringBuffer();
            Cursor c = sqliteDatabase.rawQuery("select filename from downloadedfile where downloaded=0", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getString(c.getColumnIndex("filename"))).append("','");
                c.moveToNext();
            }
            c.close();

            if (sb.length() > 3) {
                sqliteDatabase.execSQL("update downloadedfile set downloaded=1 where filename in('" + sb.substring(0, sb.length() - 3) + "')");
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("school", schoolId);
                    jsonObject.put("tab_id", deviceId);
                    jsonObject.put("file_name", "'" + sb.substring(0, sb.length() - 3) + "'");
                    jsonReceived = UploadSyncParser.makePostRequest(update_downloaded_file, jsonObject);
                    if (jsonReceived.getInt(TAG_SUCCESS) == 1)
                        Log.d("update", "downloaded_file");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        int manualSync = SharedPreferenceUtil.getManualSync(context);
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean screenLocked = km.inKeyguardRestrictedInputMode();

        if (manualSync == 1) {
            SharedPreferenceUtil.updateManualSync(context, 2);
            Intent intent = new Intent(context, in.principal.activity.ProcessFiles.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (screenLocked) {
            SharedPreferenceUtil.updateIsSync(context, 1);
            Intent intent = new Intent(context, in.principal.activity.ProcessFiles.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            SharedPreferenceUtil.updateSleepSync(context, 1);
        }
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
                        Log.d("downloading", "completed");
                        mStatus = Status.COMPLETED;
                        downloadCompleted = true;
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
                downloadCompleted = true;
                exception = true;
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

}
