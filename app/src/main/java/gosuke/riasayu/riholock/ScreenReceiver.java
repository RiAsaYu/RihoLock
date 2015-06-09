package gosuke.riasayu.riholock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver {
    static final String TAG = "RihoLock:ScreenReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        if(Intent.ACTION_USER_PRESENT.equals(intent.getAction()))
        {
            Log.d(TAG, "Unlock!");
            Intent service = new Intent(context, RihoLockService.class);
            context.startService(service);
        }
    }
}
