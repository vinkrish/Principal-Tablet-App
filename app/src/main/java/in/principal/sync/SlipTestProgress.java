package in.principal.sync;

import in.principal.dao.SchoolDao;
import in.principal.dao.SlipTesttDao;
import in.principal.dao.SubjectTeachersDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.SubjectTeacher;
import in.principal.util.AppGlobal;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class SlipTestProgress {
	private Context context;
	private SqlDbHelper sqlHandler;
	private SQLiteDatabase sqliteDatabase;
	private ProgressDialog pDialog;

	public SlipTestProgress(Context con_text){
		context = con_text;
	}

	class CalledStProgress extends AsyncTask<String, String, String>{
		private int avg;

		protected void onPreExecute(){
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Preparing data (SlipTest Progress)...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			sqlHandler = AppGlobal.getSqlDbHelper();
			sqliteDatabase = AppGlobal.getSqliteDatabase();
			
			List<SubjectTeacher> stList = SubjectTeachersDao.selectSubjectTeacher(sqliteDatabase);
			for(SubjectTeacher st: stList){
				avg = SlipTesttDao.findSlipTestPercentage(st.getSectionId(), st.getSubjectId(), st.getSchoolId(), sqliteDatabase);
				sqlHandler.initStAvg(st.getClassId(), st.getSectionId(), st.getSubjectId(), avg);
			}
			return null;
		}

		protected void onPostExecute(String s){
			super.onPostExecute(s);

			SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt("tablet_lock", 0);
			editor.putInt("is_sync", 0);
			editor.putInt("first_sync", 0);
			editor.putInt("sleep_sync", 0);
			editor.apply();
			TempDao.updateSyncComplete(sqliteDatabase);
			int schoolId = SchoolDao.getSchoolId(sqliteDatabase);
			sqlHandler.deleteLocked(sqliteDatabase);
			sqlHandler.createIndex(sqliteDatabase);
			sqlHandler.createTrigger(schoolId, sqliteDatabase);	
			pDialog.dismiss();
			
			Intent intent = new Intent(context, in.principal.adapter.SyncService.class);
			context.startService(intent);

			Intent i = new Intent(context, in.principal.activity.LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}
	}

	public void findStProgress(){
		new CalledStProgress().execute();
	}

}
