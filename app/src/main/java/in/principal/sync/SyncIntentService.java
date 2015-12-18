package in.principal.sync;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Environment;
import android.util.Log;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import in.principal.dao.TempDao;
import in.principal.model.TransferModel;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.Constants;
import in.principal.util.SharedPreferenceUtil;
import in.principal.util.Util;

public class SyncIntentService extends IntentService implements StringConstant {
    private SqlDbHelper sqlHandler;
    private SQLiteDatabase sqliteDatabase;
    private Context context;
    private int block, schoolId;
    private String zipFile, deviceId;
    private SharedPreferences sharedPref;

    public SyncIntentService() {
        super("SyncIntentService");
        context = AppGlobal.getContext();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            checkDownloadStatus();
            decideDownload();
        }
    }

    private void checkDownloadStatus() {

        sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.getApplicationContext().registerReceiver(null, ifilter);
        int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        JSONObject ack_json = new JSONObject();
        Temp t = TempDao.selectTemp(sqliteDatabase);
        schoolId = t.getSchoolId();
        deviceId = t.getDeviceId();
        try {
            ack_json.put("school", schoolId);
            ack_json.put("tab_id", deviceId);
            ack_json.put("battery_status", batteryLevel);
            JSONObject jsonReceived = new JSONObject(RequestResponseHandler.reachServer(ask_for_download_file, ack_json));
            block = jsonReceived.getInt(TAG_SUCCESS);
            if (jsonReceived.getInt("update") == 1) {
                String folder = jsonReceived.getString("version");
                SharedPreferenceUtil.updateApkUpdate(context, 1, folder);
            }
            zipFile = jsonReceived.getString("folder_name");
            String s = jsonReceived.getString("files");
            String[] sArray = s.split(",");
            for (String split : sArray) {
                TempDao.updateSyncTimer(sqliteDatabase);
                sqlHandler.insertDownloadedFile(split);
            }
        } catch (JSONException e) {
            zipFile = "";
            e.printStackTrace();
        } catch (NullPointerException e) {
            zipFile = "";
            e.printStackTrace();
        }

    }

    private void decideDownload() {
        if (block != 2 && zipFile != "") {
            Log.d("downloadFile", "uh");
            String fileName = "download/" + schoolId + "/zipped_folder/" + zipFile;
            TransferManager mTransferManager = new TransferManager(Util.getCredProvider(context));
            DownloadModel model = new DownloadModel(context, fileName, mTransferManager);
            model.download();
        } else {
            exitSync();
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
                        mStatus = Status.COMPLETED;
                        unzipAndAck();
                    } else if (event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
                        exitSync();
                    } else if (event.getEventCode() == ProgressEvent.CANCELED_EVENT_CODE) {
                        exitSync();
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
                exitSync();
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

    private void unzipAndAck() {
        unZipIt();
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
                JSONObject jsonReceived = new JSONObject(RequestResponseHandler.reachServer(update_downloaded_file, jsonObject));
                if (jsonReceived.getInt(TAG_SUCCESS) == 1)
                    Log.d("update", "downloaded_file");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        finishSync();
    }

    public void unZipIt() {
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

    private void finishSync() {
        Log.d("finishSync", "uh");
        int manualSync = sharedPref.getInt("manual_sync", 0);
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean screenLocked = km.inKeyguardRestrictedInputMode();

        if (manualSync == 1) {
            SharedPreferenceUtil.updateManualSync(context, 2);
            Intent intent = new Intent(context, in.principal.activity.ProcessFiles.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (screenLocked) {
            Intent intent = new Intent(context, in.principal.activity.ProcessFiles.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            SharedPreferenceUtil.updateSleepSync(context, 1);
        }
    }

    private void exitSync() {
        Log.d("exitSync", "uh");
        SharedPreferences.Editor editor = sharedPref.edit();
        int manualSync = sharedPref.getInt("manual_sync", 0);
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean screenLocked = km.inKeyguardRestrictedInputMode();
        if (block == 2) {
            editor.putInt("manual_sync", 0);
            editor.putInt("tablet_lock", 2);
            editor.apply();
        } else if (manualSync == 1) {
            editor.putInt("manual_sync", 0);
            editor.apply();
            Intent intent = new Intent(context, in.principal.activity.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (screenLocked) {
            Intent i = new Intent(context, in.principal.activity.ProcessFiles.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
        } else {
            SharedPreferenceUtil.updateSleepSync(context, 1);
        }
    }

}
