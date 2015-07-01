package in.principal.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BootSync extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferences sharedPref = context.getSharedPreferences("db_access", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putInt("boot_sync", 1);
		editor.apply();

		intent.setClassName("in.principal.activity", "in.principal.activity.LoginActivity");
		intent.putExtra("start_sync", 1);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

}
