package in.principal.activity;

import in.principal.sync.FirstTimeSync;
import in.principal.util.AnimationUtils;
import in.principal.util.AppGlobal;
import in.principal.util.CommonDialogUtils;
import in.principal.util.ExceptionHandler;
import in.principal.util.NetworkUtils;
import in.principal.util.SharedPreferenceUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MasterAuthentication extends BaseActivity {
    private TextView adminUser, adminPass, deviceId;
    private boolean tvflag, authflag;
    private String passwordText;
    private Context context;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_master_authentication);

        context = AppGlobal.getContext();

        sharedPref = this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        int newlyUpdated = sharedPref.getInt("newly_updated", 0);
        if (newlyUpdated == 1) {
            authSuccess();
        } else
            init();
    }

    private void init(){
        adminUser = (TextView) findViewById(R.id.adminUserName);
        adminPass = (TextView) findViewById(R.id.adminPassword);
        deviceId = (TextView) findViewById(R.id.deviceId);

        String android_id = Secure.getString(getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        deviceId.setText(android_id);

        initializeButton();
    }

    private void initializeButton(){
        int[] buttonIds = {R.id.num1, R.id.num2, R.id.num3, R.id.num4, R.id.num5, R.id.num6, R.id.num7, R.id.num8, R.id.num9, R.id.num0};
        for (int i = 0; i < 10; i++) {
            Button b = (Button) findViewById(buttonIds[i]);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (adminUser.getText().toString().equals("Username")) {
                        adminUser.setText("");
                    } else if (adminPass.getText().toString().equals("Password") && tvflag) {
                        adminPass.setText("");
                    }
                    if (adminUser.getText().toString().equalsIgnoreCase("|")) {
                        adminUser.setText("");
                        adminPass.setHint("Password");
                    } else if (adminPass.getText().toString().equalsIgnoreCase("|")) {
                        passwordText = "";
                        adminPass.setText("");
                    }

                    if (v.getId() == R.id.num1) {
                        updateFields("1");
                    }
                    if (v.getId() == R.id.num2) {
                        updateFields("2");
                    }
                    if (v.getId() == R.id.num3) {
                        updateFields("3");
                    }
                    if (v.getId() == R.id.num4) {
                        updateFields("4");
                    }
                    if (v.getId() == R.id.num5) {
                        updateFields("5");
                    }
                    if (v.getId() == R.id.num6) {
                        updateFields("6");
                    }
                    if (v.getId() == R.id.num7) {
                        updateFields("7");
                    }
                    if (v.getId() == R.id.num8) {
                        updateFields("8");
                    }
                    if (v.getId() == R.id.num9) {
                        updateFields("9");
                    }
                    if (v.getId() == R.id.num0) {
                        updateFields("0");
                    }
                }

            });
        }

        findViewById(R.id.numclear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvflag) {
                    adminPass.setText("|");
                    if (adminUser.getText().toString().equalsIgnoreCase("|")) {
                        adminUser.setText("");
                        adminUser.setHint("Username");
                    }
                } else {
                    adminUser.setText("|");
                    if (adminPass.getText().toString().equalsIgnoreCase("|")) {
                        adminPass.setText("");
                        adminPass.setHint("Password");
                    }
                }

            }
        });

    }

    public void adminUserClicked(View v) {
        if (adminPass.getText().toString().equalsIgnoreCase("|")) {
            adminPass.setText("");
            adminPass.setHint("Password");
        }
        adminUser.setText("|");
        tvflag = false;
    }

    public void adminPassClicked(View v) {
        if (adminUser.getText().toString().equalsIgnoreCase("|")) {
            adminUser.setText("");
            adminUser.setHint("Username");
        }
        adminPass.setText("|");
        tvflag = true;
    }

    private void updateFields(String value) {
        if (tvflag) {
            adminPass.setText(new StringBuffer(adminPass.getText()).append("*"));
            String sb = passwordText + value;
            passwordText = sb;
            if (passwordText.length() == 5)
                authenticate();
        } else {
            String s = adminUser.getText().toString();
            StringBuffer sb = new StringBuffer(s);
            sb.append(value);
            adminUser.setText(sb);
            String s2 = adminUser.getText().toString();
            if (s2.length() == 5)
                preauthenticate();
        }
    }

    private void preauthenticate() {
        adminPass.setText("|");
        tvflag = true;
    }

    private void authenticate() {
        String s = adminUser.getText().toString();
        if (s.isEmpty()) {
            authflag = false;
        } else {
            String enteredId = adminUser.getText().toString();
            if (enteredId.equals("11111") && passwordText.equals("11111")) {
                authflag = true;
                authSuccess();
            }

        }
        if (!authflag) {
            CommonDialogUtils.displayAlertWhiteDialog(this, "Admin is not Authenticated");
        }

        adminUser.setText("Username");
        adminPass.setText("Password");
        tvflag = false;
    }

    private void authSuccess() {
        if (NetworkUtils.isNetworkConnected(context)) {
            setContentView(R.layout.sync);
            SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt("first_sync", 1);
            editor.apply();
            new FirstTimeSync().callFirstTimeSync();
        } else {
            CommonDialogUtils.displayAlertWhiteDialog(this, "Check Internet");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.master_authentication, menu);
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
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferenceUtil.updateFirstSync(this, 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AnimationUtils.activityExit(MasterAuthentication.this);
    }
}
