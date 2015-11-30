package in.principal.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by vinkrish.
 */
public class SharedPreferenceUtil {

    public static void updateFirstSync(Context context, int i) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("first_sync", i);
        editor.apply();
    }

    public static void updateSavedVersion(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("saved_version", "v1.3");
        editor.apply();
    }

    public static String getSavedVersion(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        return sharedPref.getString("saved_version", "v1.3");
    }

    public static void updateSleepSync(Context context, int i) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("sleep_sync", i);
        editor.apply();
    }

    public static void updateBootSync(Context context, int i) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("boot_sync", i);
        editor.apply();
    }

    public static void updateApkUpdate(Context context, int i, String folder) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("apk_update", i);
        editor.putInt("newly_updated", 1);
        editor.putString("apk_folder", folder);
        editor.apply();
    }

    public static int getTabletLock(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        return sharedPref.getInt("tablet_lock", 0);
    }

    public static int getManualSync(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        return sharedPref.getInt("manual_sync", 0);
    }

    public static void updateManualSync(Context context, int i) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("manual_sync", i);
        editor.apply();
    }

    public static void updateTabletLock(Context context, int i) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("tablet_lock", i);
        editor.apply();
    }

}
