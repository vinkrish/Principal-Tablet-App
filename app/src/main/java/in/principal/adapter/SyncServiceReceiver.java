package in.principal.adapter;

import in.principal.sync.CallFTP;
import in.principal.util.AppGlobal;
import in.principal.util.NetworkUtils;

import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class SyncServiceReceiver extends BroadcastReceiver {
	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
		int is_first_sync = sharedPref.getInt("first_sync",0);
		int tabletLock = sharedPref.getInt("tablet_lock", 0);
		int bootSync = sharedPref.getInt("boot_sync", 0);
		
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		boolean isScreen = powerManager.isScreenOn();
		WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
		                                 | PowerManager.ACQUIRE_CAUSES_WAKEUP, "WakeLock");
		
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		boolean screenLocked = km.inKeyguardRestrictedInputMode();
		
		if(screenLocked || !isScreen){
			wakeLock.acquire();
			long endTime = System.currentTimeMillis() + 5*1000;
			while (System.currentTimeMillis() < endTime) {}
			wakeLock.release();
		}
		
		if (NetworkUtils.isNetworkConnected(context) && is_first_sync==0 && tabletLock==0 && bootSync==0){
			if(AppGlobal.isActive()){
				new CallFTP().syncFTP();
			}else{
				Intent i = new Intent(context, in.principal.activity.LoginActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("create", 1);
				context.startActivity(i);
			}
		}else if(tabletLock==1 && is_first_sync==0){
			Intent i = new Intent(context, in.principal.activity.LockActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}else if(tabletLock==2 && is_first_sync==0){
			Intent i = new Intent(context, in.principal.activity.ServerBlock.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			context.startActivity(i);
		}else if(is_first_sync==0){
			SharedPreferences.Editor editor = sharedPref.edit();
			editor.putInt("sleep_sync", 1);
			editor.apply();
		}
	}

	public void SetAlarm(Context context){
		AlarmManager am=(AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent i = new Intent(context, SyncServiceReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 7, pi);
	}
}
