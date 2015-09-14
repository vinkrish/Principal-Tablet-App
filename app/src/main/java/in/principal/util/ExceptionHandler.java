package in.principal.util;

import in.principal.activity.Restart;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings.Secure;
import android.util.Log;

/**
 * Created by vinkrish.
 */

public class ExceptionHandler implements UncaughtExceptionHandler {
    private final Activity myContext;
    private final String LINE_SEPARATOR = "\n";

    public ExceptionHandler(Activity act) {
        myContext = act;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        StringWriter stackTrace = new StringWriter();
        ex.printStackTrace(new PrintWriter(stackTrace));
        StringBuilder errorReport = new StringBuilder();
        errorReport.append(stackTrace.toString());
        errorReport.append(LINE_SEPARATOR);
        String android_id = Secure.getString(myContext.getBaseContext().getContentResolver(), Secure.ANDROID_ID);
        errorReport.append(android_id);

        Log.d("error", errorReport + "");

        SharedPreferences sharedPref = myContext.getApplicationContext().getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("is_sync", 0);
        editor.putInt("is_screen", 0);
        editor.apply();

        int isSync = sharedPref.getInt("is_sync", 0);
        Log.d("ExceptionHandling", isSync + "");

        Intent intent = new Intent(myContext, Restart.class);
        intent.putExtra("error", errorReport.toString());
        myContext.startActivity(intent);

        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);

    }

}
