package in.principal.activity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import in.principal.sqlite.SqlDbHelper;
import in.principal.dao.ActivitiDao;
import in.principal.dao.ExmAvgDao;
import in.principal.dao.SlipTesttDao;
import in.principal.dao.StAvgDao;
import in.principal.dao.SubActivityDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.Temp;
import in.principal.activity.ProcessFiles;
import in.principal.sync.StringConstant;
import in.principal.sync.UploadSyncParser;
import in.principal.util.AppGlobal;
import in.principal.util.ExceptionHandler;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProcessFiles extends BaseActivity implements StringConstant{
	private SqlDbHelper sqlHandler;
	private SQLiteDatabase sqliteDatabase;
	private int schoolId;
	private String deviceId;
	private ProgressBar progressBar;
	private TextView txtPercentage, txtSync;
	private boolean isException = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_files);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

		txtPercentage = (TextView) findViewById(R.id.txtPercentage);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		txtSync = (TextView) findViewById(R.id.syncing);

		sqlHandler = AppGlobal.getSqlDbHelper();
		sqliteDatabase = AppGlobal.getSqliteDatabase();

		new ProcessedFiles().execute();
	}
	
	class ProcessedFiles extends AsyncTask<String, String, String>{
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
			schoolId = t.getSchoolId();
			deviceId = t.getDeviceId();

			ArrayList<String> downFileList = new ArrayList<>();
			Cursor c1 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=0", null);
			c1.moveToFirst();
			while(!c1.isAfterLast()){
				downFileList.add(c1.getString(c1.getColumnIndex("filename")));
				c1.moveToNext();
			}
			c1.close();

			Log.d("process_file_req", "...");

			int fileCount = downFileList.size();
			int fileIndex = 0;
			int queryCount = 0;
			int queryIndex = 0;
			try{
				for(String f: downFileList){
					fileIndex += 1;
					queryIndex = 0;
					queryCount = countLines(f);
					File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), f);
					BufferedReader input =  new BufferedReader(new FileReader(file));
					try {
						String line = null;
						while (( line = input.readLine()) != null){
							queryIndex += 1;
							int percent = (int)(((double)queryIndex/queryCount)*100);
							publishProgress(percent+"",percent+"","processing file "+fileIndex+" of "+fileCount);
							try{
								sqliteDatabase.execSQL(line);
							}catch(SQLException e){
								SharedPreferences sp = ProcessFiles.this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
								SharedPreferences.Editor editr = sp.edit();
								editr.putInt("tablet_lock", 1);
								editr.putInt("is_sync", 0);
								editr.putInt("sleep_sync", 0);
								editr.apply();
								String except = e+"";
								try{
									sqliteDatabase.execSQL("insert into locked(FileName,LineNumber,StackTrace) values('"+f+"',"+queryIndex+",'"+except.replaceAll("['\"]", " ")+"')");
								}catch(SQLException ex){
									e.printStackTrace();
								}
								isException = true;
							}
						}
					}finally {
						input.close();
					}
					sqliteDatabase.execSQL("update downloadedfile set processed=1 where filename='"+f+"'");
					file.delete();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
			Log.d("process_file_res", "...");

			publishProgress("100",100+"","acknowledge processes file");

			StringBuilder sb = new StringBuilder();
			Cursor c2 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=1 and isack=0", null);
			c2.moveToFirst();
			while(!c2.isAfterLast()){
				sb.append(c2.getString(c2.getColumnIndex("filename"))).append("','");
				c2.moveToNext();
			}
			c2.close();

			Log.d("ack_file_req", "...");
			if(sb.length()>3){
				try{
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("school", schoolId);
					jsonObject.put("tab_id", deviceId);
					jsonObject.put("file_name", "'"+sb.substring(0, sb.length()-3)+"'");
					jsonReceived = UploadSyncParser.makePostRequest(update_processed_file, jsonObject);
					if(jsonReceived.getInt(TAG_SUCCESS)==1){
						sqliteDatabase.execSQL("update downloadedfile set isack=1 where processed=1 and filename in ('"+sb.substring(0, sb.length()-3)+"')");
					}
				}catch(JSONException e){
					e.printStackTrace();
				}catch (ConnectException e) {
					e.printStackTrace();
				}
			}
			Log.d("ack_file_res", "...");

			publishProgress("0",0+"","calculating average");

			ArrayList<Integer> examIdList = new ArrayList<>();
			ArrayList<Integer> subjectIdList = new ArrayList<>();
			ArrayList<Integer> activityIdList = new ArrayList<>();
			ArrayList<Integer> subActIdList = new ArrayList<>();
			ArrayList<Integer> sectionIdList = new ArrayList<>();

			Cursor c3 = sqliteDatabase.rawQuery("select distinct ExamId,SubjectId from avgtrack " +
					"where Type=0 and ActivityId=0 and SubActivityId=0 and SectionId=0 and ExamId!=0 and SubjectId!=0", null);
			c3.moveToFirst();
			while(!c3.isAfterLast()){
				examIdList.add(c3.getInt(c3.getColumnIndex("ExamId")));
				subjectIdList.add(c3.getInt(c3.getColumnIndex("SubjectId")));
				c3.moveToNext();
			}
			c3.close();
			for(int i=0,j=examIdList.size(); i<j; i++){
				ExmAvgDao.insertExmAvg(examIdList.get(i), subjectIdList.get(i), sqliteDatabase);
				ExmAvgDao.checkExamMarkEmpty(examIdList.get(i), subjectIdList.get(i), sqliteDatabase);
			}
			examIdList.clear();
			subjectIdList.clear();

			Cursor c4 = sqliteDatabase.rawQuery("select distinct ExamId,SubjectId from avgtrack " +
					"where Type=1 and ActivityId=0 and SubActivityId=0 and SectionId=0 and ExamId!=0 and SubjectId!=0", null);
			c4.moveToFirst();
			while(!c4.isAfterLast()){
				examIdList.add(c4.getInt(c4.getColumnIndex("ExamId")));
				subjectIdList.add(c4.getInt(c4.getColumnIndex("SubjectId")));
				c4.moveToNext();
			}
			c4.close();
			for(int i=0,j=examIdList.size(); i<j; i++){
				ExmAvgDao.updateExmAvg(examIdList.get(i), subjectIdList.get(i), sqliteDatabase);
				ExmAvgDao.checkExamMarkEmpty(examIdList.get(i), subjectIdList.get(i), sqliteDatabase);
			}
			examIdList.clear();
			subjectIdList.clear();

			publishProgress("25",25+"","calculating average");

			Cursor c5 = sqliteDatabase.rawQuery("select distinct ExamId,ActivityId,SubjectId from avgtrack " +
					"where Type=0 and SubActivityId=0 and SectionId=0 and ExamId!=0 and ActivityId!=0 and SubjectId!=0", null);
			c5.moveToFirst();
			while(!c5.isAfterLast()){
				examIdList.add(c5.getInt(c5.getColumnIndex("ExamId")));
				activityIdList.add(c5.getInt(c5.getColumnIndex("ActivityId")));
				subjectIdList.add(c5.getInt(c5.getColumnIndex("SubjectId")));
				c5.moveToNext();
			}
			if(c5.getCount()>0){
				ActivitiDao.updateActivityAvg(activityIdList, sqliteDatabase);
				ActivitiDao.checkActivityMarkEmpty(activityIdList, sqliteDatabase);
				ExmAvgDao.insertExmActAvg(examIdList, subjectIdList, sqliteDatabase);
				ExmAvgDao.checkExmActMarkEmpty(examIdList, subjectIdList, sqliteDatabase);
			}
			c5.close();
			examIdList.clear();
			activityIdList.clear();
			subjectIdList.clear();

			Cursor c6 = sqliteDatabase.rawQuery("select distinct ExamId,ActivityId,SubjectId from avgtrack " +
					"where Type=1 and SubActivityId=0 and SectionId=0 and ExamId!=0 and ActivityId!=0 and SubjectId!=0", null);
			c6.moveToFirst();
			while(!c6.isAfterLast()){
				examIdList.add(c6.getInt(c6.getColumnIndex("ExamId")));
				activityIdList.add(c6.getInt(c6.getColumnIndex("ActivityId")));
				subjectIdList.add(c6.getInt(c6.getColumnIndex("SubjectId")));
				c6.moveToNext();
			}
			if(c6.getCount()>0){
				ActivitiDao.updateActivityAvg(activityIdList, sqliteDatabase);
				ActivitiDao.checkActivityMarkEmpty(activityIdList, sqliteDatabase);
				ExmAvgDao.updateExmActAvg(examIdList, subjectIdList, sqliteDatabase);
				ExmAvgDao.checkExmActMarkEmpty(examIdList, subjectIdList, sqliteDatabase);
			}
			c6.close();
			examIdList.clear();
			activityIdList.clear();
			subjectIdList.clear();

			publishProgress("50",50+"","calculating average");

			Cursor c7 = sqliteDatabase.rawQuery("select distinct ExamId,ActivityId,SubActivityId,SubjectId from avgtrack " +
					"where Type=0 and SectionId=0 and ExamId!=0 and ActivityId!=0 and SubActivityId!=0 and SubjectId!=0", null);
			c7.moveToFirst();
			while(!c7.isAfterLast()){
				examIdList.add(c7.getInt(c7.getColumnIndex("ExamId")));
				activityIdList.add(c7.getInt(c7.getColumnIndex("ActivityId")));
				subActIdList.add(c7.getInt(c7.getColumnIndex("SubActivityId")));
				subjectIdList.add(c7.getInt(c7.getColumnIndex("SubjectId")));
				c7.moveToNext();
			}
			if(c7.getCount()>0){
				SubActivityDao.updateSubActivityAvg(subActIdList, sqliteDatabase);
				ActivitiDao.updateActSubActAvg(activityIdList, sqliteDatabase);
				ExmAvgDao.insertExmActAvg(examIdList, subjectIdList, sqliteDatabase);
				SubActivityDao.checkSubActivityMarkEmpty(subActIdList, sqliteDatabase);
				ActivitiDao.checkActivityMarkEmpty(activityIdList, sqliteDatabase);
				ExmAvgDao.checkExmSubActMarkEmpty(examIdList,subjectIdList, sqliteDatabase);
			}
			c7.close();
			examIdList.clear();
			activityIdList.clear();
			subActIdList.clear();
			subjectIdList.clear();

			Cursor c8 = sqliteDatabase.rawQuery("select distinct ExamId,ActivityId,SubActivityId,SubjectId from avgtrack " +
					"where Type=1 and SectionId=0 and ExamId!=0 and ActivityId!=0 and SubActivityId!=0 and SubjectId!=0", null);
			c8.moveToFirst();
			while(!c8.isAfterLast()){
				examIdList.add(c8.getInt(c8.getColumnIndex("ExamId")));
				activityIdList.add(c8.getInt(c8.getColumnIndex("ActivityId")));
				subActIdList.add(c8.getInt(c8.getColumnIndex("SubActivityId")));
				subjectIdList.add(c8.getInt(c8.getColumnIndex("SubjectId")));
				c8.moveToNext();
			}
			if(c8.getCount()>0){
				SubActivityDao.updateSubActivityAvg(subActIdList, sqliteDatabase);
				ActivitiDao.updateActSubActAvg(activityIdList, sqliteDatabase);
				ExmAvgDao.updateExmActAvg(examIdList, subjectIdList, sqliteDatabase);
				SubActivityDao.checkSubActivityMarkEmpty(subActIdList, sqliteDatabase);
				ActivitiDao.checkActivityMarkEmpty(activityIdList, sqliteDatabase);
				ExmAvgDao.checkExmSubActMarkEmpty(examIdList,subjectIdList, sqliteDatabase);
			}
			c8.close();
			examIdList.clear();
			activityIdList.clear();
			subActIdList.clear();
			subjectIdList.clear();

			publishProgress("75",75+"","calculating average");

			Cursor c9 = sqliteDatabase.rawQuery("select distinct SubjectId,SectionId from avgtrack " +
					"where Type=0 and ExamId=0 and ActivityId=0 and SubActivityId=0 and SubjectId!=0 and SectionId!=0", null);
			c9.moveToFirst();
			while(!c9.isAfterLast()){
				subjectIdList.add(c9.getInt(c9.getColumnIndex("SubjectId")));
				sectionIdList.add(c9.getInt(c9.getColumnIndex("SectionId")));
				c9.moveToNext();
			}
			c9.close();
			for(int i=0,j=subjectIdList.size(); i<j; i++){
				int updatedSTAvg = SlipTesttDao.findSlipTestPercentage(sectionIdList.get(i), subjectIdList.get(i), schoolId, sqliteDatabase);
				StAvgDao.updateSlipTestAvg(sectionIdList.get(i), subjectIdList.get(i), updatedSTAvg, schoolId, sqliteDatabase);
			}
			subjectIdList.clear();
			sectionIdList.clear();

			Cursor c10 = sqliteDatabase.rawQuery("select distinct SubjectId,SectionId from avgtrack " +
					"where Type=1 and ExamId=0 and ActivityId=0 and SubActivityId=0 and SubjectId!=0 and SectionId!=0", null);
			c10.moveToFirst();
			while(!c10.isAfterLast()){
				subjectIdList.add(c10.getInt(c10.getColumnIndex("SubjectId")));
				sectionIdList.add(c10.getInt(c10.getColumnIndex("SectionId")));
				c10.moveToNext();
			}
			c10.close();
			for(int i=0,j=subjectIdList.size(); i<j; i++){
				int updatedSTAvg = SlipTesttDao.findSlipTestPercentage(sectionIdList.get(i), subjectIdList.get(i), schoolId, sqliteDatabase);
				StAvgDao.updateSlipTestAvg(sectionIdList.get(i), subjectIdList.get(i), updatedSTAvg, schoolId, sqliteDatabase);
			}
			subjectIdList.clear();
			sectionIdList.clear();

			sqlHandler.deleteTable("avgtrack", sqliteDatabase);

			return null;
		}
		protected void onPostExecute(String s){
			super.onPostExecute(s);

			SharedPreferences sharedPref = ProcessFiles.this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = sharedPref.edit();
			//	editor.putInt("tablet_lock", 0);
			editor.putInt("is_sync", 0);
			editor.putInt("sleep_sync", 0);
			editor.apply();

			if(isException){
				Intent intent = new Intent(ProcessFiles.this, in.principal.activity.LockActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}else{
				Intent intent = new Intent(ProcessFiles.this, in.principal.activity.LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
		}
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.process_files, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed(){}
}
