package in.principal.activity;

import in.principal.sync.UploadError;
import in.principal.util.CommonDialogUtils;
import in.principal.util.NetworkUtils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by vinkrish.
 */
public class Restart extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restart);

        Intent i = getIntent();
        String s = i.getStringExtra("error");
        Log.d("error message", s);

        SharedPreferences sharedPref = this.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("is_sync", 0);
        editor.putInt("first_sync", 0);
        editor.putInt("sleep_sync", 0);
        editor.apply();


        if (NetworkUtils.isNetworkConnected(this)) {
            new UploadError(this, s).upError();
        } else {
            CommonDialogUtils.displayAlertWhiteDialog(this, "Please be in WiFi zone or check the status of WiFi");
        }
        Intent intent = new Intent(this.getApplicationContext(), in.principal.activity.LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
    }
}
