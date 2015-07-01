package in.principal.adapter;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {
	SyncServiceReceiver ssr = new SyncServiceReceiver();

	@Override
	public void onCreate() {
		super.onCreate();     
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) 
	{
		ssr.SetAlarm(SyncService.this);
		return START_STICKY;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		ssr.SetAlarm(this.getApplicationContext());
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
