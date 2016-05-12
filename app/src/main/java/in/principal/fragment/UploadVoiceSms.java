package in.principal.fragment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.json.JSONException;
import org.json.JSONObject;

import com.amazonaws.mobileconnectors.s3.transfermanager.Transfer;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.model.ProgressEvent;
import com.amazonaws.services.s3.model.ProgressListener;

import in.principal.activity.R;
import in.principal.dao.SchoolDao;
import in.principal.dao.TempDao;
import in.principal.model.TransferModel;
import in.principal.sqlite.School;
import in.principal.sqlite.Temp;
import in.principal.sync.RequestResponseHandler;
import in.principal.sync.StringConstant;
import in.principal.util.AppGlobal;
import in.principal.util.Constants;
import in.principal.util.PKGenerator;
import in.principal.util.ReplaceFragment;
import in.principal.util.Util;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by vinkrish.
 * I would write this class a better way if i've to start over again, optimize it if you can.
 */
@SuppressWarnings("deprecation")
public class UploadVoiceSms extends Fragment implements StringConstant {
    private int voiceSmsKey, schoolId, principalId, s3Count;
    private String ids, deviceId;
    private MediaRecorder myRecorder;
    private MediaPlayer myPlayer;
    private String outputFile = null, messageTo;
    private Button startBtn, stopBtn, playBtn, stopPlayBtn, uploadBtn, deleteBtn;
    private TextView status;
    private EditText customName;
    private String fileName, zipName;
    private ProgressDialog progressBar;
    private SQLiteDatabase sqliteDatabase;
    private Context appContext;
    private boolean uploadComplete, exception;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.upload_voice_sms, container, false);
        appContext = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();

        Bundle b = getArguments();
        voiceSmsKey = b.getInt("key");

        if (voiceSmsKey == 0) {
            messageTo = "All Students";
            StringBuilder sb = new StringBuilder();
            Cursor c = sqliteDatabase.rawQuery("select Mobile1 from students", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getLong(c.getColumnIndex("Mobile1"))).append(",");
                c.moveToNext();
            }
            c.close();
            ids = sb.substring(0, sb.length() - 1);
        } else if (voiceSmsKey == 1) {
            messageTo = "All Teachers";
            StringBuilder sb = new StringBuilder();
            Cursor c = sqliteDatabase.rawQuery("select Mobile from teacher", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getLong(c.getColumnIndex("Mobile"))).append(",");
                c.moveToNext();
            }
            c.close();
            ids = sb.substring(0, sb.length() - 1);
        } else if (voiceSmsKey == 2) {
            messageTo = "Class";
            StringBuilder sb = new StringBuilder();
            Cursor c = sqliteDatabase.rawQuery("select Mobile1 from students where ClassId in (" + b.getString("ids") + ")", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getLong(c.getColumnIndex("Mobile1"))).append(",");
                c.moveToNext();
            }
            c.close();
            ids = sb.substring(0, sb.length() - 1);
        } else if (voiceSmsKey == 3) {
            messageTo = "Section";
            StringBuilder sb = new StringBuilder();
            Cursor c = sqliteDatabase.rawQuery("select Mobile1 from students where SectionId in (" + b.getString("ids") + ")", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getLong(c.getColumnIndex("Mobile1"))).append(",");
                c.moveToNext();
            }
            c.close();
            ids = sb.substring(0, sb.length() - 1);
        } else {
            messageTo = "Student";

            StringBuilder sb = new StringBuilder();
            Cursor c = sqliteDatabase.rawQuery("select Mobile1 from students where StudentId in (" + b.getString("ids") + ")", null);
            c.moveToFirst();
            while (!c.isAfterLast()) {
                sb.append(c.getLong(c.getColumnIndex("Mobile1"))).append(",");
                c.moveToNext();
            }
            c.close();

            ids = sb.substring(0, sb.length() - 1);
        }

        ArrayList<School> auth = SchoolDao.selectSchool(sqliteDatabase);
        for (School school : auth) {
            principalId = school.getPrincipalTeacherId();
        }

        myPlayer = new MediaPlayer();

        Button voiceSmsBtn = (Button) view.findViewById(R.id.voiceSms);
        voiceSmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReplaceFragment.replace(new VoiceSms(), getFragmentManager());
            }
        });

        customName = (EditText) view.findViewById(R.id.customName);
        status = (TextView) view.findViewById(R.id.status);

        startBtn = (Button) view.findViewById(R.id.start);
        startBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {

                File r = android.os.Environment.getExternalStorageDirectory();
                final File d = new File(r.getAbsolutePath() + "/Voice");
                d.mkdirs();

                fileName = PKGenerator.getPrimaryKey() + ".mp3";
                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice/" + fileName;
                myRecorder = new MediaRecorder();
                myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                myRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                myRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                myRecorder.setOutputFile(outputFile);
                start();

            }
        });

        stopBtn = (Button) view.findViewById(R.id.stop);
        stopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        playBtn = (Button) view.findViewById(R.id.play);
        playBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                play();
            }
        });

        stopPlayBtn = (Button) view.findViewById(R.id.stopPlay);
        stopPlayBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlay();
            }
        });

        uploadBtn = (Button) view.findViewById(R.id.upload);
        uploadBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                status.setText("Status : Uploading");
                new CalledFTPSync().execute();
            }
        });

        deleteBtn = (Button) view.findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                File delFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice/" + fileName);
                delFile.delete();
                playBtn.setEnabled(false);
                uploadBtn.setEnabled(false);
                deleteBtn.setEnabled(false);
                status.setText("Status : Deleted");
            }
        });

        return view;
    }

    public void onCompletion(MediaPlayer mp) {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                status.setText("Status : Stop playing");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        try {
            myRecorder.prepare();
            myRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        status.setText("Status : Recording");
        startBtn.setEnabled(false);
        stopBtn.setEnabled(true);
        playBtn.setEnabled(false);
        deleteBtn.setEnabled(false);
        uploadBtn.setEnabled(false);
    }

    public void stop() {
        try {
            myRecorder.stop();
            myRecorder.release();
            myRecorder = null;

            startBtn.setEnabled(true);
            stopBtn.setEnabled(false);
            playBtn.setEnabled(true);
            uploadBtn.setEnabled(true);
            deleteBtn.setEnabled(true);
            status.setText("Status : Stop recording");

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            myPlayer = new MediaPlayer();
            myPlayer.setOnCompletionListener(new OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    try {
                        if (myPlayer != null) {
                            myPlayer.stop();
                            myPlayer.release();
                            myPlayer = null;
                            playBtn.setEnabled(true);
                            startBtn.setEnabled(true);
                            deleteBtn.setEnabled(true);
                            stopPlayBtn.setEnabled(false);
                            status.setText("Status : Stop playing");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            myPlayer.setDataSource(outputFile);
            myPlayer.prepare();
            myPlayer.start();

            playBtn.setEnabled(false);
            stopPlayBtn.setEnabled(true);
            status.setText("Status : Playing");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopPlay() {
        try {
            if (myPlayer != null) {
                myPlayer.stop();
                myPlayer.release();
                myPlayer = null;
                playBtn.setEnabled(true);
                startBtn.setEnabled(true);
                stopPlayBtn.setEnabled(false);
                deleteBtn.setEnabled(true);
                status.setText("Status : Stop playing");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CalledFTPSync extends AsyncTask<String, Integer, String> {
        private JSONObject jsonReceived;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new ProgressDialog(UploadVoiceSms.this.getActivity());
            progressBar.setCancelable(false);
            progressBar.setMessage("Sending voice message ...");
            progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressBar.setProgress(0);
            progressBar.setMax(100);
            progressBar.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        protected String doInBackground(String... arg0) {
            TransferManager mTransferManager = new TransferManager(Util.getCredProvider(appContext));
            uploadComplete = false;
            exception = false;
            s3Count = 1;

            publishProgress(20);
            createUploadFile();
            publishProgress(40);

            File rooted = android.os.Environment.getExternalStorageDirectory();
            File direc = new File(rooted.getAbsolutePath() + "/Upload");
            File file = new File(direc, zipName);
            UploadModel model = new UploadModel(appContext, "upload/zipped_folder/" + zipName, mTransferManager);
            model.upload();

            while (!uploadComplete) {
                Log.d("upload", "...");
            }

            publishProgress(60);
            if (!exception) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("school", schoolId);
                    jsonObject.put("tab_id", deviceId);
                    jsonObject.put("file_name", zipName.substring(0, zipName.length() - 3) + "sql");
                    jsonReceived = new JSONObject(RequestResponseHandler.reachServer(acknowledge_uploaded_file, jsonObject));
                    if (jsonReceived.getInt(TAG_SUCCESS) == 1) {
                        file.delete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            s3Count = 2;
            //	File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Voice");
            zipName = fileName;
            //	final File newFile = new File(dir, fileName);

            uploadComplete = false;
            UploadModel model2 = new UploadModel(appContext, "voicecall/" + zipName, mTransferManager);
            model2.upload();
            publishProgress(80);
            while (!uploadComplete) {
                Log.d("upload", "...");
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            status.setText("Status : Uploaded");
            progressBar.dismiss();
            uploadBtn.setEnabled(false);
            ReplaceFragment.clearBackStack(getFragmentManager());
            ReplaceFragment.replace(new Dashbord(), getFragmentManager());
        }
    }

    private void createUploadFile() {
        Temp t = TempDao.selectTemp(sqliteDatabase);
        deviceId = t.getDeviceId();
        schoolId = t.getSchoolId();
        long timeStamp = PKGenerator.getPrimaryKey();
        zipName = timeStamp + "_" + deviceId + "_" + schoolId + ".zip";
        File root = android.os.Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Upload");
        dir.mkdirs();
        File file = new File(dir, timeStamp + "_" + deviceId + "_" + schoolId + ".sql");
        file.delete();
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write("insert into voicecalls(SchoolId, Phone, file_path, Role, UserId) values(" + schoolId + ",'" + ids + "','upload\\" + fileName + "','Principal'," + principalId + ");");
            writer.close();
            FileOutputStream fileOutputStream = new FileOutputStream(new File(dir, zipName));
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);
            ZipEntry zipEntry = new ZipEntry(file.getName());
            zipOutputStream.putNextEntry(zipEntry);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buf)) > 0) {
                zipOutputStream.write(buf, 0, bytesRead);
            }
            fileInputStream.close();
            zipOutputStream.closeEntry();
            zipOutputStream.close();
            fileOutputStream.close();
            file.delete();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
    }

    public class UploadModel extends TransferModel {
        private String fileNam;
        private Upload mUpload;
        private ProgressListener mListener;
        private Status mStatus;

        public UploadModel(Context context, String key, TransferManager manager) {
            super(context, Uri.parse(key), manager);
            fileNam = key;
            mStatus = Status.IN_PROGRESS;
            mListener = new ProgressListener() {
                @Override
                public void progressChanged(ProgressEvent event) {
                    if (event.getEventCode() == ProgressEvent.COMPLETED_EVENT_CODE) {
                        mStatus = Status.COMPLETED;
                        Log.d("upload", "complete");
                        uploadComplete = true;
                    } else if (event.getEventCode() == ProgressEvent.FAILED_EVENT_CODE) {
                        exception = true;
                        uploadComplete = true;
                    } else if (event.getEventCode() == ProgressEvent.CANCELED_EVENT_CODE) {
                        exception = true;
                        uploadComplete = true;
                    }
                }
            };
        }

        @Override
        public Status getStatus() {
            return mStatus;
        }

        @Override
        public Transfer getTransfer() {
            return mUpload;
        }

        public void upload() {
            try {
                if (s3Count == 1) {
                    File root = android.os.Environment.getExternalStorageDirectory();
                    File dir = new File(root.getAbsolutePath() + "/Upload");
                    File file = new File(dir, zipName);
                    mUpload = getTransferManager().upload(
                            Constants.BUCKET_NAME.toLowerCase(Locale.US), fileNam, file);
                    mUpload.addProgressListener(mListener);
                } else {
                    File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Voice");
                    File file = new File(dir, zipName);
                    mUpload = getTransferManager().upload(
                            Constants.BUCKET_NAME.toLowerCase(Locale.US), fileNam, file);
                    mUpload.addProgressListener(mListener);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void abort() {
        }

        @Override
        public void pause() {
        }

        @Override
        public void resume() {
        }
    }

}
