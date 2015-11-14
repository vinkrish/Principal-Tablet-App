package in.principal.sync;

import android.app.IntentService;
import android.app.KeyguardManager;
import android.content.Intent;
import android.content.Context;
import android.os.PowerManager;

public class WakeLockIntentService extends IntentService {

    public WakeLockIntentService() {
        super("WakeLockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            PowerManager powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
            boolean isScreen = powerManager.isScreenOn();
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK
                    | PowerManager.ACQUIRE_CAUSES_WAKEUP, "WakeLock");

            KeyguardManager km = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            boolean screenLocked = km.inKeyguardRestrictedInputMode();

            if (screenLocked || !isScreen) {
                wakeLock.acquire();

                long endTime = System.currentTimeMillis() + 3*1000;
                while (System.currentTimeMillis() < endTime) {
                    synchronized (this) {
                        try {
                            wait(endTime - System.currentTimeMillis());
                        } catch (Exception e) {
                        }
                    }
                }

                wakeLock.release();
            }
        }
    }

}
