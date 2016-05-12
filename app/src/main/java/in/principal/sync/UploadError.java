package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;

import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by vinkrish.
 */
public class UploadError implements StringConstant {
    private SQLiteDatabase sqliteDatabase;
    private String errorReport;

    public UploadError(String error) {
        errorReport = error;
        sqliteDatabase = AppGlobal.getSqliteDatabase();
    }

    class CalledUploadError extends AsyncTask<String, String, String> {
        private JSONObject jsonReceived;

        @Override
        protected String doInBackground(String... arg0) {
            JSONObject json = new JSONObject();
            Temp t = TempDao.selectTemp(sqliteDatabase);
            String deviceId = t.getDeviceId();
            int schoolId = t.getSchoolId();
            try {
                json.put("school", schoolId);
                json.put("tab_id", deviceId);
                json.put("log", errorReport);
                json.put("date", getToday());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                jsonReceived = new JSONObject(RequestResponseHandler.reachServer(logged, json));
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    public void upError() {
        new CalledUploadError().execute();
    }

}
