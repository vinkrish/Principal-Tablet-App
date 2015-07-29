package in.principal.activity;

import java.io.IOException;
import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import in.principal.sqlite.Temp;
import in.principal.activity.R;
import in.principal.activity.LockActivity;
import in.principal.dao.TempDao;
import in.principal.sync.FirstTimeSync;
import in.principal.sync.FirstTimeSyncParser;
import in.principal.sync.StringConstant;
import in.principal.sync.UploadSyncParser;
import in.principal.util.AppGlobal;
import in.principal.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

public class LockActivity extends BaseActivity implements StringConstant{
	private ProgressDialog pDialog;
	private Button butSend, butRefresh;
	private SQLiteDatabase sqliteDatabase;
	private String deviceId, fileName, stackTrace;
	private Temp t;
	private JSONObject jsonReceived;
	private int lineNumber, syncSent, isSent, schoolId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		butSend = (Button)findViewById(R.id.sendLocked);
		butRefresh = (Button)findViewById(R.id.refreshLocked);

		sqliteDatabase = AppGlobal.getSqliteDatabase();
		pDialog = new ProgressDialog(this);

		t = TempDao.selectTemp(sqliteDatabase);
		schoolId = t.getSchoolId();
		deviceId = t.getDeviceId();

		Cursor c = sqliteDatabase.rawQuery("select * from locked", null);
		c.moveToFirst();
		while(!c.isAfterLast()){
			fileName = c.getString(c.getColumnIndex("FileName"));
			lineNumber = c.getInt(c.getColumnIndex("LineNumber"));
			isSent = c.getInt(c.getColumnIndex("IsSent"));
			stackTrace = c.getString(c.getColumnIndex("StackTrace"));
			c.moveToNext();
		}
		c.close();
		
		if(isSent==0){
			butRefresh.setVisibility(View.GONE);
			butSend.setVisibility(View.VISIBLE);
		}else{
			butSend.setVisibility(View.GONE);
			butRefresh.setVisibility(View.VISIBLE);
			SharedPreferenceUtil.updateFirstSync(this, 1);
			new FirstTimeSync().callFirstTimeSync();
		}
	}
	
	public void sendClicked(View view){
		new SendLocked().execute();
	}

	public void refreshClicked(View view){
        SharedPreferenceUtil.updateFirstSync(this, 1);
		new FirstTimeSync().callFirstTimeSync();
	}
	
	class SendLocked extends AsyncTask<String, String, String>{
		protected void onPreExecute(){
			super.onPreExecute();
			pDialog.setMessage("Sending crash report...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			JSONObject jsonObject = new JSONObject();
			try{
				jsonObject.put("filename", fileName);
				jsonObject.put("school", schoolId);
				jsonObject.put("tab_id", deviceId);
				jsonObject.put("line_number", lineNumber);
				jsonReceived = FirstTimeSyncParser.makePostRequest(block_a_tab, jsonObject);
				syncSent = jsonReceived.getInt(TAG_SUCCESS);
			}catch(JSONException e){
				e.printStackTrace();
			}catch(IOException e){
				e.printStackTrace();
			}

			JSONObject json = new JSONObject();
			try{
				json.put("school", schoolId);
				json.put("tab_id", deviceId);
				json.put("log", stackTrace);
				json.put("date", getToday());
				jsonReceived = UploadSyncParser.makePostRequest(logged, json);
			}catch (JSONException e1) {
				e1.printStackTrace();
			}catch (ConnectException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(String s){
			super.onPostExecute(s);
			pDialog.dismiss();
			if(syncSent == 1){
				butSend.setVisibility(View.GONE);
				butRefresh.setVisibility(View.VISIBLE);
				sqliteDatabase.execSQL("update locked set IsSent=1");
			}
		}
	}

	private String getToday() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date today = new Date();
		return dateFormat.format(today);
	}
	
	@Override
	public void onBackPressed(){}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
        SharedPreferenceUtil.updateFirstSync(this, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.lock, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
}
