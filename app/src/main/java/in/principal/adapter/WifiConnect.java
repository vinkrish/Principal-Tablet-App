package in.principal.adapter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;

import in.principal.util.NetworkUtils;

public class WifiConnect extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		context.sendBroadcast(new Intent("INTERNET_STATUS"));
		if(!NetworkUtils.isNetworkConnected(context)){
			WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);
		}
	}
}
