package in.principal.sync;

import in.principal.dao.ExmAvgDao;
import in.principal.util.AppGlobal;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Created by vinkrish.
 */
public class ExamProgress {
    private Context context;
    private SQLiteDatabase sqliteDatabase;
    private ProgressDialog pDialog;

    public ExamProgress(Context con_text) {
        context = con_text;
    }

    class CalledExamProgress extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Preparing data (Exam Progress)...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            sqliteDatabase = AppGlobal.getSqliteDatabase();

            ExmAvgDao.insertExmAvg(sqliteDatabase);
            ExmAvgDao.insertExmActAvg(sqliteDatabase);
            ExmAvgDao.insertExmSubActAvg(sqliteDatabase);

            return null;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pDialog.dismiss();
            new SlipTestProgress(context).findStProgress();
        }
    }


    public void findExmProgress() {
        new CalledExamProgress().execute();
    }

}
