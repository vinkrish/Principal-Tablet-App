package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.model.TransferModel;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.Constants;
import in.principal.util.SharedPreferenceUtil;
import in.principal.util.Util;

import java.io.File;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import com.amazonaws.mobileconnectors.s3.transfermanager.Download;
import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

/**
 * Created by vinkrish.
 */
public class FirstTimeDownload implements StringConstant {
    private ProgressDialog pDialog;
    private SqlDbHelper sqlHandler;
    private String zipFile, fileName;
    private Context context;
    private int schoolId, block;
    private SQLiteDatabase sqliteDatabase;

    public FirstTimeDownload() {
        context = AppGlobal.getActivity();
        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        pDialog = new ProgressDialog(context);
    }

    private class RunFirstTimeSync extends AsyncTask<Void, String, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog.setMessage("Downloading File ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.setMax(100);
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.show();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        @Override
        protected Void doInBackground(Void... params) {
            sqlHandler.removeIndex(sqliteDatabase);
            sqlHandler.dropTrigger(sqliteDatabase);

            Temp t = TempDao.selectTemp(sqliteDatabase);
            String deviceId = t.getDeviceId();

            publishProgress("10");

            JSONObject ack_json = new JSONObject();
            try {
                ack_json.put("tab_id", deviceId);
                JSONObject jsonReceived = new JSONObject(RequestResponseHandler.reachServer(request_first_time_sync, ack_json));
                block = jsonReceived.getInt(TAG_SUCCESS);

                publishProgress("25");

                schoolId = jsonReceived.getInt("schoolId");
                TempDao.updateSchoolId(schoolId, sqliteDatabase);
                zipFile = jsonReceived.getString("folder_name");
                String s = jsonReceived.getString("file_names");
                String[] sArray = s.split(",");
                for (String split : sArray) {
                    sqlHandler.insertDownloadedFile(split);
                }
                fileName = "first_time_sync/zipped_folder/" + zipFile;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }

            if (block == 1) {
                SharedPreferenceUtil.updateTabletLock(context, 0);
                sqlHandler.deleteTable("locked", sqliteDatabase);
                //UploadSqlDao.deleteTable("locked", sqliteDatabase);
            }

            if (block != 2) {
                publishProgress("50");

                sqliteDatabase.execSQL("DROP TABLE IF EXISTS sliptestmark_" + schoolId);
                sqliteDatabase.execSQL("CREATE TABLE sliptestmark_" + schoolId + "(SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, SubjectId INTEGER, NewSubjectId INTEGER," +
                        " SlipTestId INTEGER, StudentId INTEGER, Mark TEXT, DateTimeRecordInserted DATETIME, PRIMARY KEY(SlipTestId, StudentId))");

                TransferManager mTransferManager = new TransferManager(Util.getCredProvider(context));
                DownloadModel model = new DownloadModel(context, fileName, mTransferManager);
                model.download();
            }

            return null;
        }

        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
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
                        continueSync();
                    } else if (event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
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

    private void exitSync(){
        pDialog.dismiss();
        Intent intent = new Intent(context, in.principal.activity.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    private void continueSync(){
        pDialog.dismiss();
        if (block != 2 && zipFile != "") {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new FirstTimeProcessTask(context, zipFile).execute();
                }
            }, 300);
        } else {
            Intent intent = new Intent(context, in.principal.activity.LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);
        }
    }

    public void callFirstTimeSync() {
        sqlHandler.deleteTables(sqliteDatabase);
        new RunFirstTimeSync().execute();
    }

}
