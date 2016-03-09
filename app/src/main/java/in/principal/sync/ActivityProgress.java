package in.principal.sync;

import in.principal.dao.ActivitiDao;
import in.principal.util.AppGlobal;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by vinkrish.
 */
public class ActivityProgress {
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private ProgressDialog pDialog;

    public ActivityProgress(Context con_text) {
        context = con_text;
    }

    class CalledActivityProgress extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Preparing data (Activity Progress)...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            sqliteDatabase = AppGlobal.getSqliteDatabase();

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            new ExamProgress(context).findExmProgress();
        }
    }

    public void findActProgress() {
        new CalledActivityProgress().execute();
    }

}
