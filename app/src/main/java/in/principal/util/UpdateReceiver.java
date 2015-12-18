package in.principal.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class UpdateReceiver extends BroadcastReceiver {
    public UpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isNetworkConnected(context)) {
        } else {
            Intent i = new Intent("in.vinkrish.networkChange");
            context.sendBroadcast(i);
        }
    }
}
