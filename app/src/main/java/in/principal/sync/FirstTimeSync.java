package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class FirstTimeSync implements StringConstant{
	private ProgressDialog pDialog;
	private SqlDbHelper sqlHandler;
	private String deviceId, zipFile;
	private Context context;
	private int schoolId, block;
	private SQLiteDatabase sqliteDatabase;

	public FirstTimeSync(){
		context = AppGlobal.getActivity();
		sqlHandler = AppGlobal.getSqlDbHelper();
		sqliteDatabase = AppGlobal.getSqliteDatabase();
		pDialog = new ProgressDialog(context);
	}

	private class RunFirstTimeSync extends AsyncTask<Void, String, Void>{

		protected void onPreExecute(){
			super.onPreExecute();
			pDialog.setMessage("Downloading/Processing File ...");
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
			deviceId = t.getDeviceId();		

			publishProgress("10");

			JSONObject ack_json = new JSONObject();
			try{
				ack_json.put("tab_id", deviceId);
				Log.d("get_first_files_req", "1");
				JSONObject jsonReceived = FirstTimeSyncParser.makePostRequest(request_first_time_sync, ack_json);
				block = jsonReceived.getInt(TAG_SUCCESS);
				Log.d("get_first_files_res", "1");

				publishProgress("25");

				schoolId = jsonReceived.getInt("schoolId");
				TempDao.updateSchoolId(schoolId, sqliteDatabase);
				zipFile = jsonReceived.getString("folder_name");
				String s = jsonReceived .getString("file_names");
				String[] sArray = s.split(",");
				for(String split: sArray){
					sqlHandler.insertDownloadedFile(split);
				}
			}catch(JSONException e){
				zipFile = "";
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();	
			}
			
			if(block==1){
				SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putInt("tablet_lock", 0);
				editor.apply();
				sqlHandler.deleteTable("locked", sqliteDatabase);
			}

			if(block!=2){
				publishProgress("50");

				sqliteDatabase.execSQL("DROP TABLE IF EXISTS sliptestmark_"+schoolId);
				sqliteDatabase.execSQL("CREATE TABLE sliptestmark_"+schoolId+"(SchoolId INTEGER, ClassId INTEGER, SectionId INTEGER, SubjectId INTEGER, NewSubjectId INTEGER," +
						" SlipTestId INTEGER, StudentId INTEGER, Mark TEXT, DateTimeRecordInserted DATETIME, PRIMARY KEY(SlipTestId, StudentId))");
				
			}
			return null;
		}

		protected void onPostExecute(Void v){
			super.onPostExecute(v);
			pDialog.dismiss();
			if(block!=2 && zipFile!=""){
				new DownloadModelTask(context, zipFile).execute();
			}
		}
	}

	public void callFirstTimeSync(){
		sqlHandler.deleteTables(sqliteDatabase);
		new RunFirstTimeSync().execute();
	}

}
