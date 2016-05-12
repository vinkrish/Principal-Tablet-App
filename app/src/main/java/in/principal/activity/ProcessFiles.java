package in.principal.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import in.principal.dao.SlipTesttDao;
import in.principal.dao.StAvgDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.sync.FirstTimeDownload;
import in.principal.sync.RequestResponseHandler;
import in.principal.sync.StringConstant;
import in.principal.util.AppGlobal;
import in.principal.util.ExceptionHandler;

/**
 * Created by vinkrish.
 * This needs to be optimized, good luck with that.
 */
public class ProcessFiles extends BaseActivity implements StringConstant {
    private SqlDbHelper sqlHandler;
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private String savedVersion;
    private ProgressBar progressBar;
    private TextView txtPercentage, txtSync;
    private boolean isException = false, isFirstTimeSync = false;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_process_files);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        //registerReceiver(broadcastReceiver, new IntentFilter("in.vinkrish.networkChange"));

        txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtSync = (TextView) findViewById(R.id.syncing);

        sqlHandler = AppGlobal.getSqlDbHelper();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        context = AppGlobal.getContext();

        sharedPref = getSharedPreferences("db_access", Context.MODE_PRIVATE);
        savedVersion = sharedPref.getString("saved_version", "v1.3");

        new ProcessedFiles().execute();
    }

    public int countLines(String filename) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename)));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }

    @Override
    public void onBackPressed() {
    }

    class ProcessedFiles extends AsyncTask<String, String, String> {
        private JSONObject jsonReceived;

        @Override
        protected void onProgressUpdate(String... progress) {
            txtPercentage.setText(progress[0] + "%");
            progressBar.setProgress(Integer.parseInt(progress[1]));
            txtSync.setText(progress[2]);
        }

        @Override
        protected String doInBackground(String... params) {
            Temp t = TempDao.selectTemp(sqliteDatabase);
            int schoolId = t.getSchoolId();
            String deviceId = t.getDeviceId();

            isFirstTimeSync = false;

            ArrayList<String> downFileList = new ArrayList<>();
            Cursor c1 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=0 and downloaded=1", null);
            c1.moveToFirst();
            while (!c1.isAfterLast()) {
                downFileList.add(c1.getString(c1.getColumnIndex("filename")));
                c1.moveToNext();
            }
            c1.close();

            int fileCount = downFileList.size();
            int fileIndex = 0;
            int queryCount = 0;
            int queryIndex = 0;
            try {
                for (String f : downFileList) {
                    fileIndex += 1;
                    queryIndex = 0;
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), f);
                    queryCount = countLines(f);
                    BufferedReader input = new BufferedReader(new FileReader(file));
                    try {
                        String line = null;
                        while ((line = input.readLine()) != null) {
                            queryIndex += 1;
                            int percent = (int) (((double) queryIndex / queryCount) * 100);
                            publishProgress(percent + "", percent + "", "processing file " + fileIndex + " of " + fileCount);
                            try {
                                sqliteDatabase.execSQL(line);
                            } catch (SQLException e) {
                                SharedPreferences sp = ProcessFiles.this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editr = sp.edit();
                                editr.putInt("tablet_lock", 1);
                                editr.apply();
                                String except = e + "";
                                try {
                                    sqliteDatabase.execSQL("insert into locked(FileName,LineNumber,StackTrace) values('" + f + "'," + queryIndex + ",'" + except.replaceAll("['\"]", " ") + "')");
                                } catch (SQLException ex) {
                                    e.printStackTrace();
                                }
                                isException = true;
                            }
                        }
                    } finally {
                        input.close();
                    }
                    sqliteDatabase.execSQL("update downloadedfile set processed=1 where filename='" + f + "'");
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
                isFirstTimeSync = true;
            }

            publishProgress("50", 50 + "", "acknowledge processed file");

            StringBuilder sb = new StringBuilder();
            Cursor c2 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=1 and isack=0", null);
            c2.moveToFirst();
            while (!c2.isAfterLast()) {
                sb.append(c2.getString(c2.getColumnIndex("filename"))).append("','");
                c2.moveToNext();
            }
            c2.close();

            if (!isFirstTimeSync) {
                if (sb.length() > 3) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("school", schoolId);
                        jsonObject.put("tab_id", deviceId);
                        jsonObject.put("file_name", "'" + sb.substring(0, sb.length() - 3) + "'");
                        jsonObject.put("version", savedVersion);
                        jsonReceived = new JSONObject(RequestResponseHandler.reachServer(update_processed_file, jsonObject));
                        if (jsonReceived.getInt(TAG_SUCCESS) == 1) {
                            sqliteDatabase.execSQL("update downloadedfile set isack=1 where processed=1 and filename in ('" + sb.substring(0, sb.length() - 3) + "')");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            publishProgress("0", 0 + "", "calculating average");

            ArrayList<Integer> subjectIdList = new ArrayList<>();
            ArrayList<Integer> sectionIdList = new ArrayList<>();


            publishProgress("60", 60 + "", "calculating average");

            Cursor c9 = sqliteDatabase.rawQuery("select distinct SubjectId,SectionId from avgtrack " +
                    "where Type=0 and ExamId=0 and ActivityId=0 and SubActivityId=0 and SubjectId!=0 and SectionId!=0", null);
            c9.moveToFirst();
            while (!c9.isAfterLast()) {
                subjectIdList.add(c9.getInt(c9.getColumnIndex("SubjectId")));
                sectionIdList.add(c9.getInt(c9.getColumnIndex("SectionId")));
                c9.moveToNext();
            }
            c9.close();
            for (int i = 0, j = subjectIdList.size(); i < j; i++) {
                double updatedSTAvg = SlipTesttDao.findSlipTestPercentage(sectionIdList.get(i), subjectIdList.get(i), sqliteDatabase);
                StAvgDao.updateSlipTestAvg(sectionIdList.get(i), subjectIdList.get(i), updatedSTAvg, sqliteDatabase);
            }
            subjectIdList.clear();
            sectionIdList.clear();

            Cursor c10 = sqliteDatabase.rawQuery("select distinct SubjectId,SectionId from avgtrack " +
                    "where Type=1 and ExamId=0 and ActivityId=0 and SubActivityId=0 and SubjectId!=0 and SectionId!=0", null);
            c10.moveToFirst();
            while (!c10.isAfterLast()) {
                subjectIdList.add(c10.getInt(c10.getColumnIndex("SubjectId")));
                sectionIdList.add(c10.getInt(c10.getColumnIndex("SectionId")));
                c10.moveToNext();
            }
            c10.close();
            for (int i = 0, j = subjectIdList.size(); i < j; i++) {
                double updatedSTAvg = SlipTesttDao.findSlipTestPercentage(sectionIdList.get(i), subjectIdList.get(i), sqliteDatabase);
                StAvgDao.updateSlipTestAvg(sectionIdList.get(i), subjectIdList.get(i), updatedSTAvg, sqliteDatabase);
            }
            subjectIdList.clear();
            sectionIdList.clear();

            publishProgress("80", 80 + "", "calculating average");

            sqlHandler.deleteTable("avgtrack", sqliteDatabase);

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("sleep_sync", 0);
            editor.apply();
            if (isException) {
                editor.apply();
                Intent intent = new Intent(context, in.principal.activity.LockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } else if (isFirstTimeSync) {
                editor.putInt("first_sync", 1);
                editor.apply();
                new FirstTimeDownload().callFirstTimeSync();
            } else {
                Intent intent = new Intent(context, in.principal.activity.LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }
    }

}
