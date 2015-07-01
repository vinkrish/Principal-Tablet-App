package in.principal.sync;

import in.principal.dao.ActivitiDao;
import in.principal.util.AppGlobal;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class ActivityProgress {
	private Context context;
	private SQLiteDatabase sqliteDatabase;
	private ProgressDialog pDialog;

	public ActivityProgress(Context con_text){
		context = con_text;
	}

	class CalledActivityProgress extends AsyncTask<String, String, String>{

		protected void onPreExecute(){
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
			
			ActivitiDao.updateActivityAvg(sqliteDatabase);
			ActivitiDao.updateSubactActAvg(sqliteDatabase);

			ActivitiDao.checkActivityIsMark(sqliteDatabase);
		//	ActivitiDao.checkActivityMarkEmpty(sqliteDatabase);

			ActivitiDao.checkActSubActIsMark(sqliteDatabase);
		//	ActivitiDao.checkActSubActMarkEmpty(sqliteDatabase);

			return null;
		}

		protected void onPostExecute(String s){
			super.onPostExecute(s);
			pDialog.dismiss();
			new ExamProgress(context).findExmProgress();
		}
	}

	public void findActProgress(){
		new CalledActivityProgress().execute();
	}

}
