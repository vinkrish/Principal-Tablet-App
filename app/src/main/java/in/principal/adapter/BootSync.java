package in.principal.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import in.principal.util.SharedPreferenceUtil;

public class BootSync extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		SharedPreferenceUtil.updateBootSync(context, 1);

		intent.setClassName("in.principal.activity", "in.principal.activity.LoginActivity");
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

}
