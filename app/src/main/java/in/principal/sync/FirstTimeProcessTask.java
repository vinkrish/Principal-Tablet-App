package in.principal.sync;

import in.principal.dao.TempDao;
import in.principal.sqlite.Temp;
import in.principal.util.AppGlobal;
import in.principal.util.SharedPreferenceUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

/**
 * Created by vinkrish.
 */

@SuppressWarnings("deprecation")
public class FirstTimeProcessTask extends AsyncTask<String, String, String> implements StringConstant {
    private Context context;
    private ProgressDialog pDialog;
    private String zipFile;

    public FirstTimeProcessTask(Context context, String fileName) {
        zipFile = fileName;
        this.context = context;
        pDialog = new ProgressDialog(context);
    }

    protected void onPreExecute() {
        super.onPreExecute();
        pDialog.setMessage("Processing File ...");
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
    protected String doInBackground(String... params) {
        SQLiteDatabase sqliteDatabase = AppGlobal.getSqliteDatabase();

        String savedVersion = SharedPreferenceUtil.getSavedVersion(context);

        publishProgress("75");
        Temp t = TempDao.selectTemp(sqliteDatabase);
        String deviceId = t.getDeviceId();
        int schoolId = t.getSchoolId();

        unZipIt(zipFile);

        ArrayList<String> downFileList2 = new ArrayList<>();
        Cursor c3 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=0", null);
        c3.moveToFirst();
        while (!c3.isAfterLast()) {
            downFileList2.add(c3.getString(c3.getColumnIndex("filename")));
            c3.moveToNext();
        }
        c3.close();

        Log.d("process_file_req", "...");

        int queryCount = 0;
        int queryIndex = 0;
        for (String f : downFileList2) {
            sqliteDatabase.execSQL("update downloadedfile set downloaded=1 where filename='" + f + "'");
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("school", schoolId);
                jsonObject.put("tab_id", deviceId);
                jsonObject.put("file_name", "'" + f + "'");
                JSONObject jsonReceived = new JSONObject(RequestResponseHandler.reachServer(update_downloaded_file, jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            queryIndex = 0;
            try {
                queryCount = countLines(f);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), f);
                BufferedReader input = new BufferedReader(new FileReader(file));
                try {
                    String line = null;
                    while ((line = input.readLine()) != null) {
                        queryIndex += 1;
                        int percent = (int) (((double) queryIndex / queryCount) * 100);
                        publishProgress(percent + "");
                        try {
                            if (!line.trim().equals("")) {
                                sqliteDatabase.execSQL(line);
                            }
                        } catch (SQLException e) {
                                /*SharedPreferences sp = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editr = sp.edit();
							editr.putInt("tablet_lock", 1);
							editr.putInt("is_sync", 0);
							editr.putInt("sleep_sync", 0);
							editr.putInt("first_sync", 0);
							editr.apply();
							try{
								sqliteDatabase.execSQL("insert into locked(FileName,LineNumber,StackTrack) values('"+f+"',"+queryIndex+","+e+")");
							}catch(SQLException ex){}
							Intent intent = new Intent();
							intent.setClassName("in.principal", "in.principal.LockActivity");
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
							context.startActivity(intent);*/
                        }
                    }
                } finally {
                    input.close();
                }
                sqliteDatabase.execSQL("update downloadedfile set processed=1 where filename='" + f + "'");
                file.delete();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        Log.d("process_file_res", "...");

        publishProgress("100", 100 + "", "acknowledge processes file");

        ArrayList<String> isAckList = new ArrayList<>();
        Cursor c4 = sqliteDatabase.rawQuery("select filename from downloadedfile where processed=1 and isack=0", null);
        c4.moveToFirst();
        while (!c4.isAfterLast()) {
            isAckList.add(c4.getString(c4.getColumnIndex("filename")));
            c4.moveToNext();
        }
        c4.close();

        Log.d("ack_file_req", "...");
        if (isAckList.size() > 0) {
            for (String s : isAckList) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("school", schoolId);
                    jsonObject.put("tab_id", deviceId);
                    jsonObject.put("file_name", "'" + s + "'");
                    jsonObject.put("version", savedVersion);
                    JSONObject jsonReceived = new JSONObject(RequestResponseHandler.reachServer(update_processed_file, jsonObject));
                    if (jsonReceived.getInt(TAG_SUCCESS) == 1) {
                        sqliteDatabase.execSQL("update downloadedfile set isack=1 where processed=1 and filename='" + s + "'");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pDialog.dismiss();

        int tabletLock = SharedPreferenceUtil.getTabletLock(context);
        if (tabletLock == 1) {
            SharedPreferenceUtil.updateFirstSync(context, 0);
        } else {
            new SlipTestProgress(context).findStProgress();
            //new SubActivityProgress(context).findSubActProgress();
        }
    }

    public void unZipIt(String zipFile) {
        byte[] buffer = new byte[1024];
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath());
        try {
            if (!dir.exists()) {
                dir.mkdir();
            }

            ZipInputStream zis = new ZipInputStream(new FileInputStream(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), zipFile)));
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {
                String fileNam = ze.getName();
                File newFile = new File(dir + File.separator + fileNam);

                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();

            File zip = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), zipFile);
            zip.delete();

        } catch (IOException ex) {
            ex.printStackTrace();
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

}
