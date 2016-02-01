package in.principal.adapter;

import in.principal.sync.SyncIntentService;
import in.principal.sync.WakeLockIntentService;
import in.principal.util.AppGlobal;
import in.principal.util.NetworkUtils;
import in.principal.util.SharedPreferenceUtil;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by vinkrish.
 */
public class SyncServiceReceiver extends BroadcastReceiver {
    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(final Context context, Intent intent) {
        SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
        int is_first_sync = sharedPref.getInt("first_sync", 0);
        int tabletLock = sharedPref.getInt("tablet_lock", 0);
        int bootSync = sharedPref.getInt("boot_sync", 0);

        Intent wakeLockIntent = new Intent(context, WakeLockIntentService.class);
        context.startService(wakeLockIntent);

        if (NetworkUtils.isNetworkConnected(context) &&
                is_first_sync == 0 &&
                tabletLock == 0 &&
                bootSync == 0) {
            if (AppGlobal.isActive()) {

                Intent syncService = new Intent(context, SyncIntentService.class);
                context.startService(syncService);

                /*PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
                boolean isScreen = pm.isScreenOn();
                if (!isScreen) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putInt("manual_sync", 1);
                    editor.apply();
                    Intent processIntent = new Intent(context, in.principal.activity.ProcessFiles.class);
                    processIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(processIntent);
                }*/
            } else {
                Intent i = new Intent(context, in.principal.activity.LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        } else if (tabletLock == 1 && is_first_sync == 0) {
            Intent i = new Intent(context, in.principal.activity.LockActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (tabletLock == 2 && is_first_sync == 0) {
            Intent i = new Intent(context, in.principal.activity.ServerBlock.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
        } else if (is_first_sync == 0) {
            SharedPreferenceUtil.updateSleepSync(context, 1);
        }
    }

    public void SetAlarm(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SyncServiceReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 3, pi);
    }
}
