package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.sqlite.SqlDbHelper;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;

import java.io.IOException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.util.Log;

public class CallFTP implements StringConstant{
	private SqlDbHelper sqlHandler;
	private SQLiteDatabase sqliteDatabase;
	private Context context;
	private int block;
	private String zipFile;

	public CallFTP(){
		context = AppGlobal.getActivity();
		sqlHandler = AppGlobal.getSqlDbHelper();
		sqliteDatabase = AppGlobal.getSqliteDatabase();
	}

	class CalledFTPSync extends AsyncTask<String, String, String>{
		private JSONObject jsonReceived;
		@Override
		protected String doInBackground(String... arg0) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.getApplicationContext().registerReceiver(null, ifilter);
			int batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

			JSONObject ack_json = new JSONObject();
			Temp t = TempDao.selectTemp(sqliteDatabase);
			int schoolId = t.getSchoolId();
			String deviceId = t.getDeviceId();
			try{
				ack_json.put("school", schoolId);
				ack_json.put("tab_id", deviceId);
				ack_json.put("battery_status", batteryLevel);
				Log.d("get_file_req", "1");
				jsonReceived = UploadSyncParser.makePostRequest(ask_for_download_file, ack_json);
				Log.d("get_file_res", "1");
				block = jsonReceived.getInt(TAG_SUCCESS);
				zipFile = jsonReceived.getString("folder_name");
				String s = jsonReceived .getString("files");
				String[] sArray = s.split(",");
				for(String split: sArray){
					TempDao.updateSyncTimer(sqliteDatabase);
					sqlHandler.insertDownloadedFile(split);
				}
			}catch(JSONException e){
				zipFile = "";
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String s){
			super.onPostExecute(s);
			SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			if(block!=2 && zipFile!=""){
				new IntermediateDownloadTask(context, zipFile).execute();
			}else if(block==2){
				editor.putInt("tablet_lock", 2);
				editor.apply();
			}
		}
	}	

	public void syncFTP(){
		new CalledFTPSync().execute();
	}

}