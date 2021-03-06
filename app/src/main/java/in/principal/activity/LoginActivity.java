package in.principal.activity;

import android.animation.ObjectAnimator;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings.Secure;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import in.principal.dao.SchoolDao;
import in.principal.dao.TeacherDao;
import in.principal.dao.TempDao;
import in.principal.sqlite.School;
import in.principal.sqlite.Temp;
import in.principal.sync.SyncIntentService;
import in.principal.util.AnimationUtils;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.ExceptionHandler;
import in.principal.util.NetworkUtils;
import in.principal.util.SharedPreferenceUtil;

/**
 * Created by vinkrish.
 * I would write this class a better way if i've to start over again, optimize it if you can.
 */
public class LoginActivity extends BaseActivity {
    private SQLiteDatabase sqliteDatabase;
    private Context context;
    private boolean tvflag, authflag;
    private String syncTimed, passwordText;
    private TextView userName, password;
    private int length;
    private String savedId, savedPassword;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        context = AppGlobal.getContext();
        sqliteDatabase = AppGlobal.getSqliteDatabase();
        sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);

        registerBroadcastReceiver();

        SharedPreferenceUtil.updateSavedVersion(this);

        int tabletLock = sharedPref.getInt("tablet_lock", 0);
        if (tabletLock == 0) {
            initView();
        } else if (tabletLock == 1) {
            Intent i = new Intent(this, in.principal.activity.LockActivity.class);
            startActivity(i);
        } else if (tabletLock == 2) {
            Intent i = new Intent(this, in.principal.activity.ServerBlock.class);
            startActivity(i);
        }

        if (!TeacherDao.isTeacherPresent(sqliteDatabase)) {
            SharedPreferences.Editor editr = sharedPref.edit();
            editr.putInt("newly_updated", 1);
            editr.apply();
        }

        int apkUpdate = sharedPref.getInt("apk_update", 0);
        int newlyUpdated = sharedPref.getInt("newly_updated", 0);
        if (apkUpdate == 1) {
            Intent i = new Intent(this, in.principal.activity.UpdateApk.class);
            startActivity(i);
            AnimationUtils.activityEnter(this);
        } else if (newlyUpdated == 1) {
            Intent i = new Intent(this, in.principal.activity.MasterAuthentication.class);
            startActivity(i);
        }

        int bootSync = sharedPref.getInt("boot_sync", 0);
        if (bootSync == 1) {
            Intent service = new Intent(this, in.principal.adapter.SyncService.class);
            startService(service);
            SharedPreferenceUtil.updateBootSync(this, 0);
        }
    }

    private void initView() {
        int isSync = sharedPref.getInt("is_sync", 0);
        if (isSync == 0) {

            findViewById(R.id.admin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(LoginActivity.this, in.principal.activity.MasterAuthentication.class);
                    startActivity(i);
                    AnimationUtils.activityEnter(LoginActivity.this);
                }
            });

            findViewById(R.id.admin).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    View image = findViewById(R.id.admin);
                    ObjectAnimator anim
                            = ObjectAnimator.ofFloat(image, "alpha",
                            1.0f, 0.25f, 0.75f, 0.5f, 0.25f, 1.0f);
                    anim.setDuration(4000);
                    anim.start();
                    return true;
                }
            });

            String android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);

            TempDao.updateDeviceId(android_id, sqliteDatabase);

            TextView timeSync = (TextView) findViewById(R.id.syncTime);
            userName = (TextView) findViewById(R.id.userName);
            password = (TextView) findViewById(R.id.password);

            Temp temp = TempDao.selectTemp(sqliteDatabase);
            syncTimed = temp.getSyncTime();

            timeSync.setText(syncTimed);

            ArrayList<School> auth = SchoolDao.selectSchool(sqliteDatabase);
            for (School school : auth) {
                length = String.valueOf(school.getPrincipalTeacherId()).length();
                savedId = school.getPrincipalTeacherId() + "";
                savedPassword = school.getPrincipalTeacherId() + "";
            }

            int[] buttonIds = {R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.num0};
            for (int i = 0; i < 10; i++) {
                Button b = (Button) findViewById(buttonIds[i]);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (userName.getText().toString().equals("Username")) {
                            userName.setText("");
                        } else if (password.getText().toString().equals("Password") && tvflag) {
                            password.setText("");
                        }
                        if (userName.getText().toString().equalsIgnoreCase("|")) {
                            userName.setText("");
                            password.setHint("Password");
                        } else if (password.getText().toString().equalsIgnoreCase("|")) {
                            passwordText = "";
                            password.setText("");
                        }

                        if (v.getId() == R.id.num1) updateFields("1");
                        else if (v.getId() == R.id.num2) updateFields("2");
                        else if (v.getId() == R.id.num3) updateFields("3");
                        else if (v.getId() == R.id.num4) updateFields("4");
                        else if (v.getId() == R.id.num5) updateFields("5");
                        else if (v.getId() == R.id.num6) updateFields("6");
                        else if (v.getId() == R.id.num7) updateFields("7");
                        else if (v.getId() == R.id.num8) updateFields("8");
                        else if (v.getId() == R.id.num9) updateFields("9");
                        else if (v.getId() == R.id.num0) updateFields("0");
                    }
                });
            }
            findViewById(R.id.numclear).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (tvflag) {
                        password.setText("|");
                        if (userName.getText().toString().equalsIgnoreCase("|")) {
                            userName.setText("");
                            userName.setHint("Username");
                        }
                    } else {
                        userName.setText("|");
                        if (password.getText().toString().equalsIgnoreCase("|")) {
                            password.setText("");
                            password.setHint("Password");
                        }
                    }
                }
            });
        }
    }

    public void syncClicked(View v) {
        if (NetworkUtils.isNetworkConnected(context)) {
            Intent syncService = new Intent(context, SyncIntentService.class);
            context.startService(syncService);
        } else {
            CommonDialogUtils.displayAlertWhiteDialog(this, "Please check the internet connection");
        }
    }

    public void usernameClicked(View v) {
        if (password.getText().toString().equalsIgnoreCase("|")) {
            password.setText("");
            password.setHint("Password");
        }
        userName.setText("|");
        tvflag = false;
    }

    public void passwordClicked(View v) {
        if (userName.getText().toString().equalsIgnoreCase("|")) {
            userName.setText("");
            userName.setHint("Username");
        }
        password.setText("|");
        tvflag = true;
    }

    private void updateFields(String value) {
        if (tvflag) {
            password.setText(new StringBuffer(password.getText()).append("*"));
            String sb = passwordText + value;
            passwordText = sb;
            if (passwordText.length() == length)
                authenticate();
        } else {
            String s = userName.getText().toString();
            StringBuffer sb = new StringBuffer(s);
            sb.append(value);
            userName.setText(sb);
            String s2 = userName.getText().toString();
            if (s2.length() == length)
                preauthenticate();
        }
    }

    private void preauthenticate() {
        password.setText("|");
        tvflag = true;
    }

    private void authenticate() {
        String s = userName.getText().toString();
        if (s.isEmpty()) {
            authflag = false;
        } else {
            String enteredId = userName.getText().toString();
            if (enteredId.equals(savedId) && passwordText.equals(savedPassword)) {
                authflag = true;
                authSuccess();
            }
        }
        if (!authflag) {
            CommonDialogUtils.displayAlertWhiteDialog(this, "User is not Authenticated");
        }
        userName.setText("Username");
        password.setText("Password");
        tvflag = false;
    }

    private void authSuccess() {
        String csvSplitBy = "-";
        String line = getToday();
        String[] data = line.split(csvSplitBy);
        int year = Integer.parseInt(data[0]);
        int month = Integer.parseInt(data[1]) - 1;
        int day = Integer.parseInt(data[2]);
        TempDao.setThreeAbsDays(year, month, day, sqliteDatabase);

        Intent intentt = new Intent(this, in.principal.activity.Dashboard.class);
        startActivity(intentt);
        AnimationUtils.activityEnterVertical(LoginActivity.this);
    }

    private String getToday() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date today = new Date();
        return dateFormat.format(today);
    }

    BroadcastReceiver screenOnOffReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String strAction = intent.getAction();

            KeyguardManager myKM = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);

            if (strAction.equals(Intent.ACTION_SCREEN_OFF) || strAction.equals(Intent.ACTION_SCREEN_ON)) {
                if (myKM.inKeyguardRestrictedInputMode()) {
                    SharedPreferences pref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
                    int is_first_sync = pref.getInt("first_sync", 0);
                    int tabletLock = pref.getInt("tablet_lock", 0);

                    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                    boolean isScreen = pm.isScreenOn();

                    if (NetworkUtils.isNetworkConnected(context) &&
                            !isScreen &&
                            is_first_sync == 0 &&
                            tabletLock == 0) {
                        Intent intentProcess = new Intent(context, in.principal.activity.ProcessFiles.class);
                        intentProcess.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intentProcess);
                    }
                    //System.out.println("Screen off " + "LOCKED");
                } else {
                    //System.out.println("Screen off " + "UNLOCKED");
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerBroadcastReceiver();
        super.onResume();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(screenOnOffReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        File sd = Environment.getExternalStorageDirectory();
        File data = Environment.getDataDirectory();
        FileChannel source = null;
        FileChannel destination = null;
        String currentDBPath = "/data/" + "in.principal.activity" + "/databases/principal.db";
        String backupDBPath = "principal";
        File currentDB = new File(data, currentDBPath);
        File backupDB = new File(sd, backupDBPath);
        try {
            source = new FileInputStream(currentDB).getChannel();
            destination = new FileOutputStream(backupDB).getChannel();
            destination.transferFrom(source, 0, source.size());
            source.close();
            destination.close();
            Toast.makeText(this, "DB Exported!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerBroadcastReceiver() {
        final IntentFilter theFilter = new IntentFilter();
        theFilter.addAction(Intent.ACTION_SCREEN_ON);
        theFilter.addAction(Intent.ACTION_SCREEN_OFF);
        this.registerReceiver(screenOnOffReceiver, theFilter);
    }

}
